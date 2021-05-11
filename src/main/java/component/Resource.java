package component;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class Resource {

	private int id;
	private STATUS status;

	public Resource(int id) {
		this.id = id;
		this.status = STATUS.NOT_USEFUL;
	}

	public enum STATUS {
		ASSIGNED,
		NOT_ASSIGNED,
		USEFUL,
		NOT_USEFUL
	};
}
