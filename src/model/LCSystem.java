package model;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import java.util.Arrays;

import static model.MLOProblem.GE;
import static model.MLOProblem.LE;

public class LCSystem implements Cloneable {
    /**
     * La matrice représentant le système sans les symboles d'inégalité.
     *
     * @implNote Les coefficients de <code>b</code> sont dans la dernière colonne de la matrice.
     */
    private Matrix2 matrix;
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
        this.matrix = new Matrix2(problem.getNbConstraints() + 1, problem.getNbVars() + 1);
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

    /**
     * La matrice représentant le système sans les symboles d'inégalité.
     *
     * @return la matrice ainsi que les coefficients <code>b</code>
     */
    public Matrix2 getMatrix() {
        return matrix;
    }

    public void setMatrixRow(double[] values, int i, boolean signe) {
        for (int j = 0; j < this.matrix.columnCount(); ++j) {
            this.matrix.set(i, j, values[j]);
        }

        //vérifie si changement de signe ou non
        if(signe) {
            if(ineqTypes[i] == GE)
                this.ineqTypes[i] = LE;
            else if(ineqTypes[i] == LE)
                this.ineqTypes[i] = GE;
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
        return this.ineqTypes;
    }

    /**
     * Retourne les types des variables présentes dans le système de contraintes.
     *
     * @return les types des variables
     */
    public MLOProblem.VarType[] getVarTypes() {
        return this.varTypes;
    }

    public void appendIneqType(int eq) {
        final int[] newIneqtypes = new int[this.ineqTypes.length + 1];

        System.arraycopy(this.ineqTypes, 0, newIneqtypes, 0, this.ineqTypes.length);
        newIneqtypes[this.ineqTypes.length] = eq;

        this.ineqTypes = newIneqtypes;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < this.matrix.rowCount(); ++i) {
            if (i == 0) {
                builder.append("⎧  ");
            } else if (i == this.matrix.rowCount() - 1) {
                builder.append("⎩  ");
            } else if (i == Math.floorDiv(this.matrix.rowCount(), 2)) {
                builder.append("⎨  ");
            } else {
                builder.append("⎪  ");
            }

            for (int j = 0; j < this.matrix.columnCount() - 1; ++j) {
                builder.append(String.format("% 15.7f ", this.matrix.get(i, j)));
            }

            final int ineqType = this.ineqTypes[i];
            builder
                    .append(ineqType == LE ? "⩽ " : ineqType == GE ? "⩾ " : "= ")
                    .append(String.format("% 15.7f", this.matrix.get(i, this.matrix.columnCount() - 1)))
                    .append("\n");
        }

        return builder.toString();
    }

    @Override
    public LCSystem clone() {
        try {
            LCSystem clone = (LCSystem) super.clone();
            clone.matrix = this.matrix.clone();
            clone.ineqTypes = this.ineqTypes.clone();
            clone.varTypes = this.varTypes.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
