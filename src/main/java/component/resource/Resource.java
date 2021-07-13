package component.resource;

import common.STATUS;
import common.TYPE;
import component.skill.Skill;
import lombok.*;

import java.io.Serializable;
import java.security.Signature;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Resource implements Serializable {

	private int id;
	private STATUS status;
	private double cost;
}
