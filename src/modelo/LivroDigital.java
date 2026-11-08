// REQUISITO 9 - Pacotes: modelo especializa livros para materiais digitais.
package modelo;

// REQUISITO 7.3 - Polimorfismo de subtipagem: LivroDigital substitui Livro com sua própria regra de multa.
// REQUISITO 6.2 - Acoplamento dinâmico: a filha herda a ficha pública da classe concreta Livro.
// REQUISITO 8 - Interface: o contrato Exportavel herdado é especializado para preservar os dados digitais.
public class LivroDigital extends Livro {
    // REQUISITO 1.1 - Encapsulamento: tamanho e formato só são acessados por métodos públicos.
    private float tamanhoMb;
    private String formato;

    // REQUISITO 3 - Construtor: adiciona os dados digitais ao cadastro bibliográfico completo.
    public LivroDigital(int codigo, String titulo, int ano, String isbn, int numeroPaginas,
                        float valorReposicao, Genero genero, float tamanhoMb, String formato) {
        // REQUISITO 3 - Construtor: Livro inicializa os dados comuns e o ISBN imutável herdado.
        super(codigo, titulo, ano, isbn, numeroPaginas, valorReposicao, genero);
        this.tamanhoMb = tamanhoMb;
        this.formato = formato;
    }

    public float getTamanhoMb(){
        return tamanhoMb;
    }

    public void setTamanhoMb(float tamanhoMb){
        this.tamanhoMb = tamanhoMb;
    }

    public String getFormato(){
        return formato;
    }

    public void setFormato(String formato){
        this.formato = formato;
    }

    // REQUISITO 7.3 - Polimorfismo de subtipagem: o exemplar digital não exige devolução física e não cobra multa.
    @Override
    public float calcularMulta(int dias) { return 0f; }

    // REQUISITO 8 - Interface: a versão digital acrescenta os campos específicos ao formato persistido.
    // REQUISITO 13 - Arquivo: o identificador LIVRODIGITAL permite reconstruir a subclasse correta.
    @Override
    public String paraLinhaArquivo() {
        return "LIVRODIGITAL;" + getCodigo() + ";" + getTitulo() + ";" + getAnoPublicacao()
                + ";" + getIsbn() + ";" + getNumeroPaginas() + ";" + getValorReposicao()
                + ";" + getGenero() + ";" + getStatus() + ";" + tamanhoMb + ";" + formato;
    }

    @Override
    public String toString() {
        return "Livro digital #" + getCodigo() + " | " + getTitulo() + " | " + getAnoPublicacao()
                + " | " + formato + " | " + tamanhoMb + " MB | " + getStatus();
    }
}
