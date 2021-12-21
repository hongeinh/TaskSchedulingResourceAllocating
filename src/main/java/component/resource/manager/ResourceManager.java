package component.resource.manager;

import component.resource.HumanResource;
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
public abstract class ResourceManager {

	/**
	 * Configuration for resources
	 */
	private ResourceConfig resourceConfig;

	/**
	 * Resources that need to be managed
	 * */
	private List<List<Resource>> resourceList;

	public abstract void initResourceList();

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
