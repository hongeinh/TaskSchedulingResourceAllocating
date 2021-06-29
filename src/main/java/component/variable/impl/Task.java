package component.variable.impl;

import component.resource.SkillsInResource;
import component.resource.Resource;
import component.variable.Variable;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class Task extends Variable {
	private int orderId;
	private int id;
	private double scheduledTime;
	private double duration;
	private double start;
	private double idle;
	private double weight;
	private List<Variable> predecessors;
	private List<Variable> descendants;
	private List<SkillsInResource> requiredSkillsInResources;
	private List<Resource> requiredMachines;

	public Task() {
		this.predecessors = new ArrayList<>();
		this.descendants = new ArrayList<>();
		this.requiredSkillsInResources = new ArrayList<>();
		this.requiredMachines = new ArrayList<>();
	}


	@Override
	public Object getValue() {
		return this.getRequiredSkillsInResources();
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof HashMap ) {
			HashMap<String, Object> parameters = (HashMap <String, Object>) value;
//			this.orderId = (Integer) parameters.get("orderId");
			this.id = (Integer) parameters.get("id");
			this.duration =  (Double) parameters.get("duration");
			this.scheduledTime = (Double) parameters.get("scheduledTime");
			this.weight = (Double) parameters.get("weight");
		}
	}


	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("*** Task " + this.id + ":" +
				"\tScheduled start: " + this.scheduledTime +
				"\tStart: " + this.start +
				"\tDuration: " + this.duration +
				"\tResource: ");
		for (SkillsInResource skillsInResource: requiredSkillsInResources) {
			if (skillsInResource.getResource().getStatus() == Resource.STATUS.ASSIGNED)
				stringBuilder.append(skillsInResource.getResource().getId() + " ");
		}
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
		for(SkillsInResource skillsInResource: requiredSkillsInResources) {
			double exp = skillsInResource.getRequiredSkills().get(i).getExperienceLevel();
			maxExperience = (maxExperience > exp) ? maxExperience : exp;
		}

		return maxExperience;
	}

	public double getTotalExperienceForSkill(int i) {
		double totalExperience = 0.0;
		for(SkillsInResource skillsInResource: requiredSkillsInResources) {
			totalExperience += skillsInResource.getRequiredSkills().get(i).getExperienceLevel();
		}
		return totalExperience;
	}

	public int getNumberOfAssignedResources() {
		int resourceCount = 0;
		for (SkillsInResource skillsInResource: requiredSkillsInResources) {
			if (skillsInResource.getResource().getStatus() == Resource.STATUS.ASSIGNED)
				resourceCount++;
		}
		return resourceCount;
	}

}
