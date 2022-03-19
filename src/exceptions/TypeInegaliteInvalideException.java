package exceptions;

import model.MLOProblem;

public class TypeInegaliteInvalideException extends Exception {
    public TypeInegaliteInvalideException(final int eqTy) {
        super("Type d'inégalité '" + eqTy + "' invalide: doit être un parmi [GE=" + MLOProblem.GE + ", EQ=" + MLOProblem.EQ + ", LE=" + MLOProblem.LE + "]");
    }
}
