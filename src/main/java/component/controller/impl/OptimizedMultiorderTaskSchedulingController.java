package component.controller.impl;

import component.resource.config.HumanMachineResourceConfig;
import component.resource.manager.ResourceManager;
import component.variable.Variable;
import component.variable.impl.Order;
import component.variable.impl.Task;
import representation.Solution;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OptimizedMultiorderTaskSchedulingController extends FixedMultiorderTaskSchedulingController {

	private ResourceManager resourceManager;

	public OptimizedMultiorderTaskSchedulingController() {
		super();
//		this.resourceManager = new HumanMachineResourceConfig();
	}

	@Override
	public List<Variable> setupVariables(Map<Object, Object> parameters, double k) {

		int numberOfHumanResource = (int) parameters.get("numberOfHumanResources");
		int numberOfMachineResource = (int) parameters.get("numberOfMachineResources");

		// TODO: tim cach init resource config
//		resourceManager.setResourceConfig(config);
		resourceManager.initResourceList();

		// Get the orders
		List<Variable> orders = (List<Variable>) parameters.get("orders");

		// Create tasks and with precedence constraints
		List<Task> templateTasks = createTasks(parameters);
		setupAllTasksUsableResources(templateTasks, parameters);
		cloneTemplateTasksForAllOrders(orders, templateTasks);

		// Assign resources differently to each task of each order
		for (Variable variable: orders) {
			List<Task> tasks = (List<Task>) variable.getValue();
			tasks = assignResourcesToAllTask(tasks, k);
			((Order) variable).setTasks(tasks);
		}

		// set up all orders' tasks' default time
		double maxDuration = (double) parameters.get("maxDuration");
		orders = setupVariablesDefaultTimes(orders, maxDuration, k);
		//TODO: --> calculate time based on the resources used.


		// Sort according to task
		Collections.shuffle(orders);
//		orders.forEach(item -> System.out.print(((Order) item).getId()));
//		System.out.println();
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
