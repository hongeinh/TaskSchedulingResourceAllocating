package component.variable.impl;

import common.STATUS;
import component.resource.HumanResource;
import component.resource.MachineResource;
import component.resource.Resource;
import component.variable.Variable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class Task extends Variable {

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

	public Task() {
		this.predecessors = new ArrayList<>();
		this.descendants = new ArrayList<>();
		this.requiredMachinesResources = new ArrayList<>();
		this.requiredHumanResources = new ArrayList<>();
	}
	@Override
	public Object getValue() {
		return this.getRequiredHumanResources();
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof HashMap ) {
			HashMap<String, Object> parameters = (HashMap <String, Object>) value;
			this.id = (Integer) parameters.get("id");
			this.duration =  (Double) parameters.get("duration");
			this.scheduledTime = (Double) parameters.get("scheduledTime");
		}
	}

	private String getResourceIdsString(List<? extends Resource> resources, String delimeter) {
		List<String> humanResourceIds = resources.stream()
				.map(resource -> Integer.toString(resource.getId()))
				.collect(Collectors.toList());
		return humanResourceIds.stream().map(String::valueOf).collect(Collectors.joining(delimeter));
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		if(this.orderId != 0)
			stringBuilder.append("\nOrder: " + orderId + "\t");
		stringBuilder.append("Task " + this.id + ":" +
				"\tScheduled start: " + this.scheduledTime +
				"\tStart: " + this.start +
				"\tDuration: " + this.duration +
				"\tHuman Resource: ");
		stringBuilder.append(getResourceIdsString(requiredHumanResources, " "));
		stringBuilder.append("\t---\tMachine Resource: ");
		stringBuilder.append(getResourceIdsString(requiredMachinesResources, " "));
		return stringBuilder.toString();
	}

	@Override
	public int compareTo(Variable o) {
		if (this.getDescendants().contains(o)) {
			return -1;
		} else if (((Task) o).getDescendants().contains(this)) {
			return 1;
		} else {
			return (int) (this.getScheduledTime() - ((Task) o).getScheduledTime());
		}
	}

	public double getMaxExperienceForSkill(int i) {
		double maxExperience = 0.0;
		for(Resource resource: requiredHumanResources) {
			double exp = ((HumanResource) resource).getSkills().get(i).getExperienceLevel();
			maxExperience = (maxExperience > exp) ? maxExperience : exp;
		}

		return maxExperience;
	}

	public double getTotalExperienceForSkill(int i) {
		double totalExperience = 0.0;
		for(Resource resource: requiredHumanResources) {
			totalExperience += ((HumanResource) resource).getSkills().get(i).getExperienceLevel();
		}
		return totalExperience;
	}

	public int getNumberOfAssignedResources() {
		int resourceCount = 0;
		for (Resource Resource: requiredHumanResources) {
			if (Resource.getStatus() == STATUS.ASSIGNED)
				resourceCount++;
		}
		return resourceCount;
	}

//	public void setSimilarValueTask(Task oldTask) {
//		setPredecessors(oldTask.getPredecessors());
//		setDescendants(oldTask.getDescendants());
//		setSimilarRequiredMachines(oldTask.getRequiredMachinesResources());
//		setSimilarrequiredHumanResources(oldTask.getRequiredHumanResources());
//	}
//
//	private void setSimilarRequiredMachines(List<Resource> oldMachines) {
//		for (Resource resource: oldMachines) {
//			Resource machineResource = Resource.builder()
//					.id(resource.getId())
//					.type(resource.getType())
//					.cost(resource.getCost())
//					.build();
//			this.getRequiredMachinesResources().add(machineResource);
//		}
//	}
//
//	private void setSimilarrequiredHumanResources(List<Resource> oldResources) {
//		for (Resource oldResource: oldResources) {
//			List<Skill> oldSkillList = ((HumanResource) oldResource).getSkills();
//			List<Skill> newSkillList = new ArrayList<>();
//			for (Skill oldSkill: oldSkillList) {
//				Skill newSkill = Skill.builder()
//						.id(oldSkill.getId())
//						.experienceLevel(oldSkill.getExperienceLevel())
//						.build();
//				newSkillList.add(newSkill);
//			}
//			HumanResource humanResource = HumanResource.builder()
//					.id(oldResource.getId())
//					.type(oldResource.getType())
//					.cost(oldResource.getCost())
//					.build();
//			this.requiredHumanResources.addAll((Collection<? extends Resource>) humanResource);
//		}
//	}
}
