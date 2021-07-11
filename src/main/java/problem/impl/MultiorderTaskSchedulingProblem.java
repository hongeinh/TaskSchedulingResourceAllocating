package problem.impl;

import component.controller.VariableController;
import component.resource.Resource;
import component.skill.Skill;
import component.variable.Variable;
import component.variable.impl.Task;
import solution.Solution;

import java.util.ArrayList;
import java.util.List;
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
	public Solution evaluateDuration(Solution solution) {
		List<Variable> variables = solution.getVariables();
		int id = ((Task) variables.get(variables.size() - 1)).getId();
		List<Variable> evaluatingVariables = getTimeEvaluatingVariables(variables, id);
		int numberOfOrders = evaluatingVariables.size();
		int numberOfTasks = variables.size()/numberOfOrders;
		double totalDelay = 0;

		for (int i = 0; i < numberOfOrders; i++) {
			double delay = calculateEachOrderIdleTime(variables.subList(i * numberOfTasks, (i + 1) * numberOfTasks + 1));
			totalDelay += delay;
		}

		solution.getObjectives()[0] = totalDelay/numberOfOrders;
		return solution;
	}

	// TODO
	public Solution evaluateCost(Solution solution) {
		List<Variable> variables = solution.getVariables();
		double totalCost = 0.0;

		for (Variable variable: variables) {
			double taskHumanCost = calculateHumanCost(((Task) variable).getRequiredSkillsInResources(), ((Task) variable).getDuration());
			double taskMachineCost = calculateMachineCost(((Task) variable).getRequiredMachinesResources(), ((Task) variable).getDuration());
			totalCost += taskHumanCost + taskMachineCost;
		}
		solution.getObjectives()[1] = totalCost;
		return solution;
	}

	private double calculateMachineCost(List<Resource> machineResources, double duration) {
		double taskMachineCost = 0;
		for (Resource machine: machineResources) {
			double machineCost = machine.getCost() * duration;
			taskMachineCost += machineCost;
		}
		return taskMachineCost;
	}
	private double calculateHumanCost(List<SkillsInResource> skillsInResources, double duration) {
		double taskHumanCost = 0;
		for (SkillsInResource skillsInResource: skillsInResources) {
			double resourceCost = skillsInResource.getResource().getCost();
			double totalLexp = 0;
			List<Skill> skills = skillsInResource.getRequiredSkills();
			for (Skill skill: skills) {
				totalLexp += skill.getExperienceLevel();
			}
			taskHumanCost += resourceCost * totalLexp;
		}
		return taskHumanCost;
	}

	private List<Variable> getTimeEvaluatingVariables(List<Variable> variables, int id) {
		List<Variable> evaluatingVariables = new ArrayList<>();
		evaluatingVariables.add((variables.get(variables.size() - 1)));

		for (Variable variable: variables) {
			if (((Task) variable).getId() == id) {
				evaluatingVariables.add(variable);
			}
		}

		return evaluatingVariables;
	}

	private double calculateEachOrderIdleTime(List<Variable> variables) {
		double delay = 0;
		for (Variable var: variables) {
			double idle = ((Task) var).getScheduledTime() - ((Task) var).getStart();
			idle = idle < 0 ? 0 : (1/(1 + idle));
			delay += idle;
			((Task) var).setIdle(idle);
		}

		return delay;
	}

}
