package model;

import exceptions.LigneIdentiqueException;
import exceptions.LignePresenteException;

public class PivotGauss {
    private final LCSystem system;

    public PivotGauss(LCSystem sys){
        this.system = sys;
    }

    /**
     * Échange de la ligne Li avec la ligne Lj
     * @param Li la ligne à remplacer par Lj
     * @param Lj la ligne qui remplace Li
     * @throws LignePresenteException l'indice ne fait pas partie de la matrice
     * @throws LigneIdentiqueException les indices sont les mêmes
     */
    public void echange(int Li, int Lj) throws LignePresenteException, LigneIdentiqueException {

        //récupération de la longueur d'une ligne
        int n = system.getMatrix()[0].length;

        //récupération du nombre de ligne
        int N = system.getMatrix().length;

        //Vérification que les lignes font parties de la matrice
        if(Li >= N || Li < 0)
            throw new LignePresenteException(Li);
        if(Lj >= N || Lj < 0)
            throw new LignePresenteException(Lj);
        if(Li == Lj)
            throw new LigneIdentiqueException();

        double[][] tab = system.getMatrix();

        double[] tabi = new double[n];
        double[] tabj = new double[n];

        for (int k = 0; k < n; k++) {
            tabi[k] = tab[Lj][k];
            tabj[k] = tab[Li][k];
        }

        // Apllique les modifications sur la matrice
        system.setMatrix(tabi, Li, false);
        system.setIneqTypes(Li, system.getIneqTypes()[Lj]);
        system.setMatrix(tabj, Lj, false);
        system.setIneqTypes(Lj, system.getIneqTypes()[Li]);
    }

    /**
     * Soustraction d’une ligne Lk par le pivot Li
     * @param Lk l’indice de la ligne à modifier
     * @param Li l’indice de la ligne du pivot
     * @throws LignePresenteException l'indice ne fait pas partie de la matrice
     * @throws LigneIdentiqueException les indices sont les mêmes
     */
    public void soustraction(int Lk, int Li, double lambda) throws LignePresenteException, LigneIdentiqueException {
        //récupération de la longueur d'une ligne
        int n = system.getMatrix()[0].length;

        //récupération du nombre de ligne
        int N = system.getMatrix().length;

        //Vérification que les lignes font parties de la matrice
        if(Li >= N || Li < 0)
            throw new LignePresenteException(Li);
        if(Lk >= N || Lk < 0)
            throw new LignePresenteException(Lk);
        if(Li == Lk)
            throw new LigneIdentiqueException();

        //Récupération de la matrice actuelle
        double[][] tab = system.getMatrix();

        double[] tabi = new double[n];
        double[] tabk = new double[n];
        //Multiplication si necessaire
        for (int k = 0; k < n; k++){
            tabi[k] = lambda * tab[Li][k];
        }
        for (int k = 0; k < n; k++){
            tabk[k] = tab[Lk][k] - tabi[k];
        }

        system.setMatrix(tabk, Lk, false);

    }

    public void multiplication(int Li, double lambda) throws LignePresenteException,LigneIdentiqueException{
        //Taille de la ligne Li
        int n = system.getMatrix()[Li].length;

        //récupération du nombre de ligne
        int N = system.getMatrix().length;

        if(Li >= N)
            throw new LignePresenteException(Li);

        double[][] tab = system.getMatrix();

        double[] tabi = new double[n];

        for (int k = 0; k < n; k++){
            tabi[k] = lambda * tab[Li][k];
        }

        system.setMatrix(tabi, Li, lambda < 0);
    }

    @Override
    public String toString() {
        return "PivotGauss{" +
                "\n" + system +
                '}';
    }
}
