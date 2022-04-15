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
        try (MLOProblem pb = new MLOProblem(1)
                    .withObjective("0.2322118")
                    .withConstraint("-0.7304874", EQ, "-1.8154692")
                    .withConstraint("-0.9625044", LE, "0.0168881")
                    .withConstraint("-0.5639250", LE, "0.2804805")) {

            final double solution = pb.solve();

            final LCSystem system = new LCSystem(pb, solution);
            System.out.println(system);

            final LCSystem gaussSystem = system.clone();
            final LCSystem daalmansSystem = system.clone();

            PivotGauss pg = new PivotGauss(gaussSystem);
            pg.run();
            System.out.println(pg);

            //System.out.println("------ Daalmans --------");
            Daalmans daa = new Daalmans(daalmansSystem);
            daa.run();
            System.out.println(daa);
        } catch (LpSolveException | ProblemeSansVariablesException | TypeInegaliteInvalideException e) {
            e.printStackTrace();
        }
    }
}