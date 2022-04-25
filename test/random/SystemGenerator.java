package random;

import exceptions.problems.NonResoluException;
import exceptions.problems.ProblemeSansVariablesException;
import exceptions.problems.TailleLigneInvalideException;
import exceptions.problems.TypeInegaliteInvalideException;
import lpsolve.LpSolveException;
import model.MLOProblem;

import java.util.Random;

import static model.MLOProblem.*;

public class SystemGenerator {
    private static final int NBCV = 10;

    private final MLOProblem pb;
    private double solve;

    //Intervalle de la génération aléatoire
    private final double bornInf;
    private final double bornSup;

    private final Random random;

    /**
     * Génère un problème d'optimisation linéaire mixte
     * @param bornInf borne inférieure de l'intervalle de génération des coefficients
     * @param bornSup borne supérieure de l'intervalle de génération des coefficients
     * @throws ProblemeSansVariablesException nbVar est = 0 dans le constructeur du MLOProblem
     * @throws LpSolveException lpSolve a échoué
     * @throws TypeInegaliteInvalideException lorsque le type d'égalité de l'équation est différent de GE, LE et EQ
     */
    public SystemGenerator(double bornInf, double bornSup) throws ProblemeSansVariablesException, LpSolveException, TypeInegaliteInvalideException {
        this.bornInf = bornInf;
        this.bornSup = bornSup;

        random = new Random();
        this.pb = new MLOProblem(random.nextInt(NBCV));

        //Appel la génaration du nombre de contrainte plus les contraintes
        contraints();
        //Création des coef de la fonction objective
        objectiv();

        //Ajout des variables réelles, entières ou mixtes
        try {
            varType();
        } catch (TailleLigneInvalideException e) {
            e.printStackTrace();
        }
    }

    /**
     * Génère les contraintes aléatoirement
     * @throws LpSolveException lpSolve a échoué
     * @throws TypeInegaliteInvalideException lorsque le type d'égalité de l'équation est différent de GE, LE et EQ
     */
    private void contraints() throws LpSolveException, TypeInegaliteInvalideException {
        int nbV = pb.getNbVars();
        int nbC = random.nextInt(NBCV - 1) + 1;

        int[] tab = new int[]{LE, GE, EQ};

        for (int i = 0; i < nbC; i++){
            StringBuilder s = new StringBuilder();
            for (int j = 0; j < nbV; j++){
                s.append(random.nextDouble() * (bornSup - bornInf) + bornInf).append(" ");
            }
            int randomm = random.nextInt(tab.length);
            double sol = random.nextDouble() * (bornSup - bornInf) + bornInf;
            pb.withConstraint(s.toString(), tab[randomm], String.valueOf(sol));
        }
    }

    /**
     * Génère la fonction objective aléatoirement
     * @throws LpSolveException lpSolve a échoué
     */
    private void objectiv() throws LpSolveException {
        int nbV = pb.getNbVars();
        StringBuilder s = new StringBuilder();
        for (int j = 0; j < nbV; j++){
            s.append(random.nextDouble() * (bornSup - bornInf) + bornInf).append(" ");
        }
        pb.withObjective(s.toString());
    }

    private void varType() throws TailleLigneInvalideException, LpSolveException {
        VarType[] tab = new VarType[pb.getNbVars()];
        for (int i = 0; i < pb.getNbVars(); i++){
            int randomm = random.nextInt(2);
            if(randomm == 0)
                tab[i] = VarType.REAL;
            else
                tab[i] = VarType.INT;
        }
        //System.out.println(tab[0]);
        pb.withVarTypes(tab);
    }

    /**
     * Vérifie s'il existe une solution optimal au problème
     * @return true s'il existe une solution
     * @throws NonResoluException le problème n'a pas encore été résolu avec la méthode `solve()`
     * @throws LpSolveException lpSolve a échoué
     */
    public boolean solveExist() throws NonResoluException, LpSolveException {
        this.solve = pb.solve();
        return pb.isOptimal();
    }

    /**
     * Retourne le problème d'optimisation linéaire mixte
     * @return le problème d'optimisation linéaire mixte
     */
    public MLOProblem getPb() {
        return pb;
    }

    /**
     * Retourne la solution optimale du problème
     * @return la solution optimale du problème
     */
    public double getSolve() {
        return solve;
    }
}
