// REQUISITO 9 - Pacotes: modelo representa também os periódicos do acervo.
package modelo;

// REQUISITO 10 - Classe abstrata: Revista concretiza Item com multa própria de periódico.
// REQUISITO 8 - Interface: periódicos participam da mesma rotina de exportação dos livros.
public class Revista extends Item implements Exportavel {
    // REQUISITO 1.1 - Encapsulamento: edição e periodicidade são protegidas por métodos de acesso.
    private int numeroEdicao;
    private String periodicidade;

    // REQUISITO 3 - Construtor: identifica a edição específica que poderá ser emprestada.
    public Revista(int codigo, String titulo, int ano, int numeroEdicao, String periodicidade) {
        // REQUISITO 3 - Construtor: Item inicializa a identificação e a disponibilidade do periódico.
        super(codigo, titulo, ano);
        this.numeroEdicao = numeroEdicao;
        this.periodicidade = periodicidade;
    }

    public int getNumeroEdicao(){
        return numeroEdicao;
    }

    public void setNumeroEdicao(int numeroEdicao){
        this.numeroEdicao = numeroEdicao;
    }

    public String getPeriodicidade(){
        return periodicidade;
    }

    public void setPeriodicidade(String periodicidade){
        this.periodicidade = periodicidade;
    }


    // REQUISITO 10 - Classe abstrata: a multa de revista é de setenta e cinco centavos por dia.
    @Override
    public float calcularMulta(int dias){
        return Math.max(dias, 0) * 0.75f;
    }

    // REQUISITO 8 - Interface: todos os dados da edição são fornecidos ao exportador.
    // REQUISITO 13 - Arquivo: a linha preserva a disponibilidade e os dados do periódico.
    @Override
    public String paraLinhaArquivo() {
        return "REVISTA;" + getCodigo() + ";" + getTitulo() + ";" + getAnoPublicacao()
                + ";" + numeroEdicao + ";" + periodicidade + ";" + getStatus();
    }

    @Override
    public String toString() {
        return "Revista #" + getCodigo() + " | " + getTitulo() + " | " + getAnoPublicacao()
                + " | Edição: " + numeroEdicao + " | " + periodicidade + " | " + getStatus();
    }
}
