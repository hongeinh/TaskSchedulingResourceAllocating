package algorithm.nsgaii;

import operator.Operator;
import problem.Problem;
import solution.Solution;

import java.util.ArrayList;
import java.util.List;

public class ImprovedNSGAIIAlgorithm extends NSGAIIAlgorithm{


	public ImprovedNSGAIIAlgorithm(int solutionSetSize) {
		super(solutionSetSize);
	}

	@Override
	public List<Solution> createInitialSolutionSet(Problem problem) {
		List<Solution> solutions = new ArrayList<>();
		int maxDuration = (Integer) problem.getParameters().get("maxDuration");

		for (int i  = 0; i < this.getSolutionSetSize(); i++) {

			solutions.add(createInitialSolution(problem, (double) i/this.getSolutionSetSize()));
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

}
