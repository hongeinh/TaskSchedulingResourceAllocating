import algorithm.Algorithm;
import algorithm.comparator.RankingAndCrowdingDistanceComparator;
import algorithm.nsgaii.ImprovedNSGAIIAlgorithm;
import component.controller.VariableController;
import component.controller.impl.MultiorderTaskSchedulingController;
import component.variable.impl.Order;
import operator.crossover.impl.ProposedCrossoverOperator1;
import operator.mutation.impl.BitInversionMutationOperator;
import operator.selection.impl.RouletteWheelSelectionOperator;
import problem.Problem;
import problem.impl.MultiorderTaskSchedulingProblem;
import scenario.TSRAScenario;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
		ImprovedNSGAIIAlgorithm algorithm = new ImprovedNSGAIIAlgorithm();
		algorithm.addOperator(new RouletteWheelSelectionOperator());
		algorithm.addOperator(new ProposedCrossoverOperator1());
		algorithm.addOperator(new BitInversionMutationOperator());
		algorithm.setComparator(new RankingAndCrowdingDistanceComparator());
		algorithm.setMatingPoolSize(10);


		return algorithm;
	}

	public static Map<Object, Object> createParams() {
		Map<Object, Object> params = createOrders();

		params.put("treq", TSRAScenario.treq);
		params.put("lexp", TSRAScenario.lexp);
		params.put("mreq", TSRAScenario.mreq);
		params.put("tasks", TSRAScenario.tasks);


		params.put("numberOfTasks", TSRAScenario.numberOfTasks);
		params.put("numberOfHumanResources", TSRAScenario.numberOfHumanResources);
		params.put("numberOfMachineResources", TSRAScenario.numberOfMachineResources);
		params.put("numberOfSkills", TSRAScenario.numberOfSkills);

		params.put("humanCosts", TSRAScenario.humanCosts);
		params.put("machineCosts", TSRAScenario.machineCosts);

		params.put("scheduledTimes", TSRAScenario.scheduledTimes);
		params.put("durations", TSRAScenario.durations);
		params.put("maxDuration", TSRAScenario.maxDuration);
		params.put("maxWeight", TSRAScenario.maxWeight);

		params.put("numberOfObjectives", TSRAScenario.numberOfObjectives);
		params.put("numberOfFitness", TSRAScenario.numberOfFitness);

		return params;
	}

	private static Map<Object, Object> createOrders() {
		HashMap<Object, Object> params = new HashMap<>();
		ArrayList<Order> orders = new ArrayList<>();

		for (int i = 0; i < TSRAScenario.numberOfOrders; i++) {
//			Order order = new Order(i, TSRAScenario.weights[i], TSRAScenario.orderDeadlines[i]);
			Order order = Order.builder()
					.id(i)
					.weight(TSRAScenario.weights[i])
					.penaltyRate(TSRAScenario.orderPenaltyRate[i])
					.totalTimeAllowed(TSRAScenario.orderDeadlines[i])
					.build();
			orders.add(order);
		}
		params.put("orders", orders);
		return params;
	}
}
