package problem;

import component.Variable;
import component.controller.VariableController;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import solution.Solution;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public abstract class Problem {

	protected Map<Object, Object> parameters;
	protected VariableController variableController;

	public abstract double[] evaluate(Solution solution);

	public abstract double[] evaluateConstraints(Solution solution);

}
