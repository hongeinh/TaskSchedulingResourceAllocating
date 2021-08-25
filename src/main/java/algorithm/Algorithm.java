package algorithm;

import lombok.Getter;
import lombok.Setter;
import operator.Operator;
import problem.Problem;
import representation.Solution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public abstract class Algorithm {

	protected List<Operator> operators;
	protected int eliteSetSize;
	protected int eliteRate;

	protected double geneMutationProbability;


	public Algorithm() {
		this.operators = new ArrayList<>();
	}


	public abstract List<Solution> executeAlgorithm(Problem problem) throws CloneNotSupportedException, IOException;

	public void addOperator(Operator operator) {
		this.operators.add(operator);
	}
}
