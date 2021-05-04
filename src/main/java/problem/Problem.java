package problem;

import component.Variable;
import solution.Solution;

import java.util.List;
import java.util.Map;


public abstract class Problem {

	protected Map<Object, Object> parameters;

	public Problem (Map<Object, Object> parameters) {
		this.parameters = parameters;
	}

	public abstract double[] evaluate(Solution solution);

	public abstract double[] evaluateConstraints(Solution solution);

	abstract class VariableController {
		public abstract List<Variable> createVariables(Map<Object, Object> parameters);
	}
}
