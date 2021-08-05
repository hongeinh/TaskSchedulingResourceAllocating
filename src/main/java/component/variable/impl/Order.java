package component.variable.impl;

import component.variable.Variable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

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

	@Override
	public int compareTo(@NotNull Variable o) {
		if (o instanceof Order) {
			Order otherOrder = (Order) o;
			return this.totalTimeAllowed > otherOrder.getTotalTimeAllowed()? 1 :
						(this.totalTimeAllowed ==otherOrder.getTotalTimeAllowed()? 0 : -1);
		}
		return 0;
	}
}
