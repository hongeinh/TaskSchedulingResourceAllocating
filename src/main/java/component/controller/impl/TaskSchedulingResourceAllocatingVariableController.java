package component.controller.impl;

import component.*;
import component.controller.VariableController;

import java.util.*;

public class TaskSchedulingResourceAllocatingVariableController extends VariableController {

    public Variable setVariableParameters(Variable variable, double k) {
        variable = setVariableTime(variable, k);
        variable = setVariableResource(variable);
        return variable;
    }

    public Variable setVariableTime(Variable variable, double  k) {
        List<Variable> predescessors = ((Task) variable).getPredecessors();

        for (Variable pre: predescessors) {
            double start = ((Task) variable).getStart() > ((Task) pre).getStart() + ((Task) pre).getDuration() ?
                            ((Task) variable).getStart() :
                            ((Task) pre).getStart() + ((Task) pre).getDuration();

            double rand = Math.floor(Math.random()*k);

            ((Task) variable).setDuration(start);
            ((Task) variable).setScheduledTime(start + rand);
        }
        return variable;
    }


    public Variable setVariableResource(Variable variable) {

        List<SkillsInResource> skillsInResources = ((Task) variable).getRequiredSkillsInResources();
        skillsInResources = this.isUseful(skillsInResources);

        for (SkillsInResource skillsInResource: skillsInResources) {
//            if (skillsInResource.getResource().getStatus() == Resource.STATUS.NOT_USEFUL)
//                skillsInResource.getResource().setStatus(Resource.STATUS.NOT_ASSIGNED);
             if (skillsInResource.getResource().getStatus() == Resource.STATUS.USEFUL){
                double rand = Math.random();
                if (rand >= 0.5)
                    skillsInResource.getResource().setStatus(Resource.STATUS.ASSIGNED);
                else
                    skillsInResource.getResource().setStatus(Resource.STATUS.NOT_ASSIGNED);
            }
        }

        ((Task) variable).setRequiredSkillsInResources(skillsInResources);
        return variable;
    }

    public List<SkillsInResource> isUseful(List<SkillsInResource> skillsInResources) {
        for (SkillsInResource skillsInResource : skillsInResources) {
            List<Skill> skills = skillsInResource.getRequiredSkills();
            for (Skill rSkill : skills) {
                if (rSkill.getExperienceLevel() > 0) {
                    skillsInResource.getResource().setStatus(Resource.STATUS.USEFUL);
                    break;
                }
            }
        }
        return skillsInResources;
    }


    @Override
    public List<Variable> createVariables(Map<Object, Object> parameters, double k) {

        int numberOfTasks = (Integer) parameters.get("numberOfTasks");
        List<Variable> variables = new ArrayList<>();

        for (int i = 0; i < numberOfTasks; i++) {
            Map<String, Object> params = new HashMap<>();

            double scheduledTime = ((double[])parameters.get("scheduledTimes") )[i];
            double duration = ( (double[]) parameters.get("durations"))[i];

            params.put("id", i);
            params.put("duration", duration);
            params.put("scheduledTime", scheduledTime);

            Variable variable = new Task();
            variable.setValue(params);
            variables.add(variable);
        }

        int numberOfSkills = (Integer) parameters.get("numberOfSkills");
        int numberOfResources = (Integer) parameters.get("numberOfResources");

        variables = setNeighbours(variables, (int[][]) parameters.get("tasks"));
        variables = setResourcesAndSkills(variables, (int[][]) parameters.get("treq"), (double[][]) parameters.get("lexp"), numberOfSkills, numberOfResources);

        int maxDuration = (int) parameters.get("maxDuration");
        for (Variable variable : variables)
            this.setVariableParameters(variable, k * maxDuration);
        Collections.sort(variables);
        return variables;
    }


    /**
     * This method sets the neighbours for each variable: predecessors and descendents
     * @param variables		List		Set of variables
     * @param tasks			int[][]	   	Relationship matrix
     *
     * @implNote    		tasks[i][j] = 1 means Variable i is predecessors of Variable j,
     * 										and Variable j is descendent of Variable i
     *
     * @return variables	The same variables with set descendants and predecessors for each variable.
     *
     * */
    public List<Variable> setNeighbours(List<Variable> variables, int[][] tasks) {
        int size = variables.size();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i != j) {
                    if ( tasks[i][j] == 1) {
                        ((Task) variables.get(i)).getDescendants().add(variables.get(j));
                        ((Task) variables.get(j)).getPredecessors().add(variables.get(j));
                    } else if (tasks[j][i] == 1) {
                        ((Task) variables.get(i)).getPredecessors().add(variables.get(j));
                        ((Task) variables.get(j)).getDescendants().add(variables.get(j));
                    }
                }
            }
        }
        return variables;
    }

    /**
     * This method sets the resources and skills for each variable. Only adds the required skills
     *
     * @param variables         List		Set of variables
     * @param treq              int[][]     Task-Skill matrix
     * @param lexp              double[][]	Resource-Skill matrix
     * @param numberOfSkills    int			The maximum number of skills a variable may have
     * @param numberOfResources int			The total number of resources
     * @return variables        The same variables with set skills for each variable.
     *
     *
     * @implNote skills[i][j] = 1 means Variable i needs Skill j
     */
    public List<Variable> setResourcesAndSkills(List<Variable> variables, int[][] treq, double[][] lexp, int numberOfSkills, int numberOfResources) {
        for (Variable variable : variables) {
            for (int i = 0; i < numberOfResources; i++) {
                SkillsInResource skillsInResource = new SkillsInResource();
                skillsInResource.setResource(new Resource(i));
                List<Skill> skills = skillsInResource.getRequiredSkills();
                for (int j = 0; j < numberOfSkills; j++) {
                    /* Only add skill to resource if the skill is necessary to the task */
                    if (treq[((Task) variable).getId()][j] == 1) {
                        skills.add(new Skill(j, lexp[i][j]));
                    }
                }
                skillsInResource.setRequiredSkills(skills);
                ((Task) variable).getRequiredSkillsInResources().add(skillsInResource);
            }
        }
        return variables;
    }
}
