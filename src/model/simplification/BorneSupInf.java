package model.simplification;

import lpsolve.LpSolve;
import model.LCSystem;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BorneSupInf {
    private LCSystem lcSystem;

    private Map<Integer, Double> max;
    private Map<Integer, Double> min;


    public BorneSupInf(LCSystem lcSystem){
        this.lcSystem = lcSystem;
        this.min = new HashMap<>();
        this.max = new HashMap<>();
    }

    private void borneSupInf(){
        int N = lcSystem.getMatrix().rowCount(); //Parcours des contraintes
        int n = lcSystem.getMatrix().columnCount(); //Parcours des variables
        int nbVar =  n - 2;
        double valMax = Double.parseDouble(null);
        double valMin = Double.parseDouble(null);
        for(int i = nbVar; i < N; i++){
            if(born(i, n)){
                if(lcSystem.getIneqTypes()[i] == LpSolve.GE){
                    max.put(i,lcSystem.getMatrix().get(i,n-1));
                    valMax = lcSystem.getMatrix().get(i, n-1);
                }
                else{
                    min.put(i,lcSystem.getMatrix().get(i,n-1));
                    valMin = lcSystem.getMatrix().get(i, n-1);
                }
            }
        }

        if(valMax != Double.parseDouble(null)){
            for (Map.Entry<Integer, Double> entry : max.entrySet()) {
                if(valMax < entry.getValue()){
                    valMax = entry.getValue();
                }
            }
        }
        if(valMin != Double.parseDouble(null)){
            for (Map.Entry<Integer, Double> entry : min.entrySet()) {
                if(valMin < entry.getValue()){
                    valMin = entry.getValue();
                }
            }
        }

    }

    private boolean born(int L, int n){
        for(int j = 0; j < n - 2; j++){
            if(lcSystem.getMatrix().get(L,j) != 0)
                return false;
        }
        return true;
    }

}
