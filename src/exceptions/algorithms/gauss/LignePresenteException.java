package exceptions.algorithms.gauss;

public class LignePresenteException extends Exception{

    public LignePresenteException(int L){
        super("La ligne " + L + " ne correspond pas à une ligne de la matrice");
    }
}
