package algorithm;

import component.variable.Variable;
import component.variable.impl.Task;
import lombok.Getter;
import lombok.Setter;
import operator.Operator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import problem.Problem;
import representation.Solution;
import utils.CSVHeaders;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public abstract class Algorithm {

	protected List<Operator> operators;
	protected int solutionSetSize;
	protected int eliteSetSize;
	protected int eliteRate;

	protected double geneMutationProbability;
	protected int maxGeneration;


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


	public void displaySolutions(List<Solution> solutions, String dir, String filename, boolean isAppend) throws IOException {
		String dirname = "/result/solution/";
		PrintWriter printWriter = createPrintWriter(dirname + dir, filename, isAppend);
		for (Solution solution : solutions) {
			printWriter.println(solution.toString() + "\n");
			displaySolutionVariables(solution.getVariables(), dirname + dir + "variables/", filename);
		}
		printWriter.close();

	}

	protected void displaySolutionVariables(List<Variable> variables, String dirname, String filename) throws IOException {
		PrintWriter printWriter = createPrintWriter(dirname, filename, false);
		for (Variable variable: variables) {
			printWriter.println(variable.toString() + "\n");
//			displayVariableChromosome((List<Task>)variable.getValue(), dirname + "chromosomes/", filename);
		}
	}

	protected void displayVariableChromosome(List<Task> value, String dirname, String filename) throws IOException {
//		PrintWriter printWriter = createPrintWriter(dirname, filename, false);
//			for (Task obj: (List) value) {
//				printWriter.println(obj.toString() + "\n");
//			}
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
