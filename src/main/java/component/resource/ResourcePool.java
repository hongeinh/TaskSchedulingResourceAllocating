package component.resource;

import java.util.List;

public class ResourcePool {
	private List<Resource> humanResources;
	private List<Resource> machineResources;

	public ResourcePool(int numberOfHumanResources, int numberOfMachineResources) {
		for (int i = 0; i < numberOfHumanResources; i++) {
			this.humanResources.add(new Resource(i));
		}
		for (int i = 0; i < numberOfMachineResources; i++) {
			this.machineResources.add(new Resource(i));
		}
	}
}
