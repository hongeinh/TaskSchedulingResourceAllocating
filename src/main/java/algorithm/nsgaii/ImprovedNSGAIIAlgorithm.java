package algorithm.nsgaii;

import operator.Operator;
import problem.Problem;
import solution.Solution;

import java.util.ArrayList;
import java.util.List;

public class ImprovedNSGAIIAlgorithm extends NSGAIIAlgorithm{


	public List<Solution> createInitialSolutionSet(Problem problem) {
		List<Solution> solutions = new ArrayList<>();
		int maxDuration = (Integer) problem.getParameters().get("maxDuration");

		for (int i  = 0; i < this.getSolutionSetSize(); i++) {

			solutions.add(createInitialSolution(problem, maxDuration*i/this.getSolutionSetSize()));
		}
		return solutions;
	}

	public Solution createInitialSolution(Problem problem, double k) {
		Solution solution = new Solution();
		solution.setObjectives(new double[(Integer) problem.getParameters().get("numberOfObjectives")]);
		solution.setFitness(new double[(Integer) problem.getParameters().get("numberOfFitness")]);
		solution.setVariables(problem.getVariableController().createVariables(problem.getParameters(), k));
		return solution;
	}

//	public Solution createOffspringSolution(Problem problem, double k){}

	public List<Solution> evaluateSolutionSet(Problem problem, List<Solution> solutions) {
		double[] objectives = new double[(Integer) problem.getParameters().get("numberOfObjectives")];
		for (Solution solution: solutions) {
			objectives = problem.evaluate(solution);
			solution.setObjectives(objectives);
		}

		this.getComparator().computeRank(solutions);
		return solutions;
	}

	public List<Solution> reproduceOffspringSolutionSet(List<Solution> solutions) {

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


}
