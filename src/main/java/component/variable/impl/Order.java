package component.variable.impl;

import component.variable.Variable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class Order implements Variable{

	private int id;
	private double weight;
	private double volume;
	private double penaltyRate;
	private double totalTimeAllowed;
	private double totalTimeSpent;
	private double totalCost;
	List<Task> tasks;

	@Override
	public Object getValue() {
		return tasks;
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof List) {
			this.tasks = (List<Task>) value;
		}
	}

	public double getTotalTimeSpent() {
		double totalTime = 0;
		for (Task task: tasks) {
			totalTime += task.getDuration();
		}
		return totalTime;
	}

	public double getTotalCost() {
		double totalCost = 0;
		for (Task task: tasks) {
			totalCost += task.getTaskCost();
		}
		return totalCost;
	}
	@Override
	public int compareTo(Variable o) {
		if (o instanceof Order) {
			Order otherOrder = (Order) o;
			return this.totalTimeAllowed > otherOrder.getTotalTimeAllowed()? 1 :
						(this.totalTimeAllowed ==otherOrder.getTotalTimeAllowed()? 0 : -1);
		}
		return 0;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		String DELIMETER = ",";
		stringBuilder.append(this.id + DELIMETER +
						this.getTotalTimeAllowed() + DELIMETER +
						this.getTotalTimeSpent() + DELIMETER +
						this.getTotalCost() + DELIMETER);

		return stringBuilder.toString();
	}
}
