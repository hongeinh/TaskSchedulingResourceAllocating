package component;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Skill {

	private int id;
	private double experienceLevel;

	public String toString() {
		return "Skill " + this.id + " has exp level of " + this.experienceLevel;
	}
}
