package representation;

import component.variable.Variable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class Solution implements Comparable, Serializable {
	private int id;
	private List<Variable> variables;
	private double[] objectives;
	private double[] fitness;
	private int dominationCount;
	private List<Solution> dominatedSolutions;

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();

		for (Variable variable: variables) {
			stringBuilder.append(variables.toString());
			stringBuilder.append("\n++++++++++\n");
		}

		return stringBuilder.toString();
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
		int equalCount = 0;
		int lesserCount = 0;
		for (int i = 0; i < objectiveSize; i++) {
			if (this.getObjectives()[i] < sol.getObjectives()[i])
				greaterCount++;
			else if(this.getObjectives()[i] == sol.getObjectives()[i])
				equalCount++;
			else if(this.getObjectives()[i] > sol.getObjectives()[i])
				lesserCount++;
		}

		if (greaterCount == objectiveSize)
			return 1;
		else if (lesserCount == objectiveSize)
			return -1;
		else if (equalCount == objectiveSize)
			return 0;
		else if (lesserCount + equalCount == objectiveSize)
			return -1;
		else if (greaterCount + equalCount == objectiveSize)
			return 1;
		else
			return 0;
	}

	public boolean notExistIn(List<Solution> solutions) {
		for (Solution solution: solutions) {
			double [] otherObjectives = solution.getObjectives();
			if (this.objectives[0] == otherObjectives[0] &&
			this.objectives[1] == otherObjectives[1] &&
			this.objectives[2] == otherObjectives[2]) {
				return false;
			}
		}
		return true;
	}
}
