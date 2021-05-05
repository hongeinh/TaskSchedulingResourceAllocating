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
	private List<Skill> skills;

	public Resource(int id) {
		this.id = id;
		this.skills = new ArrayList<>();
		this.status = STATUS.NOT_ASSIGNED;
	}

	public enum STATUS {
		ASSIGNED,
		NOT_ASSIGNED,
		USEFUL,
		NOT_USEFUL
	};
}
