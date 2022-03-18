package model;

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
        this.matrix = new double[problem.getNbConstraints()][problem.getNbVars() + 1];
        this.ineqTypes = new int[problem.getNbConstraints()];
        this.varTypes = new MLOProblem.VarType[problem.getNbVars()];

        for (int i = 0; i < problem.getNbConstraints(); ++i) {
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
}
