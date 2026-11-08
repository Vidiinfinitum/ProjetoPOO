// REQUISITO 9 - Pacotes: sistema apresenta o destaque composto por entidades de modelo.
package sistema;

import modelo.Genero;
import modelo.LivroDigital;

public class VitrineDigital {
    // REQUISITO 1.1 - Encapsulamento: o destaque é interno e a vitrine expõe somente sua apresentação.
    // REQUISITO 6.2 - Acoplamento dinâmico: o atributo possui o tipo concreto da filha LivroDigital.
    private LivroDigital destaque;

    // REQUISITO 3 - Construtor: a vitrine prepara um e-book fixo para apresentação no menu.
    public VitrineDigital() {
        // REQUISITO 6.2 - Acoplamento dinâmico: o atributo da filha é instanciado diretamente como filha.
        // REQUISITO 3 - Construtor: o e-book possui todos os dados necessários à ficha herdada.
        // REQUISITO 1.3 - Enumeração: o gênero tecnológico identifica o assunto do destaque.
        destaque = new LivroDigital(9000, "Tecnologia e bibliotecas digitais", 2025,
                "978-85-0000-900-0", 180, 45.00f, Genero.TECNOLOGIA, 4.5f, "PDF");
    }

    public String exibirDestaque() {
        // REQUISITO 6.2 - Acoplamento dinâmico: a referência filha chama o método existente somente no pai Livro.
        return destaque.montarFicha();
    }
}
