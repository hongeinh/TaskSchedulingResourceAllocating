package operator.mutation;

import operator.Operator;

public class MutationOperator extends Operator {

	public MutationOperator() {
		super();
	}

	@Override
	public Object execute(Object solutions) {
		return solutions;
	}
}
