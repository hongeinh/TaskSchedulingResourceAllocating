package component.controller.impl;

import component.variable.Variable;
import component.variable.impl.Order;
import component.variable.impl.Task;
import utils.DataUtil;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class FixedMultiorderTaskSchedulingController extends TaskSchedulingResourceAllocatingVariableController {

	@Override
	public List<Variable> setupVariables(Map<Object, Object> parameters, double k) {

		List<Variable> orders = (List<Variable>) parameters.get("orders");

		// create template tasks for all orders with resources
		List<Task> templateTasks = createTasks(parameters);
		setupUsableResources(templateTasks, parameters);
		assignResourcesToTasks(templateTasks, k);
		cloneTemplateTasksForAllOrders(orders, templateTasks);

		// set up all orders' tasks' time
		double maxDuration = (double) parameters.get("maxDuration");
		setupVariablesTimes(orders, maxDuration, k);

		// Sort according to task
		Collections.sort(orders);
//		orders.forEach(item -> System.out.println(());

		// Calculate time for the tasks in orders
		orders = calculateEachTaskTimeInAllOrders(orders, k);

		return orders;
	}

	/**
	 * Create a deep clone of the templateTasks and add that to each order
	 * @param orders			Orders to create clone tasks for
	 * @param templateTasks 	Template tasks for the orders
	 *
	 */
	protected void cloneTemplateTasksForAllOrders(List<Variable> orders, List<Task> templateTasks) {
		for (Variable variable : orders) {
			Order order = (Order) variable;
			List<Task> newTasks = templateTasks.stream().map(DataUtil::cloneBean).collect(Collectors.toList());
			for (Task task : newTasks) {
				task.setOrderId(order.getId());
			}
			order.setValue(newTasks);
		}
	}

	protected List<Variable> setupVariablesTimes(List<Variable> orders, double maxDuration, double k) {
		for (Variable variable : orders) {
			Order order = (Order) variable;
			List<Task> newTasks = order.getTasks();
			for (Task newTask : newTasks) {
				long duration = (long) (newTask.getDuration() * order.getWeight() / newTask.getAverageExperience());
				newTask.setDuration(duration);
			}
			// tinh tgian cho task
			calculateTasksTimeSlots(newTasks, k * maxDuration);
		}
		return orders;
	}

	protected List<Variable> calculateEachTaskTimeInAllOrders(List<Variable> orders, double k) {
		int size = orders.size();
		for (int i = 1; i < size; i++) {
			// viet ham tinh tgian dua vao previous order, khong tinh order dau tien
			List<Task> currentOrderTasks =  ((Order) orders.get(i)).getTasks();
			List<Task> previousOrderTasks =  ((Order) orders.get(i - 1)).getTasks();
			calculateTasksTimesAccordingToPreviousOrder(currentOrderTasks, previousOrderTasks, k);
		}
//		System.out.println();
		return orders;
	}

	private void calculateTasksTimesAccordingToPreviousOrder(List<Task> currentOrderTasks, List<Task> previousOrderTasks, double k) {
		int numberOfTasks = currentOrderTasks.size();

		for (int i = 0; i < numberOfTasks; i++) {
			Task currentOrderTask = currentOrderTasks.get(i);
			Task previousOrderTask = previousOrderTasks.get(i);

			LocalDateTime curOrderTaskStartTime = currentOrderTask.getStartTime();
			LocalDateTime preOrderTaskEndTime = previousOrderTask.getStartTime().plusMinutes(previousOrderTask.getDuration());

			curOrderTaskStartTime = (preOrderTaskEndTime.isAfter(curOrderTaskStartTime)) ? preOrderTaskEndTime : curOrderTaskStartTime;
			currentOrderTask.setStartTime(curOrderTaskStartTime);

			long rand = (long) k;
			double sign = Math.random() > 0.5 ? 1 : -1;
			LocalDateTime scheduledStart = null;
			if (sign == 1) {
				scheduledStart = curOrderTaskStartTime.plusMinutes(rand);
			} else if (sign == -1) {
				scheduledStart = curOrderTaskStartTime.minusMinutes(rand);
			}
			currentOrderTask.setScheduledStartTime(scheduledStart);
		}
	}

}
