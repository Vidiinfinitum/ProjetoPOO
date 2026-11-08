// REQUISITO 9 - Pacotes: modelo contém a base comum dos materiais do acervo.
package modelo;

// REQUISITO 10 - Classe abstrata: cada material concreto deve informar sua própria regra de multa.
public abstract class Item {
    // REQUISITO 1.1 - Encapsulamento: identidade e disponibilidade são protegidas por acesso público controlado.
    private int codigo;
    private String titulo;
    private int anoPublicacao;
    // REQUISITO 1.3 - Enumeração: o status representa apenas estados de disponibilidade reconhecidos.
    private StatusItem status;

    // REQUISITO 3 - Construtor: todo material nasce identificado e disponível para empréstimo.
    public Item(int codigo, String titulo, int anoPublicacao) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.anoPublicacao = anoPublicacao;
        // REQUISITO 1.3 - Enumeração: um item recém-cadastrado pode ser emprestado imediatamente.
        this.status = StatusItem.DISPONIVEL;
    }

    public int getCodigo(){
        return codigo;
    }

    public void setCodigo(int codigo){
        this.codigo = codigo;
    }

    public String getTitulo(){
        return titulo;
    }

    public void setTitulo(String titulo){
        this.titulo = titulo;
    }

    public int getAnoPublicacao(){
        return anoPublicacao;
    }

    public void setAnoPublicacao(int anoPublicacao){
        this.anoPublicacao = anoPublicacao;
    }

    public StatusItem getStatus(){
        return status;
    }

    public void setStatus(StatusItem status){
        this.status = status;
    }

    // REQUISITO 10 - Classe abstrata: a biblioteca delega a multa ao material emprestado.
    public abstract float calcularMulta(int diasAtraso);
}
