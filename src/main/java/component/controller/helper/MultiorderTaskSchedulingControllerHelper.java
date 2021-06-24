package component.controller.helper;

import component.SkillsInResource;
import component.Task;
import component.Variable;
import component.resource.Resource;

import java.util.*;

public class MultiorderTaskSchedulingControllerHelper {
	public ArrayList<Double> calculateResourceAndStart(List<SkillsInResource> requiredResources, List<Resource> assignedResources, List<Integer> assignedResourceVariable, List<Integer> assignedResourceOrder) {
		ArrayList<Double> resourcesTasksOrdersAndStart = new ArrayList<>();
		double minStart = Double.MAX_VALUE;

		ArrayList<Resource> availableResources = getAvailableResources(requiredResources, assignedResources);

		return resourcesTasksOrdersAndStart;
	}
	

	public int getIndexInAssignedList(List<Resource> assignedResources, Resource resource) {
		for (int i = 0; i < assignedResources.size(); i++) {
			if (assignedResources.get(i).getId() == resource.getId() &&
					assignedResources.get(i).getType() == resource.getType()) {
				return i;
			}
		}
		return -1;
	}

	public boolean isRequiredResource(Resource checkResource, List<SkillsInResource> skillsInResources) {
		for (SkillsInResource skillsInResource : skillsInResources) {
			Resource resource = skillsInResource.getResource();
			if (checkResource.getType() == resource.getType() && checkResource.getId() == resource.getId()) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<Resource> getAvailableResources(List<SkillsInResource> requiredResources, List<Resource> assignedResources) {
		ArrayList<Resource> availableResources = new ArrayList<>();

		for (SkillsInResource skillsInResource : requiredResources) {
			Resource resource = skillsInResource.getResource();
			if (!assignedResources.stream()
					.filter(re -> re.getType() == resource.getType() && re.getId() == resource.getId())
					.findFirst().isPresent()) {
				availableResources.add(resource);
			}
		}
		return availableResources;
	}

}
