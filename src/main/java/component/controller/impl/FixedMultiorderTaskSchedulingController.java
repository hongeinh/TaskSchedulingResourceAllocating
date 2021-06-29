package component.controller.impl;

import component.resource.Resource;
import component.resource.SkillsInResource;
import component.variable.Variable;
import component.variable.impl.Order;
import component.variable.impl.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


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
	public List<Variable> setVariables(Map<Object, Object> parameters, double k) {
		List<Variable> tasks = super.setVariables(parameters, k);

		double [] humanCosts = (double []) parameters.get("humanCosts");
		double [] machineCosts = (double []) parameters.get("machineCosts");
		double[][] mreq = (double[][]) parameters.get("mreq");
		tasks = setMachineResources(tasks, (Integer) parameters.get("numberOfMachineResources"), machineCosts, mreq);


		for (int i = 0; i < humanCosts.length; i++) {

		}
		double[] orderWeights = (double[]) parameters.get("weights");
		List<Order> orders = new ArrayList<>();


		for (int i = 0; i < orderWeights.length; i++) {
			Order order = new Order(i, orderWeights[i]);
			// sua lai thanh task copy
			order.setTasks(tasks);
			for (Variable task : order.getTasks()) {
				// set the duration according to weights
				((Task) task).setDuration(((Task) task).getDuration() * orderWeights[i]);
			}
			orders.add(order);
		}


		// Sort according to task
		Collections.sort(orders);

		// Calculate time for the tasks in orders
		orders = setVariablesTimes(orders, k);

		return joinOrders(orders);
	}

	private List<Variable> setMachineResources(List<Variable> tasks, Integer numberOfMachineResources, double[] machineCosts, double[][] mreq ) {
		int numberOfTasks = tasks.size();
		List<Resource> machineResource = new ArrayList<>();
		for (Variable variable : variables) {
			int id = ((Task) variable).getId();
			for (int i = 0; i < numberOfMachineResources; i++) {
				if (mreq[id][i] != 0) {
					Resource resource = new Resource(i, Resource.TYPE.MACHINE, machineCosts[i]);
					machineResource.add(resource);
				}
			}
			((Task) variable).setRequiredMachines(machineResource);
		}

		return variables;
	}

	private List<Order> setVariablesTimes(List<Order> orders, double k) {

		int size = orders.size();
		for (int i = 1; i < size; i++) {
			// viet ham tinh tgian dua vao previous order
			orders.get(i).setTasks(setTasksTime(orders.get(i).getTasks(), orders.get(i - 1).getTasks(), k));
		}
		return orders;
	}

	private List<Variable> setTasksTime(List<Variable> currentOrderTasks, List<Variable> previousOrderTasks, double k) {
		int size = currentOrderTasks.size();
		double startTime = calculateTaskTime(currentOrderTasks.get(0), previousOrderTasks);
		((Task) currentOrderTasks.get(0)).setStart(startTime);
		for (int i = 1; i < size; i++) {
			double precedentExitTime = ((Task) currentOrderTasks.get(i - 1)).getStart() + ((Task) currentOrderTasks.get(i - 1)).getDuration();
			((Task) currentOrderTasks.get(i)).setStart(precedentExitTime);
			List<Variable> competingTaskPreviousOrder = calculateCompetingTasks(precedentExitTime, previousOrderTasks.subList(i, size));
			startTime = calculateTaskTime(currentOrderTasks.get(i), competingTaskPreviousOrder);
			((Task) currentOrderTasks.get(i)).setStart(startTime);
			double randomScheduledStart = Math.random() * k;
			double sign = Math.random() > 0 ? 1 : -1;
			double scheduledStart = randomScheduledStart * sign + startTime;
			((Task) currentOrderTasks.get(i)).setScheduledTime(scheduledStart);
		}
		return currentOrderTasks;
	}

	private List<Variable> calculateCompetingTasks(double precedentExitTime, List<Variable> tasks) {
		List<Variable> competingTasks = new ArrayList<>(tasks);
		for (Variable task : competingTasks) {
			double endTime = ((Task) task).getStart() + ((Task) task).getDuration();
			if (endTime <= precedentExitTime) {
				competingTasks.remove(task);
			} else if (endTime > precedentExitTime) {
				break;
			}
		}
		return competingTasks;
	}

	private double calculateTaskTime(Variable currentTask, List<Variable> previousOrderTasks) {

		double humanResourceStart = 0;
		double machineResourceStart = 0;

		List<SkillsInResource> skillsInResources = new ArrayList<>(((Task) currentTask).getRequiredSkillsInResources());
		List<Resource> machineResources = new ArrayList<>(((Task) currentTask).getRequiredMachines());

		for (Variable previousOrderTask : previousOrderTasks) {
			if (skillsInResources.isEmpty() && machineResources.isEmpty()) {
				break;
			}
			if (!skillsInResources.isEmpty()) {
				List<SkillsInResource> skillsInResources1 = ((Task) previousOrderTask).getRequiredSkillsInResources();
				for (SkillsInResource skillsInResource : skillsInResources) {

					if (!isResourceUsed(skillsInResource.getResource().getId(),
							skillsInResource.getResource().getType(),
							skillsInResources1)) {
						skillsInResources.remove(skillsInResource);
						if (!skillsInResources.isEmpty()) {
							humanResourceStart = ((Task) previousOrderTask).getStart() + ((Task) previousOrderTask).getDuration();
						}
					} else {
						humanResourceStart = ((Task) previousOrderTask).getStart() + ((Task) previousOrderTask).getDuration();
					}
				}
			}

			if (!machineResources.isEmpty()) {
				List<Resource> machineResources1 = ((Task) previousOrderTask).getRequiredMachines();
				for (Resource machineResource : machineResources) {
					if (!isResourceUsed(machineResource.getId(), machineResource.getType(), machineResources1)) {
						machineResources.remove(machineResource);
						if (!machineResources.isEmpty()) {
							machineResourceStart = ((Task) previousOrderTask).getStart() + ((Task) previousOrderTask).getDuration();
						}
					} else {
						humanResourceStart = ((Task) previousOrderTask).getStart() + ((Task) previousOrderTask).getDuration();
					}
				}
			}
		}
		return humanResourceStart > machineResourceStart ? humanResourceStart : machineResourceStart;
	}

	private boolean isResourceUsed(int resourceId, Resource.TYPE resourceType, List<?> resources) {
		if (resourceType == Resource.TYPE.HUMAN) {
			for (SkillsInResource skillsInResource : (List<SkillsInResource>) resources) {
				if (skillsInResource.getResource().getId() == resourceId) {
					return true;
				}
			}
		} else if (resourceType == Resource.TYPE.MACHINE) {
			for (Resource resource : (List<Resource>) resources) {
				if (resource.getId() == resourceId) {
					return true;
				}
			}
		}
		return false;
	}

}
