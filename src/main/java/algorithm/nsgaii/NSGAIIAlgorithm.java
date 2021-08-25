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
//		displaySolutions(solutions, "parent/", "parent.csv", false);
		String parentFilePath = FileUtil.createResultDirectory("parent");
		FileUtil.writeSolutionResult(parentFilePath, solutions);

		/* Step 3: Rank the solution set*/
		System.out.println("- Rank initial solution set");
		solutions = this.getComparator().computeRankAndDistance(solutions);

		/* Step 4: Create offspring solution set*/
		/* Set up operators */
		List<Operator> operators = this.getOperators();
		operators.get(0).getParameters().put("matingPoolSize", this.getMatingPoolSize());
		operators.get(1).getParameters().put("solutionSetSize", this.getSolutionSetSize());
		operators.get(2).getParameters().put("geneMutationProbability", 0.8);
		operators.get(2).getParameters().put("chromosomeSize", ((List) solutions.get(0).getVariables().get(0).getValue()).size());
		/* Reproduce */
		System.out.print("\n- Create offspring solution set -- ");
		List<Solution> offspringSolutions = reproduceOffspringSolutionSet(solutions);
		recalculateSolutionDetails(offspringSolutions, problem);
		System.out.println("Offspring size: " + offspringSolutions.size());

		/* Step 4: Evaluate the offspring solution set, calculate objectives for each solution */
		System.out.println("- Evaluate offspring solution set");
		offspringSolutions = evaluateSolutionSet(problem, offspringSolutions);

		String offspringFilePath = FileUtil.createResultDirectory("offspring");
		FileUtil.writeSolutionResult(offspringFilePath, offspringSolutions);

		/* Step 5: Join two achieved solution sets into one jointSolution set*/
		System.out.print("- Combine solution sets -- ");
		List<Solution> jointSolutions = Stream.concat(solutions.stream(), offspringSolutions.stream())
				.collect(Collectors.toList());
		System.out.println("Joint size: " + jointSolutions.size());

		/* Step 6: Rank and distance sort for solution set to find Pareto solution set*/
		System.out.println("\n- Evaluate combined solution set");
		jointSolutions = this.getComparator().computeRankAndDistance(jointSolutions);
		System.out.println("Joint size: " + jointSolutions.size());

		System.out.println("- Computing final results---------------------------------------------------------------------");
		List<Solution> finalSolutions = jointSolutions.subList(0, eliteSetSize);
		String finalFilePath = FileUtil.createResultDirectory("final");
		FileUtil.writeSolutionResult(finalFilePath, finalSolutions);

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

		int finalOffspringSize = 0;
		double randomThreshold = Math.random();

		double minAverageFitness = Double.MAX_VALUE;
		List<Solution> matingParentSolutions;
		List<Solution> offspringSolutions = new ArrayList<>();
		List<Solution> finalOffpringSolutions = new ArrayList<>();

		for (int i = 0; i < numberOfGenerations; i++) {
			System.out.print("GEN " + i);
			matingParentSolutions = (List<Solution>) operators.get(0).execute(solutions);
			System.out.print(" crossover ");
			List<Solution> tempOffspringSolutions = (List<Solution>) operators.get(1).execute(matingParentSolutions);
			System.out.print(" mutate ");
			tempOffspringSolutions = (List<Solution>) operators.get(2).execute(tempOffspringSolutions);

			double averageFitness = calculateGenerationAverageObjectives(tempOffspringSolutions);
			if (averageFitness < minAverageFitness + randomThreshold) {
				minAverageFitness = averageFitness;
				offspringSolutions.addAll(tempOffspringSolutions);
				finalOffspringSize = tempOffspringSolutions.size();
			} else {
				break;
			}
			System.out.println();
		}

		this.getComparator().computeRankAndDistance(offspringSolutions);
		List<Double> eliteProbability = new ArrayList<>();
		List<Double> chosenProbability = new ArrayList<>();

		System.out.print("Get final ");
		int numOfFinalOffspring = 0;
		while (numOfFinalOffspring < finalOffspringSize) {
			System.out.print (finalOffpringSolutions.size() + " ");
			int size = offspringSolutions.size();
			double currentRank = 1;
			double currentEliteProbability = 0.75;
			for (int j = 0; j < size; j++) {
				if (currentEliteProbability >= 0.1) {
					if (currentRank == offspringSolutions.get(j).getFitness()[0]) {
						eliteProbability.add(j, currentEliteProbability);
						chosenProbability.add(j, Math.random());
					} else if (currentRank < offspringSolutions.get(j).getFitness()[0]) {
						currentRank = offspringSolutions.get(j).getFitness()[0];
						currentEliteProbability -= 0.15;
						eliteProbability.add(j, currentEliteProbability);
						chosenProbability.add(j, Math.random());
					}
				} else {
					eliteProbability.add(j, 0.1);
					chosenProbability.add(j, Math.random());
				}
			}

			for (int j = 0; j < size; j++) {
				if (eliteProbability.get(j) * chosenProbability.get(j) >= 0.6) {
					offspringSolutions.get(j).setId(numOfFinalOffspring);
					finalOffpringSolutions.add(offspringSolutions.get(j));
					numOfFinalOffspring++;
				}
			}
		}
		return finalOffpringSolutions;
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
