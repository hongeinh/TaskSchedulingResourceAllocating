package component.controller.impl;

import component.Skill;
import component.SkillsInResource;
import component.Task;
import component.Variable;
import component.controller.helper.MultiorderTaskSchedulingControllerHelper;
import component.resource.Resource;

import java.util.*;

public class MultiorderTaskSchedulingController extends TaskSchedulingResourceAllocatingVariableController {

	private List<List<Variable>> variableOrders = null;

	private MultiorderTaskSchedulingControllerHelper helper;

	public Variable setVariableParameters(Variable variable, double k) {

		if (variableOrders.size() == 1) {
			variable = setVariableTime(variable, k);
			variable = setVariableResource(variable);
		} else if (variables.size() > 1) {
			variable = setVariableTimeAndResouceOtherOrders(variable, k);
		}
		return variable;
	}


	private Variable setVariableTimeAndResouceOtherOrders(Variable variable, double k) {
		// TODO
		int currentOrderId = this.variableOrders.size() - 1;
		List<SkillsInResource> skillsInResources = ((Task) variable).getRequiredSkillsInResources();
		skillsInResources = this.isUseful(skillsInResources);

		variable = setSimilarTimeWithVariableOfPreviousOrder(variable, currentOrderId);
		List<SkillsInResource> humanResources = getSkillInResourceOfType(variable, Resource.TYPE.HUMAN);
		List<SkillsInResource> machineResources = getSkillInResourceOfType(variable, Resource.TYPE.MACHINE);
		Map<String, Object> assignedHumanResourceInfo = getAssignedResource(currentOrderId, humanResources, variable);
		Map<String, Object> assignedMachineResourceInfo = getAssignedResource(currentOrderId, machineResources, variable);

		List<Resource> assignedHumanResource = (List<Resource>) assignedHumanResourceInfo.get("resource");
		List<Resource> assignedMachineResource = (List<Resource>) assignedMachineResourceInfo.get("resource");

		if (assignedHumanResource.isEmpty() && assignedMachineResource.isEmpty()) {
			variable = setVariableTime(variable, k);
			variable = setVariableResource(variable);
		} else {
			List<Integer> assignedHumanResourceVariable = (List<Integer>) assignedHumanResourceInfo.get("task");
			List<Integer> assignedHumanResourceOrder = (List<Integer>) assignedHumanResourceInfo.get("order");
			ArrayList<Double> humanInfo = this.helper.calculateResourceAndStart(humanResources, assignedHumanResource, assignedHumanResourceVariable, assignedHumanResourceOrder);

			List<Integer> assignedMachineResourceVariable = (List<Integer>) assignedMachineResourceInfo.get("task");
			List<Integer> assignedMachineResourceOrder = (List<Integer>) assignedHumanResourceInfo.get("order");
			ArrayList<Double> machineInfo = this.helper.calculateResourceAndStart(machineResources, assignedMachineResource, assignedMachineResourceVariable, assignedMachineResourceOrder);

			double start = Math.max(humanInfo.get(humanInfo.size() - 1),
					machineInfo.get(machineInfo.size() - 1));
			double rand = Math.random() * k;

			((Task) variable).setStart(start);
			start = start + rand;
			((Task) variable).setScheduledTime(start);
		}

		return variable;
	}

	private Map<String, Object> getAssignedResource(int currentOrderId, List<SkillsInResource> resources, Variable currentVariable) {
		List<Resource> assignedResources = new ArrayList<>();

		// assignedResourceInTask[i] = task index in order
		List<Integer> assignedResourceInTask = new ArrayList<>();
		// assignedResourceInOrder[i] = orderId
		List<Integer> assignedResourceInOrder = new ArrayList<>();

		// Check cac task trong tung order
		for (int i = currentOrderId; i >= 0; i--) {
			for (int j = 0; j < this.variableOrders.get(i).size(); j++) {
				Variable previousVar = this.variableOrders.get(i).get(j);
				List<SkillsInResource> skillsInResources = ((Task) previousVar).getRequiredSkillsInResources();
				for (SkillsInResource skillsInResource : skillsInResources) {
					Resource checkResource = skillsInResource.getResource();
					if (this.helper.isRequiredResource(checkResource, resources)) {
						// check xem lieu resource nay da duoc add vao list assigned chua
						// resource chua duoc add vao list assigned
						Optional<Resource> resourceInAssigned = assignedResources.stream()
								.filter(re -> re.getType() == checkResource.getType() && re.getId() == checkResource.getId())
								.findFirst();
						if (!resourceInAssigned.isPresent()) {
							assignedResources.add(new Resource(checkResource.getId(), checkResource.getType()));
							assignedResourceInTask.add(j);
							assignedResourceInOrder.add(i);
						} else {
							// resource da duoc add vao list assigned
							// check thoi gian task chiem dung resource
							// neu thoi gian bat dau su dung dang lon hon scheduled end time cua task thi ignore
							// neu thoi gian ket thuc su dung som hon scheduled start time cua task thi ignore
							// neu start < thoi gian ket thuc < end thi check xem so voi task hien tai cai nao end muon hon.
							Task previousTask = (Task) previousVar;
							Task currentTask = (Task) currentVariable;
							double prevTaskEnd = previousTask.getStart() + previousTask.getDuration();
							if (prevTaskEnd > currentTask.getStart() &&
									prevTaskEnd <= currentTask.getStart() + currentTask.getDuration()) {
								int taskIndex = this.helper.getIndexInAssignedList(assignedResources, resourceInAssigned.get());
								int orderIndex = this.helper.getIndexInAssignedList(assignedResources, resourceInAssigned.get());
								Task var = (Task) this.variableOrders.get(assignedResourceInOrder.get(orderIndex).intValue()).get(assignedResourceInTask.get(taskIndex).intValue());
								if (taskIndex != 1 && orderIndex != 1 &&
										var.getDuration() + var.getDuration() < prevTaskEnd) {
									assignedResourceInTask.set(taskIndex, j);
									assignedResourceInOrder.set(orderIndex, i);
								}
							}
						}
					}
				}
			}
		}
		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("resource", assignedResources);
		returnMap.put("task", assignedResourceInTask);
		returnMap.put("order", assignedResourceInOrder);
		return returnMap;
	}

	private List<SkillsInResource> getSkillInResourceOfType(Variable variable, Resource.TYPE type) {
		List<SkillsInResource> typeResources = new ArrayList<>();
		List<SkillsInResource> taskResources = ((Task) variable).getRequiredSkillsInResources();
		for (SkillsInResource resource : taskResources) {
			if (resource.getResource().getType() == type) {
				typeResources.add(resource);
			}
		}
		return typeResources;
	}


	private Variable setSimilarTimeWithVariableOfPreviousOrder(Variable variable, int currentOrderId) {
		for (Variable variable1 : this.variableOrders.get(currentOrderId - 1)) {
			if (((Task) variable1).getId() == ((Task) variable).getId()) {
				((Task) variable).setStart(((Task) variable1).getStart());
				((Task) variable).setScheduledTime(((Task) variable1).getScheduledTime());
			}
		}
		return variable;
	}


	@Override
	public List<Variable> setVariables(Map<Object, Object> parameters, double k) {
		// Get parameters
		int numberOfSkills = (Integer) parameters.get("numberOfSkills");
		int numberOfHumanResources = (Integer) parameters.get("numberOfHumanResources");
		int numberOfMachineResources = (Integer) parameters.get("numberOfMachineResources");
		int numberOfOrders = (Integer) parameters.get("numberOfOrders");

		double[][] treq = (double[][]) parameters.get("treq");
		double[][] lexp = (double[][]) parameters.get("lexp");
		double[][] mreq = (double[][]) parameters.get("mreq");

		double[] maxDurationList = (double[]) parameters.get("maxDuration");

		// Initialize new orders
		this.variableOrders = new ArrayList<>();

		// Fill orders
		for (int i = 0; i < numberOfOrders; i++) {
			this.variableOrders.add(new ArrayList<>());
			int size = this.variableOrders.size();

			List<Variable> variables = createVariables(parameters);
			variables = setResourcesAndSkills(variables, treq, lexp, mreq, numberOfSkills, numberOfHumanResources, numberOfMachineResources);

			double maxDuration = maxDurationList[size - 1];
			for (Variable variable : variables)
				this.setVariableParameters(variable, k * maxDuration);

			this.variableOrders.get(size - 1).addAll(variables);
		}

		// return ordered list with calculated time and resource
		return joinVariableLists(variableOrders);
	}

	private List<Variable> setResourcesAndSkills(List<Variable> variables, double[][] treq, double[][] lexp, double[][] mreq, int numberOfSkills, int numberOfHumanResources, int numberOfMachineResources) {
		for (Variable variable : variables) {
			int id = ((Task) variable).getId();

			for (int i = 0; i < numberOfHumanResources; i++) {
				SkillsInResource skillsInResource = new SkillsInResource();
				skillsInResource.setResource(new Resource(i, Resource.TYPE.HUMAN));
				List<Skill> skills = new ArrayList<>();
				for (int j = 0; j < numberOfSkills; j++) {
					/* Only add skill to resource if the skill is necessary to the task */
					if (treq[id][j] != 0) {
						skills.add(new Skill(j, lexp[i][j]));
					}
				}
				skillsInResource.setRequiredSkills(skills);
				((Task) variable).getRequiredSkillsInResources().add(skillsInResource);
			}

			for (int i = 0; i < numberOfMachineResources; i++) {
				SkillsInResource skillsInResource = new SkillsInResource();
				skillsInResource.setResource(new Resource(i, Resource.TYPE.MACHINE));
				((Task) variable).getRequiredSkillsInResources().add(skillsInResource);

			}
		}
		return variables;
	}


//	public List<Variable> setOtherResources(List<Variable> variables, double[][] mreq, int numberOfMachineResources) {
//		for (Variable variable : variables) {
//			for (int i = 0; i < numberOfMachineResources; i++) {
//				if (mreq[((Task) variable).getId()][i] != 0) {
//					((Task) variable).getRequiredMachines().add(new Resource(i));
//				}
//			}
//		}
//		return variables;
//	}


	public List<Variable> joinVariableLists(List<List<Variable>> variableOrders) {
		List<Variable> variables = new ArrayList<>();
		for (List<Variable> order : variableOrders) {
			variables.addAll(order);
		}
		return variables;
	}
}
