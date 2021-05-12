package comparator;

import solution.Solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankingAndCrowdingDistanceComparator {


	/**
	 * Computes the rank for each solution in the list based on the objectives
	 *
	 * */
	public List<Solution> computeRankAndDistance(List<Solution> solutions) {
		// Step 1: Initialization step
		System.out.println("--- Step 1: Initialization step");
		int solutionSetSize = solutions.size();
		List<Solution> rankSortedSolutions = new ArrayList<>();
		List<Solution> currentFront = new ArrayList<>();


		int frontCounter = 1;

		// Step 2: Calculate the dominated solution list and domination count for each solution
		System.out.println("--- Step 2: Calculate the dominated solution list and domination count for each solution");
		for (int i = 0; i < solutionSetSize; i++) {
			// This set contains the solutions that are dominated by the currently considered solution
			List<Solution> dominatedSolutions = new ArrayList<>();
			int dominationCount = 0;
			for (int j = 0; j < solutionSetSize; j++) {
				if (i != j) {

					if(solutions.get(i).compareTo(solutions.get(j)) > 0) {
						// Solution i dominates solution j
						dominatedSolutions.add(solutions.get(j));
					} else if (solutions.get(i).compareTo(solutions.get(j)) < 0) {
						// Solution i is dominated by solution j
						dominationCount++;
					}
				}
			}

			solutions.get(i).setDominatedSolutions(dominatedSolutions);
			solutions.get(i).setDominationCount(dominationCount);

			// Step 2b: If the dominationCount of one solution is zero, it is put into the first rank
			if (dominationCount == 0) {
				System.out.println("------ Step 2b: If the dominationCount of one solution is zero, it is put into the first rank");
				double []fitness = solutions.get(i).getFitness();
				fitness[0] = frontCounter;
				solutions.get(i).setFitness(fitness);
				currentFront.add(solutions.get(i));
			}
		}

		// Step 3: Compute the crowding distance value for each solution and sort them.
		System.out.println("--- Step 3: Compute the crowding distance value for each solution and sort them.");
		rankSortedSolutions.addAll(sortByDistance(computeDistance(currentFront)));

		// Step 4: Find fronts for other solutions
		System.out.println("--- Step 4: Find fronts for other solutions");
		while (!currentFront.isEmpty()) {
			List<Solution> nextFront = new ArrayList<>();
			for (Solution currentSolution: currentFront) {
				for (Solution solution1: currentSolution.getDominatedSolutions()) {
					solution1.setDominationCount(solution1.getDominationCount()-1);
					if (solution1.getDominationCount() == 0) {
						double []fitness = solution1.getFitness();
						fitness[0] = frontCounter + 1;
						solution1.setFitness(fitness);
						nextFront.add(solution1);
					}
				}
			}
			frontCounter++;
			System.out.println("--------- Rank " + frontCounter);
			rankSortedSolutions.addAll(sortByDistance(computeDistance(nextFront)));
			currentFront = nextFront;
		}
		return rankSortedSolutions;
	}

	/**
	 * Computes the density around each solution in the list (assume that they are in the same front)
	 *
	 * */
	public List<Solution> computeDistance(List<Solution> solutions) {
		int solutionSetSize = solutions.size();
		int objectiveSize = solutions.get(0).getObjectives().length;

		// Initialize distance
		for (Solution solution: solutions) {
			double []fitness = solution.getFitness();
			fitness[1] = 0;
			solution.setFitness(fitness);
		}

		for (int i = 0; i < objectiveSize; i++) {
			solutions = sortByObjectiveValue(solutions, i);
			// Set distance for the solution with smallest objective value
			double []fitness = solutions.get(0).getFitness();
			fitness[1] = Double.MAX_VALUE;
			solutions.get(0).setFitness(fitness);

			// Set distance for the solution with smallest objective value
			fitness = solutions.get(solutionSetSize - 1).getFitness();
			fitness[1] = Double.MAX_VALUE;
			solutions.get(solutionSetSize - 1).setFitness(fitness);

			for (int j = 1; j < solutionSetSize - 1; j++) {
				fitness = solutions.get(j).getFitness();
				fitness[1] = fitness[1] + (solutions.get(j+1).getObjectives()[1] - solutions.get(j - 1).getObjectives()[1]) /
						(solutions.get(solutionSetSize - 1).getObjectives()[1] - solutions.get(0).getObjectives()[1]);
				solutions.get(j).setFitness(fitness);
			}
		}

		return solutions;
	}

	/**
	 * Sorts a list of solutions in ascending order with respect to the objective value index
	 *
	 * */
	public List<Solution> sortByObjectiveValue(List<Solution> solutions, int objectiveValueIndex) {
		int solutionSetSize = solutions.size();
		for (int i = 0; i < solutionSetSize - 1; i++) {
			for (int j = 0; j < solutionSetSize - 1 - i; j++) {
				Solution currentSolution = solutions.get(i);
				Solution nextSolution = solutions.get(i + 1);
				if (currentSolution.getObjectives()[objectiveValueIndex] > nextSolution.getObjectives()[objectiveValueIndex]) {
					Collections.swap(solutions, i, i+1);
				}
			}
		}
		return solutions;
	}

	/**
	 * Sorts a list of solutions in ascending order with respect to the distance to other solutions
	 *
	 * */
	public List<Solution> sortByDistance(List<Solution> solutions) {
		int solutionSetSize = solutions.size();
		for (int i = 0; i < solutionSetSize - 1; i++) {
			for (int j = 0; j < solutionSetSize - 1 - i; j++) {
				Solution currentSolution = solutions.get(i);
				Solution nextSolution = solutions.get(i + 1);
				if (currentSolution.getFitness()[1] > nextSolution.getFitness()[1] ) {
					Collections.swap(solutions, i, i+1);
				}
			}
		}
		return solutions;
	}
}
