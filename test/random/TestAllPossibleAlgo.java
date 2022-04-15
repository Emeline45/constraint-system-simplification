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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestAllPossibleAlgo {
    private String getTimeFromNanos(final long nanos) {
        double nanos_ = (double) nanos;
        String currentUnit = "ns";
        if (Math.abs(nanos_) > 1000) {
            currentUnit = "µs";
            nanos_ /= 1000;
        }
        if (Math.abs(nanos_) > 1000) {
            currentUnit = "ms";
            nanos_ /= 1000;
        }
        if (Math.abs(nanos_) > 1000) {
            currentUnit = "s";
            nanos_ /= 1000;
        }

        return nanos_ + currentUnit;
    }

    @Test
    public void testAll() throws ProblemeSansVariablesException, LpSolveException, TypeInegaliteInvalideException, NonResoluException {
        SystemGenerator s;
        do {
            s = new SystemGenerator(-1, 1);
        } while (!s.solveExist());

        final LCSystem system = new LCSystem(s.getPb(), s.getSolve());
        Runner r = new Runner();

        List<Runner.RunStatus> results = r.run(system);

        final SystemComparator comp = new SystemComparator();
        results.sort((r1, r2) -> comp.compare(r1.finalSystem, r2.finalSystem));

        // TODO: généraliser à X systèmes
        // TODO: faire inf, sup, moyenne des temps d'exécution sur X systèmes
        // TODO: comparer globalement toutes les méthodes (nb fois meilleure, pire, équivalente, ...)
        // TODO: afficher les différences de temps entre chaque méthode
        // TODO: afficher le facteur déterminant de la comparaison (nb contraintes, nb 0, ...) ?r
        System.out.println("Sur le système :\n" + system);

        for (int i = 0; i < results.size(); ++i) {
            Runner.RunStatus status = results.get(i);

            StringBuilder msg = new StringBuilder();
            msg.append("Méthode ")
                    .append(status.order)
                    .append("\n")
                    .append("- Temps d'exécution : ")
                    .append(getTimeFromNanos(status.runtimeNanos))
                    .append("\n")
                    .append("- Comparaison détaillée :\n");

            for (int j = 0; j < results.size(); ++j) {
                if (j == i) continue;

                Runner.RunStatus status1 = results.get(j);

                int c = comp.compare(status1.finalSystem, status.finalSystem);

                msg.append("    - Contre méthode ")
                        .append(status1.order)
                        .append("\n")
                        .append("      ")
                        .append(c < 0 ? "Moins bonne simplification" : c == 0 ? "Simplification équivalente" : "Meilleure simplification")
                        .append("\n");
            }

            System.out.println(msg);
        }
//
//        for (Runner.RunStatus st : results) {
//            System.out.println(st);
//        }
    }

    @Test
    public void test1() throws ProblemeSansVariablesException, LpSolveException, TypeInegaliteInvalideException, NonResoluException {
        SystemGenerator s = new SystemGenerator(-1, 1);
        while(!s.solveExist()) {
            s = new SystemGenerator(- 1, 1);

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

        while (sum != 100){
            SystemGenerator s = new SystemGenerator(-1, 1);
            while (!s.solveExist()) {
                s = new SystemGenerator(-1, 1);

            }
            sum +=1;
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
            for (int a: gaussSystem.getIneqTypes()) {
                if(a == LpSolve.EQ)
                    sum1EQ += 1;
            }
            for (int a: daalmansSystem.getIneqTypes()) {
                if(a == LpSolve.EQ)
                    sum2EQ += 1;
            }
            if(sum1EQ > sum2EQ)
                nb[0] += 1;
            else if(sum2EQ > sum1EQ)
                nb[1] += 1;
            else{
                int sum1_0 = 0;
                int sum2_0 = 0;
                for(int i = 0; i < gaussSystem.getMatrix().rowCount(); i++){
                    for(int j = 0; j < gaussSystem.getMatrix().columnCount(); j++){
                        if(gaussSystem.getMatrix().get(i,j) == 0)
                            sum1_0 += 1;
                    }
                }
                for(int i = 0; i < gaussSystem.getMatrix().rowCount(); i++){
                    for(int j = 0; j < gaussSystem.getMatrix().columnCount(); j++){
                        if(gaussSystem.getMatrix().get(i,j) == 0)
                            sum2_0 += 1;
                    }
                }
                if(sum1_0 > sum2_0 && gaussSystem.getMatrix().rowCount() < daalmansSystem.getMatrix().rowCount())
                    nb[0] += 1;
                else if(sum1_0 < sum2_0 && gaussSystem.getMatrix().rowCount() > daalmansSystem.getMatrix().rowCount())
                    nb[1] += 1;
                else{
                    nb[3] += 1; //nb[1] += 1;
                }
            }
        }
        System.out.println(nb[0] + "//" + nb[1] + "\\\\" + nb[3]);
    }
}
