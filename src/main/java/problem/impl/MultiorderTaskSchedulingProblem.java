package problem.impl;

import common.STATUS;
import component.controller.VariableController;
import component.resource.HumanResource;
import component.resource.MachineResource;
import component.skill.Skill;
import component.variable.Variable;
import component.variable.impl.Order;
import component.variable.impl.Task;
import representation.Solution;

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
	 * numberOfHumanResources: 	int			number of resources in this problem
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

	/**
	 * First objective: Time
	 * Second objective: Quality
	 * Third objective: Cost (human cost + machine cost + penalty cost)
	 * */
	@Override
	public Solution evaluate(Solution solution) {
		solution = evaluateIdleDuration(solution);
		solution = evaluateQuality(solution);
		solution = evaluateTotalCost(solution);
		return solution;
	}



	/**
	 * Counts the total idle time between tasks in one order and between orders
	 *
	 * */
	public Solution evaluateIdleDuration(Solution solution) {

		List<Variable> variables = solution.getVariables();

		double totalDelay = 0;
		int variableSize = variables.size();

		for (int i = 0; i < variableSize; i++) {
			List<Task> tasks = (List<Task>) variables.get(i).getValue();
			totalDelay += calculateEachOrderIdleTime(tasks);
			if (i != 0) {
				totalDelay += calculateElapsedTimeUntilNextOrder(variables.get(i - 1), variables.get(i));
			}
		}
//		solution.getObjectives()[0] = totalDelay/variableSize;
		solution.getObjectives()[0] = totalDelay;

		return solution;
	}

	private double calculateElapsedTimeUntilNextOrder(Variable variable, Variable variable1) {
		Task firstTask1 = ((List<Task>) variable.getValue()).get(0);
		Task firstTask2 = ((List<Task>) variable1.getValue()).get(0);

		double start1 = firstTask1.getStart();
		double start2 = firstTask2.getStart();
		double delay = start1 - start2;
		return delay > 0 ? delay : 0;
	}

	/**
	 * Calculate the idle time between the scheduled start and the actual start of each task in the same order
	 * */
	private double calculateEachOrderIdleTime(List<Task> tasks) {
		double delay = 0;
		for (Task task: tasks) {
			double idle = task.getScheduledTime() - task.getStart();
			idle = idle <= 0 ? 0 : (1/(1 + idle));
			delay += idle;
			task.setIdle(idle);
		}

		return delay;
	}

	/**
	 * Evaluate the total cost for each order based on its human cost, machine cost and penalty cost when finishing time is after deadline
	 * */
	public Solution evaluateTotalCost(Solution solution) {
		List<Variable> variables = solution.getVariables();
		double totalCostAllOrders = 0.0;

		for (Variable variable: variables) {
			Order order = (Order) variable;
			List<Task> tasks = order.getTasks();
			double totalCost = 0;

			for(Task task: tasks) {

				double taskHumanCost = calculateHumanCost( task.getRequiredHumanResources(), task.getDuration());

				double taskMachineCost = calculateMachineCost(task.getRequiredMachinesResources(), task.getDuration());

				double totalTimeSpent = order.getTotalTimeSpent();
				double totalTimeAllowed = order.getTotalTimeAllowed();
				double penaltyCost = totalTimeSpent <= totalTimeAllowed ? 0 : (totalTimeSpent - totalTimeAllowed);

				penaltyCost *= order.getPenaltyRate();

				totalCost += taskHumanCost + taskMachineCost + penaltyCost;
			}
			totalCostAllOrders += totalCost;
			order.setTotalCost(totalCost);
		}
		solution.getObjectives()[2] = totalCostAllOrders;
		return solution;
	}


	private double calculateMachineCost(List<MachineResource> machineResources, double duration) {
		double taskMachineCost = 0;
		for (MachineResource machine: machineResources) {
			if (machine.getStatus() == STATUS.ASSIGNED) {
				double machineCost = machine.getCost() * duration * machine.getConsumeFactor();
				taskMachineCost += machineCost;
			}
		}
		return taskMachineCost;
	}
	private double calculateHumanCost(List<HumanResource> resources, double duration) {
		double taskHumanCost = 0;
		for (HumanResource resource: resources) {
			if (resource.getStatus() == STATUS.ASSIGNED) {
				double resourceCost = resource.getCost();
				double totalLexp = 0;
				List<Skill> skills = resource.getSkills();
				for (Skill skill: skills) {
					totalLexp += skill.getExperienceLevel();
				}
				taskHumanCost += resourceCost * totalLexp;
			}
		}
		return taskHumanCost * duration;
	}

	/**
	 * This function evaluates the quality of each order in a solution based on the order's complete time against its deadline
	 * @param solution	The Solution to be evaluated
	 *
	 *
	 * */
	private Solution evaluateQuality(Solution solution) {
		List<Variable> variables = solution.getVariables();
		double timeQuality = 0;
		for (Variable variable: variables) {
			Order order = (Order) variable;
			double totalTimeSpent = order.getTotalTimeSpent();
			double totalTimeAllowed = order.getTotalTimeAllowed();
			if (totalTimeAllowed == 0 || totalTimeSpent == 0)
				continue;
			timeQuality += totalTimeSpent <= totalTimeAllowed ? 1 : (totalTimeAllowed/totalTimeSpent);
		}
		solution.getObjectives()[1] = timeQuality/variables.size();
		return solution;
	}


	@Override
	public double[] evaluateConstraints(Solution solution) {
		double[] constraints = new double[2];
		constraints[0] = evaluateResourceContraints(solution);
		constraints[1] = evaluateTimeConstraints(solution);
		return constraints;
	}

	private double evaluateTimeConstraints(Solution solution) {
		boolean isValidTime = true;
		List<Variable> variables = solution.getVariables();
		for (Variable variable: variables) {
			List<Task> tasks = (List<Task>) variable.getValue();
			if (!isValidTaskTimeContraints(tasks)) {
				isValidTime = false;
				break;
			}
		}
		return isValidTime == true ? 1 : 0;
		
	}

	private boolean isValidTaskTimeContraints(List<Task> tasks) {
		for (Task task: tasks) {
			if (task.getDuration() >= 8) {
				return false;
			}
		}
		return true;
	}

	private double evaluateResourceContraints(Solution solution) {
		int numberOfHumanResource = (int) this.parameters.get("numberOfHumanResources");
		int numberMachineResource = (int) this.parameters.get("numberOfMachineResources");

		double humanResourceConflict = this.humanResourceConflictHelper.evaluateResourceConflict(solution, numberOfHumanResource);
		double machineResourceConflict = this.machineResourceConflictHelper.evaluateResourceConflict(solution, numberMachineResource);

		return humanResourceConflict + machineResourceConflict;
	}

}
