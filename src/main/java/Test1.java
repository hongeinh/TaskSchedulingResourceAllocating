import algorithm.comparator.RankingAndCrowdingDistanceComparator;
import algorithm.nsgaii.ImprovedNSGAIIAlgorithm;
import component.controller.VariableController;
import component.controller.impl.TaskSchedulingResourceAllocatingVariableController;
import operator.crossover.impl.ProposedCrossoverOperator1;
import operator.mutation.impl.BitInversionMutationOperator;
import operator.selection.impl.RouletteWheelSelectionOperator;
import problem.Problem;
import problem.impl.TaskSchedulingResourceAllocatingProblem;
import representation.Solution;
import scenario.TSRAScenario;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test1 {
	public static void main(String[] args) throws CloneNotSupportedException, IOException {

		System.out.println("Start flow");
		Map<Object, Object> params = createParams();
		VariableController variableController = new TaskSchedulingResourceAllocatingVariableController();
		Problem problem = new TaskSchedulingResourceAllocatingProblem(params, variableController);

		ImprovedNSGAIIAlgorithm algorithm = new ImprovedNSGAIIAlgorithm();
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

		params.put("treq", TSRAScenario.treq);
		params.put("lexp", TSRAScenario.lexp);
		params.put("tasks", TSRAScenario.tasks);
		params.put("numberOfTasks", TSRAScenario.numberOfTasks);
		params.put("numberOfHumanResources", TSRAScenario.numberOfHumanResources);
		params.put("numberOfSkills", TSRAScenario.numberOfSkills);
		params.put("scheduledTimes", TSRAScenario.scheduledTimes);
		params.put("durations", TSRAScenario.durations);
		params.put("numberOfObjectives", TSRAScenario.numberOfObjectives);
		params.put("numberOfFitness", TSRAScenario.numberOfFitness);
		params.put("maxDuration", TSRAScenario.maxDuration);

		return params;
	}
}
