// REQUISITO 9 - Pacotes: modelo concentra os dados das pessoas da biblioteca.
package modelo;

// REQUISITO 10 - Classe abstrata: cada papel de pessoa precisa definir seu limite de empréstimos.
public abstract class Pessoa {
    // REQUISITO 1.1 - Encapsulamento: nome e CPF ficam protegidos e acessíveis por métodos públicos.
    private String nome;
    private String cpf;

    // REQUISITO 3 - Construtor: inicializa os dados comuns das pessoas concretas.
    public Pessoa(String nome, String cpf) {
        this.nome = nome;
        this.cpf = cpf;
    }

    public String getNome(){
        return nome;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public String getCpf(){
        return cpf;
    }

    public void setCpf(String cpf){
        this.cpf = cpf;
    }

    // REQUISITO 10 - Classe abstrata: o limite depende do papel e não possui valor genérico em Pessoa.
    public abstract int calcularLimiteEmprestimos();
}
