import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import java.util.LinkedList;

/**
 * @authors Emeline BONTE, Ghilain BERGERON, Khaled SADEGH
 * @version 1.1
 */

public class MLOProblem {
    private LinkedList<String> values;
    private LinkedList<String> B;
    private LinkedList<Integer> types;
    private String objFun;
    private int nbVar;
    public static Integer LE = LpSolve.LE;
    public static Integer EQ = LpSolve.EQ;
    public static Integer GE = LpSolve.GE;

    /**
     * @author Raphaël Bagat
     * Constructor.
     * @param nbVar Nombre de variables du problème.
     */
    public MLOProblem(int nbVar){
        assert(nbVar>=0):"";
        values = new LinkedList<>();
        B = new LinkedList<>();
        types = new LinkedList<>();
        this.nbVar = nbVar;
    }

    /**
     * @author Raphaël Bagat
     * Ajout d'une contrainte au problème.
     * @param values A String that contains the value of the row. Format: v1 v2 v3 ...
     * @param type The type of the constraint. (Less than or equal LE, Equal EQ, Greater than or equal GE)
     * @param b A String that contains the value of the right hand side.
     */
    public void addConstraint(String values, int type, String b){
        B.addLast(b);
        this.values.addLast(values);
        types.addLast(type);
    }

    /**
     * @author Raphaël Bagat
     * Sets the objective function of the problem.
     * @param objFun A String that contains the value of the row.
     */
    public void setObjFun(String objFun){
        this.objFun = objFun;
    }

    /**
     * @author Raphaël Bagat
     * Gets the values of the constraints in a LinkedList. Format: v1 v2 v3 ...
     * @return The values of the constraints in a LinkedList. Format: v1 v2 v3 ...
     */
    public LinkedList<String> getValues() {
        return values;
    }

    /**
     * @author Raphaël Bagat
     * Gets the values of the right hand side vales.
     * @return The values of the right hand side values.
     */
    public LinkedList<String> getB() {
        return B;
    }

    /**
     * @author Raphaël Bagat
     * Gets the types of the constraints in a LinkedList.
     * @return The types of the constraints in a LinkedList.
     */
    public LinkedList<Integer> getTypes() {
        return types;
    }

    /**
     * @author Raphaël Bagat
     * Gets the objective function.
     * @return The objective function.
     */
    public String getObjFun() {
        return objFun;
    }

    /**
     * @author Raphaël Bagat
     * Gets the number of variables.
     * @return The number of variables.
     */
    public int getNbVar() {
        return nbVar;
    }

    /**
     * @author Raphaël Bagat
     * Gets the number of columns.
     * @return The number of columns.
     */
    public int getNbRows(){
        return B.size();
    }

    /**
     * Calcul de la solution optimale du problème
     * @return la solution optimale du problème
     */
    public double getSolverLpSolve(){
        double solve = 0;
        try {
            // Create a problem with nbVar variables and 0 constraints
            LpSolve solver = LpSolve.makeLp(0, this.nbVar);

            // add constraints
            for(int i = 0; i < values.size(); i++){
                solver.strAddConstraint(this.values.get(i), this.types.get(i), Double.parseDouble(this.B.get(i)));
            }

            // set objective function
            solver.strSetObjFn(this.objFun);

            // solve the problem
            solver.solve();
            solve = solver.solve();

            // print solution temp
            System.out.println("Value of objective function: " + solver.getObjective());
            double[] var = solver.getPtrVariables();
            for (int i = 0; i < var.length; i++) {
                System.out.println("Value of var[" + i + "] = " + var[i]);
            }

            // delete the problem and free memory
            solver.deleteLp();
        }
        catch (LpSolveException e) {
            e.printStackTrace();
        }
        return solve;
    }
}
