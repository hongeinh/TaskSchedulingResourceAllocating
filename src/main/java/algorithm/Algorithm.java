package algorithm;

import lombok.Getter;
import lombok.Setter;
import operator.Operator;
import problem.Problem;
import solution.Solution;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public abstract class Algorithm {

	protected List<Operator> operators;
	protected int solutionSetSize;


	public Algorithm() {
		this.operators = new ArrayList<>();
		this.solutionSetSize = 20;
	}

	public Algorithm(int solutionSetSize) {
		this.operators = new ArrayList<>();
		this.solutionSetSize = solutionSetSize;
	}


	public abstract List<Solution> executeAlgorithm(Problem problem) throws CloneNotSupportedException, IOException;

	public void addOperator(Operator operator) {
		this.operators.add(operator);
	}

	public void removeOperator(Operator operator) {
		if(this.operators.contains(operator)) {
			this.operators.remove(operator);
		}
	}

	public void displaySolutions(List<Solution> solutions, String filename) throws IOException {
		PrintWriter printWriter = createPrintWriter("/result/solution/", filename);
		int i = 1;
		for (Solution solution: solutions) {
			printWriter.println(i + ". " + solution.toString() +"\n");
			i++;
		}
	}

	public void displayFitness(List<Solution> solutions, String filename) throws IOException {
		PrintWriter printWriter = createPrintWriter("/result/fitness/", filename);
		int i = 1;
		printWriter.println("SolutionId, Rank, Distance");
		for (Solution solution: solutions) {
			printWriter.println(i + ", " + solution.getFitness()[0] + ", " + solution.getFitness()[1] +"\n");
			i++;
		}
	}

	public void displayObjectives(List<Solution> solutions, String filename) throws IOException {
		PrintWriter printWriter = createPrintWriter("/result/objective/", filename);
		int i = 1;
		printWriter.println("SolutionId, Duration, Cost, Conflict");
		int objectiveSize = solutions.get(0).getObjectives().length;

		for (Solution solution: solutions) {
			StringBuilder printString = new StringBuilder(i + ", ");
			for (int j = 0; j < objectiveSize - 1; j++) {
				printString.append(solution.getObjectives()[j] + ",");

			}
			printString.append(solution.getObjectives()[objectiveSize - 1] + "\n");
			printWriter.println(printString);
			i++;
		}
	}

	private PrintWriter createPrintWriter(String dirname, String filename) throws IOException {
		String currentPath = System.getProperty("user.dir") +"/src/main/java";
		System.out.println(currentPath);
		FileWriter fileWriter = new FileWriter(currentPath +  dirname + filename);
		return new PrintWriter(fileWriter);
	}

}
