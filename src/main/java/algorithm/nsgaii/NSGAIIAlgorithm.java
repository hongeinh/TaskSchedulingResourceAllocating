package algorithm.nsgaii;

import algorithm.Algorithm;
import lombok.Getter;
import lombok.Setter;
import problem.Problem;
import solution.Solution;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Setter
@Getter
public class NSGAIIAlgorithm extends Algorithm {

	protected int matingPoolSize;

	public List<Solution> executeAlgorithm(Problem problem) {
		List<Solution> solutions = createInitialSolutionSet(problem);
		solutions = evaluateSolutionSet(solutions);
		List<Solution> offspringSolutions = reproduceOffspringSolutionSet(solutions);
		List<Solution> jointSolutions = Stream.concat(solutions.stream(), offspringSolutions.stream())
				.collect(Collectors.toList());
		return null;
	}

	public List<Solution> createInitialSolutionSet(Problem problem) {
		return null;
	}

	public List<Solution> evaluateSolutionSet(List<Solution> solutions) {
		return null;
	}

	public List<Solution> reproduceOffspringSolutionSet(List<Solution> solutions) {
		return null;
	}


}
