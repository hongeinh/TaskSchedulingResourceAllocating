package operator.mutation.impl;

import common.STATUS;
import component.resource.HumanResource;
import component.variable.Variable;
import component.variable.impl.Order;
import component.variable.impl.Task;
import operator.mutation.MutationOperator;
import representation.Solution;
import utils.NumberUtil;

import java.util.List;

public class BitInversionMutationOperator extends MutationOperator {

    @Override
    public Object execute(Object object){
        double geneMutationProbability = (double) this.getParameters().get("geneMutationProbability");
        List<Solution> solutions = (List<Solution>) object;
        for (Solution solution: solutions) {
            mutate(solution, geneMutationProbability);
        }
        return solutions;
    }

    public Solution mutate(Solution mutatedSolution, double geneMutationProbability) {
        for (int i = 0; i < mutatedSolution.getVariables().size(); i++) {
            Variable variable = mutatedSolution.getVariables().get(i);
            double probability = Math.random();

            if (probability < geneMutationProbability) {
                int pos1 = 0;
                int pos2 = 0;

                List<Task> tasks = (List<Task>) variable.getValue();
                while (pos1 == pos2 && pos1 == 0) {
                    pos1 = NumberUtil.getRandomNumber(0, ((List<Task>) variable.getValue()).size());
                    pos2 = NumberUtil.getRandomNumber(0, ((List<Task>)  variable.getValue()).size());
                    if (pos1 > pos2) {
                        int temp = pos1;
                        pos1 = pos2;
                        pos2 = temp;
                    }
                }
                int mutatedChromosome = NumberUtil.getRandomNumber(0, tasks.get(0).getRequiredHumanResources().size());
                for (int j = pos1; j <= pos2; j++) {
                    double random = Math.random();
                    HumanResource resource = tasks.get(i).getRequiredHumanResources().get(mutatedChromosome);
                    if (resource.getStatus() != STATUS.ASSIGNED && random < 0.5) {
                        resource.setStatus(STATUS.ASSIGNED);
                    } else if (resource.getStatus() == STATUS.ASSIGNED && random < 0.5) {
                        resource.setStatus(STATUS.NOT_ASSIGNED);
                    }
                }
                double mutatedWeight = NumberUtil.floor2DecimalPoints(Math.random());
                double sign = Math.random() >= 5 ? 1 : -1;
                double weight = ((Order) variable).getWeight() + mutatedWeight * sign;
                ((Order) variable).setWeight(weight);
            }
        }

        return mutatedSolution;
    }
}
