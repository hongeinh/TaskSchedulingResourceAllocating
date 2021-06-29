package component.controller.impl;

import component.controller.VariableController;
import component.resource.Resource;
import component.resource.SkillsInResource;
import component.skill.Skill;
import component.variable.Variable;
import component.variable.impl.Task;

import java.util.*;

public class TaskSchedulingResourceAllocatingVariableController extends VariableController {

	protected List<Variable> variables = null;

	protected Variable setVariableParameters(Variable variable, double k) {
		variable = setVariableTime(variable, k);
		variable = setVariableResource(variable);
		return variable;
	}

	protected Variable setVariableTime(Variable variable, double k) {
		List<Variable> predescessors = ((Task) variable).getPredecessors();
		Task currentVariable = (Task) variable;
		for (Variable pre : predescessors) {
			Task predecessor = (Task) pre;
			double start = 0;
			if (currentVariable.getStart() > predecessor.getStart() + predecessor.getDuration()) {
				start = currentVariable.getStart();
			} else {
				start = (predecessor.getStart() + predecessor.getDuration());
			}

			double rand = Math.random() * k;

			((Task) variable).setStart(start);
			start = start + rand;
			((Task) variable).setScheduledTime(start);
		}
		return variable;
	}


	protected Variable setVariableResource(Variable variable) {

		List<SkillsInResource> skillsInResources = ((Task) variable).getRequiredSkillsInResources();
		skillsInResources = this.isUseful(skillsInResources);

		for (SkillsInResource skillsInResource : skillsInResources) {
			if (skillsInResource.getResource().getStatus() == Resource.STATUS.USEFUL) {
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

	protected List<SkillsInResource> isUseful(List<SkillsInResource> skillsInResources) {
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
	public List<Variable> setVariables(Map<Object, Object> parameters, double k) {

		List<Variable> variables = createVariables(parameters);

		int numberOfSkills = (Integer) parameters.get("numberOfSkills");
		int numberOfHumanResources = (Integer) parameters.get("numberOfHumanResources");
		int[][] treq = (int[][]) parameters.get("treq");
		double[][] lexp = (double[][]) parameters.get("lexp");
		double[] humanCosts = (double[]) parameters.get("humanCosts");

		variables = setResourcesAndSkills(variables, treq, lexp, humanCosts, numberOfSkills, numberOfHumanResources);

		int maxDuration = (int) parameters.get("maxDuration");
		for (Variable variable : variables)
			this.setVariableParameters(variable, k * maxDuration);

		return variables;
	}

	protected List<Variable> createVariables(Map<Object, Object> parameters) {
		if (this.variables == null) {
			int numberOfTasks = (Integer) parameters.get("numberOfTasks");

			this.variables = new ArrayList<>();

			for (int i = 0; i < numberOfTasks; i++) {
				Map<String, Object> params = new HashMap<>();

				double scheduledTime = ((double[]) parameters.get("scheduledTimes"))[i];
				double duration = ((double[]) parameters.get("durations"))[i];
				params.put("id", i);
				params.put("duration", duration);
				params.put("scheduledTime", scheduledTime);

				Variable variable = new Task();
				variable.setValue(params);
				variables.add(variable);
			}


			variables = setVariableNeighbours(variables, (int[][]) parameters.get("tasks"));
			Collections.sort(variables);
		}
		return this.variables;
	}

	/**
	 * This method sets the neighbours for each variable: predecessors and descendents
	 *
	 * @param variables List		Set of variables
	 * @param tasks     int[][]	   	Relationship matrix
	 * @return variables    The same variables with set descendants and predecessors for each variable.
	 * @implNote tasks[i][j] = 1 means Variable i is predecessors of Variable j,
	 * and Variable j is descendent of Variable i
	 */
	protected List<Variable> setVariableNeighbours(List<Variable> variables, int[][] tasks) {
		int size = variables.size();

		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++) {
				if (i != j) {
					Task ti = (Task) variables.get(i);
					Task tj = (Task) variables.get(j);
					if (tasks[i][j] == 1) {
						ti.getDescendants().add(tj);
						tj.getPredecessors().add(ti);
					} else if (tasks[j][i] == 1) {
						ti.getPredecessors().add(tj);
						tj.getDescendants().add(ti);
					}
				}
			}
		}
		return variables;
	}

	/**
	 * This method sets the resources and skills for each variable. Only adds the required skills
	 *
	 * @param variables              List		Set of variables
	 * @param treq                   int[][]     Task-Skill matrix
	 * @param lexp                   double[][]	Resource-Skill matrix
	 * @param numberOfSkills         int			The maximum number of skills a variable may have
	 * @param numberOfHumanResources int			The total number of resources
	 * @return variables        The same variables with set skills for each variable.
	 * @implNote skills[i][j] = 1 means Variable i needs Skill j
	 */
	protected List<Variable> setResourcesAndSkills(List<Variable> variables, int[][] treq, double[][] lexp, double[] humanCosts, int numberOfSkills, int numberOfHumanResources) {
		for (Variable variable : variables) {
			for (int i = 0; i < numberOfHumanResources; i++) {
				SkillsInResource skillsInResource = new SkillsInResource();
				skillsInResource.setResource(new Resource(i, Resource.TYPE.HUMAN, humanCosts[i]));
				List<Skill> skills = skillsInResource.getRequiredSkills();
				for (int j = 0; j < numberOfSkills; j++) {
					/* Only add skill to resource if the skill is necessary to the task */
					if (treq[((Task) variable).getId()][j] != 0) {
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
