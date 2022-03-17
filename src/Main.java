public class Main {
    public static void main(String[] args) {
        test();
    }

    public static void test(){
        MLOProblem pb = new MLOProblem(2);
        pb.addConstraint("4 -1", pb.GE, "4");
        pb.addConstraint("1 -1", pb.LE, "4");
        pb.addConstraint("1 1", pb.LE, "10");
        pb.addConstraint("1 1", pb.GE, "5");
        pb.addConstraint("1 0", pb.GE, "2");
        pb.addConstraint("0 1", pb.GE, "2");

        pb.setObjFun("2 -6");

        pb.getSolverLpSolve();
    }
}