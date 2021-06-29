package problem.impl;

import component.controller.VariableController;
import solution.Solution;

import java.util.Map;

public class MultiorderTaskSchedulingProblem extends TaskSchedulingResourceAllocatingProblem {
	/**
	 * This method will return a list of variables to the algorithm to find its solution
	 * parameters 		 	Map
	 * treq:				int[][] 		Task - Skill matrix
	 * lexp: 				double[][]		Resource - Skill matrix
	 * tasks: 				int[][]			Task matrix
	 * numberOfTasks: 		int				number of tasks in this problem
	 * numberOfHumanResources: 	int				number of resources in this problem
	 * numberOfSkills: 		int				number of skills in this problem
	 * scheduledTimes: 		List<Integer>	list of scheduled time for variables
	 * durations: 			List<Integer>	list of durations for variables
	 * numberOfObjectives:	int				the number of objectives needed to calculate for solutions.
	 * numberOfFitness:		int				the number of fitness for this solution
	 * maxDuration			int				maximum duration for all tasks
	 *
	 * @param params
	 * @param variableController
	 */
	public MultiorderTaskSchedulingProblem(Map<Object, Object> params, VariableController variableController) {
		super(params, variableController);
	}

	@Override
	public Solution evaluate(Solution solution) {
		solution = evaluateDuration(solution);
		solution = evaluateCost(solution);
		solution = evaluateAssignment(solution);
		return solution;
	}

	@Override
	public double[] evaluateConstraints(Solution solution) {
		return new double[0];
	}


	// TODO
	public Solution evaluateCost(Solution solution) {
		return solution;
	}


}
