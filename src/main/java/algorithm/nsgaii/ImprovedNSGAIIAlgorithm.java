package algorithm.nsgaii;

import component.variable.Variable;
import component.variable.impl.Order;
import component.variable.impl.Task;
import problem.Problem;
import solution.Solution;

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
		int maxDuration = (Integer) problem.getParameters().get("maxDuration");
		int i = 0;
		while(i < this.solutionSetSize) {
			Solution initialSolution = createInitialSolution(problem, (double) i * maxDuration/this.getSolutionSetSize());
			if (problem.evaluateConstraints(initialSolution)) {
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

}
