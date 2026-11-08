// REQUISITO 9 - Pacotes: sistema coordena o domínio importado exclusivamente de modelo.
package sistema;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;
import modelo.Bibliotecario;
import modelo.Emprestimo;
import modelo.EmprestimoNegadoException;
import modelo.Item;
import modelo.Livro;
import modelo.Pessoa;
import modelo.StatusItem;
import modelo.Usuario;

public class Biblioteca {
    // REQUISITO 1.1 - Encapsulamento: coleções e responsável ficam privados para preservar as regras do serviço.
    private String nome;

    // REQUISITO 5 - Associação por Agregação: o responsável existe independentemente desta biblioteca.
    private Bibliotecario responsavel;

    // REQUISITO 1.4 - ArrayList: coleções dinâmicas armazenam acervo, usuários e vínculos ativos.
    // REQUISITO 5 - Associação por Agregação: itens e usuários são recebidos prontos pelos cadastros.
    private ArrayList<Item> acervo;

    private ArrayList<Usuario> usuarios;

    private ArrayList<Emprestimo> emprestimos;

    // REQUISITO 3 - Construtor: uma biblioteca começa com identidade, responsável e registros vazios.
    // REQUISITO 5 - Associação por Agregação: recebe o bibliotecário criado pelo ponto de entrada.
    public Biblioteca(String nome, Bibliotecario responsavel) {
        this.nome = nome;
        this.responsavel = responsavel;
        // REQUISITO 1.4 - ArrayList: as três listas crescem conforme os cadastros e as operações.
        this.acervo = new ArrayList<Item>();
        this.usuarios = new ArrayList<Usuario>();
        this.emprestimos = new ArrayList<Emprestimo>();
    }

    // REQUISITO 2.1 - Método com argumentos variados (varargs): o mesmo cadastro aceita um ou vários materiais.
    // REQUISITO 5 - Associação por Agregação: os exemplares recebidos já existem fora desta biblioteca.
    public int cadastrarItens(Item... itens) {
        int adicionados = 0;

        // REQUISITO 2.1 - Método com argumentos variados (varargs): o FOR verifica cada argumento e evita códigos repetidos.
        for (Item item : itens) {
            if (buscarItem(item.getCodigo()) == null) {
                acervo.add(item);
                adicionados++;
            }
        }

        return adicionados;
    }

    // REQUISITO 5 - Associação por Agregação: associa o usuário existente sem criar sua carteirinha aqui.
    public void cadastrarUsuario(Usuario usuario) {
        if (buscarUsuario(usuario.getCpf()) != null) {
            throw new IllegalArgumentException("Já existe um usuário com esse CPF.");
        }

        usuarios.add(usuario);
    }

    // REQUISITO 11 - Exceção: o chamador deve tratar as recusas de negócio antes de confirmar o empréstimo.
    public Emprestimo registrarEmprestimo(String cpf, int codigoItem) throws EmprestimoNegadoException {
        Usuario usuario = buscarUsuario(cpf);

        Item item = buscarItem(codigoItem);

        if (usuario == null) {
            // REQUISITO 11 - Exceção: impede empréstimo sem um usuário cadastrado e informa a causa.
            // REQUISITO 3 - Construtor: cria a falha específica que será tratada no menu.
            throw new EmprestimoNegadoException("Usuário não encontrado para o CPF informado.");
        }

        if (item == null) {
            // REQUISITO 11 - Exceção: impede registrar um exemplar ausente do acervo.
            // REQUISITO 3 - Construtor: mantém a mensagem de item inexistente no objeto da falha.
            throw new EmprestimoNegadoException("Item não encontrado para o código informado.");
        }

        // REQUISITO 1.3 - Enumeração: somente o estado disponível permite abrir um novo empréstimo.
        if (item.getStatus() != StatusItem.DISPONIVEL) {
            // REQUISITO 11 - Exceção: evita emprestar simultaneamente o mesmo exemplar.
            // REQUISITO 3 - Construtor: transporta a razão de indisponibilidade até a interface.
            throw new EmprestimoNegadoException("O item não está disponível para empréstimo.");
        }

        // REQUISITO 8 - Interface: consulta a validade pelo comportamento Validavel do usuário.
        if (!usuario.estaValido()) {
            // REQUISITO 11 - Exceção: bloqueia o acesso de uma carteirinha vencida.
            // REQUISITO 3 - Construtor: cria a recusa específica de validade da carteirinha.
            throw new EmprestimoNegadoException("A carteirinha do usuário está vencida.");
        }

        // REQUISITO 10 - Classe abstrata: a regra concreta de Usuario determina seu limite permitido.
        if (usuario.getEmprestimosAtivos() >= usuario.calcularLimiteEmprestimos()) {
            // REQUISITO 11 - Exceção: impede ultrapassar o limite pessoal de empréstimos ativos.
            // REQUISITO 3 - Construtor: informa a causa de limite atingido ao tratamento da interface.
            throw new EmprestimoNegadoException("O usuário atingiu o limite de empréstimos ativos.");
        }

        LocalDate hoje = LocalDate.now();

        // REQUISITO 1.5 - Constante: o prazo declarado em outra classe determina a devolução de todo empréstimo.
        LocalDate prevista = LocalDate.now().plusDays(Emprestimo.PRAZO_PADRAO_DIAS);

        // REQUISITO 3 - Construtor: cria o vínculo somente depois de validar todas as condições.
        // REQUISITO 5 - Associação por Agregação: usuário e item encontrados são reutilizados no vínculo.
        Emprestimo emprestimo = new Emprestimo(usuario, item, hoje, prevista);

        // REQUISITO 1.3 - Enumeração: a mudança para emprestado impede novos empréstimos do exemplar.
        item.setStatus(StatusItem.EMPRESTADO);

        usuario.incrementarEmprestimos();

        emprestimos.add(emprestimo);

        return emprestimo;
    }

    public float registrarDevolucao(int codigoItem, LocalDate dataDevolucao) {
        for (int indice = 0; indice < emprestimos.size(); indice++) {
            Emprestimo emprestimo = emprestimos.get(indice);

            if (emprestimo.getItem().getCodigo() == codigoItem) {
                if (dataDevolucao.isBefore(emprestimo.getDataEmprestimo())) {
                    throw new IllegalArgumentException("A devolução não pode ser anterior ao empréstimo.");
                }

                LocalDate prevista = emprestimo.getDataPrevistaDevolucao();

                // REQUISITO 2.2 - Método estático: o cálculo compartilhado garante atraso mínimo de zero dias.
                int dias = Utilitarios.calcularDiasAtraso(prevista, dataDevolucao);

                Item item = emprestimo.getItem();

                // REQUISITO 10 - Classe abstrata: a implementação concreta do material determina sua multa.
                float multa = item.calcularMulta(dias);

                // REQUISITO 1.3 - Enumeração: a devolução libera o exemplar para o próximo usuário.
                item.setStatus(StatusItem.DISPONIVEL);

                emprestimo.getUsuario().decrementarEmprestimos();

                emprestimos.remove(indice);

                return multa;
            }
        }
        throw new IllegalArgumentException("Não existe empréstimo ativo para esse item.");
    }

    // REQUISITO 7.1 - Polimorfismo de sobrecarga: a busca textual localiza partes do título sem distinguir maiúsculas.
    public ArrayList<Item> buscar(String titulo) {
        // REQUISITO 1.4 - ArrayList: reúne todos os resultados da pesquisa textual.
        ArrayList<Item> encontrados = new ArrayList<Item>();

        String trecho = titulo.toLowerCase(Locale.ROOT);

        for (Item item : acervo) {
            if (item.getTitulo().toLowerCase(Locale.ROOT).contains(trecho)) {
                encontrados.add(item);
            }
        }

        return encontrados;
    }

    // REQUISITO 7.1 - Polimorfismo de sobrecarga: o parâmetro inteiro seleciona a pesquisa pelo ano de publicação.
    public ArrayList<Item> buscar(int anoPublicacao) {
        // REQUISITO 1.4 - ArrayList: reúne os materiais publicados no ano solicitado.
        ArrayList<Item> encontrados = new ArrayList<Item>();

        for (Item item : acervo) {
            if (item.getAnoPublicacao() == anoPublicacao) {
                encontrados.add(item);
            }
        }

        return encontrados;
    }

    public Item buscarItem(int codigo) {
        for (Item item : acervo) {
            if (item.getCodigo() == codigo) { return item; }
        }

        return null;
    }

    public Usuario buscarUsuario(String cpf) {
        for (Usuario usuario : usuarios) {
            if (usuario.getCpf().equals(cpf)) { return usuario; }
        }

        return null;
    }

    public Livro buscarLivro(int codigo) {
        Item item = buscarItem(codigo);

        // REQUISITO 7.3 - Polimorfismo de subtipagem: tanto Livro quanto LivroDigital são retornados como Livro concreto.
        return item instanceof Livro livro ? livro : null;
    }

    // REQUISITO 7.3 - Polimorfismo de subtipagem: a referência do pai concreto aceita o filho concreto na simulação.
    public float simularMultaLivro(Livro livro, int dias) {
        // REQUISITO 7.3 - Polimorfismo de subtipagem: o despacho dinâmico escolhe a multa física ou digital.
        return livro.calcularMulta(dias);
    }

    public int consultarLimite(Pessoa pessoa) {
        // REQUISITO 10 - Classe abstrata: a referência Pessoa consulta a política do usuário ou bibliotecário concreto.
        return pessoa.calcularLimiteEmprestimos();
    }

    // REQUISITO 13 - Arquivo: a carga recompõe os vínculos ativos e seus efeitos no domínio.
    // REQUISITO 5 - Associação por Agregação: o empréstimo recebido já referencia usuário e item cadastrados.
    public void restaurarEmprestimo(Emprestimo emprestimo) {
        if (buscarUsuario(emprestimo.getUsuario().getCpf()) != emprestimo.getUsuario() || buscarItem(emprestimo.getItem().getCodigo()) != emprestimo.getItem()) {
            throw new IllegalArgumentException("O empréstimo referencia objetos não cadastrados.");
        }

        for (Emprestimo ativo : emprestimos) {
            if (ativo.getItem() == emprestimo.getItem()) {
                throw new IllegalArgumentException("O item já possui um empréstimo ativo.");
            }
        }

        emprestimos.add(emprestimo);

        // REQUISITO 1.3 - Enumeração: o material restaurado permanece indisponível enquanto houver vínculo ativo.
        emprestimo.getItem().setStatus(StatusItem.EMPRESTADO);

        emprestimo.getUsuario().incrementarEmprestimos();
    }

    public String getNome(){
        return nome;
    }

    public Bibliotecario getResponsavel(){
        return responsavel;
    }

    // REQUISITO 1.1 - Encapsulamento: cópias das listas impedem remover registros contornando o serviço.
    // REQUISITO 1.4 - ArrayList: os getters preservam os tipos de coleção esperados pelos relatórios.
    public ArrayList<Item> getAcervo(){
        return new ArrayList<Item>(acervo);
    }

    public ArrayList<Usuario> getUsuarios(){
        return new ArrayList<Usuario>(usuarios);
    }

    public ArrayList<Emprestimo> getEmprestimos(){
        return new ArrayList<Emprestimo>(emprestimos);
    }

    public String gerarRelatorioAcervo() {
        // REQUISITO 7.4 - Polimorfismo paramétrico: o tipo Item é informado explicitamente para o relatório do acervo.
        // REQUISITO 2.2 - Método estático: reutiliza a formatação compartilhada das listas da aplicação.
        return "Acervo de " + nome + "\n\n" + Utilitarios.<Item>formatarLista(acervo);
    }
}
