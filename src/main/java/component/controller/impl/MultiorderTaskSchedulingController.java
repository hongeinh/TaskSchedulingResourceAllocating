package component.controller.impl;

import component.Skill;
import component.SkillsInResource;
import component.Task;
import component.Variable;
import component.resource.Resource;
import component.resource.ResourcePool;

import java.util.*;

public class MultiorderTaskSchedulingController extends TaskSchedulingResourceAllocatingVariableController {

	private List<List<Variable>> variableOrders = null;

	private ResourcePool resourcePool = null;

	public Variable setVariableTime(Variable variable, double  k) {
		List<Variable> predescessors = ((Task) variable).getPredecessors();
		Task currentVariable = (Task) variable;
		for (Variable pre: predescessors) {
			Task predecessor = (Task) pre;
			double start = 0;
			if(currentVariable.getStart() > predecessor.getStart() + predecessor.getDuration()) {
				start = currentVariable.getStart();
			} else {
				start = (predecessor.getStart() + predecessor.getDuration());
			}

			double rand = Math.random()*k;

			((Task) variable).setStart(start);
			start = start + rand;
			((Task) variable).setScheduledTime(start);
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

	@Override
	public List<Variable> setVariables(Map<Object, Object> parameters, double k) {

		// Get parameters
		int numberOfSkills = (Integer) parameters.get("numberOfSkills");
		int numberOfHumanResources = (Integer) parameters.get("numberOfHumanResources");
		int numberOfMachineResources = (Integer) parameters.get("numberOfMachineResources");
		int numberOfOrders = (Integer) parameters.get("numberOfOrders");
		ArrayList<double[][]> treq = (ArrayList<double[][]>) parameters.get("treq");
		ArrayList<double[][]> lexp = (ArrayList<double[][]>) parameters.get("lexp");
		double[] maxDurationList = (double[]) parameters.get("maxDuration");

		// Initialize new orders and resource pool
		this.variableOrders = new ArrayList<>();
		this.resourcePool = new ResourcePool(numberOfHumanResources, numberOfMachineResources);

		// Fill resource pool


		// Fill orders
		for (int i = 0; i < numberOfOrders; i++) {
			this.variableOrders.add(new ArrayList<>());
			int size = this.variableOrders.size();

			List<Variable> variables = createVariables(parameters);
			variables = setResourcesAndSkills(variables, treq.get(size - 1), lexp.get(size - 1), numberOfSkills, numberOfHumanResources);

			double maxDuration = maxDurationList[size - 1];
			for (Variable variable : variables)
				this.setVariableParameters(variable, k * maxDuration);

			this.variableOrders.get(size - 1).addAll(variables);
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
	 * @param numberOfHumanResources int			The total number of resources
	 * @return variables        The same variables with set skills for each variable.
	 *
	 *
	 * @implNote skills[i][j] = 1 means Variable i needs Skill j
	 */
	public List<Variable> setResourcesAndSkills(List<Variable> variables, double[][] treq, double[][] lexp, int numberOfSkills, int numberOfHumanResources) {
		for (Variable variable : variables) {
			for (int i = 0; i < numberOfHumanResources; i++) {
				SkillsInResource skillsInResource = new SkillsInResource();
				skillsInResource.setResource(new Resource(i));
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
