package component.controller.impl;

import component.variable.Variable;
import representation.Solution;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OptimizedMultiorderTaskSchedulingController extends MultiorderTaskSchedulingController {


	public OptimizedMultiorderTaskSchedulingController() {
		super();
	}

	@Override
	public List<Variable> setupVariableResources(List<Variable> orders, Map<Object, Object> parameters, double k) {

		return orders;
	}

	@Override
	public void recalculateSolutionDetails(List<Solution> offspringSolutions) {
		for (Solution solution: offspringSolutions) {
			List<Variable> orders = solution.getVariables();
			Collections.sort(orders);
			calculateEachTaskTimeInAllOrders(orders, 0);
		}
	}
}
