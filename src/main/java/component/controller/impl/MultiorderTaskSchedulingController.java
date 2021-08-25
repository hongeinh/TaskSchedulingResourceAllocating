package component.controller.impl;

import component.variable.Variable;
import component.variable.impl.Task;
import representation.Solution;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MultiorderTaskSchedulingController extends FixedMultiorderTaskSchedulingController {

	@Override
	public List<Variable> setupVariables(Map<Object, Object> parameters, double k) {
		// Get the orders
		List<Variable> orders = (List<Variable>) parameters.get("orders");

		// Create tasks and with precedence constraints
		List<Task> templateTasks = createTasks(parameters);
		setupAllTasksUsefulResources(templateTasks, parameters);
		cloneTemplateTasksForAllOrders(orders, templateTasks);

		// Assign resources differently to each task of each order
		for (Variable variable: orders) {
			List<Task> tasks = (List<Task>) variable.getValue();
			assignResourcesToAllTask(tasks, k);
		}

		// set up all orders' tasks' default time
		double maxDuration = (double) parameters.get("maxDuration");
		setupVariablesDefaultTimes(orders, maxDuration, k);
		//TODO: --> calculate time based on the resources used.


		// Sort according to task
		Collections.sort(orders);

		// Calculate time for the tasks in orders
		orders = calculateEachTaskTimeInAllOrders(orders, k);

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
