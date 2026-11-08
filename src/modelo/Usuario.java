// REQUISITO 9 - Pacotes: modelo define o usuário e sua carteirinha sem acessar sistema.
package modelo;

import java.time.LocalDate;

// REQUISITO 10 - Classe abstrata: Usuario concretiza o limite exigido por Pessoa.
// REQUISITO 8 - Interface: os dois contratos permitem exportar o usuário e validar seu acesso.
public class Usuario extends Pessoa implements Exportavel, Validavel {
    // REQUISITO 1.1 - Encapsulamento: a carteirinha e o contador só mudam pelo comportamento do usuário.
    // REQUISITO 4 - Associação por Composição: a carteirinha é parte do usuário, sem substituição externa.
    private Carteirinha carteirinha;
    private int emprestimosAtivos;

    // REQUISITO 3 - Construtor: novos cadastros recebem um ano de validade automaticamente.
    // REQUISITO 7.1 - Polimorfismo de sobrecarga: a versão sem data atende ao cadastro comum.
    public Usuario(String nome, String cpf) {
        // REQUISITO 3 - Construtor: a superclasse inicializa os dados pessoais compartilhados.
        super(nome, cpf);
        // REQUISITO 4 - Associação por Composição: o próprio usuário cria e possui sua carteirinha.
        // REQUISITO 3 - Construtor: a carteirinha nasce com a identidade do usuário e validade anual.
        this.carteirinha = new Carteirinha("CART-" + cpf, LocalDate.now().plusYears(1));
        this.emprestimosAtivos = 0;
    }

    // REQUISITO 3 - Construtor: a restauração recupera a validade original, inclusive se vencida.
    // REQUISITO 7.1 - Polimorfismo de sobrecarga: a versão com data preserva cadastros persistidos.
    public Usuario(String nome, String cpf, LocalDate validadeCarteirinha) {
        // REQUISITO 3 - Construtor: os dados pessoais restaurados são inicializados em Pessoa.
        super(nome, cpf);
        // REQUISITO 4 - Associação por Composição: mesmo na carga, a carteirinha nasce dentro do usuário.
        // REQUISITO 3 - Construtor: a data recebida evita renovar indevidamente uma carteirinha vencida.
        this.carteirinha = new Carteirinha("CART-" + cpf, validadeCarteirinha);
        this.emprestimosAtivos = 0;
    }

    public Carteirinha getCarteirinha(){
        return carteirinha;
    }
    public int getEmprestimosAtivos(){
        return emprestimosAtivos;
    }

    // REQUISITO 10 - Classe abstrata: o papel de usuário permite até três empréstimos ativos.
    @Override
    public int calcularLimiteEmprestimos(){
        return 3;
    }

    // REQUISITO 8 - Interface: a validade do usuário é determinada pela carteirinha que ele possui.
    @Override
    public boolean estaValido(){
        return carteirinha.estaValido();
    }

    public void incrementarEmprestimos(){
        emprestimosAtivos++;
    }
    public void decrementarEmprestimos(){
        if (emprestimosAtivos > 0) {
            emprestimosAtivos--;
        }
    }

    // REQUISITO 8 - Interface: a exportação inclui a validade necessária para reconstruir a composição.
    // REQUISITO 13 - Arquivo: datas ISO preservam o valor independentemente da formatação da interface.
    @Override
    public String paraLinhaArquivo() {
        return "USUARIO;" + getNome() + ";" + getCpf() + ";" + carteirinha.getValidade();
    }

    @Override
    public String toString() {
        return getNome() + " | CPF: " + getCpf() + " | Carteirinha: " + carteirinha.getNumero()
                + " | Validade: " + carteirinha.getValidade() + " | Empréstimos ativos: " + emprestimosAtivos;
    }
}
