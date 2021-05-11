package problem.impl;

import component.Resource;
import component.SkillsInResource;
import component.Task;
import component.Variable;
import component.controller.VariableController;
import problem.Problem;
import solution.Solution;

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


	/**
	 * Evaluates the objective functions of a solution.
	 *
	 *
	 * @return solution
	 * */
	@Override
	public Solution evaluate(Solution solution) {
		solution = evaluateDuration(solution);
		solution = evaluateExperience(solution);
		solution = evaluateAssignment(solution);
		return solution;
	}

	@Override
	public double[] evaluateConstraints(Solution solution) {
		return new double[0];
	}

	public Solution evaluateDuration(Solution solution) {
		double delay = 0;
		for (Variable var: solution.getVariables()) {
			double idle = ((Task) var).getScheduledTime() - ((Task) var).getStart();
			idle = idle < 0 ? 0 : (1/(1 + idle));
			delay += idle;
			((Task) var).setIdle(idle);
		}
		solution.getObjectives()[0] = delay/solution.getVariables().size();
		return solution;
	}


	public Solution evaluateExperience(Solution solution) {
		// The total objective value
		double experience = 0;

		List<Variable> variables = solution.getVariables();
		for (Variable variable: variables) {
			// experience level for each task
			double treq = 0;

			List<SkillsInResource> skillsInResources = ((Task) variable).getRequiredSkillsInResources();

			// Get the number of skills
			int skillSize = skillsInResources.get(0).getRequiredSkills().size();
			// Get the number of assigned resources
			int numberOfAssignedResources = ((Task) variable).getNumberOfAssignedResources();
			for (int i = 0; i < skillSize; i++) {
				treq += ((Task) variable).getMaxExperienceForSkill(i) +
						numberOfAssignedResources * ((Task) variable).getTotalExperienceForSkill(i);
			}
			experience = treq/skillSize;
		}
		solution.getObjectives()[1] = experience/variables.size();
		return solution;
	}

	/**
	 * Evaluates the assignment objective function for this problem
	 * @param solution		Solution		Solution to be evaluated
	 * @return objective	double			The evaluation for this objective function
	 *
	 * */
	public Solution evaluateAssignment(Solution solution) {
		double assignment = 0;
		int resourceSize = ((Task) solution.getVariables().get(0)).getRequiredSkillsInResources().size();
		for (int i = 0; i < resourceSize; i++) {
			double resourceAssignmentCount = countResourceAssignedTimes(solution.getVariables(), i);
			double rjAssignment = 0;
			if (resourceAssignmentCount > 0) {
				rjAssignment = countResourceConflict(solution.getVariables(), i)/resourceAssignmentCount;
				assignment += rjAssignment;
			}
		}
		solution.getObjectives()[2] = assignment/resourceSize;
		return solution;
	}

	public double countResourceAssignedTimes(List<Variable> variables, int resourceId) {
		double count = 0;
		for (Variable variable: variables) {
			if (((Task) variable).getRequiredSkillsInResources().get(resourceId).getResource().getStatus() == Resource.STATUS.ASSIGNED)
				count++;
		}
		return count;
	}

	public double countResourceConflict(List<Variable> variables, int resourceId) {
		double count = 0;
		int variableSize = variables.size();
		int[] conflictMap = new int[variableSize];
		for (int i = 0; i < variableSize; i++) {
			for (int j = i + 1; j < variableSize; j++) {
				boolean isConflict = isResourceConflict(variables.get(i), variables.get(j), resourceId);
				if (isConflict) {
					conflictMap[i] = 1;
					conflictMap[j] = 1;
				}
			}
		}

		for (int i = 0; i < variableSize; i++) {
			if (conflictMap[i] == 1)
				count++;
		}
		return count;
	}

	public boolean isResourceConflict(Variable var1, Variable var2, int resourceId) {
		Resource resource1 = ((Task) var1).getRequiredSkillsInResources().get(resourceId).getResource();
		Resource resource2 = ((Task) var2).getRequiredSkillsInResources().get(resourceId).getResource();
		if (resource1.getStatus() == resource2.getStatus() && resource1.getStatus() == Resource.STATUS.ASSIGNED) {
			double start1 = ((Task) var1).getStart(); double end1 = ((Task) var1).getDuration() + start1;
			double start2 = ((Task) var1).getStart(); double end2 = ((Task) var1).getDuration() + start1;

			if ( (start1 < start2 && start2 < end1) || (start2 < start1 && start1 < end2)) {
				return true;
			}
		}
		return false;
	}

}
