package random;

import exceptions.problems.NonResoluException;
import exceptions.problems.ProblemeSansVariablesException;
import exceptions.problems.TypeInegaliteInvalideException;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import model.LCSystem;
import model.simplification.Daalmans;
import model.simplification.PivotGauss;
import org.junit.jupiter.api.Test;
import runner.Runner;
import runner.SystemComparator;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TestAllPossibleAlgo {
    /**
     * Le nombre de systèmes à générer pour la comparaison.
     */
    private final static int SAMPLES_SIZE = 100;

    private String getTimeFromNanos(double nanos, final String fmt) {
        String currentUnit = "ns";
        if (Math.abs(nanos) > 1000) {
            currentUnit = "µs";
            nanos /= 1000;
        }
        if (Math.abs(nanos) > 1000) {
            currentUnit = "ms";
            nanos /= 1000;
        }
        if (Math.abs(nanos) > 1000) {
            currentUnit = " s";
            nanos /= 1000;
        }

        return String.format(fmt, nanos) + " " + currentUnit;
    }

    @SuppressWarnings("SameParameterValue")
    private void showPercentage(final int x, final int nbRuns, final StringBuilder sb) {
        final String nbRunsStringified = Integer.toString(nbRuns);

        sb
            .append(String.format("%" + nbRunsStringified.length() + "d", x))
            .append("/")
            .append(nbRuns)
            .append("× (")
            .append(String.format("%5.1f", (double) x / nbRuns * 100))
            .append("% du temps")
            .append(")");
    }

    private <T extends Number> double[] computeMinMaxMeanStddev(final List<T> times) {
        final int n = times.size();

        double mean = n > 0 ? times.get(0).doubleValue() : 0;
        double msq = 0;
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        double delta;
        for (int j = 1; j < n; ++j) {
            double time = times.get(j).doubleValue();

            delta = time - mean;
            mean += delta / j;
            msq += delta * (time - mean);

            min = Math.min(min, time);
            max = Math.max(max, time);
        }
        double stddev = Math.sqrt(n > 0 ? msq / (n - 1) : 0);

        return new double[] {min, max, mean, stddev};
    }

    @Test
    public void testAndCompareAll() throws ProblemeSansVariablesException, LpSolveException, TypeInegaliteInvalideException, NonResoluException {
        Runner r = new Runner();
        var perms = r.getPermutations();

        var globalStatus = new HashMap<>(
                perms.stream()
                        .collect(Collectors.toMap(Function.identity(), _l -> new ArrayList<Runner.RunStatus>())));

        // TODO: afficher les différences de temps entre chaque méthode
        // TODO: afficher le facteur déterminant de la comparaison (nb contraintes, nb 0, ...) ?

        SystemGenerator s;
        for (int i = 0; i < SAMPLES_SIZE; ++i) {

            do {
                s = new SystemGenerator(-1, 1);
            } while (!s.solveExist());

            final LCSystem system = new LCSystem(s.getPb(), s.getSolve());
            List<Runner.RunStatus> results = r.run(system).collect(Collectors.toList());

            for (Runner.RunStatus stat : results) {
                globalStatus.get(stat.order).add(stat);
            }
        }

        StringBuilder sb = new StringBuilder();
        final SystemComparator cmp = new SystemComparator();

        int nb = 0;
        for (var entry : globalStatus.entrySet()) {
            var method = entry.getKey();
            var stats = entry.getValue();

            var times = stats.stream()
                    .map(stat -> stat.runtimeNanos)
                    .collect(Collectors.toUnmodifiableList());

            final double[] infos = computeMinMaxMeanStddev(times);
            final double min = infos[0];
            final double max = infos[1];
            final double mean = infos[2];
            final double stddev = infos[3];

            var simplificationIndices = stats.stream()
                    .map(stat -> stat.simpIndex)
                    .collect(Collectors.toUnmodifiableList());

            final double[] infos2 = computeMinMaxMeanStddev(simplificationIndices);
            final double minIndex = infos2[0];
            final double maxIndex = infos2[1];
            final double meanIndex = infos2[2];

            sb.append("Combinaison #")
                    .append(++nb)
                    .append(" : {")
                    .append(method.stream()
                            .map(Class::getSimpleName)
                            .collect(Collectors.joining(" ▶ ")))
                    .append("}\n")
                    .append("  [")
                    .append(SAMPLES_SIZE)
                    .append(" runs]\n")
                    .append("  Temps (moyen ± σ) (min … max) : ")
                    .append("(")
                    .append(getTimeFromNanos(mean, "%5.1f"))
                    .append(" ± ")
                    .append(getTimeFromNanos(stddev, "%5.1f"))
                    .append(") (")
                    .append(getTimeFromNanos(min, "%.1f"))
                    .append(" … ")
                    .append(getTimeFromNanos(max, "%.1f"))
                    .append(")\n")
                    .append("  Pourcentage de simplification moyen (min … max) : ")
                    .append(String.format("%5.1f", meanIndex))
                    .append("% (")
                    .append(String.format("%5.1f", minIndex))
                    .append("% … ")
                    .append(String.format("%5.1f", maxIndex))
                    .append("%)\n");

            // comparaison aux autres combinaisons
            int nb_ = 0;
            for (var entry2 : globalStatus.entrySet()) {
                nb_++;
                var method2 = entry2.getKey();
                var stats2 = entry2.getValue();

                if (method2 == method)
                    continue;

                int nbTimesBetter = 0;
                int nbTimesWorse = 0;
                int nbTimesEqual = 0;
                for (int k = 0; k < SAMPLES_SIZE; ++k) {
                    final Runner.RunStatus r1 = stats.get(k);
                    final Runner.RunStatus r2 = stats2.get(k);

                    final int comp1 = cmp.compare(r1.finalSystem, r2.finalSystem);
                    final int comp2 = cmp.compare(r2.finalSystem, r1.finalSystem);

                    if (comp1 < 0 && comp2 > 0) nbTimesBetter++;
                    else if (comp1 > 0 && comp2 < 0) nbTimesWorse++;
                    else if (comp1 == comp2) nbTimesEqual++;
                    else {
                        // NOTE : cela ne semble pas être affiché :)
                        System.err.println("--- Comparaison incohérente ---");
                        System.err.println(r1.finalSystem);
                        System.err.println(r2.finalSystem);
                    }
                }

                final double[] infos_ = computeMinMaxMeanStddev(stats2.stream().map(stat -> stat.runtimeNanos).collect(Collectors.toUnmodifiableList()));
                final double mean_ = infos_[2];

                sb.append("  VS combinaison #")
                        .append(nb_)
                        .append(" : {")
                        .append(method2.stream()
                                .map(Class::getSimpleName)
                                .collect(Collectors.joining(" ▶ ")))
                        .append("}\n")
                        .append("    Temps       : environ ")
                        .append(String.format("%.2f", Math.max(mean, mean_) / Math.min(mean, mean_)))
                        .append("× plus ")
                        .append(mean <= mean_ ? "rapide" : "lent")
                        .append("\n")
                        .append("    Meilleure   : ");
                showPercentage(nbTimesBetter, SAMPLES_SIZE, sb);
                sb.append("\n")
                        .append("    Pire        : ");
                showPercentage(nbTimesWorse, SAMPLES_SIZE, sb);
                sb.append("\n")
                        .append("    Équivalente : ");
                showPercentage(nbTimesEqual, SAMPLES_SIZE, sb);
                sb.append("\n");
            }
        }

        System.out.println(sb);
    }

    @Test
    public void test1() throws ProblemeSansVariablesException, LpSolveException, TypeInegaliteInvalideException, NonResoluException {
        SystemGenerator s = new SystemGenerator(-1, 1);
        while (!s.solveExist()) {
            s = new SystemGenerator(-1, 1);

        }
        final LCSystem system = new LCSystem(s.getPb(), s.getSolve());
        System.out.println(system);

        final LCSystem gaussSystem = system.clone();
        final LCSystem daalmansSystem = system.clone();

        PivotGauss pg = new PivotGauss(gaussSystem);
        pg.run();
        System.out.println(pg);

        Daalmans daa = new Daalmans(daalmansSystem);
        daa.run();
        System.out.println(daa);
    }

    @Test
    public void test100() throws ProblemeSansVariablesException, LpSolveException, TypeInegaliteInvalideException, NonResoluException {
        int sum = 0;
        int[] nb = new int[15];

        while (sum != 100) {
            SystemGenerator s = new SystemGenerator(-1, 1);
            while (!s.solveExist()) {
                s = new SystemGenerator(-1, 1);

            }
            sum += 1;
            final LCSystem system = new LCSystem(s.getPb(), s.getSolve());
            System.out.println(system);

            final LCSystem gaussSystem = system.clone();
            final LCSystem daalmansSystem = system.clone();

            PivotGauss pg = new PivotGauss(gaussSystem);
            pg.run();
            System.out.println(pg);

            Daalmans daa = new Daalmans(daalmansSystem);
            daa.run();
            System.out.println(daa);

            int sum1EQ = 0;
            int sum2EQ = 0;
            for (int a : gaussSystem.getIneqTypes()) {
                if (a == LpSolve.EQ)
                    sum1EQ += 1;
            }
            for (int a : daalmansSystem.getIneqTypes()) {
                if (a == LpSolve.EQ)
                    sum2EQ += 1;
            }
            if (sum1EQ > sum2EQ)
                nb[0] += 1;
            else if (sum2EQ > sum1EQ)
                nb[1] += 1;
            else {
                int sum1_0 = 0;
                int sum2_0 = 0;
                for (int i = 0; i < gaussSystem.getMatrix().rowCount(); i++) {
                    for (int j = 0; j < gaussSystem.getMatrix().columnCount(); j++) {
                        if (gaussSystem.getMatrix().get(i, j) == 0)
                            sum1_0 += 1;
                    }
                }
                for (int i = 0; i < gaussSystem.getMatrix().rowCount(); i++) {
                    for (int j = 0; j < gaussSystem.getMatrix().columnCount(); j++) {
                        if (gaussSystem.getMatrix().get(i, j) == 0)
                            sum2_0 += 1;
                    }
                }
                if (sum1_0 > sum2_0 && gaussSystem.getMatrix().rowCount() < daalmansSystem.getMatrix().rowCount())
                    nb[0] += 1;
                else if (sum1_0 < sum2_0 && gaussSystem.getMatrix().rowCount() > daalmansSystem.getMatrix().rowCount())
                    nb[1] += 1;
                else {
                    nb[3] += 1; //nb[1] += 1;
                }
            }
        }
        System.out.println(nb[0] + "//" + nb[1] + "\\\\" + nb[3]);
    }
}
