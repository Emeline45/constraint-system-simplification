package model;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Une matrice à 2 dimensions contenant des objets de type <code>Double</code>.
 */
public class Matrix2 implements Iterable<Double[]>, Cloneable {
    private Double[][] innerMatrix;

    public Matrix2(final int rowCount, final int columnCount) {
        this.innerMatrix = new Double[rowCount][columnCount];
    }

    private Matrix2(final Double[][] matrix) {
        this.innerMatrix = matrix.clone();

        for (int i = 0; i < matrix.length; ++i) {
            this.innerMatrix[i] = matrix[i].clone();
        }
    }

    /**
     * Change la valeur de la case ligne <code>i</code> colonne <code>j</code> au paramètre <code>value</code> donné.
     *
     * @param i la ligne de la case
     * @param j la colonne de la case
     * @param value la nouvelle valeur de la case
     */
    public void set(final int i, final int j, final Double value) {
        this.innerMatrix[i][j] = value;
    }

    /**
     * Récupère la valeur de la case ligne <code>i</code> colonne <code>j</code>.
     *
     * @param i la ligne de la case
     * @param j la colonne de la case
     * @return <code>null</code> si la case n'a pas été initialisée, sinon la valeur contenue dedans
     */
    public Double get(final int i, final int j) {
        assert(i >= 0 && i < this.rowCount());
        assert(j >= 0 && j < this.columnCount());

        return this.innerMatrix[i][j];
    }

    /**
     * Récupère la ligne <code>i</code> de la matrice sous la forme d'un tableau.
     *
     * @param i le numéro de la ligne
     * @return un tableau contenant la ligne
     */
    public Double[] row(final int i) {
        assert(i >= 0 && i < this.rowCount());

        return this.innerMatrix[i];
    }

    @Override
    public Iterator<Double[]> iterator() {
        return new Iterator<>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < Matrix2.this.innerMatrix.length;
            }

            @Override
            public Double[] next() {
                return Matrix2.this.innerMatrix[i++];
            }
        };
    }

    /**
     * Retourne le nombre de lignes dans la matrice.
     *
     * @return le nombre de lignes dans la matrice
     */
    public int rowCount() {
        return this.innerMatrix.length;
    }

    /**
     * Retourne le nombre de colonnes dans la matrice.
     *
     * @return le nombre de colonnes dans la matrice
     */
    public int columnCount() {
        return this.innerMatrix.length == 0 ? 0 : this.innerMatrix[0].length;
    }

    /**
     * Rajoute une ligne à la fin de la matrice.
     *
     * @param row la ligne à rajouter
     */
    public void appendRow(final Double[] row) {
        assert(row.length == this.columnCount());

        final Double[][] newMatrix =  new Double[this.innerMatrix.length + 1][this.columnCount()];

        System.arraycopy(this.innerMatrix, 0, newMatrix, 0, this.innerMatrix.length);
        System.arraycopy(row, 0, newMatrix[this.innerMatrix.length], 0, row.length);

        this.innerMatrix = newMatrix;
    }

    /**
     * Retire la ligne à l'indice <code>i</code> de la matrice.
     *
     * @implNote Tous les indices après <code>i</code> sont décalés de 1
     *
     * @param i l'indice de la ligne à retirer
     */
    public void removeRow(final int i) {
        final Double[][] proxyMatrix = new Double[this.innerMatrix.length - 1][this.columnCount()];

        System.arraycopy(this.innerMatrix, 0, proxyMatrix, 0, i);
        System.arraycopy(this.innerMatrix, i + 1, proxyMatrix, i, this.innerMatrix.length - i - 1);

        this.innerMatrix = proxyMatrix;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[ ");

        for (int i = 0; i < this.rowCount(); ++i) {
            builder.append(Arrays.toString(this.innerMatrix[i])).append("\n  ");
        }

        return builder.append(" ]").toString();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Matrix2 clone() {
        return new Matrix2(this.innerMatrix);
    }
}