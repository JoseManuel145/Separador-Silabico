import java.util.List;

public class SeparadorSilabico {

    // FASE I: Definición del Alfabeto
    private static final String VOCALES_FUERTES = "aeoáéó";
    private static final String VOCALES_DEBILES = "iuü";
    private static final String VOCALES_DEBILES_ACENTUADAS = "íú";

    private static final List<String> DIGRAFOS = List.of("ch", "ll", "rr");
    private static final List<String> GRUPOS_INSEPARABLES = List.of(
            "bl", "cl", "fl", "gl", "pl", "tl",
            "br", "cr", "fr", "gr", "pr", "tr", "dr"
    );

    static class Resultado {
        String palabra;
        String reglas;
        public Resultado(String p, String r) { this.palabra = p; this.reglas = r; }
    }

    // FASE II: Motor de Reglas (DFA)
    public static Resultado silabear(String palabra) {
        StringBuilder sb = new StringBuilder();
        StringBuilder reglas = new StringBuilder();
        String p = palabra.toLowerCase(); // Normalización
        int n = p.length();

        sb.append(palabra.charAt(0)); // Estado Inicial

        for (int i = 1; i < n; i++) {
            char cActual = p.charAt(i);
            char cAnt = p.charAt(i - 1);

            // 1. Reglas Vocálicas (Prioridad Alta)
            if (esVocal(cAnt) && esVocal(cActual)) {
                // Hiato: VF+VF o VD(t)+VF
                if ((esFuerte(cAnt) && esFuerte(cActual)) ||
                        (VOCALES_DEBILES_ACENTUADAS.indexOf(cAnt) != -1 && esFuerte(cActual)) ||
                        (esFuerte(cAnt) && VOCALES_DEBILES_ACENTUADAS.indexOf(cActual) != -1)) {
                    sb.append('-');
                    reglas.append("Hiato, ");
                } else {
                    reglas.append("Diptongo, "); // Unión
                }
            }
            // 2. Reglas Consonánticas
            else if (!esVocal(cActual)) {
                if (esVocal(cAnt)) { // V-C
                    // Lookahead: Si sigue vocal (VCV), la consonante inicia sílaba
                    if (i + 1 < n && esVocal(p.charAt(i + 1))) {
                        sb.append('-');
                        reglas.append("VCV, ");
                    }
                }
                else if (!esVocal(cAnt)) { // CC
                    String par = "" + cAnt + cActual;
                    if (!DIGRAFOS.contains(par) && !GRUPOS_INSEPARABLES.contains(par)) {
                        sb.append('-'); // Separar si no es grupo inseparable
                        reglas.append("CC, ");
                    }
                }
            }

            // Caso especial 'h' intercalada entre vocales
            if (cActual == 'h' && esVocal(cAnt) && (i+1<n && esVocal(p.charAt(i+1)))) {
                sb.append('-');
            }

            sb.append(cActual);
        }

        String rStr = reglas.toString();
        if (rStr.length() > 2) rStr = rStr.substring(0, rStr.length() - 2);

        return new Resultado(sb.toString(), rStr);
    }

    // Métodos Auxiliares
    private static boolean esVocal(char c) {
        return esFuerte(c) || esDebil(c) || VOCALES_DEBILES_ACENTUADAS.indexOf(c) != -1;
    }
    private static boolean esFuerte(char c) { return VOCALES_FUERTES.indexOf(c) != -1; }
    private static boolean esDebil(char c) { return VOCALES_DEBILES.indexOf(c) != -1; }

    // FASE III: Salida
    public static void main(String[] args) {
        String[] entradas = {
                "autonomia", "murcielago", "teatro", "ahorro",
                "computadora", "ciencia", "cancion", "ángel", "esternocleidomastoideo"
        };

        System.out.println("----------------------------------------------------------------------");
        System.out.printf("%-15s | %-20s | %-25s%n", "Palabra", "Separación", "Reglas");
        System.out.println("----------------------------------------------------------------------");

        for (String w : entradas) {
            Resultado res = silabear(w);
            System.out.printf("%-15s | %-20s | %-25s%n", w, res.palabra, res.reglas);
        }
    }
}