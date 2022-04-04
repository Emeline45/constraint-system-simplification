package model;

import exceptions.problems.ProblemeSansVariablesException;
import exceptions.problems.TailleLigneInvalideException;
import exceptions.problems.TypeInegaliteInvalideException;
import lpsolve.LpSolveException;
import model.simplification.Daalmans;
import model.simplification.PivotGauss;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import random.SystemGenerator;

import static model.MLOProblem.*;

public class MLOProblemTest {
    private static final double DELTA = 0.00000001;

    @Test
    public void test1() throws LpSolveException, TypeInegaliteInvalideException, ProblemeSansVariablesException {
        final MLOProblem pb = new MLOProblem(2)
                .withObjective("2 -6")
                .withConstraint("4 -1", GE, "4")
                .withConstraint("1 -1", LE, "4")
                .withConstraint("1 1", LE, "10")
                .withConstraint("1 1", GE, "5")
                .withConstraint("1 0", GE, "2")
                .withConstraint("0 1", GE, "2");
        final double solution = pb.solve();

        final LCSystem system = new LCSystem(pb, solution);
        System.out.println(system);

        final LCSystem gaussSystem = system.clone();
        final LCSystem daalmansSystem = system.clone();

        PivotGauss pg = new PivotGauss(gaussSystem);
        pg.applicationPivotGauss();
        System.out.println(pg);

        Daalmans daa = new Daalmans(daalmansSystem);
        daa.run();
        System.out.println(daa);

        // résultat attendu : -37.60000000
        Assertions.assertEquals(-37.6, solution, DELTA);
    }

    @Test
    public void test2() throws LpSolveException, TypeInegaliteInvalideException, ProblemeSansVariablesException {
        final MLOProblem pb = new MLOProblem(2)
                .withObjective("-1 1")
                .withConstraint("2 -1", GE, "-2")
                .withConstraint("1 -1", LE, "2")
                .withConstraint("1 1", LE, "5")
                .withConstraint("1 0", GE, "0")
                .withConstraint("0 1", GE, "0");
        final double solution = pb.solve();

        final LCSystem system = new LCSystem(pb, solution);
        System.out.println(system);

        final LCSystem gaussSystem = system.clone();
        final LCSystem daalmansSystem = system.clone();

        PivotGauss pg = new PivotGauss(gaussSystem);
        pg.applicationPivotGauss();
        System.out.println(pg);

        Daalmans daa = new Daalmans(daalmansSystem);
        daa.run();
        System.out.println(daa);

        // résultat attendu -2.00000000
        Assertions.assertEquals(-2, solution, DELTA);
    }

    @Test
    public void test3() throws LpSolveException, TypeInegaliteInvalideException, ProblemeSansVariablesException {
        final MLOProblem pb = new MLOProblem(2)
                .withObjective("1 0")
                .withConstraint("1 1", GE, "1")
                .withConstraint("1 -1", LE, "2")
                .withConstraint("0 1", LE, "2")
                .withConstraint("1 0", LE, "3")
                .withConstraint("1 0", GE, "0")
                .withConstraint("0 1", GE, "0");
        final double solution = pb.solve();

        final LCSystem system = new LCSystem(pb, solution);
        System.out.println(system);

        final LCSystem gaussSystem = system.clone();
        final LCSystem daalmansSystem = system.clone();

        PivotGauss pg = new PivotGauss(gaussSystem);
        pg.applicationPivotGauss();
        System.out.println(pg);

        Daalmans daa = new Daalmans(daalmansSystem);
        daa.run();
        System.out.println(daa);

        // résultat attendu : 0
        Assertions.assertEquals(0, solution, DELTA);
    }

    @Test
    public void test4() throws LpSolveException, TypeInegaliteInvalideException, ProblemeSansVariablesException {
        final MLOProblem pb = new MLOProblem(6)
                .withObjective("-2 -3 -1 1 -6 -7")
                .withConstraint("1 0 0 0 0 0", GE, "0.00002")
                .withConstraint("-2 -3 -1 1 -6 -7", GE, "-5");
        final double solution = pb.solve();

        final LCSystem system = new LCSystem(pb, solution);
        System.out.println(system);

        final LCSystem gaussSystem = system.clone();
        final LCSystem daalmansSystem = system.clone();

        PivotGauss pg = new PivotGauss(gaussSystem);
        pg.applicationPivotGauss();
        System.out.println(pg);

        Daalmans daa = new Daalmans(daalmansSystem);
        daa.run();
        System.out.println(daa);

        // résultat attendu : -5.00000000
        Assertions.assertEquals(-5, solution, DELTA);
    }

    @Test
    public void test5() throws LpSolveException, TypeInegaliteInvalideException, TailleLigneInvalideException, ProblemeSansVariablesException {
        final MLOProblem pb = new MLOProblem(2)
                .withObjective("1 3")
                .withConstraint("1 3", GE, "9")
                .withConstraint("1 0", LE, "8")
                .withConstraint("3 -1", GE, "-3")
                .withConstraint("0 1", LE, "6")
                .withVarTypes(VarType.REAL, VarType.INT);
        final double solution = pb.solve();

        final LCSystem system = new LCSystem(pb, solution);
        System.out.println(system);

        final LCSystem gaussSystem = system.clone();
        final LCSystem daalmansSystem = system.clone();

        PivotGauss pg = new PivotGauss(gaussSystem);
        pg.applicationPivotGauss();
        System.out.println(pg);

        Daalmans daa = new Daalmans(daalmansSystem);
        daa.run();
        System.out.println(daa);

        // résultat attendu : 9
        Assertions.assertEquals(9, solution, DELTA);

    }
}
