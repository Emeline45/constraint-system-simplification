package model;

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
     */
    public void echange(int Li, int Lj) throws LignePresenteException {

        //récupération de la longueur d'une ligne
        int n = system.getMatrix()[0].length;

        //récupération du nombre de ligne
        int N = system.getMatrix().length;

        //Vérification que les lignes font parties de la matrice
        if(Li >= N || Li < 0)
            throw new LignePresenteException(Li);
        if(Lj >= N || Lj < 0)
            throw new LignePresenteException(Lj);

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

    private void soustraction(){

    }

    private void multiplication(){

    }

    @Override
    public String toString() {
        return "PivotGauss{" +
                "\n" + system +
                '}';
    }
}
