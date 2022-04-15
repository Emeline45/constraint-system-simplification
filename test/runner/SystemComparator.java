package runner;

import model.LCSystem;
import model.MLOProblem;
import model.Matrix2;

import java.util.Comparator;

/**
 * Un comparateur de systèmes de contraintes linéaires selon les critères suivants :
 *
 * <code>o1</code> est meilleur que <code>o2</code> si <ul>
 * <li> soit le nombre de 0 dans <code>A1</code> est supérieur au nombre de 0 dans <code>A2</code>
 *   et le nombre de contraintes dans <code>o1</code> est inférieur ou égal à celui dans <code>o2</code> </li>
 * <li> soit le nombre de 0 dans <code>A1</code> est égal au nombre de 0 dans <code>A2</code>
 *   et le nombre de contraintes dans <code>o1</code> est égal au nombre de contraintes dans <code>o2</code>
 *   et le nombre d'égalités dans <code>o1</code> est supérieur au nombre d'égalités dans <code>o2</code> </li>
 * </ul>
 *
 * <code>o1` est équivalent à <code>o2</code> si : <ul>
 * <li> le nombre de 0 dans <code>A1</code> est égal au nombre de 0 dans <code>A2</code>
 *   et le nombre de contraintes dans <code>o1</code> est égal au nombre de contraintes dans <code>o2</code>
 *   et le nombre d'égalités dans <code>o1</code> est égal au nombre d'égalités dans <code>o2</code> </li>
 * </ul>
 *
 * sinon, <code>o1</code> est moins simplifié que <code>o2</code>.
 */
public class SystemComparator implements Comparator<LCSystem> {
    @Override
    public int compare(LCSystem o1, LCSystem o2) {
        int nbZeroInO1 = 0;
        int nbZeroInO2 = 0;

        final Matrix2 m1 = o1.getMatrix();
        final Matrix2 m2 = o2.getMatrix();

        // NOTE: on ignore la dernière colonne puisqu'il s'agit du vecteur `b`
        for (int j = 0; j < m1.columnCount() - 1; ++j) {
            for (int i = 0; i < m1.rowCount(); ++i) {
                nbZeroInO1 += m1.get(i, j) == 0 ? 1 : 0;
            }
        }
        for (int j = 0; j < m2.columnCount() - 1; ++j) {
            for (int i = 0; i < m2.rowCount(); ++i) {
                nbZeroInO2 += m2.get(i, j) == 0 ? 1 : 0;
            }
        }

        int sizeB1 = m1.rowCount();
        int sizeB2 = m2.rowCount();

        if (nbZeroInO1 > nbZeroInO2 && sizeB1 <= sizeB2)
            return -1;

        int nbEqInO1 = 0;
        int nbEqInO2 = 0;

        for (int eqType : o1.getIneqTypes()) {
            nbEqInO1 += eqType == MLOProblem.EQ ? 1 : 0;
        }
        for (int eqType : o2.getIneqTypes()) {
            nbEqInO2 += eqType == MLOProblem.EQ ? 1 : 0;
        }

        if (nbZeroInO1 == nbZeroInO2 && sizeB1 == sizeB2 && nbEqInO1 < nbEqInO2)
            return -1;

        if (nbZeroInO1 == nbZeroInO2 && sizeB1 == sizeB2 && nbEqInO1 == nbEqInO2)
            return 0;

        return 1;
    }
}
