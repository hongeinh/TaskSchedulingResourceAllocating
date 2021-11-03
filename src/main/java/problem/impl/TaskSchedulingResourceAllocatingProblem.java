package problem.impl;

import component.controller.VariableController;
import component.variable.Variable;
import component.variable.impl.Task;
import problem.Problem;
import problem.helper.HumanResourceConflictHelper;
import problem.helper.MachineResourceConflictHelper;
import representation.Solution;
import utils.TimeUtils;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;


public class TaskSchedulingResourceAllocatingProblem extends Problem {

	protected HumanResourceConflictHelper humanResourceConflictHelper = new HumanResourceConflictHelper();

	protected MachineResourceConflictHelper machineResourceConflictHelper = new MachineResourceConflictHelper();

	/**
	 * This method will return a list of variables to the algorithm to find its solution
	 * parameters 		 Map
	 *                   treq:				int[][] 		Task - Skill matrix
	 *                   lexp: 				double[][]		Resource - Skill matrix
	 *                   tasks: 			int[][]			Task matrix
	 *                   numberOfTasks: 	int				number of tasks in this problem
	 *                   numberOfHumanResources: int				number of resources in this problem
	 *                   numberOfSkills: 	int				number of skills in this problem
	 *                   scheduledTimes: 	List<Integer>	list of scheduled time for variables
	 *                   durations: 		List<Integer>	list of durations for variables
	 *                   numberOfObjectives:int				the number of objectives needed to calculate for solutions.
	 *                   numberOfFitness:	int				the number of fitness for this solution
	 *                   maxDuration		double				maximum duration for all tasks
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
		return new double[2];
	}

	public Solution evaluateDuration(Solution solution) {
		double delay = 0;
		List<Variable> variables = solution.getVariables();

		for (Variable var: solution.getVariables()) {
			List<Task> tasks = (List<Task>) var.getValue();
			for (Task task: tasks) {
				long idle = TimeUtils.calculateTimeDifferenceWithTimeUnit(task.getScheduledStartTime(), task.getStartTime(), ChronoUnit.MINUTES);
				idle = idle < 0 ? 0 : (1/(1 + idle));
				delay += idle;
				task.setIdle(idle);
			}

		}
		solution.getObjectives()[0] = delay/solution.getVariables().size();
		return solution;
	}


	public Solution evaluateExperience(Solution solution) {
//		// The total objective value
//		double experience = 0;
//
//		List<Variable> variables = solution.getVariables();
//		for (Variable variable: variables) {
//			// experience level for each task
//			double treq = 0;
//			List<Task> tasks = (List<Task>) variable.getValue();
//			for (Task task: tasks) {
//
//			}
//			List<HumanResource> skillsInResources = ((Task) variable).getRequiredHumanResources();
//
//			// Get the number of skills
//			int skillSize = skillsInResources.get(0).getSkills().size();
//			// Get the number of assigned resources
//			int numberOfAssignedResources = ((Task) variable).getNumberOfAssignedResources();
//			for (int i = 0; i < skillSize; i++) {
//				treq += ((Task) variable).getMaxExperienceForSkill(i) +
//						numberOfAssignedResources * ((Task) variable).getTotalExperienceForSkill(i);
//			}
//			experience = treq/skillSize;
//		}
//		solution.getObjectives()[1] = experience/variables.size();
		return solution;
	}

	/**
	 * Evaluates the assignment objective function for this problem
	 * @param solution		Solution		Solution to be evaluated
	 * @return objective	double			The evaluation for this objective function
	 *
	 * */
	public Solution evaluateAssignment(Solution solution) {
		int numberOfHumanResource = (int) this.parameters.get("numberOfHumanResources");
		int numberMachineResource = (int) this.parameters.get("numberOfMachineResources");

		double humanResourceConflict = this.humanResourceConflictHelper.evaluateResourceConflict(solution, numberOfHumanResource);
		double machineResourceConflict = this.machineResourceConflictHelper.evaluateResourceConflict(solution, numberMachineResource);

		solution.getObjectives()[2] = (humanResourceConflict + machineResourceConflict) / 2;

		return solution;
	}


}
