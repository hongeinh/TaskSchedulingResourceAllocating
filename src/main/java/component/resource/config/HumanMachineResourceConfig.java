package component.resource.config;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Configuration for human and machine resources
 */
@Getter
@Setter
@Builder
public class HumanMachineResourceConfig extends ResourceConfig {

	int numberOfHumanResources;
	int numberOfMachineResources;
	int numberOfSkills;
	double[] lexp;

}
