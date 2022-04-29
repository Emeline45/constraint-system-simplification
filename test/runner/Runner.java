package runner;

import model.LCSystem;
import model.Matrix2;
import model.simplification.Daalmans;
import model.simplification.PivotGauss;
import model.simplification.Simplification;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Classe permettant de faire tourner toutes les combinaisons de simplifications
 * possibles.
 */
public class Runner {
    private final static double DELTA = 0.000000001;

    private final Class<Simplification>[] algorithms;
    private final List<List<Class<Simplification>>> permutations;

    public Runner() {
        //noinspection unchecked
        this.algorithms = new Class[]{Daalmans.class, PivotGauss.class};
        //this.permutations =
                //.flatMap(s -> s.map(List::stream));

        this.permutations = IntStream.rangeClosed(1, this.algorithms.length)
                .mapToObj(i -> this.permutations(i, this.algorithms))
                .filter(l -> !l.isEmpty())
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableList());
    }

    //////////////////////////

    /**
     * Fais tourner toutes les simplifications sur le système en entrée.
     *
     * @param system le système en entrée de toutes les simplifications.
     *
     *               Attention, celui-ci n'est pas modifié pendant l'exécution.
     * @return une liste de résultat d'exécution pour chaque méthode
     */
    public Stream<RunStatus> run(final LCSystem system) {
        return this.forEach(l -> this.runOn(l, system.clone()));
    }

    /**
     * Retourne toutes les combinaisons de simplification.
     *
     * @return une liste contenant toutes les permutations
     */
    public List<List<Class<Simplification>>> getPermutations() {
        return new ArrayList<>(permutations);
    }

    /**
     * Génère toutes les permutations de taille <code>k</code> du tableau donné.
     *
     * @param size la taille de chaque permutation
     * @param objs le tableau contenant les éléments dont on veut les permutations
     * @param <E> le type des éléments dans le tableau
     * @return une liste contenant toutes les <code>k</code>-permutations
     */
    private <E> List<List<E>> permutations(final int size, final E[] objs) {
        return permK(new ArrayList<>(Arrays.asList(objs)), 0, size);
    }

    /**
     * Génère récursivement toutes les permutations de taille <code>k</code>.
     * @param p la liste contenant les éléments à permuter
     * @param i
     * @param k la taille des permutations
     * @param <E> le type des éléments dans la liste
     * @return une liste contenant toutes les <code>k</code>-permutations
     */
    private static <E> List<List<E>> permK(List<E> p, int i, int k) {
        if (i == k) {
            return List.of(new ArrayList<>(p.subList(0, k)));
        }

        List<List<E>> perms = new ArrayList<>();
        for (int j = i; j < p.size(); j++) {
            Collections.swap(p, i, j);
            perms.addAll(permK(p, i + 1, k));
            Collections.swap(p, i, j);
        }
        return perms;
    }

    /**
     * Applique une action sur toutes les permutations.
     *
     * @param runner l'action à appliquer, prenant en paramètre la liste des simplifications à exécuter
     * @param <T> le type de retour de l'action
     * @return une liste contenant tous les résultats de chaque action
     *
     *         Tout élément <code>null</code> est enlevé de cette liste.
     */
    private <T> Stream<T> forEach(Function<List<Class<Simplification>>, T> runner) {
        return this.permutations.stream().map(runner).filter(o -> !Objects.isNull(o));
    }

    /**
     * Exécute une combinaison de simplification sur le système de contraintes linéaires donné.
     *
     * @param simpls la combinaison de simplification
     * @param system le système de contraintes linéaires considéré
     *
     *               Attention, celui-ci est modifié par cette méthode
     * @return un statut d'exécution de cette simplification (<code>null</code> si aucune simplification n'a pu être exécutée)
     */
    private RunStatus runOn(final List<Class<Simplification>> simpls, final LCSystem system) {
        List<Simplification> simplifications = simpls.stream().map(c -> {
                    try {
                        return c.getConstructor(LCSystem.class).newInstance(system);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        return null;
                    }
                })
                .filter(o -> !Objects.isNull(o))
                .collect(Collectors.toUnmodifiableList());

        if (simplifications.isEmpty())
            return null;

        double nb0In = 0;
        final int initRowCount = system.getMatrix().rowCount();
        final int initColumnCount = system.getMatrix().columnCount() - 1;
        {
            final Matrix2 matrix = system.getMatrix();
            for (int i = 0; i < initRowCount; ++i) {
                for (int j = 0; j < initColumnCount; ++j) {
                    if (Math.abs(matrix.get(i, j)) <= DELTA)
                        nb0In += 1;
                }
            }
        }

        final long startingTime = System.nanoTime();
        for (Simplification simp : simplifications) {
            simp.run();
        }
        final long endingTime = System.nanoTime();

        double nb0Out = 0;
        final int finalRowCount = system.getMatrix().rowCount();
        final int finalColumnCount = system.getMatrix().columnCount() - 1;
        {
            final Matrix2 matrix = system.getMatrix();
            for (int i = 0; i < initRowCount; ++i) {
                if (i >= finalRowCount) {
                    nb0Out += initColumnCount;
                    continue;
                }

                for (int j = 0; j < initColumnCount; ++j) {
                    if (j >= finalColumnCount)
                        nb0Out += 1;
                    if (Math.abs(matrix.get(i, j)) <= DELTA)
                        nb0Out += 1;
                }
            }
        }

        final double matrixSize = initColumnCount * initRowCount;
        final double index = (nb0Out / matrixSize - nb0In / matrixSize) * 100;

        return new RunStatus(endingTime - startingTime, system, index, simpls);
    }

    /**
     * Une classe contenant les résultats d'une exécution d'une simplification.
     */
    public static class RunStatus {
        /**
         * Le nombre de nanosecondes nécessaires au calcul.
         */
        public final long runtimeNanos;
        /**
         * Le système de contraintes linéaires final, résultat de l'application de la simplification.
         */
        public final LCSystem finalSystem;
        /**
         * Les classes utilisées pour réaliser cette simplification, par ordre d'application.
         */
        public final List<Class<Simplification>> order;
        /**
         * L'indice de simplification du système en entrée.
         */
        public final double simpIndex;

        public RunStatus(final long runtimeInNanos, final LCSystem system, final double simplificationIndex, final List<Class<Simplification>> order) {
            this.runtimeNanos = runtimeInNanos;
            this.finalSystem = system.clone();
            this.order = order;
            this.simpIndex = simplificationIndex;
        }

        @SuppressWarnings("StringBufferReplaceableByString")
        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();

            builder.append("- Algorithms: ")
                    .append(this.order.stream().map(Class::getSimpleName).collect(Collectors.toUnmodifiableList()))
                    .append("\n- Execution took: ")
                    .append(this.runtimeNanos)
                    .append("ns\n- Final system:\n")
                    .append(this.finalSystem);

            return builder.toString();
        }
    }
}
