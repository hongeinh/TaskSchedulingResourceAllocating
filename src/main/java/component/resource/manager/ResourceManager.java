package component.resource.manager;

import component.resource.HumanResource;
import component.resource.MachineResource;
import component.resource.Resource;
import component.resource.config.ResourceConfig;
import component.timeframe.TimeSlot;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * A medium for managing resources
 */
public class ResourceManager {

	/**
	 * Configuration for resources
	 */
	private ResourceConfig resourceConfig;

	/**
	 * Resources that need to be managed
	 * */
	private List<List<Resource>> resourceList;

	public void initResourceList(){
		// TODO
		List<HumanResource> humanResources = this.setHumanResources();
		List<MachineResource> machineResources = this.setMachineResources();

	}

	private List<HumanResource> setHumanResources() {
		List<HumanResource> resources = new ArrayList<>();

		int numberOfHumanResource = this.resourceConfig.getNumberOfHumanResources();
		for (int i = 0; i < numberOfHumanResource; i++) {
			HumanResource humanResource = HumanResource.builder()
					.id(i)
					.usedTimeSlots(new ArrayList<>())
					.build();

			resources.add(humanResource);
		}
		return resources;
	}


	private List<MachineResource> setMachineResources() {
		List<MachineResource> resources = new ArrayList<>();

		return resources;
	}
	/**
	 * Check if resource is available at a given timeslot
	 * @param requiredResource
	 * @param time
	 * @return boolean
	 */
	public boolean isResourceAvailable(Resource requiredResource, LocalDateTime time) {
		for(List<Resource> resources: resourceList) {
			for (Resource resource: resources) {
				// only check if same kind of resource
				if (resource.getClass().equals(requiredResource.getClass())) {
					if (resource.equals(requiredResource)) {
						List<TimeSlot> usedTimeSlot = resource.getUsedTimeSlots();

					}
				} else {
					break;
				}
			}
		}
		return false;
	}

	public List<List<Resource>> getAvailableResources() {
		// TODO
		return null;
	}
}
