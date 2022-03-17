package tests;

import model.MLOProblem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MLOProblemTest {

    @Test
    public void test1(){
        MLOProblem pb = new MLOProblem(2);
        pb.addConstraint("4 -1", pb.GE, "4");
        pb.addConstraint("1 -1", pb.LE, "4");
        pb.addConstraint("1 1", pb.LE, "10");
        pb.addConstraint("1 1", pb.GE, "5");
        pb.addConstraint("1 0", pb.GE, "2");
        pb.addConstraint("0 1", pb.GE, "2");

        pb.setObjFun("2 -6");

        // résultat attendu : -37.60000000

        pb.getSolverLpSolve();
    }

    @Test
    public void test2(){
        MLOProblem pb = new MLOProblem(2);
        pb.addConstraint("2 -1", pb.GE, "-2");
        pb.addConstraint("1 -1", pb.LE, "2");
        pb.addConstraint("1 1", pb.LE, "5");
        pb.addConstraint("1 0", pb.GE, "0");
        pb.addConstraint("0 1", pb.GE, "0");

        pb.setObjFun("-1 1");

        // résultat attendu -2.00000000

        pb.getSolverLpSolve();
    }

    @Test
    public void test3(){
        MLOProblem pb = new MLOProblem(2);
        pb.addConstraint("1 1", pb.GE, "1");
        pb.addConstraint("1 -1", pb.LE, "2");
        pb.addConstraint("0 1", pb.LE, "2");
        pb.addConstraint("1 0", pb.LE, "3");
        pb.addConstraint("1 0", pb.GE, "0");
        pb.addConstraint("0 1", pb.GE, "0");

        pb.setObjFun("1 0");

        //résultat attendu : 0

        pb.getSolverLpSolve();
    }

    @Test
    public void test4(){
        MLOProblem pb = new MLOProblem(6);
        pb.addConstraint("1 0 0 0 0 0", pb.GE, "0.00002");
        pb.addConstraint("-2 -3 -1 1 -6 -7", pb.GE, "-5");

        pb.setObjFun("-2 -3 -1 1 -6 -7");

        // résultat attendu : -5.00000000

        pb.getSolverLpSolve();
    }

    @Test
    public void test5(){
        MLOProblem pb = new MLOProblem(2);
        pb.addConstraint("1 3", pb.GE, "9");
        pb.addConstraint("1 0", pb.LE, "8");
        pb.addConstraint("3 -1", pb.GE, "-3");
        pb.addConstraint("0 1", pb.LE, "6");

        pb.setObjFun("1 3");

        pb.getSolverLpSolve();
    }

}