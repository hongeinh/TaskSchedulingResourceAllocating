package component.resource.config;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceConfig {
	int numberOfHumanResources;
	int numberOfMachineResources;
	int numberOfSkills;
	double[] lexp;
}
