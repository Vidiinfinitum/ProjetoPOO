// REQUISITO 9 - Pacotes: sistema reúne o fluxo de interação com as entidades do pacote modelo.
package sistema;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import javax.swing.JOptionPane;
import modelo.Emprestimo;
import modelo.EmprestimoNegadoException;
import modelo.Genero;
import modelo.Item;
import modelo.Livro;
import modelo.LivroDigital;
import modelo.Revista;
import modelo.Usuario;

public class MenuPrincipal {
    // REQUISITO 1.1 - Encapsulamento: os serviços usados pelo menu permanecem privados.
    // REQUISITO 5 - Associação por Agregação: o menu recebe serviços já existentes, sem recriar a biblioteca.
    private Biblioteca biblioteca;
    private ArquivoBiblioteca arquivo;
    private VitrineDigital vitrine;

    // REQUISITO 3 - Construtor: conecta o menu à biblioteca efetivamente carregada e aos serviços de arquivo e vitrine.
    public MenuPrincipal(Biblioteca biblioteca, ArquivoBiblioteca arquivo, VitrineDigital vitrine) {
        this.biblioteca = biblioteca;
        this.arquivo = arquivo;
        this.vitrine = vitrine;
    }

    // REQUISITO 12 - Entrada de dados por caixas de diálogo: todo o fluxo utiliza somente JOptionPane.
    public void executar() {
        String opcoes = """
                Sistema de Biblioteca

                1 - Cadastrar livro
                2 - Cadastrar revista
                3 - Cadastrar usuário
                4 - Registrar empréstimo
                5 - Registrar devolução
                6 - Buscar por título
                7 - Buscar por ano
                8 - Listar acervo
                9 - Listar usuários e empréstimos
                10 - Simular multa de livro
                11 - Ver destaque digital
                12 - Consultar limites de empréstimo
                13 - Salvar dados
                0 - Sair
                """;
        while (true) {
            try {
                // REQUISITO 12 - Entrada de dados por caixas de diálogo: o menu recebe a opção por entrada textual.
                String escolha = JOptionPane.showInputDialog(null, opcoes, biblioteca.getNome(), JOptionPane.QUESTION_MESSAGE);

                if (escolha == null) {
                    if (confirmarSaida()) { return; }
                    continue;
                }

                int opcao = Integer.parseInt(escolha.trim());

                switch (opcao) {
                    case 1 -> cadastrarLivro();

                    case 2 -> cadastrarRevista();

                    case 3 -> cadastrarUsuario();

                    case 4 -> registrarEmprestimo();

                    case 5 -> registrarDevolucao();

                    case 6 -> buscarPorTitulo();

                    case 7 -> buscarPorAno();

                    case 8 -> informar(biblioteca.gerarRelatorioAcervo());

                    case 9 -> listarUsuariosEEmprestimos();

                    case 10 -> simularMulta();

                    case 11 -> {
                        // REQUISITO 6.2 - Acoplamento dinâmico: a opção apresenta a ficha herdada pelo destaque digital.
                        informar(vitrine.exibirDestaque());
                    }

                    case 12 -> consultarLimites();

                    case 13 -> {
                        // REQUISITO 13 - Arquivo: o usuário pode persistir os dados sem sair do menu.
                        arquivo.salvar(biblioteca);

                        informar("Dados salvos com sucesso.");
                    }

                    case 0 -> {
                        if (confirmarSaida()){
                            return;
                        }
                    }

                    default -> erro("Opção inválida. Escolha um número entre 0 e 13.");
                }
            } catch (NumberFormatException excecao) {
                // REQUISITO 11 - Exceção: entradas numéricas incorretas voltam ao menu sem encerrar o programa.
                erro("Número inválido. Digite apenas um número no campo solicitado.");
            } catch (DateTimeParseException excecao) {
                // REQUISITO 11 - Exceção: datas inexistentes ou fora do formato recebem orientação amigável.
                erro("Data inválida. Use dd/MM/yyyy e informe uma data existente, por exemplo 25/12/2026.");
            } catch (IOException excecao) {
                // REQUISITO 11 - Exceção: uma falha de gravação mantém o menu aberto para permitir nova tentativa.
                // REQUISITO 13 - Arquivo: o usuário é informado de que a persistência não foi concluída.
                erro("Não foi possível salvar os dados: " + excecao.getMessage());
            } catch (IllegalArgumentException excecao) {
                // REQUISITO 11 - Exceção: validações de cadastro e devolução são apresentadas sem quebrar o fluxo.
                erro(excecao.getMessage());
            }
        }
    }

    private void cadastrarLivro() {
        // REQUISITO 12 - Entrada de dados por caixas de diálogo: a confirmação escolhe entre livro físico e digital.
        int resposta = JOptionPane.showConfirmDialog(null, "O livro é digital?", "Cadastrar livro",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (resposta == JOptionPane.CANCEL_OPTION || resposta == JOptionPane.CLOSED_OPTION){
            return;
        }

        boolean digital = resposta == JOptionPane.YES_OPTION;

        Integer codigo = lerInteiro("Código do livro:", false);

        if (codigo == null){
            return;
        }

        String titulo = lerTexto("Título do livro:");

        if (titulo == null){
            return;
        }

        Integer ano = lerInteiro("Ano de publicação:", false);

        if (ano == null){
            return;
        }

        String isbn = lerTexto("ISBN:");

        if (isbn == null) {
            return;
        }

        Integer paginas = lerInteiro("Número de páginas:", false);

        if (paginas == null){
            return;
        }

        Float reposicao = lerDecimal("Valor de reposição (ex.: 45,50):");

        if (reposicao == null){
            return;
        }

        // REQUISITO 12 - Entrada de dados por caixas de diálogo: um diálogo limita a seleção aos gêneros válidos.
        // REQUISITO 1.3 - Enumeração: as opções são obtidas diretamente dos gêneros reconhecidos pelo modelo.
        Object escolha = JOptionPane.showInputDialog(null, "Gênero:", "Cadastrar livro", JOptionPane.QUESTION_MESSAGE, null, Genero.values(), Genero.FICCAO);

        if (escolha == null){
            return;
        }

        // REQUISITO 1.3 - Enumeração: converte a seleção em classificação tipada do livro.
        Genero genero = Genero.valueOf(escolha.toString());
        Livro livro;

        if (digital) {
            Float tamanho = lerDecimal("Tamanho em MB (ex.: 5,5):");

            if (tamanho == null){
                return;
            }

            String formato = lerTexto("Formato (ex.: PDF ou EPUB):");

            if (formato == null){
                return;
            }

            // REQUISITO 3 - Construtor: cria a versão digital com todos os dados obtidos pelos diálogos.
            // REQUISITO 7.3 - Polimorfismo de subtipagem: a referência Livro recebe uma instância concreta LivroDigital.
            livro = new LivroDigital(codigo, titulo, ano, isbn, paginas, reposicao, genero, tamanho, formato);
        } else {
            // REQUISITO 3 - Construtor: cria o exemplar físico com os dados completos do cadastro.
            livro = new Livro(codigo, titulo, ano, isbn, paginas, reposicao, genero);
        }
        // REQUISITO 2.1 - Método com argumentos variados (varargs): o menu usa o mesmo cadastro com um único livro.
        // REQUISITO 5 - Associação por Agregação: o livro pronto é entregue à biblioteca.
        int adicionados = biblioteca.cadastrarItens(livro);

        informarResultadoCadastro(adicionados);
    }

    private void cadastrarRevista() {
        Integer codigo = lerInteiro("Código da revista:", false);

        if (codigo == null){
            return;
        }

        String titulo = lerTexto("Título da revista:");

        if (titulo == null) {
            return;
        }

        Integer ano = lerInteiro("Ano de publicação:", false);

        if (ano == null) {
            return;
        }

        Integer edicao = lerInteiro("Número da edição:", false);

        if (edicao == null) {
            return;
        }

        String periodicidade = lerTexto("Periodicidade (ex.: mensal):");

        if (periodicidade == null) {
            return;
        }

        // REQUISITO 3 - Construtor: cria a edição solicitada pelos diálogos de cadastro.
        Revista revista = new Revista(codigo, titulo, ano, edicao, periodicidade);

        // REQUISITO 2.1 - Método com argumentos variados (varargs): uma revista usa a mesma operação de cadastro dos livros.
        // REQUISITO 5 - Associação por Agregação: o periódico existe antes de entrar no acervo.
        informarResultadoCadastro(biblioteca.cadastrarItens(revista));
    }

    private void informarResultadoCadastro(int quantidade) {
        if (quantidade == 0) {
            erro("Já existe um item com esse código. Nenhum item foi adicionado.");
        } else {
            informar("Item cadastrado com sucesso.");
        }
    }

    private void cadastrarUsuario() {
        String nome = lerTexto("Nome do usuário:");

        if (nome == null){
            return;
        }

        String cpf = lerTexto("CPF do usuário (use sempre o mesmo formato):");

        if (cpf == null) {
            return;
        }

        // REQUISITO 3 - Construtor: o cadastro comum utiliza a validade automática de um ano.
        // REQUISITO 4 - Associação por Composição: o construtor de Usuario cria a carteirinha sem participação do menu.
        // REQUISITO 7.1 - Polimorfismo de sobrecarga: a versão sem data atende ao novo cadastro.
        Usuario usuario = new Usuario(nome, cpf);

        // REQUISITO 5 - Associação por Agregação: a biblioteca recebe o usuário já criado.
        biblioteca.cadastrarUsuario(usuario);

        informar("Usuário cadastrado. Carteirinha válida até " + usuario.getCarteirinha().getValidade().format(DateTimeFormatter.ofPattern("dd/MM/uuuu")) + ".");
    }

    private void registrarEmprestimo() {
        String cpf = lerTexto("CPF do usuário:");

        if (cpf == null){
            return;
        }

        Integer codigo = lerInteiro("Código do item:", false);

        if (codigo == null){
            return;
        }

        try {
            // REQUISITO 11 - Exceção: o menu chama a operação que pode recusar o empréstimo por regra de negócio.
            Emprestimo emprestimo = biblioteca.registrarEmprestimo(cpf, codigo);

            informar("Empréstimo registrado. Devolução prevista: " + emprestimo.getDataPrevistaDevolucao().format(DateTimeFormatter.ofPattern("dd/MM/uuuu")) + ".");
        } catch (EmprestimoNegadoException excecao) {
            // REQUISITO 11 - Exceção: trata aqui a exceção checada da opção 4 com a mensagem específica da recusa.
            // REQUISITO 12 - Entrada de dados por caixas de diálogo: a recusa é exibida em JOptionPane de erro.
            JOptionPane.showMessageDialog(null, excecao.getMessage(), "Empréstimo negado", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarDevolucao() {
        Integer codigo = lerInteiro("Código do item a devolver:", false);

        if (codigo == null){
            return;
        }

        // REQUISITO 12 - Entrada de dados por caixas de diálogo: uma data informada permite testar devolução com atraso.
        String entrada = JOptionPane.showInputDialog(null, "Data de devolução (dd/MM/yyyy), ou deixe vazio para hoje:", "Registrar devolução", JOptionPane.QUESTION_MESSAGE);

        if (entrada == null){
            return;
        }

        LocalDate data = entrada.isBlank() ? LocalDate.now() : LocalDate.parse(entrada.trim(), DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT));

        float multa = biblioteca.registrarDevolucao(codigo, data);

        // REQUISITO 2.2 - Método estático: a mensagem de devolução utiliza a formatação monetária compartilhada.
        informar("Devolução registrada. Multa: " + Utilitarios.formatarMoeda(multa) + ".");
    }

    private void buscarPorTitulo() {
        String titulo = lerTexto("Título ou parte do título:");

        if (titulo == null){
            return;
        }

        // REQUISITO 7.1 - Polimorfismo de sobrecarga: o argumento String escolhe a busca parcial por título.
        // REQUISITO 7.4 - Polimorfismo paramétrico: informa explicitamente Item para formatar o resultado da busca.
        // REQUISITO 2.2 - Método estático: reutiliza a apresentação de coleções da aplicação.
        informar(Utilitarios.<Item>formatarLista(biblioteca.buscar(titulo)));
    }

    private void buscarPorAno() {
        Integer ano = lerInteiro("Ano de publicação:", false);

        if (ano == null){
            return;
        }

        // REQUISITO 7.1 - Polimorfismo de sobrecarga: intValue fornece um int para selecionar a busca pelo ano.
        // REQUISITO 7.4 - Polimorfismo paramétrico: Item é explícito na formatação dos materiais encontrados.
        // REQUISITO 2.2 - Método estático: a mesma apresentação é aplicada à busca por ano.
        informar(Utilitarios.<Item>formatarLista(biblioteca.buscar(ano.intValue())));
    }

    private void listarUsuariosEEmprestimos() {
        // REQUISITO 7.4 - Polimorfismo paramétrico: Usuario e Emprestimo são informados explicitamente em cada chamada.
        // REQUISITO 2.2 - Método estático: uma rotina compartilhada apresenta dois tipos distintos de registro.
        informar("USUÁRIOS\n" + Utilitarios.<Usuario>formatarLista(biblioteca.getUsuarios())
                + "\nEMPRÉSTIMOS ATIVOS\n" + Utilitarios.<Emprestimo>formatarLista(biblioteca.getEmprestimos()));
    }

    private void simularMulta() {
        Integer codigo = lerInteiro("Código do livro físico ou digital:", false);

        if (codigo == null){
            return;
        }

        Integer dias = lerInteiro("Dias de atraso (zero ou mais):", true);

        if (dias == null){
            return;
        }

        // REQUISITO 7.3 - Polimorfismo de subtipagem: a referência do pai concreto pode apontar para LivroDigital.
        Livro livro = biblioteca.buscarLivro(codigo);

        if (livro == null){
            erro("Não foi encontrado um livro físico ou digital com esse código.");
            return;
        }

        // REQUISITO 7.3 - Polimorfismo de subtipagem: o mesmo parâmetro Livro atende exemplares físicos e digitais.
        float multa = biblioteca.simularMultaLivro(livro, dias);

        // REQUISITO 2.2 - Método estático: a simulação apresenta o valor com a mesma moeda da devolução.
        informar("Multa simulada de " + livro.getTitulo() + ": " + Utilitarios.formatarMoeda(multa) + ".");
    }

    private void consultarLimites() {
        String cpf = lerTexto("CPF do usuário para consultar o limite:");

        if (cpf == null){
            return;
        }

        Usuario usuario = biblioteca.buscarUsuario(cpf);

        if (usuario == null){
            erro("Usuário não encontrado.");
            return;
        }

        // REQUISITO 10 - Classe abstrata: os dois papéis concretos são consultados pelo parâmetro Pessoa.
        int limiteUsuario = biblioteca.consultarLimite(usuario);

        int limiteResponsavel = biblioteca.consultarLimite(biblioteca.getResponsavel());

        informar("Usuário: " + usuario.getNome() + "\nLimite: " + limiteUsuario
                + "\nEmpréstimos ativos: " + usuario.getEmprestimosAtivos()
                + "\n\nBibliotecário responsável: " + biblioteca.getResponsavel().getNome()
                + "\nLimite: " + limiteResponsavel);
    }

    private boolean confirmarSaida() throws IOException {
        // REQUISITO 12 - Entrada de dados por caixas de diálogo: permite salvar e sair, sair sem salvar ou cancelar a saída.
        int resposta = JOptionPane.showConfirmDialog(null, "Deseja salvar os dados antes de sair?", "Encerrar",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (resposta == JOptionPane.YES_OPTION){
            // REQUISITO 13 - Arquivo: salva apenas quando solicitado; IOException mantém o menu aberto para nova tentativa.
            arquivo.salvar(biblioteca);
            return true;
        }
        
        return resposta == JOptionPane.NO_OPTION;
    }

    private String lerTexto(String mensagem) {
        // REQUISITO 12 - Entrada de dados por caixas de diálogo: todos os campos textuais tratam cancelamento como retorno ao menu.
        String entrada = JOptionPane.showInputDialog(null, mensagem, biblioteca.getNome(), JOptionPane.QUESTION_MESSAGE);

        if (entrada == null){
            return null;
        }

        entrada = entrada.trim();

        if (entrada.isEmpty()){
            throw new IllegalArgumentException("O campo não pode ficar vazio.");
        }

        // REQUISITO 13 - Arquivo: rejeita delimitadores e quebras de linha para manter uma linha inequívoca por objeto.
        if (entrada.contains(";") || entrada.contains("\n") || entrada.contains("\r")){
            throw new IllegalArgumentException("Não utilize ponto e vírgula nem quebras de linha nos campos.");
        }

        return entrada;
    }

    private Integer lerInteiro(String mensagem, boolean permiteZero) {
        String entrada = lerTexto(mensagem);

        if (entrada == null){
            return null;
        }

        int valor = Integer.parseInt(entrada);

        if (valor < 0 || (!permiteZero && valor == 0)){
            throw new IllegalArgumentException(permiteZero ? "Informe zero ou um inteiro positivo." : "Informe um inteiro positivo.");
        }

        return valor;
    }

    private Float lerDecimal(String mensagem) {
        String entrada = lerTexto(mensagem);

        if (entrada == null){
            return null;
        }

        float valor = Float.parseFloat(entrada.replace(',', '.'));

        if (!Float.isFinite(valor) || valor < 0) {
            throw new IllegalArgumentException("Informe um número decimal finito e não negativo.");
        }
        
        return valor;
    }

    private void informar(String mensagem) {
        // REQUISITO 12 - Entrada de dados por caixas de diálogo: resultados são apresentados sem construir janelas manualmente.
        JOptionPane.showMessageDialog(null, mensagem, biblioteca.getNome(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void erro(String mensagem) {
        // REQUISITO 12 - Entrada de dados por caixas de diálogo: os erros recebem uma caixa de mensagem própria.
        JOptionPane.showMessageDialog(null, mensagem, "Atenção", JOptionPane.ERROR_MESSAGE);
    }
}
