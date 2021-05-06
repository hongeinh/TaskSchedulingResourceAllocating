package algorithm.nsgaii;

import algorithm.Algorithm;
import comparator.RankingAndCrowdingDistanceComparator;
import lombok.Getter;
import lombok.Setter;
import problem.Problem;
import solution.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Setter
@Getter
public class NSGAIIAlgorithm extends Algorithm {

	protected int matingPoolSize;
	protected RankingAndCrowdingDistanceComparator comparator;

	public NSGAIIAlgorithm() {
		super();
		this.comparator = new RankingAndCrowdingDistanceComparator();
		this.matingPoolSize = 10;
	}

	public NSGAIIAlgorithm(int solutionSetSize) {
		super(solutionSetSize);
	}

	public List<Solution> executeAlgorithm(Problem problem) {
		/* Step 1: Create initial solution set*/
		List<Solution> solutions = createInitialSolutionSet(problem);

		/* Step 2: Evaluate the initial solution set, calculate objectives for each solution */
		solutions = evaluateSolutionSet(problem, solutions);

		/* Step 3: Rank the solution set*/
		solutions = this.getComparator().computeRank(solutions);

		/* Step 4: Create offspring solution set*/
		List<Solution> offspringSolutions = reproduceOffspringSolutionSet(solutions);

		/* Step 4: Evaluate the offspring solution set, calculate objectives for each solution */
		offspringSolutions = evaluateSolutionSet(problem, offspringSolutions);

		/* Step 5: Join two achieved solution sets into one jointSolution set*/
		List<Solution> jointSolutions = Stream.concat(solutions.stream(), offspringSolutions.stream())
				.collect(Collectors.toList());

		/* Step 6: Rank and distance sort for solution set to find Pareto solution set*/
		jointSolutions = this.getComparator().computeRank(jointSolutions);
		List<List<Solution>> rankedSolutions = divideIntoRanks(jointSolutions);

		System.out.println("Computing final results");
		List<Solution> finalSolutions = new ArrayList<>();
		int numberOfSolutions = 0;
		int currentRank = 1;
		while (numberOfSolutions < this.getSolutionSetSize()) {

			int size = rankedSolutions.get(currentRank - 1).size();
			if (numberOfSolutions + size <=  this.getSolutionSetSize()) {
				finalSolutions.addAll(rankedSolutions.get(currentRank));
				numberOfSolutions += rankedSolutions.get(currentRank).size();
			} else {
				int slots = numberOfSolutions + size - this.getSolutionSetSize();
				List<Solution> lastSolutions = rankedSolutions.get(currentRank);
				lastSolutions = this.getComparator().computeDistance(lastSolutions);
				// TODO: Delete this part becuz it is only for testing
				numberOfSolutions = this.getSolutionSetSize();
				for (Solution solution: lastSolutions) {

					if (numberOfSolutions <= this.getSolutionSetSize()) {
						lastSolutions.add(solution);
						slots--;
						numberOfSolutions++;
					} else {
						break;
					}
				}
			}

		}
		return finalSolutions;
	}

	public List<Solution> createInitialSolutionSet(Problem problem) {
		return null;
	}

	public List<Solution> evaluateSolutionSet(Problem problem, List<Solution> solutions) {
		return solutions;
	}

	public List<Solution> reproduceOffspringSolutionSet(List<Solution> solutions) {
		return solutions;
	}

	public List<List<Solution>> divideIntoRanks(List<Solution> solutions) {

		List<List<Solution>> rankedSolutions = new ArrayList<>();
		rankedSolutions.add(new ArrayList<>());
		rankedSolutions.add(new ArrayList<>());
		rankedSolutions.add(new ArrayList<>());

		for (Solution solution: solutions) {
			int rank = (int) solution.getFitness()[0];
			if (rankedSolutions.get(rank) == null) {
				rankedSolutions.add(rank, new ArrayList<Solution>());
				rankedSolutions.get(rank).add(solution);
			} else
				rankedSolutions.get(rank).add(solution);
		}

		return rankedSolutions;
	}
}
