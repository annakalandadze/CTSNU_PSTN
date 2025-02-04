package it.univr.di.cstnu.matlabplugin;

import com.mathworks.engine.EngineException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.univr.di.cstnu.util.OptimizationEngine;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements {@link OptimizationEngine} to offer a method for solving non-linear optimization problem.
 * It requires to access to the MatLab R2024b runtime library.
 * <br>
 * {@link OptimizationEngine} objects are necessary for using some methods of the class {@link it.univr.di.cstnu.algorithms.PSTN}.
 */
public final class MatLabEngine implements AutoCloseable, OptimizationEngine {
	/**
	 * class logger
	 */
	static final Logger LOG = Logger.getLogger(MatLabEngine.class.getName());

	/**
	 * Test.
	 *
	 * @param args not used
	 *
	 * @throws InterruptedException if MatLab has problem.
	 * @throws ExecutionException   if MatLab has problem.
	 */
	public static void main(String[] args) throws ExecutionException, InterruptedException {
		//Start MATLAB synchronously
		final MatLabEngine eng = new MatLabEngine(null);

		/*
		 * Example:  If there were 2 cont links, then there are four variables
		 * [x1,y1,x2,y2].
		 * A linear constraint might be:
		 * 2*x1 - 2*y1 + x2 - y2 >= -12
		 * <p>
		 * Given that fmincon solves A⋅x ≤ b,
		 * Thus, A = [-2, 2, -1, 1]  and b = [12]
		 */

//			int[] x = {1,3,1,10};//x1,y1,x2,y2
		final double[] x = {1000, 3000, 1000, 10000};//x1,y1,x2,y2
		final double[][] A = {{-2, 2, -1, 1}};
//			final int[] b = {12};
		final double[] b = {12000};
//			final double[] mu = {.65, 1.6};
		final double[] mu = {7.59, 8.58};
//			final double[] sigma = {.09, .22};
		final double[] sigma = {.022, .058};

		final OptimizationResult result = eng.nonLinearOptimization(x, A, b, mu, sigma);
		System.out.println("Solution: " + Arrays.toString(result.solution()));
		System.out.println("Min value: " + result.optimumValue());
		System.out.println("Exit Flag: " + result.exitFlag());
		eng.close();
	}

	/**
	 * Connector to MatLab Engine. It is safe not to share.
	 */
	private final com.mathworks.engine.MatlabEngine eng;
	/**
	 * Records if the objective function was already defined.
	 */
	private boolean objFuncDefined;

	/**
	 * Default constructor
	 *
	 * @param engine MatLab eng to re-suse. If null, this constructor initializes one.
	 */
	@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "I prefer to suppress these FindBugs warnings")
	public MatLabEngine(com.mathworks.engine.MatlabEngine engine) {
		if (engine == null) {
			try {
				this.eng = com.mathworks.engine.MatlabEngine.startMatlab();
			} catch (EngineException e) {
				System.err.println("MatLab engine cannot be started. Please, adjust the error: " + e.getMessage());
				throw new RuntimeException(e.getMessage());
			} catch (InterruptedException e) {
				System.err.println("MatLab engine was interrupted. Please, adjust the error: " + e.getMessage());
				throw new RuntimeException(e.getMessage());
			}
		} else {
			this.eng = engine;
		}
		if (Debug.ON)
			LOG.info("MatLab is connected.");
		this.objFuncDefined = false;
	}

	/**
	 * Default constructor
	 */
	public MatLabEngine() {
		this(null);
	}

	@Override
	public void close() {
		try {
			this.eng.disconnect();
		} catch (EngineException e) {
			LOG.severe("Error (that I will ignore) while disconnecting MatLab: " + e.getMessage());
		}
		if (Debug.ON)
			LOG.info("MatLab is disconnected.");
	}

	/**
	 * Finds a minimum value of the non-linear problem specified by
	 * <pre>
	 * min_x f(x) such that
	 *                   A⋅x ≤ b,
	 *                   lb ≤ x ≤ ub.
	 * </pre>
	 * b is a vector, A is a matrix, and f(x) is a function that returns a scalar. f(x) can be a nonlinear function. x, lb, and ub can be passed as vectors or
	 * matrices;
	 * <br>
	 * This method uses the MatLab Sequential Quadratic Programming (SQP) method.
	 *
	 * @param x     initial solution. Must be double
	 * @param A     constraint coefficients. Each row represents the constraint coefficient relative a negative cycle. Must be double
	 * @param b     negative cycle values. Must be double.
	 * @param mu    means of log-normal distributions of contingent link
	 * @param sigma std. dev. of log-normal distributions of contingent link
	 *
	 * @return a #OptimizationResult object representing the optimal solution
	 *
	 */
	public OptimizationResult nonLinearOptimization(double[] x, double[][] A, double[] b, double[] mu, double[] sigma)
		throws ExecutionException, InterruptedException {

		/*
		 * Put variables sigma and mu in MATLAB workspace because they are used inside the objective function
		 */
		eng.putVariable("mu", mu);
		eng.putVariable("sigma", sigma);

		final int n = x.length;
		/*
		 * Lower bound.
		 * For a variable corresponding to a lower bound of a contingent, it is = x
		 * For a variable corresponding to an upper bound of a contingent, it is = e^mu
		 * Upper bound.
		 * For a variable corresponding to a lower bound of a contingent, it is = e^mu
		 * For a variable corresponding to an upper bound of a contingent, it is = x
		 */
		final double[] lb = new double[n];
		final double[] ub = new double[n];
		for (int i = 0, j = 1; i < n; i += 2, j = i + 1) {
			lb[i] = x[i];
			ub[j] = x[j];
			final double mean = Math.exp(mu[i / 2]);
			lb[j] = ub[i] = mean;
			if (Debug.ON) {
				if (LOG.isLoggable(Level.FINEST)) {
					LOG.finest("Bounds associated to contingent link " + i / 2 + ": "
					           + lb[i] + " ≤ x_" + (i) + " ≤ " + ub[i] + "; "
					           + lb[j] + " ≤ x_" + (j) + " ≤ " + ub[j]);
				}
			}
		}

//		eng.eval("currentdir = pwd");
//		System.out.println((String) eng.getVariable("currentdir"));

		/*
		 * MatLAb nonlinear optimization.
		 * [x,fval,exitFlag] = fmincon(fun,x0,A,b,Aeq,beq,lb,ub,nonlcon,options)
		 *              - fun is the function to minimize
		 *              - x0 is the initial solution
		 *              - A is the constraint coefficients on x
		 *              - b is the constraint upper bounds ==>  A⋅x ≤ b
		 *              - Aeq is the constraint coefficients on x for equality constraints
		 * *                if there is no equality constraint, set an empty array
		 *              - beq is the constraint upper bounds for equality constraints ==>  A⋅x = b
		 *                  if there is no equality constraint, set an empty array
		 *              - lb, and up are the lower and upper bounds to solution x ==> lb ≤ x ≤ ub.
		 *                  If x(i) is unbounded below, set lb(i) = -Inf, and if x(i) is unbounded above, set ub(i) = Inf.
		 *              - nonlcon are non-linear constraints on x
		 *                  if there is no equality constraint, set an empty array
		 *              options is a record for tuning the optimization phase
		 * <p>
		 *  Some MATLAB® functions accept a 2-D array as a single input argument and use the columns of the array separately.
		 *  By default, if you pass a 2-D array to a MATLAB from Java®, the array is split into separate arguments along the second dimension. To prevent this issue, cast the 2-D array to Object:
		 *  double[][] data = {{1.0, 2.0, 3.0}, {-1.0, -2.0, -3.0}};
		 *  HandleObject[] h = eng.feval("plot", (Object) data);
		 */
		eng.eval("options = optimoptions('fmincon','Algorithm','sqp', 'ObjectiveLimit',-1.1,'Display','none');");//'Display','iter',
		final Object options = eng.getVariable("options");
//		System.out.println(options.getClass());
		//represent the user defined objective function (defined in file 'objective.m') as java variable 'objFunc'.

		if (!this.objFuncDefined) {
			this.makeObjectiveFunction();
		}
		eng.eval("obj = @(x) objective(x, mu, sigma);");
		final Object objFunc = eng.getVariable("obj");

		final Object[] results = eng.feval(3, "fmincon", objFunc, x, A, b, new double[0], new double[0], lb, ub, new double[0], options);
		final double[] xNew = (double[]) results[0];
		final double optimum = (double) results[1];
		final double exitFlag = (double) results[2];
		if (Debug.ON) {
			if (LOG.isLoggable(Level.FINEST)) {
				LOG.finest("Value using objective: "+ ((Double) eng.feval("objective",xNew,mu,sigma)));
			}
		}
//		com.mathworks.matlab.types.Struct info = (com.mathworks.matlab.types.Struct) results[3];
//		System.out.println("Info:");
//		for(Map.Entry<String, Object> entry: info.entrySet()) {
//			System.out.println("\t"+ entry.getKey()+":" + entry.getValue());
//		}
		if (Debug.ON) {
			if (LOG.isLoggable(Level.FINEST)) {
				LOG.finest("Solution: " + Arrays.toString(xNew) + "\n" + "Value: " + optimum + "\n" + "Exit flag: " + exitFlag);
			}
		}
		return new OptimizationResult(xNew, optimum, (int) exitFlag);
	}

	/**
	 * MATLAB requires that the user function must be defined in a local file. This method writes a tmp file putting the definition of the user objective
	 * function used by {@link #nonLinearOptimization(double[], double[][], double[], double[], double[])} and set the search path to find it.
	 * <br>
	 * The MatLab function is defined as {@code objective(x, mu, sigma)}.
	 * <br>
	 * It is the product of the differences of CDFs associated to considered contingent links.<br> {@code x} is the vector containing the current bounds of each
	 * contingent link.<br> If there are 3 contingent links (x1,y1), (x2,y2), (x3,y3), x = [x1,y1,x2,y2,x3,y3].<br> {@code mu} is a vector representing the
	 * location of the CDFs associated to contingent links.<br> {@code sigma} is a vector representing the std.dev. of the CDFs associated to contingent links.
	 */
	private void makeObjectiveFunction() throws ExecutionException, InterruptedException {

		if (this.objFuncDefined) {
			return;
		}
		final String matLabObjectiveFunction = """
		                                       function obj = objective(x,mu,sigma)
		                                       	% Objective function: product of differences of CDFs x is the vector containing the current bounds of each contingent link.
		                                           % For exaple, if there are 3 contingent links (x1,y1), (x2,y2), (x3,y3), x = [x1,y1,x2,y2,x3,y3]
		                                           % mu contains the mean of the log-normal distributions associated to contingent links
		                                           % sigma contains the std.dev of the log-normal distributions associated to contingent links
		                                               k = length(mu); %number of contingent links
		                                               obj = double(-1); % Negated for having the maximization
		                                               for i = [1:k]
		                                                   cdfU = logncdf(x(2*i), mu(1,i), sigma(1,i));
		                                                   cdfL = logncdf(x(2*i-1), mu(1,i), sigma(1,i));
		                                                   obj = obj * (cdfU - cdfL);
		                                               end
		                                       end
		                                       """;

		/*
		 * The path for searching 'objective.m' file that contains the objective function is the system tmp one.
		 */
		final String tmp = System.getProperty("java.io.tmpdir");
		if (Debug.ON) {
			if (LOG.isLoggable(Level.FINEST)) {
				LOG.finest("Tmp dir: " + tmp);
			}
		}

		/*
		 * Write the file "objective.m"
		 */
		try {
			final File objectiveFile = new File(tmp, "objective.m");
			final PrintWriter fileWriter = new PrintWriter(objectiveFile, StandardCharsets.UTF_8);
			fileWriter.println(matLabObjectiveFunction);
			fileWriter.flush();
			fileWriter.close();
			if (Debug.ON) {
				if (LOG.isLoggable(Level.FINEST)) {
					LOG.finest("Written file " + objectiveFile.getAbsolutePath());
				}
			}
		} catch (IOException e) {
			LOG.severe("It is not possible to create a necessary MatLab file: " + e.getMessage());
			throw new RuntimeException(e);
		}
		eng.eval("cd '" + tmp + "'");
		this.objFuncDefined = true;
	}

}
