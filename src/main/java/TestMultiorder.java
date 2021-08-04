import algorithm.Algorithm;
import algorithm.comparator.RankingAndCrowdingDistanceComparator;
import algorithm.nsgaii.ImprovedNSGAIIAlgorithm;
import common.TSRAParams;
import component.controller.VariableController;
import component.controller.impl.FixedMultiorderTaskSchedulingController;
import component.controller.impl.MultiorderTaskSchedulingController;
import component.variable.impl.Order;
import operator.crossover.impl.ProposedCrossoverOperator1;
import operator.mutation.impl.BitInversionMutationOperator;
import operator.selection.impl.RouletteWheelSelectionOperator;
import problem.Problem;
import problem.impl.MultiorderTaskSchedulingProblem;
import solution.Solution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestMultiorder {
	public static void main(String[] args) throws CloneNotSupportedException, IOException {

		System.out.println("Start flow");
		Map<Object, Object> params = createParams();
		VariableController variableController = new MultiorderTaskSchedulingController();
		Problem problem = new MultiorderTaskSchedulingProblem(params, variableController);

		Algorithm algorithm = setAlgorithm();

		System.out.println("Executing algorithm");
		algorithm.executeAlgorithm(problem);

		System.out.println("Finish flow");
	}


	public static Algorithm setAlgorithm() {
		ImprovedNSGAIIAlgorithm algorithm = new ImprovedNSGAIIAlgorithm(25);
		algorithm.addOperator(new RouletteWheelSelectionOperator());
		algorithm.addOperator(new ProposedCrossoverOperator1());
		algorithm.addOperator(new BitInversionMutationOperator());
		algorithm.setComparator(new RankingAndCrowdingDistanceComparator());
		algorithm.setMatingPoolSize(10);


		return algorithm;
	}

	public static Map<Object, Object> createParams() {
		Map<Object, Object> params = createOrders();


		params.put("treq", TSRAParams.treq);
		params.put("lexp", TSRAParams.lexp);
		params.put("mreq", TSRAParams.mreq);
		params.put("tasks", TSRAParams.tasks);


		params.put("numberOfTasks", TSRAParams.numberOfTasks);
		params.put("numberOfHumanResources", TSRAParams.numberOfHumanResources);
		params.put("numberOfMachineResources", TSRAParams.numberOfMachineResources);
		params.put("numberOfSkills", TSRAParams.numberOfSkills);

		params.put("humanCosts", TSRAParams.humanCosts);
		params.put("machineCosts", TSRAParams.machineCosts);

		params.put("scheduledTimes", TSRAParams.scheduledTimes);
		params.put("durations", TSRAParams.durations);
		params.put("maxDuration", TSRAParams.maxDuration);

		params.put("numberOfObjectives", TSRAParams.numberOfObjectives);
		params.put("numberOfFitness", TSRAParams.numberOfFitness);

		return params;
	}

	private static Map<Object, Object> createOrders() {
		HashMap<Object, Object> params = new HashMap<>();
		ArrayList<Order> orders = new ArrayList<>();

		for (int i = 0; i < TSRAParams.numberOfOrders; i++) {
			Order order = new Order(i, TSRAParams.weights[i], TSRAParams.orderDeadlines[i]);
			orders.add(order);
		}
		params.put("orders", orders);
		return params;
	}
}
