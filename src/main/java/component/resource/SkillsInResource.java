package component.resource;

import component.resource.Resource;
import component.skill.Skill;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SkillsInResource {
    private Resource resource;
    private List<Skill> requiredSkills;

    public SkillsInResource() {
        this.requiredSkills = new ArrayList<>();
    }

	public SkillsInResource getSimilarSkillInResource() {
    	SkillsInResource newSkillInResource = new SkillsInResource();
    	Resource newResource = new Resource(this.resource.getId(), this.resource.getType(), this.resource.getCost());
    	List<Skill> newSkills = new ArrayList<>(this.getRequiredSkills());


    	newSkillInResource.setRequiredSkills(newSkills);
    	newSkillInResource.setResource(newResource);

    	return newSkillInResource;
	}
}
