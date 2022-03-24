package model.simplification;

import lpsolve.LpSolve;
import model.LCSystem;
import model.Matrix2;

import java.util.HashMap;
import java.util.Map;

public class BorneSupInf {
    private final LCSystem lcSystem;

    private final Map<Integer, Double> max;
    private final Map<Integer, Double> min;


    public BorneSupInf(LCSystem lcSystem){
        this.lcSystem = lcSystem;
        this.min = new HashMap<>();
        this.max = new HashMap<>();
    }

    /**
     * Calcul la borne supérieur et inférieur des variables de la matrice
     */
    public void borneSupInf(){
        int N = lcSystem.getMatrix().rowCount(); //Parcours des contraintes
        int n = lcSystem.getMatrix().columnCount(); //Parcours des variables
        int nbVar =  n - 2;
        double valMax = Double.MAX_VALUE;
        double valMin = Double.MIN_VALUE;
        int indiceMax = -1;
        int indiceMin = -1;
        for(int i = nbVar; i < N; i++){
            if(born(i, n)){
                if(lcSystem.getIneqTypes()[i] == LpSolve.GE){
                    max.put(i,lcSystem.getMatrix().get(i,n-1));
                    valMax = lcSystem.getMatrix().get(i, n-1);
                    indiceMax = i;
                }
                else if (lcSystem.getIneqTypes()[i] == LpSolve.LE){
                    min.put(i,lcSystem.getMatrix().get(i,n-1));
                    valMin = lcSystem.getMatrix().get(i, n-1);
                    indiceMin = i;
                }
            }
        }

        if(valMax != Double.MAX_VALUE){
            for (Map.Entry<Integer, Double> entry : max.entrySet()) {
                if(valMax < entry.getValue()){
                    valMax = entry.getValue();
                    indiceMax = entry.getKey();
                }
            }
        }
        if(valMin != Double.MIN_VALUE){
            for (Map.Entry<Integer, Double> entry : min.entrySet()) {
                if(valMin < entry.getValue()){
                    valMin = entry.getValue();
                    indiceMin = entry.getKey();
                }
            }
        }

        Matrix2 m = this.lcSystem.getMatrix().clone();
        int[] ineqs = this.lcSystem.getIneqTypes().clone();

        int taille = N;
        if(N > nbVar) {
            while (taille != nbVar) {
                //lcSystem.getMatrix().removeRow(taille - 1);
                lcSystem.removeConstraint(taille - 1);
                taille = lcSystem.getMatrix().rowCount();
            }

            lcSystem.getMatrix().appendRow(m.row(indiceMax));
            lcSystem.appendIneqType(ineqs[indiceMax]);
            lcSystem.getMatrix().appendRow(m.row(indiceMin));
            lcSystem.appendIneqType(ineqs[indiceMin]);

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
            if(lcSystem.getMatrix().get(L,j) != 0)
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