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

import static utils.PipedDeepCopy.copy;

public class FixedMultiorderTaskSchedulingController extends  TaskSchedulingResourceAllocatingVariableController{

	public List<Variable> joinOrders(List<Order> orders) {
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

		double [] orderWeights = (double []) parameters.get("weights");
		List<Order> orders = new ArrayList<>();


		for (int i = 0; i < orderWeights.length; i++) {
			Order order = new Order(i, orderWeights[i]);
			order.setTasks((List<Variable>) copy(tasks));
			for (Variable task: order.getTasks()) {
				// set the duration according to weights
				((Task) task).setDuration(((Task) task).getDuration() * orderWeights[i]);
			}
		}


		// Sort according to task
		Collections.sort(orders);

		// Calculate time for the tasks in orders

		return joinOrders(orders);
	}

	private List<Order> setVariablesTimes(List<Order> orders) {

		int size = orders.size();
		for (int i = 1; i < size; i++) {
			// viet ham tinh tgian dua vao previous order
			orders.get(i).setTasks(setTasksTime(orders.get(i).getTasks(), orders.get(i-1).getTasks()));
		}
		return orders;
	}

	private List<Variable> setTasksTime(List<Variable> currentOrderTasks, List<Variable> previousOrderTasks) {
		int size = currentOrderTasks.size();
		for (int i = 0; i < size; i++) {
			double startTime = setTaskTime(currentOrderTasks.get(i), previousOrderTasks.subList(i, size));
			((Task) currentOrderTasks.get(i)).setStart(startTime);
		}
		return currentOrderTasks;
	}

	private double setTaskTime(Variable currentTask, List<Variable> previousOrderTasks) {

		double humanResourceStart = 0;

		double machineResourceStart = 0;
		List<SkillsInResource> skillsInResources = new ArrayList<>(((Task) currentTask).getRequiredSkillsInResources());
		List<Resource> machineResources = new ArrayList<>(((Task) currentTask).getRequiredMachines());

		for (Variable previousOrderTask: previousOrderTasks) {

			if (skillsInResources.isEmpty() && machineResources.isEmpty()) {
				break;
			}

			if (!skillsInResources.isEmpty()) {
				List<SkillsInResource> skillsInResources1 = ((Task) previousOrderTask).getRequiredSkillsInResources();
				for (SkillsInResource skillsInResource: skillsInResources) {

					if ( !isResourceUsed(skillsInResource.getResource().getId(),
							skillsInResource.getResource().getType(),
							skillsInResources1)) {
						skillsInResources.remove(skillsInResource);
					} else {
						humanResourceStart += ((Task) previousOrderTask).getDuration();
					}
				}
			}

			if (!machineResources.isEmpty()) {
				List<Resource> machineResources1 = ((Task) previousOrderTask).getRequiredMachines();
				for (Resource machineResource: machineResources) {
					if( !isResourceUsed(machineResource.getId(), machineResource.getType(), machineResources1)) {

					}
				}
			}
		}
		return humanResourceStart > machineResourceStart ? humanResourceStart : machineResourceStart;
	}

	private boolean isResourceUsed(int resourceId, Resource.TYPE resourceType, List<?> skillsInResources) {
		if (resourceType == Resource.TYPE.HUMAN) {

		} else if (resourceType == Resource.TYPE.MACHINE) {

		}

		return true;
	}

}
