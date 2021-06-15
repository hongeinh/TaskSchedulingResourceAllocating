import algorithm.comparator.RankingAndCrowdingDistanceComparator;
import algorithm.nsgaii.ImprovedNSGAIIAlgorithm;
import common.TSRAParams;
import component.controller.VariableController;
import component.controller.impl.MultiorderTaskSchedulingController;
import operator.crossover.impl.ProposedCrossoverOperator1;
import operator.mutation.impl.BitInversionMutationOperator;
import operator.selection.impl.RouletteWheelSelectionOperator;
import problem.Problem;
import problem.impl.MultiorderTaskSchedulingProblem;
import solution.Solution;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestMultiorder {
	public static void main(String[] args) throws CloneNotSupportedException, IOException {

		System.out.println("Start flow");
		Map<Object, Object> params = createParams();
		VariableController variableController = new MultiorderTaskSchedulingController();
		Problem problem = new MultiorderTaskSchedulingProblem(params, variableController);

		ImprovedNSGAIIAlgorithm algorithm = new ImprovedNSGAIIAlgorithm(20);
		algorithm.addOperator(new RouletteWheelSelectionOperator());
		algorithm.addOperator(new ProposedCrossoverOperator1());
		algorithm.addOperator(new BitInversionMutationOperator());
		algorithm.setComparator(new RankingAndCrowdingDistanceComparator());
		algorithm.setMatingPoolSize(10);

		System.out.println("Executing algorithm");
		List<Solution> solutions = algorithm.executeAlgorithm(problem);

		System.out.println("Finish flow");
	}



	public static Map<Object, Object> createParams() {
		Map<Object, Object> params = new HashMap<>();


		params.put("treq", TSRAParams.treq);
		params.put("lexp", TSRAParams.lexp);
		params.put("tasks", TSRAParams.tasks);
		params.put("numberOfOrders", TSRAParams.numberOfOrders );
		params.put("numberOfTasks", TSRAParams.numberOfTasks);
		params.put("numberOfHumanResources", TSRAParams.numberOfHumanResources);
		params.put("numberOfSkills", TSRAParams.numberOfSkills);
		params.put("scheduledTimes", TSRAParams.scheduledTimes);
		params.put("durations", TSRAParams.durations);
		params.put("numberOfObjectives", TSRAParams.numberOfObjectives);
		params.put("numberOfFitness", TSRAParams.numberOfFitness);
		params.put("maxDuration", TSRAParams.maxDuration);

		return params;
	}
}
