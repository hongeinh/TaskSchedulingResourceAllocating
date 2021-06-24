package component.resource;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class Resource {

	private int id;
	private STATUS status;
	private TYPE type;
	private double cost;

	public Resource(int id) {
		this.id = id;
		this.status = STATUS.NOT_USEFUL;
	}

	public Resource(int id, double cost) {
		this(id);
		this.cost = cost;
	}

	public Resource(int id, TYPE type) {
		this(id);
		this.type = type;
	}

	public Resource (int id, TYPE type, double cost) {
		this(id, type);
		this.cost = cost;
	}

	public enum STATUS {
		ASSIGNED,
		NOT_ASSIGNED,
		USEFUL,
		NOT_USEFUL
	};

	public enum TYPE {
		MACHINE,
		HUMAN
	}
}
