// REQUISITO 9 - Pacotes: o ponto de entrada pertence a sistema, sem classes no pacote padrão.
package sistema;

import java.io.IOException;
import javax.swing.JOptionPane;
import modelo.Bibliotecario;
import modelo.Genero;
import modelo.Livro;
import modelo.LivroDigital;
import modelo.Revista;
import modelo.Usuario;

public class Main {
    // REQUISITO 3 - Construtor: declarado explicitamente para a classe concreta do ponto de entrada.
    public Main() {
    }

    public static void main(String[] argumentos) {
        // REQUISITO 3 - Construtor: cria o profissional fixo independentemente da biblioteca.
        Bibliotecario responsavel = new Bibliotecario("Ana Martins", "11122233344", "BIB-001", "Diurno");

        // REQUISITO 3 - Construtor: inicializa a biblioteca que será compartilhada por todo o fluxo.
        // REQUISITO 5 - Associação por Agregação: o responsável já existe quando é recebido pela biblioteca.
        Biblioteca biblioteca = new Biblioteca("Sistema de Biblioteca", responsavel);

        // REQUISITO 3 - Construtor: define o arquivo persistente na pasta de execução.
        ArquivoBiblioteca arquivo = new ArquivoBiblioteca("dados_biblioteca.txt");

        // REQUISITO 3 - Construtor: prepara o destaque digital independente do acervo emprestável.
        VitrineDigital vitrine = new VitrineDigital();

        // REQUISITO 3 - Construtor: o menu recebe a mesma biblioteca que será carregada a seguir.
        // REQUISITO 5 - Associação por Agregação: associa serviços prontos para utilização pela interface.
        MenuPrincipal menu = new MenuPrincipal(biblioteca, arquivo, vitrine);

        if (arquivo.existe()) {
            try {
                // REQUISITO 13 - Arquivo: os dados salvos substituem a necessidade de uma nova carga demonstrativa.
                arquivo.carregar(biblioteca);
            } catch (IOException excecao) {
                // REQUISITO 11 - Exceção: falha de carga é informada e o arquivo original não é sobrescrito.
                // REQUISITO 12 - Entrada de dados por caixas de diálogo: o erro inicial também utiliza JOptionPane.
                JOptionPane.showMessageDialog(null, "Não foi possível carregar os dados: " + excecao.getMessage()
                        + "\nVerifique o arquivo antes de iniciar novamente.", "Erro de leitura", JOptionPane.ERROR_MESSAGE);

                return;
            }
        } else {
            // REQUISITO 2.1 - Método com argumentos variados (varargs): uma única chamada carrega três tipos de material.
            // REQUISITO 3 - Construtor: os três exemplares concretos nascem completos para a carga inicial.
            // REQUISITO 5 - Associação por Agregação: a biblioteca recebe os itens criados como argumentos do cadastro.
            // REQUISITO 1.3 - Enumeração: os livros iniciais recebem gêneros válidos desde sua criação.
            biblioteca.cadastrarItens(
                    new Livro(1, "Dom Casmurro", 1899, "978-85-0000-001-0", 256, 50.00f, Genero.FICCAO),
                    new LivroDigital(2, "Introdução à programação Java", 2025, "978-85-0000-002-7",
                            320, 80.00f, Genero.TECNOLOGIA, 6.5f, "PDF"),
                    new Revista(3, "Ciência em Foco", 2026, 42, "Mensal"));
                    
            // REQUISITO 3 - Construtor: o usuário demonstrativo permite testar empréstimos na primeira execução.
            // REQUISITO 4 - Associação por Composição: Usuario cria internamente sua carteirinha com validade anual.
            // REQUISITO 7.1 - Polimorfismo de sobrecarga: o cadastro inicial utiliza o construtor sem data.
            // REQUISITO 5 - Associação por Agregação: associa o usuário recém-criado ao cadastro central.
            biblioteca.cadastrarUsuario(new Usuario("Maria Souza", "12345678900"));
        }
        // REQUISITO 3 - Construtor: a instância do ponto de entrada conduz o menu após a inicialização.
        Main principal = new Main();
        // REQUISITO 12 - Entrada de dados por caixas de diálogo: o fluxo principal é conduzido pelo menu de JOptionPane.
        principal.executarMenu(menu);
    }

    private void executarMenu(MenuPrincipal menu) {
        // REQUISITO 12 - Entrada de dados por caixas de diálogo: inicia o laço de interação após a carga dos dados.
        menu.executar();
    }
}
