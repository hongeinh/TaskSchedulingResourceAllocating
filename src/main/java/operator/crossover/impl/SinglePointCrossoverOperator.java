package operator.crossover.impl;

import operator.Operator;
import operator.crossover.CrossoverOperator;
import solution.Solution;

import java.util.ArrayList;
import java.util.List;

public class SinglePointCrossoverOperator extends CrossoverOperator {

	@Override
	public Object execute(Object objects) throws CloneNotSupportedException {
		List<Solution> solutions = (List<Solution>) objects;
		int solutionSetSize = solutions.size();
		List<Solution> crossoverSolutions = new ArrayList<>();

		while (crossoverSolutions.size() < solutionSetSize) {
			int parentIndex1 = (int) Math.floor(Math.random()*solutionSetSize);
			int parentIndex2 = (int) Math.floor(Math.random()*solutionSetSize);

			if(parentIndex1 == parentIndex2)
				continue;

			Solution parent1 = solutions.get(parentIndex1);
			Solution parent2 = solutions.get(parentIndex2);

//			Solution child1 = Solution.copy(parent1);
//			Solution child2 = Solution.copy(parent2);


		}
		return crossoverSolutions;
	}
}
