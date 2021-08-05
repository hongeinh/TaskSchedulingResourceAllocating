package operator.mutation.impl;

import common.STATUS;
import component.resource.HumanResource;
import component.variable.Variable;
import operator.mutation.MutationOperator;
import solution.Solution;

import java.util.List;

public class BitInversionMutationOperator extends MutationOperator {

    @Override
    public Object execute(Object object){
        double geneMutationProbability = (double) this.getParameters().get("geneMutationProbability");
        List<Solution> solutions = (List<Solution>) object;
        for (Solution solution: solutions) {
            int index = solutions.indexOf(solution);
            solution = mutate(solution, geneMutationProbability);
            solutions.set(index, solution);
        }
        return solutions;
    }

    public Solution mutate(Solution mutatedSolution, double geneMutationProbability) {
//        for (int i = 0; i < mutatedSolution.getVariables().size(); i++) {
//            Variable variable = mutatedSolution.getVariables().get(i);
//            double probability = Math.random();
//            if (probability < geneMutationProbability) {
//                int mutateIndex = (int) Math.floor(Math.random() * (int )this.getParameters().get("chromosomeSize"));
//                HumanResource resource = ((List<HumanResource>) variable.getValue()).get(mutateIndex);
//                if (resource.getStatus() != STATUS.ASSIGNED) {
//                    resource.setStatus(STATUS.ASSIGNED);
//                } else if (resource.getStatus() == STATUS.ASSIGNED) {
//                    resource.setStatus(STATUS.NOT_ASSIGNED);
//                }
//            }
//        }
        return mutatedSolution;
    }
}
