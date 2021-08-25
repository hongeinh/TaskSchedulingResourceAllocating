package component.controller.impl;

import component.variable.Variable;
import component.variable.impl.Order;
import component.variable.impl.Task;
import utils.DataUtil;
import utils.NumberUtil;

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
		setupAllTasksResources(templateTasks, parameters);
		assignResourcesToAllTask(templateTasks, k);
		cloneTemplateTasksForAllOrders(orders, templateTasks);

		// set up all orders' tasks' time
		double maxDuration = (double) parameters.get("maxDuration");
		setupVariablesDefaultTimes(orders, maxDuration, k);

		// Sort according to task
		Collections.sort(orders);

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
		for (int i = 0; i < orders.size(); i++) {
			Order order = (Order) orders.get(i);
			List<Task> newTasks = templateTasks.stream().map(templateTask -> DataUtil.cloneBean(templateTask)).collect(Collectors.toList());
			for (Task task: newTasks) {
				task.setOrderId(i);
			}
			order.setValue(newTasks);
		}
	}

	protected void setupVariablesDefaultTimes(List<Variable> orders, double maxDuration, double k) {
		for (int i = 0; i < orders.size(); i++) {
			Order order = (Order) orders.get(i);
			List<Task> newTasks = (List<Task>) order.getValue();

			for (Task newTask : newTasks) {
				double duration = newTask.getDuration() * order.getWeight() / newTask.getAverageExperience();
				newTask.setDuration(NumberUtil.floor2DecimalPoints(duration));
			}

			// tinh tgian cho task
			calculateAllTasksTimes(newTasks, k * maxDuration);
		}
	}

	protected List<Variable> calculateEachTaskTimeInAllOrders(List<Variable> orders, double k) {

		int size = orders.size();
		for (int i = 1; i < size; i++) {
			// viet ham tinh tgian dua vao previous order, khong tinh order dau tien
			List<Task> currentOrderTasks = (List<Task>) orders.get(i).getValue();
			List<Task> previousOrderTasks =  (List<Task>)orders.get(i - 1).getValue();
			calculateTasksTimesAccordingToPreviousOrder(currentOrderTasks, previousOrderTasks, k);
		}
		return orders;
	}

	private void calculateTasksTimesAccordingToPreviousOrder(List<Task> currentOrderTasks, List<Task> previousOrderTasks, double k) {

		int numberOfTasks = currentOrderTasks.size();

		for (int i = 0; i < numberOfTasks; i++) {
			Task coTask = ( currentOrderTasks.get(i));
			Task poTask = ( previousOrderTasks.get(i));

			double coTaskStartTime = coTask.getStart();
			double poTaskEndTime = poTask.getStart() + poTask.getDuration();

			double currentOrderTaskStartTime = Math.max(poTaskEndTime, coTaskStartTime);
			coTask.setStart(currentOrderTaskStartTime);

			double rand = Math.random() * k;
			double sign = Math.random() > 0.5 ? 1 : -1;
			double scheduledStart = currentOrderTaskStartTime + rand * sign;
			coTask.setScheduledTime(scheduledStart);

		}
	}

}
