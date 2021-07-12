package component.controller.impl;

import common.STATUS;
import common.TYPE;
import component.resource.Resource;
import component.skill.Skill;
import component.variable.Variable;
import component.variable.impl.Order;
import component.variable.impl.Task;

import java.util.*;


public class FixedMultiorderTaskSchedulingController extends TaskSchedulingResourceAllocatingVariableController {

	private List<Variable> joinOrders(List<Order> orders) {
		List<Variable> variables = new ArrayList<>();
		for (Order order : orders) {
			List<Variable> orderVariables = order.getTasks();
			variables.addAll(orderVariables);
		}
		return variables;
	}

	@Override
	public List<Variable> setupVariables(Map<Object, Object> parameters, double k) {


		List<Order> orders = new ArrayList<>();
		double[] orderWeights = (double[]) parameters.get("weights");

		int[][] tasksRelation = (int[][]) parameters.get("tasks");
		for (int i = 0; i < orderWeights.length; i++) {
			Order order = new Order(i, orderWeights[i]);
			// sua lai thanh task copy
			List<Variable> newTasks = setupTemplateVariablesForOrders(parameters, k);

			int maxDuration = (int) parameters.get("maxDuration");
			for (Variable newTask : newTasks) {
				// set the duration according to weights
				double duration = ((Task) newTask).getDuration() * orderWeights[i];
				((Task) newTask).setDuration(Math.round(duration) * 100 / 100);
				((Task) newTask).getPredecessors().clear();
				((Task) newTask).getDescendants().clear();
			}

			// set lai neighbor
			newTasks = super.setupTemplateVariablesNeighbours(newTasks, tasksRelation);

			// tinh tgian cho task
			for (Variable newTask : newTasks) {
				super.setVariableTime(newTask, newTasks, k * maxDuration);
			}

			order.setTasks(newTasks);
			orders.add(order);
		}


		// Sort according to task
		Collections.sort(orders);

		// Calculate time for the tasks in orders
		orders = setVariablesTimesAccordingToOtherOrders(orders, k);

		return joinOrders(orders);
	}

	private List<Variable> setupTemplateVariablesForOrders(Map<Object, Object> parameters, double k) {
		List<Variable> tasks = super.setupVariables(parameters, k);

		double[] humanCosts = (double[]) parameters.get("humanCosts");
		double[] machineCosts = (double[]) parameters.get("machineCosts");
		double[][] mreq = (double[][]) parameters.get("mreq");
		tasks = setupMachineResources(tasks, (Integer) parameters.get("numberOfMachineResources"), machineCosts, mreq);
		tasks = setupHumanResources(tasks, humanCosts);
		return tasks;
	}

	private List<Variable> setupHumanResources(List<Variable> tasks, double [] humanCosts) {
		for (Variable variable : tasks) {
			Task task = (Task) variable;
			for (Resource resource : task.getRequiredHumanResources()) {
				resource.setCost(humanCosts[resource.getId()]);
			}
		}
		return tasks;
	}

	private List<Variable> createSimilarVariables(Map<Object, Object> parameters, List<Variable> tasks) {
		List<Variable> variables = super.createTemplateVariables(parameters);
		// set resource and skills
		variables = setupSimilarResourceAndSkillsForVariables(variables, tasks);
		return variables;
	}

	private List<Variable> setupSimilarResourceAndSkillsForVariables(List<Variable> currentVariables, List<Variable> copyVariables) {
		int numberOfTasks = currentVariables.size();


		for (int i = 0; i < numberOfTasks; i++) {
			List<Resource> copyResources = ((Task) copyVariables.get(i)).getRequiredHumanResources();
			List<Resource> newResources = new ArrayList<>();
			for (Resource resource : copyResources) {
				Resource newResource = Resource.builder()
						.id(resource.getId())
						.type(resource.getType())
						.cost(resource.getCost())
						.status(resource.getStatus())
						.build();
				List<Skill> skills = new ArrayList<>();
				for (Skill copySkill : resource.getSkills()) {
					Skill newSkill = Skill.builder()
							.id(copySkill.getId())
							.experienceLevel(copySkill.getExperienceLevel())
							.build();
					skills.add(newSkill);
				}
				newResources.add(newResource);
			}

			List<Resource> copyMachineResources = ((Task) copyVariables.get(i)).getRequiredMachinesResources();
			List<Resource> newMachineResources = new ArrayList<>();
			for (Resource copyMachineResource : copyMachineResources) {
				Resource newMachineResource = Resource.builder()
						.id(copyMachineResource.getId())
						.type(copyMachineResource.getType())
						.cost(copyMachineResource.getCost())
						.status(copyMachineResource.getStatus())
						.build();

				newMachineResources.add(newMachineResource);
			}
			((Task) currentVariables.get(i)).setRequiredMachinesResources(newMachineResources);
			((Task) currentVariables.get(i)).setRequiredHumanResources(newResources);
		}
		return currentVariables;
	}

	private List<Variable> setupMachineResources(List<Variable> tasks, Integer numberOfMachineResources, double[] machineCosts, double[][] mreq) {
		for (Variable variable : tasks) {
			List<Resource> machineResource = new ArrayList<>();
			int id = ((Task) variable).getId();
			for (int i = 0; i < numberOfMachineResources; i++) {
				if (mreq[id][i] != 0) {
					Resource resource = Resource.builder()
							.id(i)
							.type(TYPE.MACHINE)
							.status(STATUS.USEFUL)
							.cost(machineCosts[i])
							.build();
					machineResource.add(resource);
				}
			}
			((Task) variable).setRequiredMachinesResources(machineResource);
		}

		return tasks;
	}

	private List<Order> setVariablesTimesAccordingToOtherOrders(List<Order> orders, double k) {

		int size = orders.size();
		for (int i = 1; i < size; i++) {
			// viet ham tinh tgian dua vao previous order, khong tinh order dau tien
			List<Variable> currentOrderTasks = orders.get(i).getTasks();
			List<Variable> previousOrderTasks = orders.get(i - 1).getTasks();
			currentOrderTasks = setTasksTimesAccordingToPreviousOrder(currentOrderTasks, previousOrderTasks, k);
			orders.get(i).setTasks(currentOrderTasks);
		}
		return orders;
	}

	private List<Variable> setTasksTimesAccordingToPreviousOrder(List<Variable> currentOrderTasks, List<Variable> previousOrderTasks, double k) {

		int numberOfTasks = currentOrderTasks.size();

		for (int i = 0; i < numberOfTasks; i++) {
			Task currentOrderTask = ((Task) currentOrderTasks.get(i));
			Task previousOrderTask = ((Task) previousOrderTasks.get(i));
			Task currentOrderPrecedentTask = (Task) getMaxEndTimePrecedentTask(currentOrderTask, currentOrderTasks.subList(0, i + 1));

			double previousOrderTaskEndTime = previousOrderTask.getStart() + previousOrderTask.getDuration();
			double currentOrderPrecedentTaskEndTime = 0;

			if (currentOrderPrecedentTask != null) {
				currentOrderPrecedentTaskEndTime = currentOrderPrecedentTask.getStart() + currentOrderPrecedentTask.getDuration();
			}

			double currentOrderTaskStartTime = Math.max(previousOrderTaskEndTime, currentOrderPrecedentTaskEndTime);
			currentOrderTask.setStart(currentOrderTaskStartTime);

			double rand = Math.random() * k;
			double sign = Math.random() > 0.5 ? 1 : -1;
			double scheduledStart = currentOrderTaskStartTime + rand * sign;
			currentOrderTask.setScheduledTime(scheduledStart);

		}

		return currentOrderTasks;
	}

	private Variable getMaxEndTimePrecedentTask(Variable currentTask, List<Variable> subList) {
		List<Integer> precedentTasksIds = ((((Task) currentTask).getPredecessors()));
		List<Variable> precedentTasksObjects = getPrecedentTasksObjects(currentTask, precedentTasksIds);

		double maxEndTime = 0;
		Task maxEndTimeTask = null;

		for (Variable variable: subList) {
			Task task = (Task) variable;
			double endTime = task.getStart() + task.getDuration();
			if (maxEndTime < endTime) {
				maxEndTime = endTime;
				maxEndTimeTask = task;
			}
		}

		return maxEndTimeTask;
	}

}
