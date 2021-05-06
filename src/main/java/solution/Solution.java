package solution;

import component.Variable;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Solution {
	private List<Variable> variables;
	private double[] objectives;
	private double[] fitness;

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
}
