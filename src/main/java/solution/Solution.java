package solution;

import component.Variable;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.List;

@Getter
@Setter
public class Solution implements Comparable  {
	private List<Variable> variables;
	private double[] objectives;
	private double[] fitness;
	private int dominationCount;
	private List<Solution> dominatedSolutions;


	public List<Variable> getVariables() {
		return variables;
	}

	public void setVariables(List<Variable> variables) {
		this.variables = variables;
	}

	public double[] getObjectives() {
		return objectives;
	}

	public void setObjectives(double[] objectives) {
		this.objectives = objectives;
	}

	public double[] getFitness() {
		return fitness;
	}

	public void setFitness(double[] fitness) {
		this.fitness = fitness;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();

		int sz = this.getFitness().length;

		stringBuilder.append("Fitness: ");
		for (int i = 0; i < sz; i++)
			stringBuilder.append(fitness[i] + "\t");

		stringBuilder.append("\nObjectives: ");
		sz = this.getObjectives().length;
		for (int i = 0; i < sz; i++)
			stringBuilder.append(objectives[i] + "\t");

		stringBuilder.append("\nVariables:\n");
		for (Variable variable: this.variables)
			stringBuilder.append(variable.toString() + "\n");

		return stringBuilder.toString();
	}

	public static Solution copy(Solution anotherSolution) throws CloneNotSupportedException {
		Solution solution = new Solution();
		for (Variable variable: anotherSolution.getVariables()) {
			solution.getVariables().add((Variable) variable.clone());
		}
		return solution;
	}

	/**
	 * Compares two solution
	 * @param o     The solution to be compared to the current one
	 *              Solution A is considered to be dominating (greater than) solution B if all of <br>
	 *              A's objective < B's objective
	 * @return
	 *  1           A dominates B
	 *  0           A and B are not dominating each other
	 *  -1          A is dominated by B
	 *
	 * */
	@Override
	public int compareTo(Object o) {
		int objectiveSize = this.getObjectives().length;
		Solution sol = (Solution) o;
		int greaterCount = 0;
		for (int i = 0; i < objectiveSize; i++) {
			if (this.getObjectives()[i] < sol.getObjectives()[i])
				greaterCount++;
		}

		if (greaterCount == objectiveSize)
			return 1;
		else if (greaterCount == 0)
			return -1;
		else
			return 0;

	}

}
