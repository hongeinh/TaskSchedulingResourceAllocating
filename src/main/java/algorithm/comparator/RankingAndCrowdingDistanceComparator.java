package algorithm.comparator;

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
			Solution soli = solutions.get(i);
			for (int j = 0; j < solutionSetSize; j++) {
				if (i != j) {
					Solution solj = solutions.get(j);
					if(soli.compareTo(solj) > 0) {
						// Solution i dominates solution j
						dominatedSolutions.add(solj);
					} else if (soli.compareTo(solj) < 0) {
						// Solution i is dominated by solution j
						dominationCount++;
					}
				}
			}

			solutions.get(i).setDominatedSolutions(dominatedSolutions);
			solutions.get(i).setDominationCount(dominationCount);

			// Step 2b: If the dominationCount of one solution is zero, it is put into the first rank
			if (dominationCount == 0) {
				double []fitness = solutions.get(i).getFitness();
				fitness[0] = frontCounter;
				solutions.get(i).setFitness(fitness);
				currentFront.add(soli);
			}
		}

		// Step 3: Compute the crowding distance value for each solution and sort them.
		System.out.println("--- Step 3: Compute the crowding distance value for each solution and sort them.");
		rankSortedSolutions.addAll(sortByDistance(computeDistance(currentFront)));

		// Step 4: Find fronts for other solutions
		System.out.println("--- Step 4: Find fronts for other solutions");
		while (!currentFront.isEmpty()) {
			System.out.println("--------- Rank " + frontCounter);
			List<Solution> nextFront = new ArrayList<>();
			for (Solution currentSolution: currentFront) {
				List<Solution> dominatedSolutions = currentSolution.getDominatedSolutions();
				for (Solution solution1: dominatedSolutions) {
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
			if (!nextFront.isEmpty()){
				List<Solution> nextFrontSolutionWithDistance = computeDistance(nextFront);
				nextFrontSolutionWithDistance = sortByDistance(nextFrontSolutionWithDistance);
				rankSortedSolutions.addAll(nextFrontSolutionWithDistance);
			}
			currentFront = nextFront;
		}
		return rankSortedSolutions;
	}

	/**
	 * Computes the density around each solution in the list (assume that they are in the same front)
	 *
	 * */
	public List<Solution> computeDistance(@org.jetbrains.annotations.NotNull List<Solution> solutions) {
		int solutionSetSize = solutions.size();
		int objectiveSize = solutions.get(0).getObjectives().length;

		// Initialize distance
		for (Solution solution: solutions) {
			solution.getFitness()[1] = 0;
		}

		for (int i = 0; i < objectiveSize; i++) {
			solutions = sortByObjectiveValue(solutions, i);

			Solution smallestSolution = solutions.get(0);
			Solution biggestSolution = solutions.get(solutionSetSize - 1);

			for (int j = 1; j < solutionSetSize - 1; j++) {
				double [] fitness1 = solutions.get(j).getFitness();
				Solution biggerSolution =  solutions.get(j + 1);
				Solution smallerSolution = solutions.get(j - 1);
				Solution currentSolution = solutions.get(j);

				if (biggestSolution.getObjectives()[i] != smallestSolution.getObjectives()[i]) {
					fitness1[1] = fitness1[1] + (biggerSolution.getObjectives()[i] - smallerSolution.getObjectives()[i]) /
							(biggestSolution.getObjectives()[i] - smallestSolution.getObjectives()[i]);
					solutions.get(j).setFitness(fitness1);
				}
			}
//
//			solutions.get(0).getFitness()[1] = initialMax;
//			solutions.get(solutionSetSize - 1).getFitness()[1] = initialMin;
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
				Solution currentSolution = solutions.get(j);
				Solution nextSolution = solutions.get(j + 1);
				if (currentSolution.getObjectives()[objectiveValueIndex] > nextSolution.getObjectives()[objectiveValueIndex]) {
					Collections.swap(solutions, j, j+1);
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
				Solution currentSolution = solutions.get(j);
				Solution nextSolution = solutions.get(j + 1);
				if (currentSolution.getFitness()[1] < nextSolution.getFitness()[1] ) {
					Collections.swap(solutions, j, j+1);
				}
			}
		}
		return solutions;
	}
}
