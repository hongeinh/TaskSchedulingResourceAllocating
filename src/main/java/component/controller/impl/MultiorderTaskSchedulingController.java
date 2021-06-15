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

	public Variable setVariableParameters(Variable variable, double k) {
		if (variableOrders.size() == 1) {
			variable = setVariableTime(variable, k);
			variable = setVariableResource(variable);
		} else {
			variable = setVariableTimeAndResouceOtherOrders(variable, k);
		}
		return variable;
	}


	private Variable setVariableTimeAndResouceOtherOrders(Variable variable, double k) {
		// TODO
		int currentOrder = this.variableOrders.size();

		return variable;
	}

	private Resource.STATUS checkResourceStatus() {
		// TODO
		return null;
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
		ArrayList<double[][]> mreq = (ArrayList<double[][]>) parameters.get("mreq");

		double[] maxDurationList = (double[]) parameters.get("maxDuration");

		// Initialize new orders and resource pool
		this.variableOrders = new ArrayList<>();


		// Fill orders
		for (int i = 0; i < numberOfOrders; i++) {
			this.variableOrders.add(new ArrayList<>());
			int size = this.variableOrders.size();

			List<Variable> variables = createVariables(parameters);
			variables = setResourcesAndSkills(variables, treq.get(size - 1), lexp.get(size - 1), numberOfSkills, numberOfHumanResources);
			variables = setOtherResources(variables, mreq.get(size - 1), numberOfMachineResources);
			double maxDuration = maxDurationList[size - 1];
			for (Variable variable : variables)
				this.setVariableParameters(variable, k * maxDuration);

			this.variableOrders.get(size - 1).addAll(variables);
		}

		return variables;
	}

	public List<Variable> setOtherResources(List<Variable> variables, double[][] mreq, int numberOfMachineResources) {
		for (Variable variable : variables) {
			for (int i = 0; i < numberOfMachineResources; i++) {
				if (mreq[((Task) variable).getId()][i] != 0) {
					((Task) variable).getRequiredMachines().add(new Resource(i));
				}
			}
		}
		return variables;
	}
}
