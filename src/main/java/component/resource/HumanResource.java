package component.resource;

import component.skill.Skill;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class HumanResource extends Resource {
	private List<Skill> skills;
}
