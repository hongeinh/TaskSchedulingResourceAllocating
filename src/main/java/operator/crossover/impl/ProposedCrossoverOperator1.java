package operator.crossover.impl;

import common.STATUS;
import component.resource.Resource;
import component.variable.impl.Task;
import operator.crossover.CrossoverOperator;
import solution.Solution;

import java.util.ArrayList;
import java.util.List;

public class ProposedCrossoverOperator1 extends CrossoverOperator {
    @Override
    public Object execute(Object object) throws CloneNotSupportedException {
        List<Solution> matingSolutions = (List<Solution>) object;
        List<Solution> crossoveredSolutions = new ArrayList<>();

        int solutionSetSize = (int) this.getParameters().get("solutionSetSize");
        int matingSolutionSize = matingSolutions.size();
        while (crossoveredSolutions.size() <= solutionSetSize) {
            int parentIndex1 = (int) Math.floor(Math.random() * matingSolutionSize);
            int parentIndex2 = (int) Math.floor(Math.random() * matingSolutionSize);

            if(parentIndex1 != parentIndex2) {
                Solution parent1 = matingSolutions.get(parentIndex1);
                Solution parent2 = matingSolutions.get(parentIndex2);
                crossoveredSolutions.addAll(crossover(parent1, parent2));
            }
        }
        return crossoveredSolutions;
    }

    public List<Solution> crossover(Solution a, Solution b) {
        int position1 = 0;
        int position2 = 0;
        while (position1 == position2) {
            position1 = (int) Math.floor(Math.random() * a.getVariables().size());
            position2 = (int) Math.floor(Math.random() * a.getVariables().size());
        }

        for (int i = position1; i < position2; i++) {
            Task varA = (Task) a.getVariables().get(i);
            Task varB = (Task) b.getVariables().get(i);
            double diff = varA.getScheduledTime() - varB.getScheduledTime();

            if (diff < 0) {
                varA.setScheduledTime(varA.getScheduledTime() + diff * 0.1);
                varB.setScheduledTime(varB.getScheduledTime() - diff * 0.1);
            } else if (diff > 0) {
                varA.setScheduledTime(varA.getScheduledTime() - diff * 0.1);
                varB.setScheduledTime(varB.getScheduledTime() + diff * 0.1);
            }

            int numberOfResources = varA.getRequiredHumanResources().size();
            STATUS[] aAssignedResouce = new STATUS[numberOfResources];
            for (int j = 0; j < numberOfResources; j++) {
                aAssignedResouce[j] = varA.getRequiredHumanResources().get(j).getStatus();
                varA.getRequiredHumanResources().get(j).setStatus(varB.getRequiredHumanResources().get(j).getStatus());
                varB.getRequiredMachinesResources().get(j).setStatus(aAssignedResouce[j]);
            }

        }
        List<Solution> returnSolutions = new ArrayList<>();
        returnSolutions.add(a);
        returnSolutions.add(b);
        return returnSolutions;
    }
}
