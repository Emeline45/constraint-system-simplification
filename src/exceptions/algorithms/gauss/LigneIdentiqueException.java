package exceptions.algorithms.gauss;

public class LigneIdentiqueException extends Exception{
    public LigneIdentiqueException(){
        super("Les deux lignes sont identiques, vous ne pouvez pas faire de changement dessus");
    }
}
