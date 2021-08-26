package operator.crossover.impl;

import operator.crossover.CrossoverOperator;
import representation.Solution;

import java.util.ArrayList;
import java.util.List;

public class ProposedCrossoverOperator2 extends CrossoverOperator {

    /**
     * Executes crossover on all variables that depend on the current variable.
     * What is the stopping criteria ?
     *
     * */
    @Override
    public Object execute(Object object) {
        List<Solution> solutions = (List<Solution>) object;
        List<Solution> crossoveredSolutions = new ArrayList<>();

        return crossoveredSolutions;
    }


    public List<Solution> crossover(Solution a, Solution b) {
        List<Solution> returnSolutions = new ArrayList<>();
        returnSolutions.add(a);
        returnSolutions.add(b);
        return returnSolutions;
    }
}
