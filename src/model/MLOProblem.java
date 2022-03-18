package model;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import java.io.Closeable;

/**
 * @author Émeline BONTE, Ghilain BERGERON, Khaled SADEGH
 * @version 1.2
 */
public final class MLOProblem implements Closeable {
    private final LpSolve solver;

    public static final int EQ = LpSolve.EQ;
    public static final int LE = LpSolve.LE;
    public static final int GE = LpSolve.GE;

    /**
     * Crée un nouveau problème d'optimisation linéaire en nombres mixtes avec le nombre de variables donné.
     *
     * @param nbVars le nombre de variables dans le problème
     * @throws LpSolveException
     */
    public MLOProblem(final int nbVars) throws LpSolveException {
        assert (nbVars >= 0);

        this.solver = LpSolve.makeLp(0, nbVars);
        this.solver.setVerbose(0);
    }

    /**
     * Ajoute une contrainte au problème d'optimisation linéaire en nombres mixtes.
     *
     * @param row      la description de la partie gauche de la contrainte, sous la forme <code>c_1 c_2 ... c_n</code>
     *                 où les <code>c_i</code> sont les coefficients devant les variables.
     *                 <p>
     *                 Si une variable n'est pas présente dans la contrainte, un coefficient de <code>0</code> doit être indiqué.
     * @param ineqType le type d'égalité de l'équation, entre {@link #GE}, {@link #LE} et {@link #EQ}.
     * @param b        la valeur à droite de l'équation
     * @return la nouvelle instance du problème
     * @throws LpSolveException
     */
    public MLOProblem withConstraint(final String row, final int ineqType, final String b) throws LpSolveException {
        assert(ineqType == LE || ineqType == GE || ineqType == EQ);

        this.solver.strAddConstraint(row, ineqType, Double.parseDouble(b));
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
     * Indique les types des variables du problème d'optimisation linéaire en nombres mixtes.
     *
     * @param types les types de toutes les variables du problème
     *              <p>
     *              Attention, les types doivent être renseignés pour TOUTES les variables du système.
     * @return la nouvelle instance du problème
     * @throws LpSolveException
     * @implNote si cette fonction n'est pas appelée, toutes les variables sont supposées réelles.
     */
    public MLOProblem withVarTypes(final VarType... types) throws LpSolveException {
        assert (types.length == this.solver.getNcolumns());

        for (int i = 0; i < types.length; ++i) {
            switch (types[i]) {
                case INT:
                    this.solver.setInt(i, true);
                    break;
                case BINARY:
                    this.solver.setBinary(i, true);
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
        this.solver.solve();
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
     * @throws LpSolveException
     */
    public double getConstraintRHS(final int nbRow) throws LpSolveException {
        return this.solver.getRh(nbRow);
    }

    /**
     * Tous les différents types de variables disponibles pour la résolution de problèmes linéaires en nombres mixtes
     * avec lp_solve.
     */
    public enum VarType {
        INT, REAL, BINARY
    }
}
