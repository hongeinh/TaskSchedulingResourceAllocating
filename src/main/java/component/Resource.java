package component;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class Resource {

	private int id;
	private int status;
	private List<Skill> skills;

	public Resource(int id) {
		this.id = id;
		this.skills = new ArrayList<>();
	}
	public String toString() {
		return "Resource " + this.id + " is " + (this.status == 0 ? "available" : "unavailable");
	}
}
