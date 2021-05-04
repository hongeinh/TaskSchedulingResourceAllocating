package algorithm;

import lombok.Getter;
import lombok.Setter;
import operator.Operator;
import problem.Problem;
import solution.Solution;

import java.util.List;

@Setter
@Getter
public abstract class Algorithm {

	protected List<Operator> operators;
	protected int solutionSetSize;

	public abstract List<Solution> executeAlgorithm(Problem problem);

	public void addOperator(Operator operator) {
		this.operators.add(operator);
	}

	public void removeOperator(Operator operator) {
		if(this.operators.contains(operator)) {
			this.operators.remove(operator);
		}
	}

}
