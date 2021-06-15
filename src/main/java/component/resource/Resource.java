package component.resource;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class Resource {

	private int id;
	private String description;
	private STATUS status;
	private double cost;

	public Resource(int id) {
		this.id = id;
		this.status = STATUS.NOT_USEFUL;
	}

	public Resource(int id, double cost) {
		this(id);
		this.cost = cost;
	}

	public Resource(int id, String description) {
		this.id = id;
		this.description = description;
	}

//	public double calculateCost() {
//		return cost;
//	}

	public enum STATUS {
		ASSIGNED,
		NOT_ASSIGNED,
		USEFUL,
		NOT_USEFUL
	};
}
