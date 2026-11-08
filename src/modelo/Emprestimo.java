// REQUISITO 9 - Pacotes: modelo representa o vínculo temporário entre usuário e material.
package modelo;

import java.time.LocalDate;

// REQUISITO 8 - Interface: os vínculos ativos também precisam ser exportados para restauração.
public class Emprestimo implements Exportavel {
    // REQUISITO 1.5 - Constante: o prazo compartilhado orienta o cálculo da previsão feito pela Biblioteca.
    public static final int PRAZO_PADRAO_DIAS = 14;

    // REQUISITO 1.1 - Encapsulamento: os participantes e as datas são apenas consultados após a criação.
    // REQUISITO 5 - Associação por Agregação: usuário e item existem antes e depois do vínculo de empréstimo.
    private Usuario usuario;
    private Item item;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;

    // REQUISITO 3 - Construtor: registra os participantes e as datas determinadas pela biblioteca.
    // REQUISITO 5 - Associação por Agregação: recebe objetos independentes sem criá-los ou possuí-los.
    public Emprestimo(Usuario usuario, Item item, LocalDate dataEmprestimo, LocalDate dataPrevistaDevolucao) {
        this.usuario = usuario;
        this.item = item;
        this.dataEmprestimo = dataEmprestimo;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
    }

    public Usuario getUsuario(){
        return usuario;
    }

    public Item getItem(){
        return item;
    }

    public LocalDate getDataEmprestimo(){
        return dataEmprestimo;
    }

    public LocalDate getDataPrevistaDevolucao(){
        return dataPrevistaDevolucao;
    }

    // REQUISITO 8 - Interface: os identificadores permitem recuperar os mesmos participantes na carga.
    // REQUISITO 13 - Arquivo: as datas originais preservam o cálculo de atraso entre execuções.
    @Override
    public String paraLinhaArquivo() {
        return "EMPRESTIMO;" + usuario.getCpf() + ";" + item.getCodigo() + ";" + dataEmprestimo + ";" + dataPrevistaDevolucao;
    }

    @Override
    public String toString() {
        return usuario.getNome() + " (" + usuario.getCpf() + ") | Item #" + item.getCodigo()
                + ": " + item.getTitulo() + " | Empréstimo: " + dataEmprestimo
                + " | Devolução prevista: " + dataPrevistaDevolucao;
    }
}
