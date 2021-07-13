package component.resource;

import common.STATUS;
import component.skill.Skill;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class HumanResource extends Resource{
	private List<Skill> skills;

	@Builder
	public HumanResource(int id, STATUS status, double cost, List<Skill> skills) {
		super(id, status, cost);
		this.skills = skills;
	}
}
