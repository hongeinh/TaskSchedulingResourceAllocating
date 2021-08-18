package operator.crossover.impl;

import common.STATUS;
import component.variable.impl.Task;
import operator.crossover.CrossoverOperator;
import representation.Solution;
import utils.DataUtil;
import utils.NumberUtil;

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

        int size = a.getVariables().size();
        int chromosomeSize = ((ArrayList) a.getVariables().get(0).getValue()).size();

        int position1 = 0;
        int position2 = 0;
        while (position1 >= position2) {
            position1 = NumberUtil.getRandomNumber(0, chromosomeSize);
            position2 = NumberUtil.getRandomNumber(0, chromosomeSize);
        }

        int variablePosition = NumberUtil.getRandomNumber(0, size);
        Solution copyA = DataUtil.cloneBean(a);
        Solution copyB = DataUtil.cloneBean(b);


        for (int i = position1; i < position2; i++) {
            Task copyVarA =  ((List<Task>) copyA.getVariables().get(variablePosition).getValue()).get(i);
            Task copyVarB = ((List<Task>) copyB.getVariables().get(variablePosition).getValue()).get(i);

            double diff = copyVarA.getScheduledTime() - copyVarB.getScheduledTime();

            if (diff < 0) {
                copyVarA.setStart(copyVarA.getScheduledTime() + diff * 0.2);
                copyVarB.setStart(copyVarB.getScheduledTime() - diff * 0.2);
            } else if (diff > 0) {
                copyVarA.setStart(copyVarA.getScheduledTime() - diff * 0.2);
                copyVarB.setStart(copyVarB.getScheduledTime() + diff * 0.2);
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
