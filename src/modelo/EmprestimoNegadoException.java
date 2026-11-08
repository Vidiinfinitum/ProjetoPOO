// REQUISITO 9 - Pacotes: modelo declara a falha de negócio consumida por sistema.
package modelo;

// REQUISITO 11 - Exceção: a exceção checada obriga o chamador a tratar recusas de empréstimo.
public class EmprestimoNegadoException extends Exception {
    // REQUISITO 1.1 - Encapsulamento: o identificador de serialização é um detalhe privado da exceção.
    private static final long serialVersionUID = 1L;

    // REQUISITO 3 - Construtor: preserva a razão específica da recusa para exibição no menu.
    public EmprestimoNegadoException(String mensagem) {
        super(mensagem);
    }
}
