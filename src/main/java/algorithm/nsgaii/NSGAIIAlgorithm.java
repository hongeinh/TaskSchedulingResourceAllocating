package algorithm.nsgaii;

import algorithm.Algorithm;
import algorithm.comparator.RankingAndCrowdingDistanceComparator;
import lombok.Getter;
import lombok.Setter;
import operator.Operator;
import problem.Problem;
import representation.Solution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Setter
@Getter
public class NSGAIIAlgorithm extends Algorithm {

	protected int matingPoolSize;
	protected int numberOfGenerations;

	protected RankingAndCrowdingDistanceComparator comparator;

	public NSGAIIAlgorithm() {
		super();
		this.comparator = new RankingAndCrowdingDistanceComparator();
		this.matingPoolSize = 10;
		this.numberOfGenerations = 10;
	}

	public NSGAIIAlgorithm(int solutionSetSize) {
		super(solutionSetSize);
		this.comparator = new RankingAndCrowdingDistanceComparator();
		this.matingPoolSize = 10;
		this.numberOfGenerations = 10;

	}

	public List<Solution> executeAlgorithm(Problem problem) throws CloneNotSupportedException, IOException {
		long startTime = System.currentTimeMillis();
		long executionTime = 0;

		/* Step 1: Create initial solution set*/
		System.out.print("- Create initial solution set -- ");
		List<Solution> solutions = createInitialSolutionSet(problem);
		System.out.println("Initial size: " + solutions.size());

		/* Step 2: Evaluate the initial solution set, calculate objectives for each solution */
		System.out.println("- Evaluate initial solution set");
		solutions = evaluateSolutionSet(problem, solutions);
		displayObjectives(solutions, "parent/parent.csv", false, -1);
//		displaySolutions(solutions, "parent/", "parent.csv", false);

		/* Step 3: Rank the solution set*/
		System.out.println("- Rank initial solution set");
		solutions = this.getComparator().computeRankAndDistance(solutions);

		/* Step 4: Create offspring solution set*/
		/* Set up operators */
		List<Operator> operators = this.getOperators();
		operators.get(0).getParameters().put("matingPoolSize", this.getMatingPoolSize());
		operators.get(1).getParameters().put("solutionSetSize", this.getSolutionSetSize());
		operators.get(2).getParameters().put("geneMutationProbability", 0.5);
		operators.get(2).getParameters().put("chromosomeSize", ((List) solutions.get(0).getVariables().get(0).getValue()).size());
		/* Reproduce */
		System.out.print("\n- Create offspring solution set -- ");
		List<Solution> offspringSolutions = reproduceOffspringSolutionSet(solutions);
		recalculateSolutionDetails(offspringSolutions, problem);
		System.out.println("Offspring size: " + offspringSolutions.size());

		/* Step 4: Evaluate the offspring solution set, calculate objectives for each solution */
		System.out.println("- Evaluate offspring solution set");
		offspringSolutions = evaluateSolutionSet(problem, offspringSolutions);


		/* Step 5: Join two achieved solution sets into one jointSolution set*/
		System.out.print("- Combine solution sets -- ");
		List<Solution> jointSolutions = Stream.concat(solutions.stream(), offspringSolutions.stream())
				.collect(Collectors.toList());
		System.out.println("Joint size: " + jointSolutions.size());

//		List<Solution> jointSolutions = new ArrayList<>();
//		jointSolutions.addAll(solutions);
//		jointSolutions.addAll(offspringSolutions);
		/* Step 6: Rank and distance sort for solution set to find Pareto solution set*/
		System.out.println("\n- Evaluate combined solution set");
		jointSolutions = this.getComparator().computeRankAndDistance(jointSolutions);
		System.out.println("Joint size: " + jointSolutions.size());

		System.out.println("- Computing final results---------------------------------------------------------------------");
		List<Solution> finalSolutions = jointSolutions.subList(0, solutionSetSize );
		displayObjectives(finalSolutions, "/final/final.csv", false, -1);
		return jointSolutions.subList(0, 1);
	}

	public void recalculateSolutionDetails(List<Solution> offspringSolutions, Problem problem) {
	}

	public List<Solution> createInitialSolutionSet(Problem problem) throws IOException {
		return null;
	}

	public List<Solution> evaluateSolutionSet(Problem problem, List<Solution> solutions) {
		for (Solution solution: solutions) {
			problem.evaluate(solution);
		}
		return solutions;
	}

	public List<Solution> reproduceOffspringSolutionSet(List<Solution> solutions) throws CloneNotSupportedException, IOException {

		double randomThreshold = Math.random();

		double minAverageFitness = Double.MAX_VALUE;
		List<Solution> matingParentSolutions;
		List<Solution> offspringSolutions = null;
		for (int i = 0; i < numberOfGenerations; i++) {
			System.out.println("-- Gen " + i);
			System.out.println("--- Choosing");
			matingParentSolutions = (List<Solution>) operators.get(0).execute(solutions);
			System.out.println("--- Mating");
			offspringSolutions = (List<Solution>) operators.get(1).execute(matingParentSolutions);
			System.out.println("--- Mutating");
			offspringSolutions = (List<Solution>) operators.get(2).execute(offspringSolutions);

			displayObjectives(offspringSolutions, "offspring/offspring.csv", true, i);
//			displaySolutions(solutions, "offspring/", "offspring" + i + ".csv", false);
			double averageFitness = calculateGenerationAverageObjectives(solutions);
			if (averageFitness < minAverageFitness + randomThreshold) {
				minAverageFitness = averageFitness;
				solutions = offspringSolutions;
			} else {
				break;
			}
		}

		return offspringSolutions;
	}

	private double calculateGenerationAverageObjectives(List<Solution> solutions) {
		double generationAverageObjectives = 0;

		for (Solution solution: solutions) {
			double [] objectives = solution.getObjectives();
			generationAverageObjectives += Arrays.stream(objectives).sum();
		}

		return generationAverageObjectives/solutions.size();
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
			}
			rankedSolutions.get(rank).add(solution);
		}

		return rankedSolutions;
	}

}
