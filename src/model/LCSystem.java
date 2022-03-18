package model;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import java.util.Arrays;

public class LCSystem {
    /**
     * La matrice représentant le système sans les symboles d'inégalité.
     *
     * @implNote Les coefficients de <code>b</code> sont dans la dernière colonne de la matrice.
     */
    private double[][] matrix;
    /**
     * Les symboles d'inégalité omis de la matrice.
     *
     * Soit {@link MLOProblem#GE}, {@link MLOProblem#EQ} ou {@link MLOProblem#LE}.
     */
    private int[] ineqTypes;
    /**
     * Les types de chaque variable.
     */
    private MLOProblem.VarType[] varTypes;

    /**
     * Initialise un nouveau système de contraintes à partir d'un problème d'optimisation linéaire déjà résolu.
     *
     * @param problem le problème résolu
     * @param sol la solution du problème
     */
    public LCSystem(final MLOProblem problem, final double sol) throws LpSolveException {
        this.matrix = new double[problem.getNbConstraints() + 1][problem.getNbVars() + 1];
        this.ineqTypes = new int[problem.getNbConstraints() + 1];
        this.varTypes = new MLOProblem.VarType[problem.getNbVars()];

        for (int i = 0; i < problem.getNbConstraints() + 1; ++i) {
            final double[] constraint = problem.getConstraint(i);
            System.arraycopy(constraint, 1, this.matrix[i], 0, constraint.length - 1);
            this.matrix[i][problem.getNbVars()] = problem.getConstraintRHS(i);
        }
        this.matrix[0][problem.getNbVars()] = sol;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (double[] row : this.matrix) {
            for (double v : row) {
                builder.append(v).append(" ");
            }
            builder.append("\n");
        }

        return builder.toString();
    }

    /**
     * La matrice représentant le système sans les symboles d'inégalité.
     *
     * @return la matrice ainsi que les coefficients <code>b</code>
     */
    public double[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(double[] values, int i, boolean signe) {
        System.arraycopy(values, 0, this.matrix[i], 0, this.matrix[0].length);

        //vérifie si changement de signe ou non
        if(signe) {
            if(ineqTypes[i] == LpSolve.GE)
                this.ineqTypes[i] = LpSolve.LE;
            else if(ineqTypes[i] == LpSolve.LE)
                this.ineqTypes[i] = LpSolve.GE;
        }
    }

    public void setIneqTypes(int i, int value) {
        this.ineqTypes[i] = value;
    }

    /**
     * Les symboles d'inégalité omis de la matrice.
     *
     * @return soit {@link MLOProblem#GE}, {@link MLOProblem#EQ} ou {@link MLOProblem#LE}.
     */
    public int[] getIneqTypes() {
        return ineqTypes;
    }

}
