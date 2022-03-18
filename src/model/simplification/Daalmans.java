package model.simplification;

import config.Config;
import lpsolve.LpSolveException;
import model.LCSystem;
import model.MLOProblem;
import model.Matrix2;

import java.util.Arrays;

import static model.MLOProblem.EQ;

public class Daalmans {
    private final static double DELTA = 0.00000001;

    private final LCSystem system;

    public Daalmans(final LCSystem originalSystem) {
        this.system = originalSystem;
    }

    public LCSystem getSystem() {
        return this.system;
    }

    public void run() {
        this.removeFixedVariables();
        this.deleteConstantConstraints();
        this.removeRedundantVariables();
    }

    private double solve(final boolean isMax, final double[] objective) throws LpSolveException {
        final double[] obj = new double[objective.length + 1];
        obj[0] = 0.;
        System.arraycopy(objective, 0, obj, 1, objective.length);

        if (Config.VERBOSE) System.err.println("Problème :");

        try (MLOProblem pb = new MLOProblem(this.system.getMatrix().columnCount() - 1)
                .withObjective(obj)) {
            if (Config.VERBOSE) System.err.println("- Objectif : " + Arrays.toString(objective));

            if (isMax) pb.max();
            else pb.min();

            if (Config.VERBOSE) System.err.println("- Max : " + isMax);

            final Matrix2 matrix = this.system.getMatrix();

            if (Config.VERBOSE) System.err.println("- Contraintes :");
            for (int i = 0; i < matrix.rowCount(); ++i) {
                final Double[] row = matrix.row(i);

                // tout ceci est nécessaire car :
                // - `row` est de type `Double[]`, non trivialement transformable en `double[]`
                // - `row_` contient également les coeffcients de droite, qu'il faut extraire avant de donner à lp_solve
                final double[] row_ = new double[row.length];
                for (int k = 0; k < row.length; ++k) {
                    row_[k] = row[k];
                }
                final double[] row__ = new double[row_.length];
                row__[0] = 0.;
                System.arraycopy(row_, 0, row__, 1, row_.length - 1);

                if (Config.VERBOSE) System.err.println("  - [" + i + "]: " + Arrays.toString(row__) + " " + this.system.getIneqTypes()[i] + " " + row_[row_.length - 1]);

                pb.withConstraint(row__, this.system.getIneqTypes()[i], row_[row_.length - 1]);
            }

            pb.withVarTypes(this.system.getVarTypes());

            final double sol = pb.solve();
            if (Config.VERBOSE) System.err.println("- Solution : " + sol);

            return sol;
        }
    }

    private void removeFixedVariables() {
        final Matrix2  matrix = this.system.getMatrix();
        final int nbVars = matrix.columnCount() - 1;

        for (int n = 0; n < nbVars; ++n) {
            final double[] localObjective = new double[nbVars];
            localObjective[n] = 1.;

            double solMin, solMax;
            try {
                solMin = this.solve(false, localObjective);
                solMax = this.solve(true, localObjective);
            } catch (LpSolveException e) {
                e.printStackTrace();
                continue;
            }

            if (Math.abs(solMax - solMin) < DELTA) {
                // variable redondante
                for (int i = 0; i < matrix.rowCount(); ++i) {
                    final Double[] row = matrix.row(i);
                    final Double coeff =  row[n];

                    row[n] = 0.;
                    row[row.length - 1] -= coeff * solMin;
                }

                final Double[] newConstraint = new Double[matrix.columnCount()];
                newConstraint[n] = 1.;
                newConstraint[matrix.columnCount() - 1] = solMax;
                for (int i = 0; i < newConstraint.length - 1; ++i) {
                    if (i != n) newConstraint[i] = 0.;
                }

                matrix.appendRow(newConstraint);
                system.appendIneqType(EQ);
            }
        }
    }

    private void deleteConstantConstraints() {
        // TODO: enlever toutes les contraintes n'ayant que des 0.0 à gauche
    }

    private void removeRedundantVariables() {
        final int nbVars = system.getMatrix().columnCount() - 1;
        final double[] nullObjective = new double[nbVars];
    }
}
