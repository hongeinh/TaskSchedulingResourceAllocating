package component;

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
}
