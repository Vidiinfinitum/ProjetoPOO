// REQUISITO 9 - Pacotes: o contrato de exportação pertence ao domínio modelo.
package modelo;

// REQUISITO 8 - Interface: permite persistir entidades diferentes pelo mesmo contrato textual.
public interface Exportavel {
    String paraLinhaArquivo();
}
