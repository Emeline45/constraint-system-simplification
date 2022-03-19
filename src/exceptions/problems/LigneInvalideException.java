package exceptions.problems;

public class LigneInvalideException extends Exception {
    public LigneInvalideException(final int i, final int upBound) {
        super("La ligne " + i + " doit être incluse dans l'intervalle [0, " + upBound + "]");
    }
}
