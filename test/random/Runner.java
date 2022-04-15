package random;

import model.LCSystem;
import model.simplification.Daalmans;
import model.simplification.PivotGauss;
import model.simplification.Simplification;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Runner {
    private final Class<Simplification>[] algorithms;
    private final Stream<Stream<Class<Simplification>>> permutations;

    public Runner() {
        //noinspection unchecked
        this.algorithms = new Class[]{Daalmans.class, PivotGauss.class};
        //this.permutations =
                //.flatMap(s -> s.map(List::stream));

        this.permutations = IntStream.rangeClosed(1, this.algorithms.length)
                .mapToObj(i -> this.permutations(i, this.algorithms))
                .flatMap(s -> s.stream().map(List::stream));
    }

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

    //////////////////////////

    public List<RunStatus> run(final LCSystem system) {
        return this.forEach(l -> this.runOn(l, system.clone()));
    }

    private <E> List<List<E>> permutations(final int size, final E[] objs) {
        return permK(new ArrayList<>(Arrays.asList(objs)), 0, size);
    }

    private <T> List<T> forEach(Function<List<Class<Simplification>>, T> runner) {
        return this.permutations.map(s -> runner.apply(s.collect(Collectors.toUnmodifiableList())))
                .filter(o -> !Objects.isNull(o))
                .collect(Collectors.toUnmodifiableList());
    }

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

        final long startingTime = System.nanoTime();
        for (Simplification simp : simplifications) {
            simp.run();
        }
        final long endingTime = System.nanoTime();

        return new RunStatus(endingTime - startingTime, system, simpls);
    }

    public static class RunStatus {
        public final long runtimeNanos;
        public final LCSystem finalSystem;
        public final List<Class<Simplification>> order;

        public RunStatus(final long runtimeInNanos, final LCSystem system, final List<Class<Simplification>> order) {
            this.runtimeNanos = runtimeInNanos;
            this.finalSystem = system.clone();
            this.order = order;
        }

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
