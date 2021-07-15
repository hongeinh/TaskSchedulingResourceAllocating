package component.controller.impl;

import common.STATUS;
import component.controller.VariableController;
import component.resource.HumanResource;
import component.resource.MachineResource;
import component.resource.Resource;
import component.skill.Skill;
import component.variable.Variable;
import component.variable.impl.Task;

import java.util.*;


public class TaskSchedulingResourceAllocatingVariableController extends VariableController {

	protected Variable setupVariableParameters(Variable variable, List<Variable> variables, double k) {
		variable = calculateVariableTime(variable, variables, k);
		variable = setupResourceForTemplateVariable(variable);
		return variable;
	}

	protected Variable calculateVariableTime(Variable variable, List<Variable> variables, double k) {
		Task currentVariable = (Task) variable;
		List<Integer> predescessorIndexes = currentVariable.getPredecessors();

		for (Integer predecessorIndex : predescessorIndexes) {
			Task predecessor = (Task) variables.get(predecessorIndex);
			double start = 0;
			if (currentVariable.getStart() > predecessor.getStart() + predecessor.getDuration()) {
				start = currentVariable.getStart();
			} else {
				start = (predecessor.getStart() + predecessor.getDuration());
			}

			double rand = Math.random() * k;
			double sign = Math.random() > 0.5 ? 1 : -1;

			((Task) variable).setStart(start);
			double scheduledStart = start + rand * sign;
			((Task) variable).setScheduledTime(scheduledStart);
		}
		return variable;
	}


	protected Variable setupResourceForTemplateVariable(Variable variable) {

		List<HumanResource> humanResources = ((Task) variable).getRequiredHumanResources();
		this.isHumanResourceUseful(humanResources);
		this.randomizeUseful(humanResources);

		List<MachineResource> machineResources =  ((Task) variable).getRequiredMachinesResources();
		this.randomizeUseful(machineResources);

		((Task) variable).setRequiredHumanResources(humanResources);
		((Task) variable).setRequiredMachinesResources(machineResources);

		return variable;
	}


	private void randomizeUseful(List<? extends Resource> resources) {
		int countAssignedResource = 0;
		while (countAssignedResource == 0) {
			for (Resource resource : resources) {
				if (resource.getStatus() == STATUS.USEFUL) {
					double rand = Math.random();
					if (rand >= 0.75) {
						resource.setStatus(STATUS.ASSIGNED);
						countAssignedResource++;
					}
				}
			}
		}
	}

	private void isHumanResourceUseful(List<HumanResource> resources) {
		for (HumanResource resource : resources) {
			List<Skill> skills = resource.getSkills();
			for (Skill rSkill : skills) {
				if (rSkill.getExperienceLevel() > 0) {
					resource.setStatus(STATUS.USEFUL);
					break;
				}
			}
		}
	}

	@Override
	public List<Variable> setupVariables(Map<Object, Object> parameters, double k) {

		List<Variable> variables = createTemplateVariables(parameters);

		int numberOfSkills = (Integer) parameters.get("numberOfSkills");
		int numberOfHumanResources = (Integer) parameters.get("numberOfHumanResources");
		int[][] treq = (int[][]) parameters.get("treq");
		double[][] lexp = (double[][]) parameters.get("lexp");
		double[] humanCosts = (double[]) parameters.get("humanCosts");
		double[] machineCosts = (double[]) parameters.get("machineCosts");
		double[][] mreq = (double[][]) parameters.get("mreq");

		variables = setupResourcesAndSkillsForTemplateVariables(variables, treq, lexp, humanCosts, numberOfSkills, numberOfHumanResources);
		variables = setupMachineResources(variables, (Integer) parameters.get("numberOfMachineResources"), machineCosts, mreq);

		int maxDuration = (int) parameters.get("maxDuration");
		for (Variable variable : variables)
			this.setupVariableParameters(variable, variables, k * maxDuration);

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

			Task variable = Task.builder()
					.descendants(new ArrayList<>())
					.predecessors(new ArrayList<>())
					.requiredHumanResources(new ArrayList<>())
					.requiredMachinesResources(new ArrayList<>())
					.build();
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
			Task ti = (Task) variables.get(i);
			for (int j = i + 1; j < size; j++) {
				if (i != j) {
					Task tj = (Task) variables.get(j);
					if (tasks[i][j] == 1) {
						ti.getDescendants().add(j);
						tj.getPredecessors().add(i);
					} else if (tasks[j][i] == 1) {
						ti.getPredecessors().add(j);
						tj.getDescendants().add(i);
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
				HumanResource resource = HumanResource.builder()
						.id(i)
						.cost(humanCosts[i])
						.status(STATUS.NOT_USEFUL)
						.build();
				List<Skill> skills = new ArrayList<>();
				for (int j = 0; j < numberOfSkills; j++) {
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

	private List<Variable> setupMachineResources(List<Variable> tasks, Integer numberOfMachineResources, double[] machineCosts, double[][] mreq) {
		for (Variable variable : tasks) {
			List<MachineResource> machineResource = new ArrayList<>();
			int id = ((Task) variable).getId();
			for (int i = 0; i < numberOfMachineResources; i++) {
				if (mreq[id][i] != 0) {
					MachineResource resource = MachineResource.builder()
							.id(i)
							.status(STATUS.USEFUL)
							.cost(machineCosts[i])
							.build();
					machineResource.add(resource);
				}
			}
			((Task) variable).setRequiredMachinesResources(machineResource);
		}

		return tasks;
	}
}
