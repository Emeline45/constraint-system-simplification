package model.simplification;

import config.Config;
import lpsolve.LpSolve;
import model.LCSystem;
import model.MLOProblem;
import model.Matrix2;

import java.util.HashMap;
import java.util.Map;

public class BorneSupInf {
    private final static double DELTA = 0.00000001;

    private final LCSystem lcSystem;

    public BorneSupInf(LCSystem lcSystem){
        this.lcSystem = lcSystem;
    }

    /**
     * Calcule la borne supérieure et inférieure des variables de la matrice
     */
    public void borneSupInf(){
        final Matrix2 matrix = lcSystem.getMatrix();

        final int N = matrix.rowCount(); //Parcours des contraintes
        final int n = matrix.columnCount(); //Parcours des variables
        final int nbVar = n - 2;

        // NOTE : ne devrait jamais arriver : uniquement si la matrice est vide !
        if (nbVar < 0)
            return;

        double valMax = Double.MIN_VALUE;
        double valMin = Double.MAX_VALUE;

        int indiceMax = -1;
        int indiceMin = -1;
        int indiceEq = -1;

        for (int i = nbVar; i < N; ++i) {
            if (!born(i, n))
                continue;

            if (Config.VERBOSE)
                System.err.println("Borne sup/inf : contrainte " + i + " unitaire");

            final int ineqty = lcSystem.getIneqTypes()[i];
            final double value = matrix.get(i, n - 1);

            if (Config.VERBOSE)
                System.err.println("Borne sup/inf : " + (ineqty == MLOProblem.GE ? "⩾" : ineqty == MLOProblem.LE ? "⩽" : "=") + " " + value);

            switch (ineqty) {
                case MLOProblem.GE: {
                    if (value > valMax) {
                        indiceMax = i;
                        valMax = value;
                    }
                    break;
                }
                case MLOProblem.LE: {
                    if (value < valMin) {
                        indiceMin = i;
                        valMin = value;
                    }
                    break;
                }
                case MLOProblem.EQ: {
                    indiceEq = i;
                    break;
                }
                default: {}
            }
        }

        final Matrix2 m = this.lcSystem.getMatrix().clone();
        final int[] ineqs = this.lcSystem.getIneqTypes().clone();

        if (Config.VERBOSE) {
            System.err.println("Borne sub/inf :\n" + this.lcSystem);
            System.err.println("Borne sup/inf : minimum=" + indiceMin + "; maximum=" + indiceMax + "; égalité=" + indiceEq);
        }

        int taille = N;
        if(taille > nbVar) {
            while (taille > nbVar) {
                //lcSystem.getMatrix().removeRow(taille - 1);
                lcSystem.removeConstraint(taille - 1);
                taille = lcSystem.getMatrix().rowCount();
            }

            if (indiceEq != -1) {
                lcSystem.getMatrix().appendRow(m.row(indiceEq));
                lcSystem.appendIneqType(MLOProblem.EQ);
            } else {
                if (indiceMax != -1) {
                    lcSystem.getMatrix().appendRow(m.row(indiceMax));
                    lcSystem.appendIneqType(ineqs[indiceMax]);
                }
                if (indiceMin != -1) {
                    lcSystem.getMatrix().appendRow(m.row(indiceMin));
                    lcSystem.appendIneqType(ineqs[indiceMin]);
                }
            }
        }

    }

    /**
     * Vérification s'il n'y a que un élément différent de 0
     * @param L La ligne à vérifier
     * @param n la longueur de la matrice
     * @return false si un des premiers éléments est différent de 0
     */
    private boolean born(int L, int n){
        for(int j = 0; j < n - 2; j++){
            if(Math.abs(lcSystem.getMatrix().get(L,j)) > DELTA)
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BorneSupInf{" +
                "\n" + lcSystem +
                '}';
    }
}
