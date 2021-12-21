package component.resource.config;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResourceConfig {
	int numberOfHumanResources;
	int numberOfMachineResources;
	int numberOfSkills;
	double[] lexp;
}
