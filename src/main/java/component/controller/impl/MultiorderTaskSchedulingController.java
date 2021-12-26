package component.controller.impl;

import component.variable.Variable;
import component.variable.impl.Order;
import component.variable.impl.Task;
import representation.Solution;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MultiorderTaskSchedulingController extends FixedMultiorderTaskSchedulingController {


	public MultiorderTaskSchedulingController() {
		super();
	}

	@Override
	public List<Variable> setupVariables(Map<Object, Object> parameters, double k) {

		int numberOfHumanResource = (int) parameters.get("numberOfHumanResources");
		int numberOfMachineResource = (int) parameters.get("numberOfMachineResources");

		setResources(numberOfHumanResource, numberOfMachineResource);

		// Get the orders
		List<Variable> orders = (List<Variable>) parameters.get("orders");

		// Create tasks and with precedence constraints
		List<Task> templateTasks = createTasks(parameters);
		setupUsableResources(templateTasks, parameters);
		cloneTemplateTasksForAllOrders(orders, templateTasks);

		// Assign resources differently to each task of each order
		orders = setupVariableResources(orders, parameters, k);

		// Sort according to task
		Collections.shuffle(orders);
		orders = calculateEachTaskTimeInAllOrders(orders, k);

		return orders;
	}

	public List<Variable> setupVariableResources(List<Variable> orders, Map<Object, Object> parameters, double k) {
		for (Variable variable: orders) {
			List<Task> tasks = (List<Task>) variable.getValue();
			tasks = assignResourcesToTasks(tasks, k);
			((Order) variable).setTasks(tasks);
		}

		// set up all orders' tasks' default time
		double maxDuration = (double) parameters.get("maxDuration");
		orders = setupVariablesTimes(orders, maxDuration, k);

		return orders;
	}

	@Override
	public void recalculateSolutionDetails(List<Solution> offspringSolutions) {
		for (Solution solution: offspringSolutions) {
			List<Variable> orders = solution.getVariables();
			Collections.sort(orders);
			calculateEachTaskTimeInAllOrders(orders, 0);
		}
	}
}
