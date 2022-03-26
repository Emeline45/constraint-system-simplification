package exceptions.problems;

public class NonResoluException extends Exception {
    public NonResoluException() {
        super("Le problème n'a pas encore été résolu avec la méthode `solve()`");
    }
}
