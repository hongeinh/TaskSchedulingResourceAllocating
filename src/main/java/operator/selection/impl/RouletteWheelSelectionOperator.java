package operator.selection.impl;

import operator.selection.SelectionOperator;
import representation.Solution;

import java.util.ArrayList;
import java.util.List;

public class RouletteWheelSelectionOperator extends SelectionOperator {

	/**
	 * Applies the Roulette Wheel Selection Method.
	 * Each chromosome will be given a probability, and those with higher ranks have greater chance of being chosen
	 * Randomizes a number r between 0.1
	 * Continues the selection and adds up the probabilities until r value is reached.
	 * */
	public Object execute(Object objects) {
		List<Solution> solutions = (List<Solution>) objects;
		List<Solution> chosenSolutions = new ArrayList<>();
 		double random = Math.random();
 		double fitnessSum = 0;

 		// Calculate the fitness sum
		for (Solution solution: solutions) {
			fitnessSum += solution.getFitness()[0];
		}
		// Calculate the probabilityDistribution for each solution and choose solutions
		double totalProbability = 0;
		for (Solution solution: solutions) {
			double probabilityDistribution = solution.getFitness()[0]/fitnessSum;
			chosenSolutions.add(solution);
			totalProbability += probabilityDistribution ;
			if (totalProbability >= random)
				break;
		}
		return chosenSolutions;
	}

}
