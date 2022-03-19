package exceptions.problems;

public class TailleLigneInvalideException extends Exception {
    public TailleLigneInvalideException(final int size, final int expected) {
        super("La taille de la ligne d'une contrainte doit être " + expected + "(avec 0.0 dans la case 0) mais celle passée est de taille " + size);
    }
}
