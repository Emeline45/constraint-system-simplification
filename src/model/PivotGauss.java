package model;

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
    public void echange(int Li, int Lj){

        //récupération de la longueur de la ligne Li
        int n = system.getMatrix()[0].length;

        double[][] tab = system.getMatrix();

        double[] tabi = new double[n];
        double[] tabj = new double[n];

        for(int k = 0; k < n; k++){
            tabi[k] = tab[Lj][k];
            tabj[k] = tab[Li][k];
        }

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
