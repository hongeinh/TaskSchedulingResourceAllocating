package operator.crossover.impl;

import component.Resource;
import component.Task;
import component.Variable;
import operator.crossover.CrossoverOperator;
import solution.Solution;

import java.util.ArrayList;
import java.util.List;

public class ProposedCrossoverOperator1 extends CrossoverOperator {
    @Override
    public Object execute(Object object) throws CloneNotSupportedException {
        List<Solution> solutions = (List<Solution>) object;
        List<Solution> crossoveredSolutions = new ArrayList<>();

        int solutionSetSize = (int) this.getParameters().get("solutionSetSize");
        while (crossoveredSolutions.size() <= solutionSetSize) {
            int parentIndex1 = (int) Math.floor(Math.random() * solutionSetSize);
            int parentIndex2 = (int) Math.floor(Math.random() * solutionSetSize);

            if(parentIndex1 != parentIndex2) {
                Solution parent1 = solutions.get(parentIndex1);
                Solution parent2 = solutions.get(parentIndex2);
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

        for (int i = position1; i <= position2; i++) {
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

            Resource.STATUS [] aAssignedResouce = new Resource.STATUS[varA.getRequiredSkillsInResources().size()];
            for (int j = 0; j < varA.getRequiredSkillsInResources().size(); j++) {
                aAssignedResouce[i] = varA.getRequiredSkillsInResources().get(i).getResource().getStatus();
                varA.getRequiredSkillsInResources().get(i).getResource().setStatus(varB.getRequiredSkillsInResources().get(i).getResource().getStatus());
                varB.getRequiredSkillsInResources().get(i).getResource().setStatus(aAssignedResouce[i]);
            }

        }
        List<Solution> returnSolutions = new ArrayList<>();
        returnSolutions.add(a);
        returnSolutions.add(b);
        return returnSolutions;
    }
}
