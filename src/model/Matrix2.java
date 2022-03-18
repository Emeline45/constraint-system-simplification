package model;

import java.util.Iterator;

/**
 * Une matrice à 2 dimensions contenant des objets de type <code>T</code>.
 *
 * @param <T> le type des éléments contenus dans la matrice
 */
public class Matrix2<T> implements Iterable<T[]> {
    private final T[][] innerMatrix;

    public Matrix2(final int rowCount, final int columnCount) {
        //noinspection unchecked
        this.innerMatrix = (T[][]) new Object[rowCount][columnCount];
    }

    /**
     * Change la valeur de la case ligne <code>i</code> colonne <code>j</code> au paramètre <code>value</code> donné.
     *
     * @param i la ligne de la case
     * @param j la colonne de la case
     * @param value la nouvelle valeur de la case
     */
    public void set(final int i, final int j, final T value) {
        this.innerMatrix[i][j] = value;
    }

    /**
     * Récupère la valeur de la case ligne <code>i</code> colonne <code>j</code>.
     *
     * @param i la ligne de la case
     * @param j la colonne de la case
     * @return <code>null</code> si la case n'a pas été initialisée, sinon la valeur contenue dedans
     */
    public T get(final int i, final int j) {
        return this.innerMatrix[i][j];
    }

    /**
     * Récupère la ligne <code>i</code> de la matrice sous la forme d'un tableau.
     *
     * @param i le numéro de la ligne
     * @return un tableau contenant la ligne
     */
    public T[] row(final int i) {
        return this.innerMatrix[i];
    }

    @Override
    public Iterator<T[]> iterator() {
        return new Iterator<>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < Matrix2.this.innerMatrix.length;
            }

            @Override
            public T[] next() {
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
        return this.innerMatrix[0].length;
    }
}
