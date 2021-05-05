package problem.impl;

import component.Resource;
import component.Skill;
import component.Task;
import component.Variable;
import component.controller.VariableController;
import problem.Problem;
import solution.Solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TaskSchedulingResourceAllocatingProblem extends Problem {

	/**
	 * This method will return a list of variables to the algorithm to find its solution
	 * parameters 		 Map
	 *                   treq:				int[][] 		Task - Skill matrix
	 *                   lexp: 				double[][]		Resource - Skill matrix
	 *                   tasks: 			int[][]			Task matrix
	 *                   numberOfTasks: 	int				number of tasks in this problem
	 *                   numberOfResources: int				number of resources in this problem
	 *                   numberOfSkills: 	int				number of skills in this problem
	 *                   scheduledTimes: 	List<Integer>	list of scheduled time for variables
	 *                   durations: 		List<Integer>	list of durations for variables
	 *                   numberOfObjectives:int				the number of objectives needed to calculate for solutions.
	 *                   numberOfFitness:	int				the number of fitness for this solution
	 *                   maxDuration		int				maximum duration for all tasks
	 *
	 * */
	public TaskSchedulingResourceAllocatingProblem(Map<Object, Object> params, VariableController variableController) {
		super(params, variableController);
	}

	@Override
	public double[] evaluate(Solution solution) {
		double [] objectives = new double[(Integer) this.getParameters().get("numberOfObjectives")];
		objectives[0] = evaluateDuration(solution);
		objectives[1] = evaluateExperience(solution);
		objectives[2] = evaluateAssignment(solution);
		return new double[0];
	}

	@Override
	public double[] evaluateConstraints(Solution solution) {
		return new double[0];
	}

	public double evaluateDuration(Solution solution) {
		return 0;
	}
	public double evaluateAssignment(Solution solution) {
		return 0;
	}
	public double evaluateExperience(Solution solution) {
		return 0;
	}
}
