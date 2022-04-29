package model;

import config.Config;
import exceptions.problems.*;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import java.io.Closeable;

/**
 * @author Émeline BONTE, Ghilain BERGERON, Khaled SADEGH
 * @version 1.2
 */
public final class MLOProblem implements Closeable {
    private final LpSolve solver;

    private int solveStatus = -1;
    private boolean solved = false;

    public static final int EQ = LpSolve.EQ;
    public static final int LE = LpSolve.LE;
    public static final int GE = LpSolve.GE;

    /**
     * Crée un nouveau problème d'optimisation linéaire en nombres mixtes avec le nombre de variables donné.
     *
     * @param nbVars le nombre de variables dans le problème
     * @throws LpSolveException
     */
    public MLOProblem(final int nbVars) throws LpSolveException, ProblemeSansVariablesException {
        if (nbVars < 0)
            throw new ProblemeSansVariablesException();

        this.solver = LpSolve.makeLp(0, nbVars);
        this.solver.setVerbose(0);
        this.solver.setMinim();
        this.solver.setTimeout(15);

        for (int i = 0; i < nbVars; ++i) {
            this.solver.setBounds(i + 1, -this.solver.getInfinite(), this.solver.getInfinite());
        }
    }

    /**
     * Ajoute une contrainte au problème d'optimisation linéaire en nombres mixtes.
     *
     * @param row      la description de la partie gauche de la contrainte, sous la forme <code>c_1 c_2 ... c_n</code>
     *                 où les <code>c_i</code> sont les coefficients devant les variables
     *                 <p>
     *                 Si une variable n'est pas présente dans la contrainte, un coefficient de <code>0</code> doit être indiqué.
     * @param ineqType le type d'égalité de l'équation, entre {@link #GE}, {@link #LE} et {@link #EQ}.
     * @param b        la valeur à droite de l'équation
     * @return la nouvelle instance du problème
     * @throws LpSolveException
     */
    public MLOProblem withConstraint(final String row, final int ineqType, final String b) throws LpSolveException, TypeInegaliteInvalideException {
        if (ineqType != LE && ineqType != GE && ineqType != EQ)
            throw new TypeInegaliteInvalideException(ineqType);

        this.solver.strAddConstraint(row, ineqType, Double.parseDouble(b));
        return this;
    }

    /**
     * Ajoute une contrainte au problème d'optimisation linéaire en nombres mixtes.
     *
     * @param row      la description des coefficients des variables de la partie gauche de la contrainte
     *                 <p>
     *                 Si une variable n'est pas présente dans la contrainte, un coefficient de <code>0</code> doit être indiqué.
     * @param ineqType le type d'égalité de l'équation, entre {@link #GE}, {@link #LE} et {@link #EQ}.
     * @param b        la valeur à droite de l'équation
     * @return la nouvelle instance du problème
     * @throws LpSolveException
     */
    public MLOProblem withConstraint(final double[] row, final int ineqType, final double b) throws LpSolveException, TypeInegaliteInvalideException, TailleLigneInvalideException {
        if (ineqType != LE && ineqType != GE && ineqType != EQ)
            throw new TypeInegaliteInvalideException(ineqType);
        if (row.length != this.solver.getNcolumns() + 1)
            throw new TailleLigneInvalideException(row.length, this.solver.getNcolumns() + 1);

        this.solver.addConstraint(row, ineqType, b);
        return this;
    }

    /**
     * Ajoute la ligne correspondant au calcul de la fonction objectif.
     *
     * @param row la description des coefficients de la fonction objectif sous la forme <code>c_1 c_2 ... c_n</code>
     *            <p>
     *            Si une variable n'est pas présente dans la fonction objectif, un coefficient de <code>0</code>
     *            doit être indiqué.
     * @return la nouvelle instance du problème
     * @throws LpSolveException
     */
    public MLOProblem withObjective(final String row) throws LpSolveException {
        this.solver.strSetObjFn(row);
        return this;
    }

    /**
     * Ajoute la ligne correspondant au calcul de la fonction objectif.
     *
     * @param row la description des coefficients de la fonction objectif
     *            <p>
     *            Si une variable n'est pas présente dans la fonction objectif, un coefficient de <code>0</code>
     *            doit être indiqué.
     * @return la nouvelle instance du problème
     * @throws LpSolveException
     */
    public MLOProblem withObjective(final double[] row) throws LpSolveException, TailleLigneInvalideException {
        if (row.length != this.solver.getNcolumns() + 1)
            throw new TailleLigneInvalideException(row.length, this.solver.getNcolumns() + 1);

        this.solver.setObjFn(row);
        return this;
    }

    /**
     * Indique les types des variables du problème d'optimisation linéaire en nombres mixtes.
     *
     * @param types les types de toutes les variables du problème
     *              <p>
     *              Attention, les types doivent être renseignés pour TOUTES les variables du système.
     * @return la nouvelle instance du problème
     * @throws LpSolveException
     * @implNote si cette fonction n'est pas appelée, toutes les variables sont supposées réelles.
     */
    public MLOProblem withVarTypes(final VarType... types) throws LpSolveException, TailleLigneInvalideException {
        if (types.length != this.solver.getNcolumns())
            throw new TailleLigneInvalideException(types.length, this.solver.getNcolumns());

        for (int i = 0; i < types.length; ++i) {
            switch (types[i]) {
                case INT:
                    this.solver.setInt(i+1, true);
                    break;
                case BINARY:
                    this.solver.setBinary(i+1, true);
                    break;
                default:
                    // rien faire pour des variables entières
            }
        }
        return this;
    }

    /**
     * Résout le problème d'optimisation linéaire en nombres mixtes.
     *
     * @return la solution obtenue par lp_solve
     * @throws LpSolveException
     */
    public double solve() throws LpSolveException {
        this.solveStatus = this.solver.solve();
        this.solved = true;

        if (this.solveStatus == LpSolve.SUBOPTIMAL)
            System.err.println("lp_solve returned suboptimal solution by lack of time");
        if (this.solveStatus == LpSolve.TIMEOUT)
            System.err.println("lp_solve was unable to find a real solution before the timeout");

        return this.solver.getObjective();
    }

    @Override
    public void close() {
        this.solver.deleteLp();
    }

    /**
     * Retourne le nombre de contraintes dans le système.
     *
     * @implNote Après résolution, le nombre de contraintes augmente de 1.
     *
     * @return le nombre de contraintes dans le système
     */
    public int getNbConstraints() {
        return this.solver.getNrows();
    }

    /**
     * Retourne le nombre de variables dans le système.
     *
     * @return le nombre de variables du système
     */
    public int getNbVars() {
        return this.solver.getNcolumns();
    }

    /**
     * Récupère les coefficients des variables de la N-ième contrainte.
     *
     * @implNote Les coefficients des contraintes commencent à partir de l'indice <code>1</code>.
     *
     * @param nbRow l'indice <code>N</code> de la contrainte à récupérer
     *
     *              Un indice de <code>0</code> indique la fonction objectif.
     *              Pour récupérer la contrainte <code>c_i</code> du système originel, il faut donner le paramètre
     *              <code>i + 1</code> à cette méthode.
     * @return les coefficients des variables dans la contrainte
     * @throws LpSolveException
     */
    public double[] getConstraint(final int nbRow) throws LpSolveException {
        return this.solver.getPtrRow(nbRow);
    }

    /**
     * Retourne la partie droite de la N-ième contrainte du système.
     *
     * @param nbRow si N est <code>0</code>, la valeur retournée est <code>0</code>, sinon la valeur retournée
     *              est celle de la contrainte <code>N - 1</code> dans le système d'origine.
     * @return la partie droite d'une contrainte
     */
    public double getConstraintRHS(final int nbRow) {
        return this.solver.getRh(nbRow);
    }

    /**
     * Transforme le problème en un problème de maximisation.
     *
     * @return le nouveau problème modifié
     */
    public MLOProblem max() {
        this.solver.setMaxim();
        return this;
    }

    /**
     * Transforme le problème en un problème de minimisation.
     *
     * @return le nouveau problème modifié
     */
    public MLOProblem min() {
        this.solver.setMinim();
        return this;
    }

    /**
     * Retourne le type de la variable dans le problème d'optimisation en nombres mixtes.
     *
     * @param i l'indice de la variable dans le système
     * @return le type de la variable
     */
    public VarType getVarType(final int i) throws ColonneInvalideException {
        if (i < 0 || i >= this.solver.getNcolumns())
            throw new ColonneInvalideException(i, this.solver.getNcolumns() - 1);

        if (this.solver.isInt(i)) return VarType.INT;
        if (this.solver.isBinary(i)) return VarType.BINARY;
        return VarType.REAL;
    }

    /**
     * Récupère le type de la contrainte numéro <code>i</code> dans le système.
     *
     * @param i l'indice de la contrainte
     * @return le type de la contrainte dont l'indice est passé en paramètre
     * @throws LpSolveException
     */
    public int getConstraintType(final int i) throws LpSolveException, LigneInvalideException {
        if (i < 0 || i > this.solver.getNrows() - 1)
            throw new LigneInvalideException(i, this.solver.getNrows() - 1);

        return this.solver.getConstrType(i + 1);
    }

    /**
     * Vérifie si une valeur est considérée comme infinie du point de vue de lp_solve.
     *
     * @param val la valeur
     * @return <code>true</code> si la valeur est considérée infinie, <code>false</code> sinon
     */
    public boolean isInfinite(final double val) {
        return this.solver.isInfinite(val);
    }

    /**
     * Vérifie si le problème résolu est faisable ou non
     * (s'il existe une valuation des variables respectant toutes les contraintes).
     *
     * @return <code>true</code> si le problème est faisable, <code>false</code> sinon
     * @throws NonResoluException si le problème n'a pas été résolu au préalable
     */
    public boolean isInfeasable() throws NonResoluException {
        if (!this.solved)
            throw new NonResoluException();

        return this.solveStatus == LpSolve.INFEASIBLE;
    }

    /**
     * Vérifie si le problème résolu est non-borné ou non
     * (si la valeur de la fonction objectif est infinie).
     *
     * @return <code>true</code> si le problème est non-borné, <code>false</code> sinon
     * @throws NonResoluException si le problème n'a pas été résolu au préalable
     */
    public boolean isUnbounded() throws NonResoluException {
        if (!this.solved)
            throw new NonResoluException();

        return this.solveStatus == LpSolve.UNBOUNDED;
    }

    /**
     * Vérifie si le problème résolu est optimal ou sous-optimal.
     *
     * @return <code>true</code> si le problème est optimal, <code>false</code> sinon
     * @throws NonResoluException si le problème n'a pas été résolu au préalable
     */
    public boolean isOptimal() throws NonResoluException {
        if (!this.solved)
            throw new NonResoluException();

        return this.solveStatus == LpSolve.OPTIMAL || this.solveStatus == LpSolve.SUBOPTIMAL;
    }

    public void debug() {
        this.solver.printLp();
    }

    /**
     * Tous les différents types de variables disponibles pour la résolution de problèmes linéaires en nombres mixtes
     * avec lp_solve.
     */
    public enum VarType {
        INT, REAL, BINARY
    }
}
