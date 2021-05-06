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

	/**
	 * TODO: Fix the return type to update idle time for each task in the solution
	 *
	 * */
	public double evaluateDuration(Solution solution) {
		double duration = 0;
		for (Variable var: solution.getVariables()) {
			double idle = ((Task) var).getScheduledTime() - ((Task) var).getStart();
			idle = idle < 0 ? 0 : (1/(1 + idle));
			duration += idle;
		}
		return duration/solution.getVariables().size();
	}


	public double evaluateAssignment(Solution solution) {
		List<Variable> variables = solution.getVariables();
		int [][] resourceAssignmentMatrix = this.calculateResourceAssignmentMatrix(variables);

		return 0;
	}


	public double evaluateExperience(Solution solution) {
		double treq = 0;

		return 0;
	}

	/*
	* TODO: If the relationship among components changes, need to change this code.
	* */
	public int[][] calculateResourceAssignmentMatrix(List<Variable> variables) {
		int [][] resourceAssignedMatrix = new int[variables.size()][((Task) variables).getResources().size()];
		for (Variable variable: variables) {
			for (Resource resource: ((Task) variable).getResources()) {
				if (resource.getStatus() == Resource.STATUS.ASSIGNED)
					resourceAssignedMatrix[((Task) variable).getId()][resource.getId()] = 1;
				else if (resource.getStatus() == Resource.STATUS.NOT_ASSIGNED)
					resourceAssignedMatrix[((Task) variable).getId()][resource.getId()] = 0;
			}
		}
		return resourceAssignedMatrix;
	}
}
