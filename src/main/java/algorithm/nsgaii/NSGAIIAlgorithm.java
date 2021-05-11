package algorithm.nsgaii;

import algorithm.Algorithm;
import comparator.RankingAndCrowdingDistanceComparator;
import lombok.Getter;
import lombok.Setter;
import operator.Operator;
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
		this.comparator = new RankingAndCrowdingDistanceComparator();
		this.matingPoolSize = 10;
	}

	public List<Solution> executeAlgorithm(Problem problem) throws CloneNotSupportedException {
		/* Step 1: Create initial solution set*/
		List<Solution> solutions = createInitialSolutionSet(problem);

		/* Step 2: Evaluate the initial solution set, calculate objectives for each solution */
		solutions = evaluateSolutionSet(problem, solutions);

		/* Step 3: Rank the solution set*/
		solutions = this.getComparator().computeRankAndDistance(solutions);

		/* Step 4: Create offspring solution set*/
		List<Solution> offspringSolutions = reproduceOffspringSolutionSet(solutions);

		/* Step 4: Evaluate the offspring solution set, calculate objectives for each solution */
		offspringSolutions = evaluateSolutionSet(problem, offspringSolutions);

		/* Step 5: Join two achieved solution sets into one jointSolution set*/
		List<Solution> jointSolutions = Stream.concat(solutions.stream(), offspringSolutions.stream())
				.collect(Collectors.toList());

		/* Step 6: Rank and distance sort for solution set to find Pareto solution set*/
		jointSolutions = this.getComparator().computeRankAndDistance(jointSolutions);

		System.out.println("Computing final results");
		List<Solution> finalSolutions = jointSolutions.subList(0, solutionSetSize + 1);

		return finalSolutions;
	}

	public List<Solution> createInitialSolutionSet(Problem problem) {
		return null;
	}

	public List<Solution> evaluateSolutionSet(Problem problem, List<Solution> solutions) {
		for (Solution solution: solutions) {
			solution = problem.evaluate(solution);
		}
		this.getComparator().computeRankAndDistance(solutions);
		return solutions;
	}

	public List<Solution> reproduceOffspringSolutionSet(List<Solution> solutions) throws CloneNotSupportedException {

		List<Operator> operators = this.getOperators();

		/* Selection */
		operators.get(0).getParameters().put("matingPoolSize", this.getMatingPoolSize());
		List<Solution> matingParentSolutions = (List<Solution>) operators.get(0).execute(solutions);

		/* Crossover */
		operators.get(1).getParameters().put("solutionSetSize", this.getSolutionSetSize());
		List<Solution> offspringSolutions = (List<Solution>) operators.get(1).execute(matingParentSolutions);

		/* Mutation */
		offspringSolutions = (List<Solution>) operators.get(2).execute(offspringSolutions);

		return offspringSolutions;
	}

	@Deprecated
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
