// REQUISITO 9 - Pacotes: modelo reúne o papel de responsável pela biblioteca.
package modelo;

// REQUISITO 10 - Classe abstrata: Bibliotecario concretiza a política de limite de Pessoa.
public class Bibliotecario extends Pessoa {
    // REQUISITO 1.1 - Encapsulamento: matrícula e turno são acessados por métodos controlados.
    private String matricula;
    private String turno;

    // REQUISITO 3 - Construtor: cria o responsável antes de sua associação com a biblioteca.
    public Bibliotecario(String nome, String cpf, String matricula, String turno) {
        // REQUISITO 3 - Construtor: reutiliza a inicialização dos dados pessoais de Pessoa.
        super(nome, cpf);
        this.matricula = matricula;
        this.turno = turno;
    }

    public String getMatricula(){
        return matricula;
    }

    public void setMatricula(String matricula){
        this.matricula = matricula;
    }

    public String getTurno(){
        return turno;
    }

    public void setTurno(String turno){
        this.turno = turno;
    }

    // REQUISITO 10 - Classe abstrata: o papel de bibliotecário possui limite próprio de dez empréstimos.
    @Override
    public int calcularLimiteEmprestimos(){
        return 10;
    }

    @Override
    public String toString(){
        return getNome() + " | Matrícula: " + matricula + " | Turno: " + turno;
    }
}
