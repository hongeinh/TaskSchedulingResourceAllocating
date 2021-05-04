package problem.impl;

import component.Task;
import component.Variable;
import problem.Problem;
import solution.Solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TaskSchedulingResourceAllocatingProblem extends Problem {

	public TaskSchedulingResourceAllocatingProblem(Map<Object, Object> params) {
		super(params);
	}

	@Override
	public double[] evaluate(Solution solution) {
		return new double[0];
	}

	@Override
	public double[] evaluateConstraints(Solution solution) {
		return new double[0];
	}



}
