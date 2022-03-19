package exceptions.problems;

public class ProblemeSansVariablesException extends Exception {
    public ProblemeSansVariablesException() {
        super("Le nombre de variables d'un problème d'optimisation linéaire ne peut pas être négatif");
    }
}
