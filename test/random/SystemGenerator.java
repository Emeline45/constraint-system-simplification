package random;

import exceptions.problems.NonResoluException;
import exceptions.problems.ProblemeSansVariablesException;
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


    public SystemGenerator(double bornInf, double bornSup) throws ProblemeSansVariablesException, LpSolveException, TypeInegaliteInvalideException {
        this.bornInf = bornInf;
        this.bornSup = bornSup;

        random = new Random();
        this.pb = new MLOProblem(random.nextInt(NBCV));

        //Appel la génaration du nombre de contrainte plus les contraintes
        contraints();
        //Création des coef de la fonction objective
        objectiv();
    }

    private void contraints() throws LpSolveException, TypeInegaliteInvalideException {
        int nbV = pb.getNbVars();
        int nbC = random.nextInt(NBCV);

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

    private void objectiv() throws LpSolveException {
        int nbV = pb.getNbVars();
        StringBuilder s = new StringBuilder();
        for (int j = 0; j < nbV; j++){
            s.append(random.nextDouble() * (bornSup - bornInf) + bornInf).append(" ");
        }
        pb.withObjective(s.toString());
    }

    public boolean solveExist() throws NonResoluException, LpSolveException {
        this.solve = pb.solve();
        return pb.isOptimal();
    }

    public MLOProblem getPb() {
        return pb;
    }

    public double getSolve() {
        return solve;
    }
}
