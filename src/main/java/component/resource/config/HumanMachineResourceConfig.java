package component.resource.config;

import lombok.*;

/**
 * Configuration for human and machine resources
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HumanMachineResourceConfig extends ResourceConfig {

	int numberOfHumanResources;
	int numberOfMachineResources;
	int numberOfSkills;
	double[] lexp;

}
