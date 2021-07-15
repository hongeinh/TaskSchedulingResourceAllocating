package algorithm;

import lombok.Getter;
import lombok.Setter;
import operator.Operator;
import problem.Problem;
import solution.Solution;

import java.io.*;
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


	public void displaySolutions(List<Solution> solutions, String filename, boolean isAppend, int offspringGeneration) throws IOException {
		PrintWriter printWriter = createPrintWriter("/result/solution/", filename, isAppend);
		int i = 1;
		for (Solution solution : solutions) {
			printWriter.println(i + ". " + solution.toString() + "\n");
			i++;
		}
		printWriter.close();

	}

	public void displayFitness(List<Solution> solutions, String filename, boolean isAppend) throws IOException {
		PrintWriter printWriter = createPrintWriter("/result/fitness/", filename, isAppend);
		int i = 1;
		printWriter.println("SolutionId, Rank, Distance");
		for (Solution solution : solutions) {
			printWriter.println(i + ", " + solution.getFitness()[0] + ", " + solution.getFitness()[1] + "\n");
			i++;
		}
		printWriter.close();

	}

	public void displayObjectives(List<Solution> solutions, String filename, boolean isAppend, int offspringGeneration) throws IOException {
		String currentPath = System.getProperty("user.dir") + "/src/main/java";
		FileWriter fileWriter = new FileWriter(currentPath + "/result/objective/" + filename, isAppend);

		int i = 1;
		int objectiveSize = solutions.get(0).getObjectives().length;

		if (offspringGeneration == -1) {
			StringBuilder printString = new StringBuilder();
			for (Solution solution : solutions) {
				printString.append(i + ", ");
				for (int j = 0; j < objectiveSize - 1; j++) {
					printString.append(solution.getObjectives()[j] + ",");

				}
				printString.append(solution.getObjectives()[objectiveSize - 1] + "\n");
				i++;
			}
			fileWriter.write(printString.toString());
		} else {
			StringBuilder printString = new StringBuilder();
			for (Solution solution : solutions) {
				printString.append(offspringGeneration + ", " + i + ", ");
				for (int j = 0; j < objectiveSize - 1; j++) {
					printString.append(solution.getObjectives()[j] + ",");

				}
				printString.append(solution.getObjectives()[objectiveSize - 1] + "\n");
				i++;
			}
			fileWriter.write(printString.toString());

		}
		fileWriter.close();
	}

	public PrintWriter createPrintWriter(String dirname, String filename, boolean isAppend) throws IOException {
		String currentPath = System.getProperty("user.dir") + "/src/main/java";
		System.out.println(currentPath);
		FileWriter fileWriter = new FileWriter(currentPath + dirname + filename, isAppend);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		return new PrintWriter(bufferedWriter);
	}

}
