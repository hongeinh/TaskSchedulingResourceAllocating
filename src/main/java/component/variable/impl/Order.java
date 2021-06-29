package component.variable.impl;

import component.variable.Variable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import static utils.PipedDeepCopy.copy;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Comparable<Order> {
	private int id;
	private double weight;
	List<Variable> tasks;

	public Order(int id, double weight) {
		this.id = id;
		this.weight = weight;
	}

	@Override
	public int compareTo(@NotNull Order o) {
		return this.weight > o.weight ? 1 : (this.weight == o.weight ? 0 : -1);
	}

	public void setSimilarTasks(List<Variable> tasks) {
		for (Variable variable: tasks) {
			Task oldTask = (Task) variable;
			Task newTask = new Task(oldTask.getId(), oldTask.getScheduledTime(), oldTask.getStart(), oldTask.getIdle());
			newTask.setSimilarValueTask(oldTask);
		}
	}

}
