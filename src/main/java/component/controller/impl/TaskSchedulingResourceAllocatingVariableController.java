package component.controller.impl;

import common.STATUS;
import common.TYPE;
import component.controller.VariableController;
import component.resource.Resource;
import component.skill.Skill;
import component.variable.Variable;
import component.variable.impl.Task;

import java.util.*;


public class TaskSchedulingResourceAllocatingVariableController extends VariableController {

	protected List<Variable> variables = null;

	protected Variable setVariableParameters(Variable variable, double k) {
		variable = setVariableTime(variable, k);
		variable = setupResourceForTemplateVariable(variable);
		return variable;
	}

	protected Variable setVariableTime(Variable variable, double k) {
//		Task currentVariable = (Task) variable;
//		List<Integer> predescessors = currentVariable.getPredecessors();
//
//		for (Variable pre : predescessors) {
//			Task predecessor = (Task) pre;
//			double start = 0;
//			if (currentVariable.getStart() > predecessor.getStart() + predecessor.getDuration()) {
//				start = currentVariable.getStart();
//			} else {
//				start = (predecessor.getStart() + predecessor.getDuration());
//			}
//
//			double rand = Math.random() * k;
//
//			((Task) variable).setStart(start);
//			start = start + rand;
//			((Task) variable).setScheduledTime(start);
//		}
		return variable;
	}


	protected Variable setupResourceForTemplateVariable(Variable variable) {

//		List<SkillsInResource> skillsInResources = ((Task) variable).getRequiredSkillsInResources();
//		skillsInResources = this.isUseful(skillsInResources);
//
//		for (SkillsInResource skillsInResource : skillsInResources) {
//			if (skillsInResource.getResource().getStatus() == Resource.STATUS.USEFUL) {
//				double rand = Math.random();
//				if (rand >= 0.5)
//					skillsInResource.getResource().setStatus(Resource.STATUS.ASSIGNED);
//				else
//					skillsInResource.getResource().setStatus(Resource.STATUS.NOT_ASSIGNED);
//			}
//		}
//
//		((Task) variable).setRequiredSkillsInResources(skillsInResources);
		return variable;
	}

	protected List<Resource> isUseful(List<Resource> skillsInResources) {
		for (Resource skillsInResource : skillsInResources) {
			List<Skill> skills = skillsInResource.getSkills();
			for (Skill rSkill : skills) {
				if (rSkill.getExperienceLevel() > 0) {
					skillsInResource.setStatus(STATUS.USEFUL);
					break;
				}
			}
		}
		return skillsInResources;
	}


	@Override
	public List<Variable> setupVariables(Map<Object, Object> parameters, double k) {

		List<Variable> variables = createTemplateVariables(parameters);

		int numberOfSkills = (Integer) parameters.get("numberOfSkills");
		int numberOfHumanResources = (Integer) parameters.get("numberOfHumanResources");
		int[][] treq = (int[][]) parameters.get("treq");
		double[][] lexp = (double[][]) parameters.get("lexp");
		double[] humanCosts = (double[]) parameters.get("humanCosts");

		variables = setupResourcesAndSkillsForTemplateVariables(variables, treq, lexp, humanCosts, numberOfSkills, numberOfHumanResources);

		int maxDuration = (int) parameters.get("maxDuration");
		for (Variable variable : variables)
			this.setVariableParameters(variable, k * maxDuration);

		return variables;
	}

	protected List<Variable> createTemplateVariables(Map<Object, Object> parameters) {
		List<Variable> variables = new ArrayList<>();
		int numberOfTasks = (Integer) parameters.get("numberOfTasks");


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


		variables = setupTemplateVariablesNeighbours(variables, (int[][]) parameters.get("tasks"));
		Collections.sort(variables);
		return variables;
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
	protected List<Variable> setupTemplateVariablesNeighbours(List<Variable> variables, int[][] tasks) {
		int size = variables.size();

		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++) {
				if (i != j) {
					Task ti = (Task) variables.get(i);
					Task tj = (Task) variables.get(j);
					if (tasks[i][j] == 1) {
						ti.getDescendants().add(tj.getId());
						tj.getPredecessors().add(ti.getId());
					} else if (tasks[j][i] == 1) {
						ti.getPredecessors().add(tj.getId());
						tj.getDescendants().add(ti.getId());
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
	protected List<Variable> setupResourcesAndSkillsForTemplateVariables(List<Variable> variables, int[][] treq, double[][] lexp, double[] humanCosts, int numberOfSkills, int numberOfHumanResources) {
		for (Variable variable : variables) {
			Task currentTask = (Task) variable;
			int currentTaskId = currentTask.getId();

			for (int i = 0; i < numberOfHumanResources; i++) {
				Resource resource = Resource.builder()
						.id(i)
						.type(TYPE.HUMAN)
						.cost(humanCosts[i])
						.build();
				List<Skill> skills = new ArrayList<>();
				for (int j = 0; j < numberOfSkills; j++) {
					/* Only add skill to resource if the skill is necessary to the task */
					if (treq[currentTaskId][j] != 0) {
						Skill skill = Skill.builder()
								.id(j)
								.experienceLevel(lexp[i][j])
								.build();
						skills.add(skill);
					}
				}
				resource.setSkills(skills);
				((Task) variable).getRequiredHumanResources().add(resource);
			}
		}
		return variables;
	}

	private boolean isResourceUsed(int resourceId, TYPE resourceType, List<Resource> resources) {
		if (resourceType == TYPE.HUMAN) {
			for (Resource resource : resources) {
				if (resource.getId() == resourceId) {
					return true;
				}
			}
		} else if (resourceType == TYPE.MACHINE) {
			for (Resource resource : resources) {
				if (resource.getId() == resourceId) {
					return true;
				}
			}
		}
		return false;
	}
}
