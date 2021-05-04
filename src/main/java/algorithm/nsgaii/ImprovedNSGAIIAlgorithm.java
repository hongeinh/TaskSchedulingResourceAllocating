package algorithm.nsgaii;

import problem.Problem;
import solution.Solution;

import java.util.ArrayList;
import java.util.List;

public class ImprovedNSGAIIAlgorithm extends NSGAIIAlgorithm{

	public List<Solution> createInitialSolutionSet(Problem problem) {
		List<Solution> solutions = new ArrayList<>();

		return solutions;
	}

	public List<Solution> evaluateSolutionSet(List<Solution> solutions) {
		return solutions;
	}

	public List<Solution> reproduceOffspringSolutionSet(List<Solution> solutions) {
		return solutions;
	}

	public Solution createInitialSolution(Problem problem, double k) {

		return null;
	}
}
