// REQUISITO 9 - Pacotes: modelo mantém a validade de acesso aos empréstimos.
package modelo;

import java.time.LocalDate;

// REQUISITO 8 - Interface: a carteirinha fornece a regra de validade exigida por Validavel.
public class Carteirinha implements Validavel {
    // REQUISITO 1.1 - Encapsulamento: número e validade só são consultados externamente.
    private String numero;
    private LocalDate validade;

    // REQUISITO 3 - Construtor: recebe a identidade e a validade definidas pelo usuário proprietário.
    // REQUISITO 4 - Associação por Composição: somente Usuario cria carteirinhas no fluxo do sistema.
    public Carteirinha(String numero, LocalDate validade) {
        this.numero = numero;
        this.validade = validade;
    }

    public String getNumero(){
        return numero;
    }

    public LocalDate getValidade(){
        return validade;
    }

    // REQUISITO 8 - Interface: a data de hoje ainda é válida para permitir o último dia de uso.
    @Override
    public boolean estaValido() {
        return !validade.isBefore(LocalDate.now());
    }
}
