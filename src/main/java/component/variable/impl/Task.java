package component.variable.impl;

import common.STATUS;
import component.resource.HumanResource;
import component.resource.MachineResource;
import component.resource.Resource;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class Task implements Comparable<Task>, Serializable {

	private int orderId;
	private int id;
	private double scheduledTime;
	private double duration;
	private double start;
	private double idle;
	private List<Integer> predecessors;
	private List<Integer> descendants;
	private List<MachineResource> requiredMachinesResources;
	private List<HumanResource> requiredHumanResources;


	private String getUsefulResourceIdsString(List<? extends Resource> resources, String delimeter) {
		List<String> humanResourceIds = resources.stream()
				.filter(resource -> resource.getStatus() == STATUS.ASSIGNED)
				.map(resource -> Integer.toString(resource.getId()))
				.collect(Collectors.toList());
		return humanResourceIds.stream().map(String::valueOf).collect(Collectors.joining(delimeter));
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		String DELIMETER = ",";

		stringBuilder.append(this.orderId + DELIMETER +
								this.id + DELIMETER +
								this.start + DELIMETER +
								this.duration + DELIMETER);

		stringBuilder.append(getUsefulResourceIdsString(requiredHumanResources, " "));
		stringBuilder.append(DELIMETER);
		stringBuilder.append(getUsefulResourceIdsString(requiredMachinesResources, " "));
		return stringBuilder.toString();
	}

	@Override
	public int compareTo(Task o) {
		if (this.getDescendants().contains(o)) {
			return -1;
		} else if (o.getDescendants().contains(this)) {
			return 1;
		} else {
			return (int) (this.getScheduledTime() - o.getScheduledTime());
		}
	}


	public void setTaskAttributes(Object value) {
		if (value instanceof HashMap ) {
			HashMap<String, Object> parameters = (HashMap <String, Object>) value;
			this.id = (Integer) parameters.get("id");
			this.duration =  (Double) parameters.get("duration");
			this.scheduledTime = (Double) parameters.get("scheduledTime");
		}
	}

	@Deprecated
	public double getMaxExperienceForSkill(int i) {
		double maxExperience = 0.0;
		for(Resource resource: requiredHumanResources) {
			double exp = ((HumanResource) resource).getSkills().get(i).getExperienceLevel();
			maxExperience = (maxExperience > exp) ? maxExperience : exp;
		}

		return maxExperience;
	}

	@Deprecated
	public double getTotalExperienceForSkill(int i) {
		double totalExperience = 0.0;
		for(Resource resource: requiredHumanResources) {
			totalExperience += ((HumanResource) resource).getSkills().get(i).getExperienceLevel();
		}
		return totalExperience;
	}

	@Deprecated
	public int getNumberOfAssignedResources() {
		int resourceCount = 0;
		for (Resource Resource: requiredHumanResources) {
			if (Resource.getStatus() == STATUS.ASSIGNED)
				resourceCount++;
		}
		return resourceCount;
	}

	public double getTaskCost() {
		return getHumanResourceCost() + getMachineResourceCost();
	}

	private double getMachineResourceCost() {
		List<MachineResource> assignedResources = this.getRequiredMachinesResources().stream()
				.filter(machineResource -> machineResource.getStatus() == STATUS.ASSIGNED)
				.collect(Collectors.toList());
		double totalCost = 0;
		for (MachineResource resource: assignedResources) {
			totalCost += resource.getCost() * this.getDuration();
		}

		return totalCost;
	}

	private double getHumanResourceCost() {
		List<HumanResource> assignedResources = this.getRequiredHumanResources().stream()
				.filter(humanResource -> humanResource.getStatus() == STATUS.ASSIGNED)
				.collect(Collectors.toList());

		double totalCost = 0;

		for (HumanResource assignedResource: assignedResources) {
			totalCost += assignedResource.getCost() * this.getDuration();
		}
		return totalCost;
	}

	public double getAverageExperience() {
		double totalExperience = 0;
		double assignedResource = 0;
		for (HumanResource humanResource: this.getRequiredHumanResources()) {
			if (humanResource.getStatus() == STATUS.ASSIGNED) {
				totalExperience += humanResource.getAverageExp();
			}
			assignedResource++;
		}
		return  totalExperience/assignedResource;
	}

	public String getHumanResourceString() {
		return getUsefulResourceIdsString(requiredHumanResources, " ");
	}

	public String getMachineResourceString() {
		return getUsefulResourceIdsString(requiredMachinesResources, " ");
	}
}
