import exceptions.problems.ProblemeSansVariablesException;
import exceptions.problems.TypeInegaliteInvalideException;
import lpsolve.LpSolveException;
import model.LCSystem;
import model.MLOProblem;
import model.simplification.Daalmans;
import model.simplification.PivotGauss;

import static model.MLOProblem.*;

public class Main {
    public static void main(String[] args) {
        try (MLOProblem pb = new MLOProblem(2)
                    .withObjective("2 -6")
                    .withConstraint("4 -1", GE, "4")
                    .withConstraint("1 -1", LE, "4")
                    .withConstraint("1 1", LE, "10")
                    .withConstraint("1 1", GE, "5")
                    .withConstraint("1 0", GE, "2")
                    .withConstraint("0 1", GE, "2")) {

            final double solution = pb.solve();

            final LCSystem system = new LCSystem(pb, solution);
            System.out.println(system);

            final LCSystem gaussSystem = system.clone();
            final LCSystem daalmansSystem = system.clone();

            PivotGauss pg = new PivotGauss(gaussSystem);
            pg.applicationPivotGauss();
            System.out.println(pg);

            System.out.println("------ Daalmans --------");
            Daalmans daa = new Daalmans(daalmansSystem);
            daa.run();
            System.out.println(daalmansSystem);
        } catch (LpSolveException | TypeInegaliteInvalideException | ProblemeSansVariablesException e) {
            e.printStackTrace();
        }
    }
}