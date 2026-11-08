// REQUISITO 9 - Pacotes: modelo implementa os dados e as regras dos livros físicos.
package modelo;

// REQUISITO 10 - Classe abstrata: Livro fornece uma implementação concreta da multa exigida por Item.
// REQUISITO 8 - Interface: livros oferecem a representação persistível pelo contrato Exportavel.
public class Livro extends Item implements Exportavel {
    // REQUISITO 1.1 - Encapsulamento: os dados bibliográficos são privados e expostos por métodos.
    // REQUISITO 1.2 - Atributos String/int/float: ISBN, páginas e reposição descrevem cada exemplar individual.
    // REQUISITO 6.1 - Atributo final em superclasse: o ISBN mantém a identidade editorial nas subclasses.
    private final String isbn;
    private int numeroPaginas;
    private float valorReposicao;
    // REQUISITO 1.3 - Enumeração: a classificação do livro fica restrita aos gêneros cadastrados.
    private Genero genero;
    private static final float MULTA_POR_DIA = 1.50f;

    // REQUISITO 3 - Construtor: cria um livro completo para cadastro, carga ou especialização digital.
    public Livro(int codigo, String titulo, int ano, String isbn, int numeroPaginas, float valorReposicao, Genero genero) {
        // REQUISITO 3 - Construtor: Item inicializa o código, o título, o ano e a disponibilidade.
        super(codigo, titulo, ano);
        // REQUISITO 6.1 - Atributo final em superclasse: o ISBN é definido uma única vez na criação.
        this.isbn = isbn;
        this.numeroPaginas = numeroPaginas;
        this.valorReposicao = valorReposicao;
        this.genero = genero;
    }

    public String getIsbn(){
        return isbn;
    }
    
    public int getNumeroPaginas(){
        return numeroPaginas;
    }

    public void setNumeroPaginas(int numeroPaginas){
        this.numeroPaginas = numeroPaginas;
    }

    public float getValorReposicao(){
        return valorReposicao;
    }

    public void setValorReposicao(float valorReposicao){
        this.valorReposicao = valorReposicao;
    }

    public Genero getGenero(){
        return genero;
    }

    public void setGenero(Genero genero){
        this.genero = genero;
    }

    // REQUISITO 10 - Classe abstrata: concretiza a multa física sem ultrapassar o custo de reposição.
    @Override
    public float calcularMulta(int dias) {
        return Math.min(Math.max(dias, 0) * MULTA_POR_DIA, valorReposicao);
    }

    // REQUISITO 6.2 - Acoplamento dinâmico: a ficha comum é herdada integralmente por LivroDigital.
    public String montarFicha() {
        return "Código: " + getCodigo() + "\nTítulo: " + getTitulo()
                + "\nAno: " + getAnoPublicacao() + "\nISBN: " + isbn
                + "\nPáginas: " + numeroPaginas
                + "\nValor de reposição: "
                + String.format(java.util.Locale.forLanguageTag("pt-BR"), "R$ %.2f", valorReposicao)
                + "\nGênero: " + genero + "\nStatus: " + getStatus();
    }

    // REQUISITO 8 - Interface: a implementação exporta todos os valores necessários à reconstrução.
    // REQUISITO 13 - Arquivo: o status e o gênero são persistidos junto dos dados do exemplar.
    @Override
    public String paraLinhaArquivo() {
        return "LIVRO;" + getCodigo() + ";" + getTitulo() + ";" + getAnoPublicacao()
                + ";" + isbn + ";" + numeroPaginas + ";" + valorReposicao + ";" + genero + ";" + getStatus();
    }

    @Override
    public String toString() {
        return "Livro #" + getCodigo() + " | " + getTitulo() + " | " + getAnoPublicacao() + " | " + genero + " | " + getStatus();
    }
}
