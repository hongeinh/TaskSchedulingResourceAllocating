package algorithm.nsgaii;

import component.variable.Variable;
import problem.Problem;
import representation.Solution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImprovedNSGAIIAlgorithm extends NSGAIIAlgorithm{


	public ImprovedNSGAIIAlgorithm(int solutionSetSize) {
		super(solutionSetSize);
	}

	@Override
	public List<Solution> createInitialSolutionSet(Problem problem) throws IOException {
		List<Solution> solutions = new ArrayList<>();
		double maxDuration = (Double) problem.getParameters().get("maxDuration");
		double maxWeight = (Double) problem.getParameters().get("maxWeight");
		maxDuration *= maxWeight;

		int i = 0;
		while(solutions.size() < this.solutionSetSize) {
//			System.out.println("-- Solution #" + i);
			Solution initialSolution = createInitialSolution(problem, i * maxDuration/this.getSolutionSetSize());
			double[] constraints = problem.evaluateConstraints(initialSolution);
			if (constraints[0] < 0.6 && constraints[1] == 0) {
				solutions.add(initialSolution);
				i++;
			}
		}
		return solutions;
	}

	public Solution createInitialSolution(Problem problem, double k) {
		Solution solution = new Solution();
		solution.setObjectives(new double[(Integer) problem.getParameters().get("numberOfObjectives")]);
		solution.setFitness(new double[(Integer) problem.getParameters().get("numberOfFitness")]);
		List<Variable> variables = problem.getVariableController().setupVariables(problem.getParameters(), k);
		solution.setVariables(variables);
		return solution;
	}

	public void recalculateSolutionDetails(List<Solution> offspringSolutions, Problem problem) {
		problem.getVariableController().recalculateSolutionDetails(offspringSolutions);
	}

}
