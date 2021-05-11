package algorithm;

import lombok.Getter;
import lombok.Setter;
import operator.Operator;
import problem.Problem;
import solution.Solution;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public abstract class Algorithm {

	protected List<Operator> operators;
	protected int solutionSetSize;


	public Algorithm() {
		this.operators = new ArrayList<>();
		this.solutionSetSize = 20;
	}

	public Algorithm(int solutionSetSize) {
		this.operators = new ArrayList<>();
		this.solutionSetSize = solutionSetSize;
	}


	public abstract List<Solution> executeAlgorithm(Problem problem) throws CloneNotSupportedException;

	public void addOperator(Operator operator) {
		this.operators.add(operator);
	}

	public void removeOperator(Operator operator) {
		if(this.operators.contains(operator)) {
			this.operators.remove(operator);
		}
	}

}
