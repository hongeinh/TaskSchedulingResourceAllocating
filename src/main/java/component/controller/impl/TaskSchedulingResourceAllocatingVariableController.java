package component.controller.impl;

import common.STATUS;
import component.controller.VariableController;
import component.resource.HumanResource;
import component.resource.MachineResource;
import component.resource.Resource;
import component.skill.Skill;
import component.variable.Variable;
import component.variable.impl.Order;
import component.variable.impl.Task;
import representation.Solution;
import utils.NumberUtil;

import java.util.*;
import java.util.stream.Collectors;


public class TaskSchedulingResourceAllocatingVariableController extends VariableController {

	@Override
	public List<Variable> setupVariables(Map<Object, Object> parameters, double k) {

//		Order order = new Order(0, 0, Double.MAX_VALUE);
		Order order = Order.builder()
				.id(0)
				.weight(0)
				.totalTimeAllowed(Double.MAX_VALUE)
				.penaltyRate(0)
				.build();
		List<Task> tasks = createTasks(parameters);
		setupAllTasksResources(tasks, parameters, k);

		double maxDuration = (double) parameters.get("maxDuration");
		assignResourcesToAllTask(tasks, k * maxDuration);

		calculateAllTasksTimes(tasks, k);
		order.setValue(tasks);
		List<Variable> orders = new ArrayList<>();
		orders.add(order);
		return orders;
	}

	@Override
	public void recalculateSolutionDetails(List<Solution> offspringSolutions) {

	}

	/**
	 * Create a list of tasks with secured precedence constraints among task.
	 * No resource allocation.
	 * */
	protected List<Task> createTasks(Map<Object, Object> parameters) {
		List<Task> tasks = new ArrayList<>();
		int numberOfTasks = (Integer) parameters.get("numberOfTasks");

		for (int i = 0; i < numberOfTasks; i++) {
			Map<String, Object> params = new HashMap<>();

			double scheduledTime = ((double[]) parameters.get("scheduledTimes"))[i];
			double duration = ((double[]) parameters.get("durations"))[i];
			params.put("id", i);
			params.put("duration", duration);
			params.put("scheduledTime", scheduledTime);

			Task task = Task.builder()
					.descendants(new ArrayList<>())
					.predecessors(new ArrayList<>())
					.requiredHumanResources(new ArrayList<>())
					.requiredMachinesResources(new ArrayList<>())
					.build();
			task.setTaskAttributes(params);
			tasks.add(task);
		}

		tasks = setupAllTasksNeighbours(tasks, (int[][]) parameters.get("tasks"));
		Collections.sort(tasks);
		return tasks;
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
	protected List<Task> setupAllTasksNeighbours(List<Task> variables, int[][] tasks) {
		int size = variables.size();

		for (int i = 0; i < size; i++) {
			Task ti = variables.get(i);
			for (int j = i + 1; j < size; j++) {
				if (i != j) {
					Task tj = variables.get(j);
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

	protected List<Task> setupAllTasksResources(List<Task> tasks, Map<Object, Object> parameters, double k) {

		int numberOfSkills = (Integer) parameters.get("numberOfSkills");
		int numberOfHumanResources = (Integer) parameters.get("numberOfHumanResources");
		int[][] treq = (int[][]) parameters.get("treq");
		double[][] lexp = (double[][]) parameters.get("lexp");
		double[] humanCosts = (double[]) parameters.get("humanCosts");
		double[] machineCosts = (double[]) parameters.get("machineCosts");
		double[][] mreq = (double[][]) parameters.get("mreq");

		tasks = setupHumanResourcesAndSkillsForTasks(tasks, treq, lexp, humanCosts, numberOfSkills, numberOfHumanResources);
		tasks = setupMachineResources(tasks, (Integer) parameters.get("numberOfMachineResources"), machineCosts, mreq);

		return tasks;
	}

	protected void assignResourcesToAllTask(List<Task> tasks, double k) {
		for (Task task: tasks) {
			assignResourceToEachTask(task);
		}
	}

	protected void calculateAllTasksTimes(List<Task> tasks, double k) {

		for (Task task: tasks) {
			List<Integer> predecessorIndexes = task.getPredecessors();
			List<Task> predecessorTasks = getPredecessorTasks(predecessorIndexes, tasks);
			calculateEachTaskTime(task, predecessorTasks, k);
		}

	}

	private List<Task> getPredecessorTasks(List<Integer> predecessorIndexes, List<Task> tasks) {
		List<Task> predecessorsTasks = new ArrayList<>();
		for (Integer index: predecessorIndexes) {
			predecessorsTasks.add(tasks.get(index));
		}
		return predecessorsTasks;
	}

	private void calculateEachTaskTime(Task task, List<Task> predecessorTasks, double k) {

		for (Task predecessor : predecessorTasks) {
			double start = 0;
			if (task.getStart() > predecessor.getStart() + predecessor.getDuration()) {
				start = task.getStart();
			} else {
				start = (predecessor.getStart() + predecessor.getDuration());
			}

			double rand = NumberUtil.getRandomNumber(0, (int) Math.ceil(k));
			double sign = Math.random() > 0.5 ? 1 : -1;

			task.setStart(start);
			double scheduledStart = start + rand * sign;
			task.setScheduledTime(scheduledStart);
		}
	}

	/**
	 * Allocate resource for tasks
	 *
	 * */
	protected Task assignResourceToEachTask(Task task) {

		List<HumanResource> humanResources = task.getRequiredHumanResources();
		this.checkHumanResourcesUseful(humanResources);
		this.randomAssignResource(humanResources);

		List<MachineResource> machineResources =  task.getRequiredMachinesResources();
		this.randomAssignResource(machineResources);

		task.setRequiredHumanResources(humanResources);
		task.setRequiredMachinesResources(machineResources);

		return task;
	}

	private void checkHumanResourcesUseful(List<HumanResource> resources) {
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


	private void randomAssignResource(List<? extends Resource> resources) {
		if (resources.isEmpty()) {
			return ;
		}
		List<? extends Resource> usefulResources = resources.stream()
				.filter(resource -> resource.getStatus() == STATUS.USEFUL)
				.collect(Collectors.toList());

		int random = NumberUtil.getRandomNumber(0, usefulResources.size());
		int randomResourceId = usefulResources.get(random).getId();
		for (Resource resource: resources) {
			if (resource.getId() == randomResourceId)
				resource.setStatus(STATUS.ASSIGNED);
		}

	}

	/**
	 * This method sets the resources and skills for each variable. Only adds the required skills
	 *
	 * @param tasks              	List		Set of tasks
	 * @param treq                   int[][]     Task-Skill matrix
	 * @param lexp                   double[][]	Resource-Skill matrix
	 * @param numberOfSkills         int			The maximum number of skills a variable may have
	 * @param numberOfHumanResources int			The total number of resources
	 * @return variables        The same variables with set skills for each variable.
	 * @implNote skills[i][j] = 1 means Variable i needs Skill j
	 */
	private List<Task> setupHumanResourcesAndSkillsForTasks(List<Task> tasks, int[][] treq, double[][] lexp, double[] humanCosts, int numberOfSkills, int numberOfHumanResources) {
		for (Task currentTask : tasks) {
			int currentTaskId = currentTask.getId();

			List<HumanResource> humanResources = currentTask.getRequiredHumanResources();

			// Add all human resources to task and assign status with skills
			for (int i = 0; i < numberOfHumanResources; i++) {
				HumanResource resource = HumanResource.builder()
						.id(i)
						.cost(humanCosts[i])
						.status(STATUS.NOT_USEFUL)
						.build();

				// Add skills to the resource based on task's need
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
				humanResources.add(resource);
			}
		}
		return tasks;
	}

	protected List<Task> setupMachineResources(List<Task> tasks, Integer numberOfMachineResources, double[] machineCosts, double[][] mreq) {
		for (Task task : tasks) {
			List<MachineResource> machineResource = new ArrayList<>();
			int id = task.getId();
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
			task.setRequiredMachinesResources(machineResource);
		}

		return tasks;
	}
}
