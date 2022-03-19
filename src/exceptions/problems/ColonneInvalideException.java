package exceptions.problems;

public class ColonneInvalideException extends Exception {
    public ColonneInvalideException(final int i, final int upBound) {
        super("L'indice de la colonne " + i + " n'est pas inclus dans l'intervalle [0, " + upBound + "]");
    }
}
