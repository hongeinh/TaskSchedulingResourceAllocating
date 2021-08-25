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
import lombok.Getter;
import lombok.Setter;
import representation.Solution;
import utils.NumberUtil;

import java.util.*;
import java.util.stream.Collectors;


@Getter
@Setter
public class TaskSchedulingResourceAllocatingVariableController extends VariableController {

	protected List<HumanResource> allHumanResources;
	protected List<MachineResource> allMachineResources;
	protected List<Task> allTasks;

	public void setAllHumanResources(int numberOfHumanResource) {
		this.allHumanResources = new ArrayList<>();
		for (int i = 0; i < numberOfHumanResource; i++) {
			HumanResource humanResource = HumanResource.builder()
					.id(i)
					.usedTimeFrames(new ArrayList<>())
					.build();
		}
	}

	public void setAllMachineResources(int numberOfMachineResource) {
		this.allMachineResources = new ArrayList<>();
		for (int i = 0; i < numberOfMachineResource; i++) {
			MachineResource machineResource = MachineResource.builder()
					.id(i)
					.usedTimeFrames(new ArrayList<>())
					.build();
		}
	}


	@Override
	public List<Variable> setupVariables(Map<Object, Object> parameters, double k) {

		Order order = Order.builder()
				.id(0)
				.weight(0)
				.totalTimeAllowed(Double.MAX_VALUE)
				.penaltyRate(0)
				.build();
		List<Task> tasks = createTasks(parameters);
		setupAllTasksResources(tasks, parameters);

		assignResourcesToAllTask(tasks, k);

		calculateAllTasksTimes(tasks, k);
		order.setValue(tasks);
		List<Variable> orders = new ArrayList<>();
		orders.add(order);
		return orders;
	}

	// Khong lam gi o day ca
	@Override
	public void recalculateSolutionDetails(List<Solution> offspringSolutions) {

	}

	/**
	 * Create a list of tasks with secured precedence constraints among task.
	 * No resource allocation.
	 */
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

	protected List<Task> setupAllTasksResources(List<Task> tasks, Map<Object, Object> parameters) {

		int numberOfSkills = (Integer) parameters.get("numberOfSkills");
		int numberOfHumanResources = (Integer) parameters.get("numberOfHumanResources");
		int[][] treq = (int[][]) parameters.get("treq");
		double[][] lexp = (double[][]) parameters.get("lexp");
		double[] humanCosts = (double[]) parameters.get("humanCosts");
		double[] machineCosts = (double[]) parameters.get("machineCosts");
		double[][] mreq = (double[][]) parameters.get("mreq");

		tasks = setupHumanResourcesAndSkills(tasks, treq, lexp, humanCosts, numberOfSkills, numberOfHumanResources);
		tasks = setupMachineResources(tasks, (Integer) parameters.get("numberOfMachineResources"), machineCosts, mreq);

		return tasks;
	}

	protected void assignResourcesToAllTask(List<Task> tasks, double k) {
		for (Task task : tasks) {
			assignResourceToEachTask(task);
		}
	}

	protected void calculateAllTasksTimes(List<Task> tasks, double k) {

		for (Task task : tasks) {
			List<Integer> predecessorIndexes = task.getPredecessors();
			List<Task> predecessorTasks = getPredecessorTasks(predecessorIndexes, tasks);
			calculateEachTaskTime(task, predecessorTasks, k);
		}

	}

	private List<Task> getPredecessorTasks(List<Integer> predecessorIndexes, List<Task> tasks) {
		List<Task> predecessorsTasks = new ArrayList<>();
		for (Integer index : predecessorIndexes) {
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

			task.setStart(NumberUtil.floor2DecimalPoints(start));
			double scheduledStart = start + rand * sign;
			task.setScheduledTime(NumberUtil.floor2DecimalPoints(scheduledStart));
		}
	}

	/**
	 * Allocate resource for tasks
	 */
	protected Task assignResourceToEachTask(Task task) {

		List<HumanResource> humanResources = task.getRequiredHumanResources();
		this.checkHumanResourcesUseful(humanResources);
		this.randomAssignResource(humanResources);
//		this.bestFitAssignHumanResource(humanResources, task.getSkills());

		List<MachineResource> machineResources = task.getRequiredMachinesResources();
		this.randomAssignResource(machineResources);
//		this.bestFitAssignMachineResource(machineResources);

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
			return;
		}
		List<? extends Resource> usefulResources = resources.stream()
				.filter(resource -> resource.getStatus() == STATUS.USEFUL)
				.collect(Collectors.toList());
		int numOfAssignedResource = 0;
		while (numOfAssignedResource == 0) {
			int random = NumberUtil.getRandomNumber(0, usefulResources.size());
			int randomResourceId = usefulResources.get(random).getId();
			for (Resource resource : resources) {
				if (resource.getId() == randomResourceId) {
					resource.setStatus(STATUS.ASSIGNED);
					numOfAssignedResource++;
				}
			}
		}
	}

	private void bestFitAssignHumanResource(List<HumanResource> resources, List<Integer> skills) {
		// TODO
		if (resources.isEmpty())
			return;
		Comparator<HumanResource> humanResourceComparator = Comparator.comparing(HumanResource::getAverageExp, Comparator.reverseOrder());
		List<HumanResource> usefulResources = resources.stream()
				.filter(resource -> resource.getStatus() == STATUS.USEFUL)
				.sorted(humanResourceComparator)
				.collect(Collectors.toList());

	}

	private void bestFitAssignMachineResource(List<? extends Resource> resources) {
		if (resources.isEmpty())
			return;
		// TODO: assign best fit dua tren available

	}

	/**
	 * This method sets the resources and skills for each variable. Only adds the required skills
	 *
	 * @param tasks                  List		Set of tasks
	 * @param treq                   int[][]     Task-Skill matrix
	 * @param lexp                   double[][]	Resource-Skill matrix
	 * @param numberOfSkills         int			The maximum number of skills a variable may have
	 * @param numberOfHumanResources int			The total number of resources
	 * @return variables        The same variables with set skills for each variable.
	 * @implNote skills[i][j] = 1 means Variable i needs Skill j
	 */
	private List<Task> setupHumanResourcesAndSkills(List<Task> tasks, int[][] treq, double[][] lexp, double[] humanCosts, int numberOfSkills, int numberOfHumanResources) {
		for (Task currentTask : tasks) {
			int currentTaskId = currentTask.getId();
			currentTask.setSkills(new ArrayList<>());
			List<HumanResource> humanResources = currentTask.getRequiredHumanResources();

			for (int i = 0; i < numberOfSkills; i++) {
				if (treq[currentTaskId][i] != 0) {
					currentTask.getSkills().add(i);
				}
			}

			// Add all human resources to task and assign status with skills
			for (int i = 0; i < numberOfHumanResources; i++) {
				HumanResource resource = HumanResource.builder()
						.id(i)
						.cost(humanCosts[i])
						.status(STATUS.NOT_USEFUL)
						.build();

				// Add skills to the resource based on task's need
				List<Skill> skills = new ArrayList<>();
				for (Integer skillId : currentTask.getSkills()) {
					Skill skill = Skill.builder()
							.id(skillId)
							.experienceLevel(lexp[i][skillId])
							.build();
					skills.add(skill);
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
