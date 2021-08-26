package algorithm.nsgaii;

import algorithm.Algorithm;
import algorithm.comparator.RankingAndCrowdingDistanceComparator;
import lombok.Getter;
import lombok.Setter;
import operator.Operator;
import problem.Problem;
import representation.Solution;
import utils.FileUtil;

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
	protected int solutionSetSize;
	protected int eliteSetSize;

	protected RankingAndCrowdingDistanceComparator comparator;

	public NSGAIIAlgorithm() {
		super();
		this.comparator = new RankingAndCrowdingDistanceComparator();
		this.matingPoolSize = 10;
		this.numberOfGenerations = 20;
		this.solutionSetSize = 200;
		this.eliteSetSize = 20;
	}

	public NSGAIIAlgorithm(int solutionSetSize, int numberOfGenerations, int eliteSetSize) {
		this.comparator = new RankingAndCrowdingDistanceComparator();
		this.matingPoolSize = 10;
		this.numberOfGenerations = numberOfGenerations;
		this.solutionSetSize = solutionSetSize;
		this.eliteSetSize = eliteSetSize;
	}

	public List<Solution> executeAlgorithm(Problem problem) throws IOException {
		long startTime = System.currentTimeMillis();
		long executionTime = 0;

		int numberOfOrders = (int) problem.getParameters().get("numberOfOrders");

		/* Step 1: Create initial solution set*/
		System.out.print("- Create initial solution set -- ");
		List<Solution> solutions = createInitialSolutionSet(problem);
		System.out.println("Initial size: " + solutions.size());

		/* Step 2: Evaluate the initial solution set, calculate objectives for each solution */
		System.out.println("- Evaluate initial solution set");
		solutions = evaluateSolutionSet(problem, solutions);
//		displaySolutions(solutions, "parent/", "parent.csv", false);

		/* Step 3: Rank the solution set*/
		System.out.println("- Rank initial solution set");
		solutions = this.getComparator().computeRankAndDistance(solutions);

		String parentFilePath = FileUtil.createResultDirectory("parent", numberOfOrders );
		FileUtil.writeSolutionResult(parentFilePath, solutions);

		/* Step 4: Create offspring solution set*/
		/* Set up operators */
		List<Operator> operators = this.getOperators();
		operators.get(0).getParameters().put("matingPoolSize", this.getMatingPoolSize());
		operators.get(1).getParameters().put("solutionSetSize", this.getSolutionSetSize());
		operators.get(2).getParameters().put("geneMutationProbability", 0.8);
		operators.get(2).getParameters().put("chromosomeSize", ((List) solutions.get(0).getVariables().get(0).getValue()).size());
		/* Reproduce */
		System.out.print("\n- Create offspring solution set -- ");
		List<Solution> offspringSolutions = reproduceOffspringSolutionSet(solutions, 1);
		recalculateSolutionDetails(offspringSolutions, problem);
//		System.out.println("Offspring size: " + offspringSolutions.size());

		/* Step 4: Evaluate the offspring solution set, calculate objectives for each solution */
		System.out.println("- Evaluate offspring solution set");
		offspringSolutions = evaluateSolutionSet(problem, offspringSolutions);
		offspringSolutions = this.getComparator().computeRankAndDistance(offspringSolutions);

		String offspringFilePath = FileUtil.createResultDirectory("offspring", numberOfOrders);
		FileUtil.writeSolutionResult(offspringFilePath, offspringSolutions);

		/* Step 5: Join two achieved solution sets into one jointSolution set*/
		System.out.print("- Combine solution sets -- ");
		List<Solution> jointSolutions = Stream.concat(solutions.stream(), offspringSolutions.stream())
				.collect(Collectors.toList());
		System.out.println("Joint size: " + jointSolutions.size());

		/* Step 6: Rank and distance sort for solution set to find Pareto solution set*/
		System.out.println("\n- Evaluate combined solution set");
		jointSolutions = this.getComparator().computeRankAndDistance(jointSolutions);
		jointSolutions = this.removeIdenticalSolutions(jointSolutions);

		System.out.println("- Computing final results---------------------------------------------------------------------");
		List<Solution> finalSolutions = null;
		if (jointSolutions.size() < eliteSetSize) {
			finalSolutions = jointSolutions;
		} else {
			finalSolutions = jointSolutions.subList(0, eliteSetSize);
		}
		String finalFilePath = FileUtil.createResultDirectory("final", numberOfOrders);
		FileUtil.writeSolutionResult(finalFilePath, finalSolutions);

		return finalSolutions;
	}

	public void recalculateSolutionDetails(List<Solution> offspringSolutions, Problem problem) {
//		for (Solution solution : offspringSolutions) {
//			problem.getVariableController().recalculateSolutionDetails(offspringSolutions);
//		}
	}

	public List<Solution> createInitialSolutionSet(Problem problem) throws IOException {
		return null;
	}

	public List<Solution> evaluateSolutionSet(Problem problem, List<Solution> solutions) {
		for (Solution solution : solutions) {
			problem.evaluate(solution);
		}
		return solutions;
	}

	public List<Solution> reproduceOffspringSolutionSet(List<Solution> solutions, int numberOfGenerations) {

		List<Solution> offspringSolutions = reproduceOffspringGenerations(solutions, numberOfGenerations);
//		offspringSolutions = removeIdenticalSolutions(offspringSolutions);
		this.getComparator().computeRankAndDistance(offspringSolutions);
		int size = offspringSolutions.size();


		List<Solution> finalOffpringSolutions = new ArrayList<>();
		List<Double> eliteProbability = new ArrayList<>();
		List<Double> chosenProbability = new ArrayList<>();

		int i = 0;
		double currentRank = 1;
		double currentEliteProbability = 0.75;
		// Give each solution an elite probability and chosen probability
		while (i < size) {
			// assign probability
			if (currentEliteProbability >= 0.1) {
				if (currentRank == offspringSolutions.get(i).getFitness()[0]) {
					eliteProbability.add(i, currentEliteProbability);
				} else if (currentRank < offspringSolutions.get(i).getFitness()[0]) {
					currentRank = offspringSolutions.get(i).getFitness()[0];
					currentEliteProbability -= 0.15;
					eliteProbability.add(i, currentEliteProbability);
				}

			} else {
				eliteProbability.add(i, 0.1);
			}
			chosenProbability.add(i, Math.random());
			i++;
		}

		// reset
		i = 0;
		for (int j = 0; j < size; j++) {
			if (finalOffpringSolutions.size() < this.solutionSetSize) {
				if (eliteProbability.get(j) * chosenProbability.get(j) >= 0.65) {
					offspringSolutions.get(j).setId(i);
					finalOffpringSolutions.add(offspringSolutions.get(j));
					i++;
				}
			} else {
				break;
			}
		}

		return finalOffpringSolutions;
	}

	public List<Solution> reproduceOffspringGenerations(List<Solution> solutions, int numberOfGenerations) {
		if (this.numberOfGenerations < numberOfGenerations)
			return solutions;

		List<Solution> matingParentSolutions;
		List<Solution> offspringSolutions = new ArrayList<>();

		while (offspringSolutions.size() < this.solutionSetSize) {
			matingParentSolutions = (List<Solution>) operators.get(0).execute(solutions);
			List<Solution> tempOffspringSolutions = (List<Solution>) operators.get(1).execute(matingParentSolutions);
			tempOffspringSolutions = (List<Solution>) operators.get(2).execute(tempOffspringSolutions);
			offspringSolutions.addAll(tempOffspringSolutions);
//			offspringSolutions = removeIdenticalSolutions(offspringSolutions);
		}
		solutions.addAll(offspringSolutions);
//		solutions = removeIdenticalSolutions(solutions);
		return reproduceOffspringGenerations(solutions, numberOfGenerations + 1);
	}


	@Deprecated
	private double calculateGenerationAverageObjectives(List<Solution> solutions) {
		double generationAverageObjectives = 0;

		for (Solution solution : solutions) {
			double[] objectives = solution.getObjectives();
			generationAverageObjectives += Arrays.stream(objectives).sum();
		}

		return generationAverageObjectives / solutions.size();
	}

	@Deprecated
	public List<List<Solution>> divideIntoRanks(List<Solution> solutions) {

		List<List<Solution>> rankedSolutions = new ArrayList<>();
		rankedSolutions.add(new ArrayList<>());
		rankedSolutions.add(new ArrayList<>());
		rankedSolutions.add(new ArrayList<>());

		for (Solution solution : solutions) {
			int rank = (int) solution.getFitness()[0];
			if (rankedSolutions.get(rank) == null) {
				rankedSolutions.add(rank, new ArrayList<Solution>());
			}
			rankedSolutions.get(rank).add(solution);
		}

		return rankedSolutions;
	}

	public List<Solution> removeIdenticalSolutions(List<Solution> solutions) {
		List<Solution> removedIdenticalSolutions = new ArrayList<>();
		removedIdenticalSolutions.add(solutions.get(0));

		for (Solution solution : solutions) {
			if (solution.notExistIn(removedIdenticalSolutions)) {
				removedIdenticalSolutions.add(solution);
			}
		}
		return removedIdenticalSolutions;
	}
}
