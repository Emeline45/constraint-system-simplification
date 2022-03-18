package model.simplification;

import exceptions.LigneIdentiqueException;
import exceptions.LignePresenteException;
import model.LCSystem;
import model.Matrix2;

public class PivotGauss {
    private final LCSystem system;

    public PivotGauss(LCSystem sys){
        this.system = sys;
    }

    /**
     * Échange de la ligne Li avec la ligne Lj
     *
     * @param Li la ligne à remplacer par Lj
     * @param Lj la ligne qui remplace Li
     * @throws LignePresenteException l'indice ne fait pas partie de la matrice
     * @throws LigneIdentiqueException les indices sont les mêmes
     */
    public void echange(int Li, int Lj) throws LignePresenteException, LigneIdentiqueException {

        //récupération de la longueur d'une ligne
        int n = system.getMatrix().columnCount();

        //récupération du nombre de ligne
        int N = system.getMatrix().rowCount();

        //Vérification que les lignes font parties de la matrice
        if(Li >= N || Li < 0)
            throw new LignePresenteException(Li);
        if(Lj >= N || Lj < 0)
            throw new LignePresenteException(Lj);
        if(Li == Lj)
            throw new LigneIdentiqueException();

        Matrix2<Double> tab = system.getMatrix();

        double[] tabi = new double[n];
        double[] tabj = new double[n];

        for (int k = 0; k < n; k++) {
            tabi[k] = tab.get(Lj, k);
            tabj[k] = tab.get(Li, k);
        }

        // Apllique les modifications sur la matrice
        system.setMatrixRow(tabi, Li, false);
        system.setIneqTypes(Li, system.getIneqTypes()[Lj]);
        system.setMatrixRow(tabj, Lj, false);
        system.setIneqTypes(Lj, system.getIneqTypes()[Li]);
    }

    /**
     * Soustraction d’une ligne Lk par le pivot Li
     *
     * @param Lk l’indice de la ligne à modifier
     * @param Li l’indice de la ligne du pivot
     * @throws LignePresenteException l'indice ne fait pas partie de la matrice
     * @throws LigneIdentiqueException les indices sont les mêmes
     */
    public void soustraction(int Lk, int Li, double lambda) throws LignePresenteException, LigneIdentiqueException {
        //récupération de la longueur d'une ligne
        int n = system.getMatrix().columnCount();

        //récupération du nombre de ligne
        int N = system.getMatrix().rowCount();

        //Vérification que les lignes font parties de la matrice
        if(Li >= N || Li < 0)
            throw new LignePresenteException(Li);
        if(Lk >= N || Lk < 0)
            throw new LignePresenteException(Lk);
        if(Li == Lk)
            throw new LigneIdentiqueException();

        //Récupération de la matrice actuelle
        Matrix2<Double> tab = system.getMatrix();

        double[] tabi = new double[n];
        double[] tabk = new double[n];
        //Multiplication si necessaire
        for (int k = 0; k < n; k++){
            tabi[k] = lambda * tab.get(Li, k);
        }
        for (int k = 0; k < n; k++){
            tabk[k] = tab.get(Lk, k) - tabi[k];
        }

        system.setMatrixRow(tabk, Lk, false);

    }

    public void multiplication(int Li, double lambda) throws LignePresenteException,LigneIdentiqueException{
        //Taille de la ligne Li
        int n = system.getMatrix().columnCount();

        //récupération du nombre de ligne
        int N = system.getMatrix().rowCount();

        if(Li >= N)
            throw new LignePresenteException(Li);

        Matrix2<Double> tab = system.getMatrix();

        double[] tabi = new double[n];

        for (int k = 0; k < n; k++){
            tabi[k] = lambda * tab.get(Li, k);
        }

        system.setMatrixRow(tabi, Li, lambda < 0);
    }

    @Override
    public String toString() {
        return "PivotGauss{" +
                "\n" + system +
                '}';
    }
}
