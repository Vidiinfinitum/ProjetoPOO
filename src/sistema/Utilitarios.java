// REQUISITO 9 - Pacotes: sistema reúne os serviços utilizados pela aplicação.
package sistema;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Locale;

public class Utilitarios {
    // REQUISITO 3 - Construtor: o construtor privado impede instâncias de uma classe de serviços estáticos.
    private Utilitarios() {
    }

    // REQUISITO 2.2 - Método estático: o cálculo de atraso é compartilhado sem estado ou instância auxiliar.
    public static int calcularDiasAtraso(LocalDate prevista, LocalDate devolucao) {
        // REQUISITO 7.2 - Polimorfismo de coerção: converte explicitamente o resultado long em dias int para a multa.
        int dias = (int) ChronoUnit.DAYS.between(prevista, devolucao);
        
        return Math.max(dias, 0);
    }

    // REQUISITO 2.2 - Método estático: todas as mensagens de multa exibem a mesma moeda brasileira.
    public static String formatarMoeda(float valor) {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(valor);
    }

    // REQUISITO 7.4 - Polimorfismo paramétrico: uma rotina formata listas de itens, usuários ou empréstimos.
    // REQUISITO 2.2 - Método estático: a formatação compartilhada dispensa uma instância de utilitário.
    // REQUISITO 1.4 - ArrayList: recebe as coleções tipadas mantidas pela biblioteca.
    public static <T> String formatarLista(ArrayList<T> lista) {
        StringBuilder texto = new StringBuilder();

        for (T elemento : lista) {
            texto.append(elemento.toString()).append(System.lineSeparator());
        }

        return texto.length() == 0 ? "Nenhum registro encontrado." : texto.toString();
    }
}
