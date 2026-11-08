// REQUISITO 9 - Pacotes: sistema cuida da persistência sem introduzir dependência inversa em modelo.
package sistema;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import modelo.Emprestimo;
import modelo.Exportavel;
import modelo.Genero;
import modelo.Item;
import modelo.Livro;
import modelo.LivroDigital;
import modelo.Revista;
import modelo.StatusItem;
import modelo.Usuario;

public class ArquivoBiblioteca {
    // REQUISITO 1.1 - Encapsulamento: o caminho só é definido ao criar o serviço de persistência.
    private String caminho;

    // REQUISITO 3 - Construtor: permite escolher onde os dados da biblioteca serão mantidos.
    public ArquivoBiblioteca(String caminho) {
        this.caminho = caminho;
    }

    // REQUISITO 13 - Arquivo: confirma se há dados persistidos antes de decidir pela carga inicial.
    public boolean existe() { return Files.exists(Path.of(caminho)); }

    // REQUISITO 13 - Arquivo: grava uma linha por entidade e substitui o arquivo após concluir a escrita.
    // REQUISITO 11 - Exceção: falhas de entrada e saída são propagadas para tratamento com diálogo.
    public void salvar(Biblioteca biblioteca) throws IOException {
        // REQUISITO 1.4 - ArrayList: acumula as linhas para uma única gravação UTF-8.
        ArrayList<String> linhas = new ArrayList<String>();

        for (Item item : biblioteca.getAcervo()) {
            // REQUISITO 8 - Interface: o contrato Exportavel atende tanto livros quanto revistas.
            if (!(item instanceof Exportavel exportavel)) {
                throw new IOException("Há um material sem suporte à exportação no acervo.");
            }
            
            // REQUISITO 13 - Arquivo: cada material fornece sua linha com tipo, campos e status.
            linhas.add(exportavel.paraLinhaArquivo());
        }

        for (Usuario usuario : biblioteca.getUsuarios()) {
            // REQUISITO 8 - Interface: a exportação do usuário inclui a validade de sua carteirinha.
            // REQUISITO 13 - Arquivo: preserva o cadastro necessário à recuperação de empréstimos.
            linhas.add(usuario.paraLinhaArquivo());
        }

        for (Emprestimo emprestimo : biblioteca.getEmprestimos()) {
            // REQUISITO 8 - Interface: o empréstimo também implementa a representação de exportação.
            // REQUISITO 13 - Arquivo: os vínculos ativos guardam CPF, código e as duas datas originais.
            linhas.add(emprestimo.paraLinhaArquivo());
        }

        Path destino = Path.of(caminho).toAbsolutePath();

        Path temporario = Files.createTempFile(destino.getParent(), "biblioteca-", ".tmp");

        try {
            // REQUISITO 13 - Arquivo: UTF-8 mantém acentos e o temporário protege a versão anterior durante a escrita.
            Files.write(temporario, linhas, StandardCharsets.UTF_8);

            try {
                Files.move(temporario, destino, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException excecao) {
                // REQUISITO 11 - Exceção: sistemas sem troca atômica ainda podem concluir a gravação normal.
                Files.move(temporario, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporario);
        }
    }

    // REQUISITO 13 - Arquivo: a leitura valida todos os registros antes de alterar a biblioteca de destino.
    // REQUISITO 11 - Exceção: um arquivo inválido é comunicado como IOException com a linha problemática.
    public void carregar(Biblioteca biblioteca) throws IOException {
        if (!biblioteca.getAcervo().isEmpty() || !biblioteca.getUsuarios().isEmpty()
                || !biblioteca.getEmprestimos().isEmpty()) {
            throw new IOException("Carregue os dados em uma biblioteca vazia para evitar duplicações.");
        }

        // REQUISITO 3 - Construtor: uma biblioteca temporária permite validar a carga sem alterar o destino parcialmente.
        // REQUISITO 5 - Associação por Agregação: o responsável existente é compartilhado durante a validação.
        Biblioteca restaurada = new Biblioteca(biblioteca.getNome(), biblioteca.getResponsavel());

        // REQUISITO 1.4 - ArrayList: armazena empréstimos para leitura após todos os participantes existirem.
        ArrayList<String[]> vinculos = new ArrayList<String[]>();

        // REQUISITO 13 - Arquivo: a leitura usa a mesma codificação da gravação para recuperar os textos.
        int numeroLinha = 0;
        
        for (String linha : Files.readAllLines(Path.of(caminho), StandardCharsets.UTF_8)) {
            
            numeroLinha++;
            
            if (linha.isBlank()){
                continue;
            }

            String[] campos = linha.split(";", -1);

            try {
                switch (campos[0]) {
                    case "LIVRO", "LIVRODIGITAL", "REVISTA" -> carregarItem(restaurada, campos);
                    case "USUARIO" -> {
                        conferirCampos(campos, 4);
                        // REQUISITO 3 - Construtor: a versão com validade recupera exatamente a carteirinha salva.
                        // REQUISITO 4 - Associação por Composição: Usuario recria internamente sua própria carteirinha.
                        // REQUISITO 7.1 - Polimorfismo de sobrecarga: usa a alternativa com data para não renovar o cadastro.
                        Usuario usuario = new Usuario(campos[1], campos[2], LocalDate.parse(campos[3]));
                        // REQUISITO 5 - Associação por Agregação: o usuário reconstruído passa a integrar o cadastro.
                        restaurada.cadastrarUsuario(usuario);
                    }
                    case "EMPRESTIMO" -> {
                        conferirCampos(campos, 5);
                        vinculos.add(campos);
                    }
                    default -> throw new IllegalArgumentException("Tipo de registro desconhecido: " + campos[0]);
                }
            } catch (RuntimeException excecao) {
                // REQUISITO 11 - Exceção: converte falhas de conteúdo em erro de leitura compreensível.
                throw new IOException("Dados inválidos na linha " + numeroLinha + ": " + excecao.getMessage(), excecao);
            }
        }

        for (String[] campos : vinculos) {
            try {
                Usuario usuario = restaurada.buscarUsuario(campos[1]);

                Item item = restaurada.buscarItem(Integer.parseInt(campos[2]));

                if (usuario == null || item == null) {
                    throw new IllegalArgumentException("Usuário ou item do empréstimo não encontrado.");
                }

                // REQUISITO 1.3 - Enumeração: o status persistido deve concordar com o vínculo ativo.
                if (item.getStatus() != StatusItem.EMPRESTADO) {
                    throw new IllegalArgumentException("O status do item contradiz seu empréstimo ativo.");
                }

                LocalDate data = LocalDate.parse(campos[3]);

                LocalDate prevista = LocalDate.parse(campos[4]);

                if (prevista.isBefore(data)) {
                    throw new IllegalArgumentException("A previsão de devolução antecede o empréstimo.");
                }
                
                // REQUISITO 3 - Construtor: recupera as datas originais em vez de criar um novo prazo.
                // REQUISITO 5 - Associação por Agregação: associa os mesmos objetos já reconstruídos no cadastro.
                Emprestimo emprestimo = new Emprestimo(usuario, item, data, prevista);

                // REQUISITO 13 - Arquivo: recompõe a lista ativa, a disponibilidade e o contador do usuário.
                restaurada.restaurarEmprestimo(emprestimo);

                // REQUISITO 10 - Classe abstrata: a política concreta evita restaurar um contador acima do limite.
                if (usuario.getEmprestimosAtivos() > usuario.calcularLimiteEmprestimos()) {
                    throw new IllegalArgumentException("O usuário possui mais empréstimos que o limite permitido.");
                }
            } catch (RuntimeException excecao) {
                // REQUISITO 11 - Exceção: vínculos inconsistentes interrompem a carga antes de modificar o destino.
                throw new IOException("Empréstimo inválido para o CPF " + campos[1] + ": " + excecao.getMessage(), excecao);
            }
        }

        for (Item item : restaurada.getAcervo()) {
            // REQUISITO 1.3 - Enumeração: um item marcado emprestado deve ter seu vínculo correspondente.
            if (item.getStatus() == StatusItem.EMPRESTADO) {
                boolean possuiVinculo = false;

                for (Emprestimo emprestimo : restaurada.getEmprestimos()) {
                    if (emprestimo.getItem() == item) { possuiVinculo = true; break; }
                }

                if (!possuiVinculo) {
                    throw new IOException("O item " + item.getCodigo() + " está emprestado, mas não possui vínculo salvo.");
                }
            }
        }

        // REQUISITO 13 - Arquivo: apenas dados completamente validados são disponibilizados ao sistema.
        for (Usuario usuario : restaurada.getUsuarios()) {
            // REQUISITO 5 - Associação por Agregação: transfere os usuários já reconstruídos para a biblioteca real.
            biblioteca.cadastrarUsuario(usuario);

            // O contador será refeito ao restaurar os vínculos na biblioteca de destino.
            while (usuario.getEmprestimosAtivos() > 0) { usuario.decrementarEmprestimos(); }
        }

        for (Item item : restaurada.getAcervo()) {
            // REQUISITO 2.1 - Método com argumentos variados (varargs): a carga cadastra cada material pelo mesmo serviço.
            // REQUISITO 5 - Associação por Agregação: incorpora os itens existentes sem recriar seus dados.
            biblioteca.cadastrarItens(item);
        }

        for (Emprestimo emprestimo : restaurada.getEmprestimos()) {
            // REQUISITO 13 - Arquivo: restaura os efeitos dos vínculos na biblioteca que o menu realmente utiliza.
            biblioteca.restaurarEmprestimo(emprestimo);
        }
    }

    private void carregarItem(Biblioteca biblioteca, String[] campos) {
        Item item;

        if ("REVISTA".equals(campos[0])) {
            conferirCampos(campos, 7);

            // REQUISITO 3 - Construtor: reconstitui os dados específicos de cada edição.
            item = new Revista(inteiroPositivo(campos[1]), campos[2], inteiroPositivo(campos[3]), inteiroPositivo(campos[4]), campos[5]);

            // REQUISITO 1.3 - Enumeração: recupera exatamente a disponibilidade salva da revista.
            item.setStatus(StatusItem.valueOf(campos[6]));
        } else {
            boolean digital = "LIVRODIGITAL".equals(campos[0]);

            conferirCampos(campos, digital ? 11 : 9);

            int codigo = inteiroPositivo(campos[1]);

            int ano = inteiroPositivo(campos[3]);

            int paginas = inteiroPositivo(campos[5]);

            float reposicao = decimalNaoNegativo(campos[6]);

            // REQUISITO 1.3 - Enumeração: a classificação salva é convertida no gênero reconhecido pelo domínio.
            Genero genero = Genero.valueOf(campos[7]);

            if (digital) {
                // REQUISITO 3 - Construtor: reconstrói a filha concreta com tamanho e formato originais.
                item = new LivroDigital(codigo, campos[2], ano, campos[4], paginas, reposicao, genero, decimalNaoNegativo(campos[9]), campos[10]);
            } else {
                // REQUISITO 3 - Construtor: reconstrói o livro físico sem perder ISBN ou valor de reposição.
                item = new Livro(codigo, campos[2], ano, campos[4], paginas, reposicao, genero);
            }
            // REQUISITO 1.3 - Enumeração: o estado salvo é restaurado antes de validar os empréstimos.
            item.setStatus(StatusItem.valueOf(campos[8]));
        }
        // REQUISITO 2.1 - Método com argumentos variados (varargs): usa o cadastro central também durante a leitura.
        // REQUISITO 5 - Associação por Agregação: o item recém-reconstruído é recebido pela biblioteca.
        if (biblioteca.cadastrarItens(item) != 1) {
            throw new IllegalArgumentException("Código de item duplicado no arquivo.");
        }
    }

    private void conferirCampos(String[] campos, int quantidade) {
        if (campos.length != quantidade) {
            throw new IllegalArgumentException("Quantidade de campos incorreta para " + campos[0] + ".");
        }

        for (String campo : campos) {
            if (campo.isBlank()) { throw new IllegalArgumentException("O registro contém um campo vazio."); }
        }
    }

    private int inteiroPositivo(String texto) {
        int valor = Integer.parseInt(texto);

        if (valor <= 0){
            throw new IllegalArgumentException("O valor inteiro deve ser positivo.");
        }

        return valor;
    }

    private float decimalNaoNegativo(String texto) {
        float valor = Float.parseFloat(texto);

        if (!Float.isFinite(valor) || valor < 0) {
            throw new IllegalArgumentException("O valor decimal deve ser finito e não negativo.");
        }

        return valor;
    }
}
