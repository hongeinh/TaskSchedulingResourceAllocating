package algorithm.nsgaii;

import problem.Problem;
import solution.Solution;

import java.util.ArrayList;
import java.util.List;

public class ImprovedNSGAIIAlgorithm extends NSGAIIAlgorithm{

	public List<Solution> createInitialSolutionSet(Problem problem) {
		List<Solution> solutions = new ArrayList<>();
		int maxDuration = (Integer) problem.getParameters().get("maxDuration");

		for (int i  = 0; i < this.getSolutionSetSize(); i++) {
			Solution solution = new Solution();
			solution.setObjectives(new double[(Integer) problem.getParameters().get("numberOfObjectives")]);
			solution.setFitness(new double[(Integer) problem.getParameters().get("numberOfFitness")]);
			solution = createInitialSolution(solution, problem, maxDuration*i/this.getSolutionSetSize());
			solutions.add(solution);
		}
		return solutions;
	}

	public List<Solution> evaluateSolutionSet(List<Solution> solutions) {
		return solutions;
	}

	public List<Solution> reproduceOffspringSolutionSet(List<Solution> solutions) {
		return solutions;
	}

	public Solution createInitialSolution(Solution solution, Problem problem, double k) {

		return null;
	}
}
