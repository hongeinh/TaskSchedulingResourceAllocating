package operator.crossover.impl;

import common.STATUS;
import component.resource.Resource;
import component.variable.impl.Task;
import operator.crossover.CrossoverOperator;
import solution.Solution;
import utils.DataUtil;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

public class ProposedCrossoverOperator1 extends CrossoverOperator {
    @Override
    public Object execute(Object object) {
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

        Solution copyA = DataUtil.cloneBean(a);
        Solution copyB = DataUtil.cloneBean(b);


        for (int i = position1; i < position2; i++) {
            Task copyVarA = (Task) copyA.getVariables().get(i);
            Task copyVarB = (Task) copyB.getVariables().get(i);

            double diff = copyVarA.getScheduledTime() - copyVarB.getScheduledTime();

            if (diff < 0) {
                copyVarA.setScheduledTime(copyVarA.getScheduledTime() + diff * 0.1);
                copyVarB.setScheduledTime(copyVarB.getScheduledTime() - diff * 0.1);
            } else if (diff > 0) {
                copyVarA.setScheduledTime(copyVarA.getScheduledTime() - diff * 0.1);
                copyVarB.setScheduledTime(copyVarB.getScheduledTime() + diff * 0.1);
            }

            int numberOfHumanResources = copyVarA.getRequiredHumanResources().size();
            STATUS[] aAssignedHumanResouce = new STATUS[numberOfHumanResources];
            for (int j = 0; j < numberOfHumanResources; j++) {
                aAssignedHumanResouce[j] = copyVarA.getRequiredHumanResources().get(j).getStatus();
                copyVarA.getRequiredHumanResources().get(j).setStatus(copyVarB.getRequiredHumanResources().get(j).getStatus());
            }

        }
        List<Solution> returnSolutions = new ArrayList<>();

        returnSolutions.add(copyA);
        returnSolutions.add(copyB);
        return returnSolutions;
    }
}
