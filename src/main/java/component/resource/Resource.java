package component.resource;

import common.STATUS;
import common.TYPE;
import component.skill.Skill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.security.Signature;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
public class Resource {

	private int id;
	private STATUS status;
	private TYPE type;
	private double cost;
	private List<Skill> skills;




}
