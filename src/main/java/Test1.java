import algorithm.Algorithm;
import algorithm.nsgaii.ImprovedNSGAIIAlgorithm;
import common.TSRAParams;
import comparator.RankingAndCrowdingDistanceComparator;
import component.controller.VariableController;
import component.controller.impl.TaskSchedulingResourceAllocatingVariableController;
import operator.Operator;
import operator.crossover.impl.SinglePointCrossoverOperator;
import operator.mutation.MutationOperator;
import operator.selection.impl.BinaryTournamentOperator;
import problem.Problem;
import problem.impl.TaskSchedulingResourceAllocatingProblem;
import solution.Solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test1 {
	public static void main(String[] args) {

		System.out.println("Start flow");
		Map<Object, Object> params = createParams();
		VariableController variableController = new TaskSchedulingResourceAllocatingVariableController();
		Problem problem = new TaskSchedulingResourceAllocatingProblem(params, variableController);

		ImprovedNSGAIIAlgorithm algorithm = new ImprovedNSGAIIAlgorithm(20);
//		algorithm.setSolutionSetSize(20);
		algorithm.addOperator(new BinaryTournamentOperator());
		algorithm.addOperator(new SinglePointCrossoverOperator());
		algorithm.addOperator(new MutationOperator());
		algorithm.setComparator(new RankingAndCrowdingDistanceComparator());
		algorithm.setMatingPoolSize(10);

		System.out.println("Executing algorithm");
		List<Solution> solutions = algorithm.executeAlgorithm(problem);

		for (Solution solution: solutions)
			System.out.println(solution.toString());
		System.out.println("Finish flow");
	}



	public static Map<Object, Object> createParams() {
		Map<Object, Object> params = new HashMap<>();

		params.put("treq", TSRAParams.treq);
		params.put("lexp", TSRAParams.lexp);
		params.put("tasks", TSRAParams.tasks);
		params.put("numberOfTasks", TSRAParams.numberOfTasks);
		params.put("numberOfResources", TSRAParams.numberOfResources);
		params.put("numberOfSkills", TSRAParams.numberOfSkills);
		params.put("scheduledTimes", TSRAParams.scheduledTimes);
		params.put("durations", TSRAParams.durations);
		params.put("numberOfObjectives", TSRAParams.numberOfObjectives);
		params.put("numberOfFitness", TSRAParams.numberOfFitness);
		params.put("maxDuration", TSRAParams.maxDuration);

		return params;
	}
}
