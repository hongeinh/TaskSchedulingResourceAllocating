package operator.crossover.impl;

import operator.Operator;
import operator.crossover.CrossoverOperator;

public class SinglePointCrossoverOperator extends CrossoverOperator {

	public SinglePointCrossoverOperator() {
		super();
	}

	@Override
	public Object execute(Object solutions) {
		return solutions;
	}
}
