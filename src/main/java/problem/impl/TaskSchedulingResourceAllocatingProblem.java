package problem.impl;

import component.Resource;
import component.Skill;
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

	/**
	 * This method will return a list of variables to the algorithm to find its solution
	 * @param parameters Map
	 *                   treq:				int[][] 		Task - Skill matrix
	 *                   lexp: 				double[][]		Resource - Skill matrix
	 *                   tasks: 			int[][]			Task matrix
	 *                   numberOfTask: 		int				number of tasks in this problem
	 *                   numberOfResource: 	int				number of resources in this problem
	 *                   numberOfSkills: 	int				number of skills in this problem
	 *                   scheduledTimes: 	List<Integer>	list of scheduled time for variables
	 *                   durations: 		List<Integer>	list of durations for variables
	 *                   numberOfObjectives:int				the number of objectives needed to calculate for solutions.
	 *                   numberOfFitness:	int				the number of fitness for this solution
	 *                   maxDuration		int				maximum duration for all tasks
	 * @return variable		List of equivalent vairables
	 * */
	@Override
	public List<Variable> createVariables(Map<Object, Object> parameters) {

		Integer numberOfTasks = (Integer) parameters.get("numberOfTasks");

		List<Variable> variables = new ArrayList<>();

		for (int i = 0; i < numberOfTasks; i++) {
			Map<String, Object> params = new HashMap<>();

			int scheduledTime = ( (List<Integer>) parameters.get("scheduledTimes")).get(i);
			int duration = ( (List<Integer>) parameters.get("durations")).get(i);

			params.put("id", i);
			parameters.put("duration", duration);
			parameters.put("scheduledTime", scheduledTime);

			Variable variable = new Task();
			variable.set(params);
			variables.add(variable);
		}

		int numberOfSkills = (Integer) parameters.get("numberOfSkills");
		int numberOfResources = (Integer) parameters.get("numberOfResources");

		variables = setNeighbours(variables, (int [][]) parameters.get("tasks"));
		variables = setSkills(variables, (int [][]) parameters.get("skills"), numberOfSkills );
		variables = setResources(variables, (double [][]) parameters.get("resources"), numberOfSkills, numberOfResources);

		return variables;
	}


	/**
	 * This method sets the neighbours for each variable: predecessors and descendents
	 * @param variables		List		Set of variables
	 * @param tasks			int[][]	   	Relationship matrix
	 *
	 * @implNote    		tasks[i][j] = 1 means Variable i is predecessors of Variable j,
	 * 										and Variable j is descendent of Variable i
	 *
	 * @return variables	The same variables with set descendants and predecessors for each variable.
	 *
	 * */
	public List<Variable> setNeighbours(List<Variable> variables, int[][] tasks) {
		int size = variables.size();

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (i != j) {
					if ( tasks[i][j] == 1) {
						((Task) variables.get(i)).getDescendants().add(variables.get(j));
						((Task) variables.get(j)).getPredecessors().add(variables.get(j));
					} else if (tasks[j][i] == 1) {
						((Task) variables.get(i)).getPredecessors().add(variables.get(j));
						((Task) variables.get(j)).getDescendants().add(variables.get(j));
					}
				}
			}
		}
		return variables;
	}


	/**
	 * This method sets the skills needed for each variable:
	 * @param variables			List		Set of variables
	 * @param skills			int[][]	   	Task-Skill matrix
	 * @param numberOfSkills	int			The maximum number of skills a variable may have
	 * @implNote    skills[i][j] = 1 means Variable i needs Skill j
	 *
	 * @return variables		The same variables with set skills for each variable.
	 *
	 * */
	public List<Variable> setSkills(List<Variable> variables, int[][] skills, int numberOfSkills) {

		for (Variable variable: variables) {
			for	(int j = 0; j < numberOfSkills; j++ ) {
				if (skills[((Task) variable).getId()][j] == 1) {
					((Task) variable).getSkills().add(new Skill(j, 0.0));
				}
			}
		}
		return variables;
	}

	/**
	 * This method sets the resources for each variable: predecessors and descendents
	 * @param variables			List		Set of variables
	 * @param resources			double[][]	Resource-Skill matrix
	 * @param numberOfSkills	int			The maximum number of skills a variable may have
	 * @param numberOfResources	int			The total number of resources
	 * @implNote    skills[i][j] = 1 means Variable i needs Skill j
	 *
	 * @return variables		The same variables with set skills for each variable.
	 *
	 * */
	public List<Variable> setResources(List<Variable> variables, double[][] resources, int numberOfSkills, int numberOfResources) {
		for (Variable variable: variables) {
			for (int i = 0; i < numberOfResources; i++) {
				Resource resource = new Resource(i);
				for (int j = 0; j < numberOfSkills; j++) {
					if (resources[i][j] > 0) {
						resource.getSkills().add(new Skill(j, resources[i][j]));
					}
				}
				((Task) variable).getResources().add(resource);
			}
		}
		return variables;
	}

}
