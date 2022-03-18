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
    private Matrix2<Double> matrix;
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
        this.matrix = new Matrix2<>(problem.getNbConstraints() + 1, problem.getNbVars() + 1);
        this.ineqTypes = new int[problem.getNbConstraints() + 1];
        this.varTypes = new MLOProblem.VarType[problem.getNbVars()];

        for (int i = 0; i < problem.getNbConstraints() + 1; ++i) {
            final double[] constraint = problem.getConstraint(i);
            for (int j = 1; j < constraint.length; ++j) {
                this.matrix.set(i, j - 1, constraint[j]);
            }
            this.matrix.set(i, problem.getNbVars(), problem.getConstraintRHS(i));
        }
        this.matrix.set(0, problem.getNbVars(), sol);

        this.ineqTypes[0] = LpSolve.EQ; // la première équation est l'objectif 
        for (int i = 1; i < problem.getNbConstraints() + 1; ++i) {
            this.ineqTypes[i] = problem.getConstraintType(i - 1);
        }

        for (int i = 0; i < problem.getNbVars(); ++i) {
            this.varTypes[i] = problem.getVarType(i);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Double[] row : this.matrix) {
            for (Double v : row) {
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
    public Matrix2<Double> getMatrix() {
        return matrix;
    }

    public void setMatrixRow(double[] values, int i, boolean signe) {
        for (int j = 0; j < this.matrix.row(0).length; ++j) {
            this.matrix.set(i, j, values[j]);
        }

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
