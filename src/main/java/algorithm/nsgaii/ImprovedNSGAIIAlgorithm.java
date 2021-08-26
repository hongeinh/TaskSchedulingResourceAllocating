package algorithm.nsgaii;

import component.variable.Variable;
import problem.Problem;
import representation.Solution;
import utils.NumberUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImprovedNSGAIIAlgorithm extends NSGAIIAlgorithm{

	public ImprovedNSGAIIAlgorithm(){
		super();
	}

	public ImprovedNSGAIIAlgorithm(int solutionSetSize, int numberOfGeneration, int eliteSolutionSet) {
		super(solutionSetSize, numberOfGeneration, eliteSolutionSet);
	}

	@Override
	public List<Solution> createInitialSolutionSet(Problem problem) throws IOException {
		List<Solution> solutions = new ArrayList<>();
		double maxDuration = (Double) problem.getParameters().get("maxDuration");
		double maxWeight = (Double) problem.getParameters().get("maxWeight");
//		maxDuration *= maxWeight;

		int i = 0;
		int seed = 1;
		while(i < this.solutionSetSize) {

			double upperBound = (seed >= maxDuration * maxWeight) ? seed : (maxDuration * maxWeight);
			double lowerBound =  (seed <= maxDuration * maxWeight) ? seed : (maxDuration * maxWeight);
			double rand = NumberUtil.getRandomDoubleNumber(lowerBound, upperBound);
			double delim = NumberUtil.getRandomDoubleNumber(rand, rand * maxDuration);
			rand = delim / rand;
			Solution initialSolution = createInitialSolution(problem, rand);
			double[] constraints = problem.evaluateConstraints(initialSolution);
			if (constraints[0] < 0.6 && constraints[1] == 0) {
//				if (initialSolution.notExistIn(solutions)) {
//					System.out.println("+ Solution " + i);
					initialSolution.setId(i);
					solutions.add(initialSolution);
					i++;
//				}
			}
			seed++;
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
