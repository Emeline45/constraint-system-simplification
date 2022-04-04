package random;

import exceptions.problems.NonResoluException;
import exceptions.problems.ProblemeSansVariablesException;
import exceptions.problems.TypeInegaliteInvalideException;
import lpsolve.LpSolveException;
import model.LCSystem;
import model.simplification.Daalmans;
import model.simplification.PivotGauss;
import org.junit.jupiter.api.Test;

public class TestAllPossibleAlgo {

    @Test
    public void test() throws ProblemeSansVariablesException, LpSolveException, TypeInegaliteInvalideException, NonResoluException {
        SystemGenerator s = new SystemGenerator(-1, 1);
        while(!s.solveExist()) {
            s = new SystemGenerator(- 1, 1);

        }
        final LCSystem system = new LCSystem(s.getPb(), s.getSolve());
        System.out.println(system);

        final LCSystem gaussSystem = system.clone();
        final LCSystem daalmansSystem = system.clone();

        PivotGauss pg = new PivotGauss(gaussSystem);
        pg.applicationPivotGauss();
        System.out.println(pg);

        Daalmans daa = new Daalmans(daalmansSystem);
        daa.run();
        System.out.println(daa);
    }
}
