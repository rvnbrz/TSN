package TSN;


import com.google.ortools.constraintsolver.Solver;

class ORSolver {

    Solver solver;
    Solution Current;
    DataUnloader dataUnloader = new DataUnloader();
    methods chosenmethod;
    boolean DebogMode;
    
    
	static { System.loadLibrary("jniortools");}

	public ORSolver(methods _chosenmethod, Solution current, boolean debogstat) {
		setSolution(current);
		DebogMode = debogstat ;
		chosenmethod = _chosenmethod ;
	
	}
	private void setSolution(Solution current) {
		Current = current;
	}
	public enum methods {
		Romon,
		Niklas,
		Silviu,
		Jorge,
		Reza
	}
	public void Run() {

	    solver = new Solver(chosenmethod.toString());
	    SolutionMethod method = null;
	    
	    switch (chosenmethod) {
		case Romon:
			method = new Romon(solver);
			break;
		case Niklas:
			method = new Niklas(solver);
			break;
		case Silviu:
			method = new Silviu(solver);
			break;
		case Jorge:
			method = new Jorge(solver);
			break;
		default:
			method = new Silviu(solver);

		}
	    
	    method.Initialize(Current);	
		method.addConstraints();	
		method.addCosts();
		method.addDecision();		
		method.addSolverLimits();
		
    	    
	    
	    Solution OptSolution = null;
	    long start=System.currentTimeMillis();
	    while (solver.nextSolution()) { 
	    	OptSolution = method.cloneSolution();
	    	if(DebogMode) {
	    		dataUnloader.WriteData(OptSolution, chosenmethod.toString(), method.getSolutionNumber());
	    	}
			
	    	if(method.Monitor(start)) {
	    		break;
	    	}

	    }
	    solver.endSearch();
	    long end=System.currentTimeMillis();
	    int duration = (int) (end - start);
	    
	    if(OptSolution != null) {
	    	if(!DebogMode) {
	    		dataUnloader.WriteData(OptSolution, chosenmethod.toString(), method.getSolutionNumber());
	    	}
			
		    dataUnloader.CreateReport(duration);
	    }


	    // Statistics
	    System.out.println();
	    System.out.println("Solutions: " + solver.solutions());
	    System.out.println("Failures: " + solver.failures());
	    System.out.println("Branches: " + solver.branches());
	    System.out.println("Wall time: " + solver.wallTime() + "ms");

	}
}
