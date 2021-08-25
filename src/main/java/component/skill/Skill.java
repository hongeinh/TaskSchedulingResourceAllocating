package component.skill;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class Skill implements Serializable {

	private int id;
	private double experienceLevel;
}
