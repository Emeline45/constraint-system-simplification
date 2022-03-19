package model.simplification;

import config.Config;
import exceptions.problems.ProblemeSansVariablesException;
import exceptions.problems.TailleLigneInvalideException;
import exceptions.problems.TypeInegaliteInvalideException;
import lpsolve.LpSolveException;
import model.LCSystem;
import model.MLOProblem;
import model.Matrix2;
import utils.BooleanHolder;

import java.util.Arrays;

import static model.MLOProblem.*;

public class Daalmans {
    private final static double DELTA = 0.00000001;
    private final static double EPSILON = 0.00001;

    private final LCSystem system;

    public Daalmans(final LCSystem originalSystem) {
        this.system = originalSystem;
    }

    public LCSystem getSystem() {
        return this.system;
    }

    public void run() {
        try {
            this.removeFixedVariables();
            this.removeRedundantVariables();
        } catch (TypeInegaliteInvalideException | TailleLigneInvalideException | ProblemeSansVariablesException e) {
            e.printStackTrace();
        }
    }

    private double solve(final boolean isMax, final double[] objective, final BooleanHolder isInfinite) throws LpSolveException, TypeInegaliteInvalideException, TailleLigneInvalideException, ProblemeSansVariablesException {
        return solve(isMax, objective, this.system, isInfinite);
    }

    private double solve(final boolean isMax, final double[] objective, final LCSystem system, final BooleanHolder isInfinite) throws LpSolveException, TypeInegaliteInvalideException, TailleLigneInvalideException, ProblemeSansVariablesException {
        final double[] obj = new double[objective.length + 1];
        obj[0] = 0.;
        System.arraycopy(objective, 0, obj, 1, objective.length);

        if (Config.VERBOSE) System.err.println("  Problème :");

        try (MLOProblem pb = new MLOProblem(system.getMatrix().columnCount() - 1)
                .withObjective(obj)) {
            if (Config.VERBOSE) System.err.println("  - Objectif : " + Arrays.toString(objective));

            if (isMax) pb.max();
            else pb.min();

            if (Config.VERBOSE) System.err.println("  - Max : " + isMax);

            final Matrix2 matrix = system.getMatrix();

            if (Config.VERBOSE) System.err.println("  - Contraintes :");
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

                if (Config.VERBOSE)
                    System.err.println("    - [" + i + "]: " + Arrays.toString(row__) + " - " + system.getIneqTypes()[i] + " - " + row_[row_.length - 1]);

                pb.withConstraint(row__, system.getIneqTypes()[i], row_[row_.length - 1]);
            }

            pb.withVarTypes(system.getVarTypes());

            final double sol = pb.solve();
            if (Config.VERBOSE) System.err.println("  - Solution : " + sol);

            if (isInfinite != null) isInfinite.set(pb.isInfinite(sol));

            return sol;
        }
    }

    private void removeFixedVariables() throws TypeInegaliteInvalideException, TailleLigneInvalideException, ProblemeSansVariablesException {
        final Matrix2 matrix = this.system.getMatrix();
        final int nbVars = matrix.columnCount() - 1;

        for (int n = 0; n < nbVars; ++n) {
            if (Config.VERBOSE) System.err.println("Variable " + n + " fixe ?");

            final double[] localObjective = new double[nbVars];
            localObjective[n] = 1.;

            double solMin, solMax;
            try {
                solMin = this.solve(false, localObjective, null);
                solMax = this.solve(true, localObjective, null);
            } catch (LpSolveException e) {
                e.printStackTrace();
                continue;
            }

            if (Math.abs(solMax - solMin) < DELTA) {
                // variable redondante
                for (int i = 0; i < matrix.rowCount(); ++i) {
                    final Double[] row = matrix.row(i);
                    final Double coeff = row[n];

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

    private void removeRedundantVariables() throws TypeInegaliteInvalideException, TailleLigneInvalideException, ProblemeSansVariablesException {
        final Matrix2 matrix = this.system.getMatrix();

        // détail d'implantation :
        //
        // on itère en partant de la fin, comme ça si on supprime des contraintes, les indices des contraintes
        // suivantes ne sont pas changés dynamiquement
        for (int i = matrix.rowCount() - 1; i >= 0; --i) {
            if (Config.VERBOSE) System.err.println("Contrainte " + i + " redondante ?");

            boolean result = true;

            final LCSystem tmp = this.system.clone();
            final int ineqType = tmp.getIneqTypes()[i];
            final Double[] row = tmp.getMatrix().row(i);

            tmp.removeConstraint(i);

            final Matrix2 matrix2 = tmp.getMatrix();

            switch (ineqType) {
                case EQ: {
                    final Double tmpResult = row[row.length - 1];

                    row[row.length - 1] = tmpResult + EPSILON;
                    matrix2.appendRow(row);
                    tmp.appendIneqType(GE);

                    result = this.isFeasible(tmp);

                    row[row.length - 1] = tmpResult - EPSILON;
                    tmp.setIneqTypes(tmp.getIneqTypes().length - 1, LE);

                    result = result && this.isFeasible(tmp);
                    break;
                }
                case LE: {
                    row[row.length - 1] += EPSILON;

                    matrix2.appendRow(row);
                    tmp.appendIneqType(GE);

                    result = this.isFeasible(tmp);
                    break;
                }
                case GE: {
                    row[row.length - 1] -= EPSILON;

                    matrix2.appendRow(row);
                    tmp.appendIneqType(LE);

                    result = this.isFeasible(tmp);
                    break;
                }
            }

            if (!result) {
                this.system.removeConstraint(i);
            }
        }
    }

    private boolean isFeasible(final LCSystem system) throws TypeInegaliteInvalideException, TailleLigneInvalideException, ProblemeSansVariablesException {
        final Matrix2 matrix = this.system.getMatrix();
        final double[] objective = new double[matrix.columnCount() - 1];

        final BooleanHolder isInfinite = new BooleanHolder();

        try {
            this.solve(false, objective, system, isInfinite);
            return !isInfinite.get();
        } catch (LpSolveException e) {
            return false;
        }
    }
}
