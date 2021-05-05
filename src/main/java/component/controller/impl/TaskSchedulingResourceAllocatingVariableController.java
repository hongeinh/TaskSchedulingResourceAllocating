package component.controller.impl;

import component.Resource;
import component.Skill;
import component.Task;
import component.Variable;
import component.controller.VariableController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Resource> resources = ((Task) variable).getResources();
        List<Skill> skills = ((Task) variable).getSkills();

        resources = this.isUseful(resources, skills);

        for (Resource resource: resources) {
            if (resource.getStatus() == Resource.STATUS.NOT_USEFUL)
                resource.setStatus(Resource.STATUS.NOT_ASSIGNED);
            else {
                double rand = Math.random();
                if (rand >= 0.5)
                    resource.setStatus(Resource.STATUS.ASSIGNED);
                else
                    resource.setStatus(Resource.STATUS.NOT_ASSIGNED);
            }
        }

        ((Task) variable).setResources(resources);
        return variable;
    }

    public List<Resource> isUseful(List<Resource> resources, List<Skill> skills) {
        int[] isUsefulMatrix = new int[resources.size()];
        for (Resource resource: resources) {
            List<Skill> resourceSkill = resource.getSkills();
            for (Skill skill: skills) {
                for (Skill rSkill: resourceSkill) {
                    if(rSkill.getExperienceLevel() > 0 && skill.getId() == rSkill.getId()) {
                        resource.setStatus(Resource.STATUS.USEFUL);
                        isUsefulMatrix[resource.getId()] = 1;
                        break;
                    }
                }
                if (isUsefulMatrix[resource.getId()] == 1)
                    break;
            }
        }
        return resources;
    }

    @Override
    public List<Variable> createVariables(Map<Object, Object> parameters, double k) {

        Integer numberOfTasks = (Integer) parameters.get("numberOfTasks");

        List<Variable> variables = new ArrayList<>();

        for (int i = 0; i < numberOfTasks; i++) {
            Map<String, Object> params = new HashMap<>();

            double scheduledTime = ((double[])parameters.get("scheduledTimes") )[i];
            double duration = ( (double[]) parameters.get("durations"))[i];

            params.put("id", i);
            params.put("duration", duration);
            params.put("scheduledTime", scheduledTime);

            Variable variable = new Task();
            variable.set(params);
            variables.add(variable);
        }

        int numberOfSkills = (Integer) parameters.get("numberOfSkills");
        int numberOfResources = (Integer) parameters.get("numberOfResources");

        variables = setNeighbours(variables, (int [][]) parameters.get("tasks"));
        variables = setSkills(variables, (int [][]) parameters.get("treq"), numberOfSkills );
        variables = setResources(variables, (double [][]) parameters.get("lexp"), numberOfSkills, numberOfResources);

        for (Variable variable: variables)
            this.setVariableParameters(variable, k);
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
     * This method sets the skills needed for each variable:
     * @param variables			List		Set of variables
     * @param skills			int[][]	   	Task-Skill matrix
     * @param numberOfSkills	int			The maximum number of skills a variable may have
     * @implNote    skills[i][j] = 1 means Variable i needs Skill j
     *
     * @return variables		The same variables with set skills for each variable.
     *
     * */
    public List<Variable> setSkills(List<Variable> variables, int[][] skills, int numberOfSkills) {

        for (Variable variable: variables) {
            for	(int j = 0; j < numberOfSkills; j++ ) {
                if (skills[((Task) variable).getId()][j] == 1) {
                    ((Task) variable).getSkills().add(new Skill(j, 0.0));
                }
            }
        }
        return variables;
    }

    /**
     * This method sets the resources for each variable: predecessors and descendents
     * @param variables			List		Set of variables
     * @param resources			double[][]	Resource-Skill matrix
     * @param numberOfSkills	int			The maximum number of skills a variable may have
     * @param numberOfResources	int			The total number of resources
     * @implNote    skills[i][j] = 1 means Variable i needs Skill j
     *
     * @return variables		The same variables with set skills for each variable.
     *
     * */
    public List<Variable> setResources(List<Variable> variables, double[][] resources, int numberOfSkills, int numberOfResources) {
        for (Variable variable: variables) {
            for (int i = 0; i < numberOfResources; i++) {
                Resource resource = new Resource(i);
                for (int j = 0; j < numberOfSkills; j++) {
                    if (resources[i][j] > 0) {
                        resource.getSkills().add(new Skill(j, resources[i][j]));
                    }
                }
                ((Task) variable).getResources().add(resource);
            }
        }
        return variables;
    }
}
