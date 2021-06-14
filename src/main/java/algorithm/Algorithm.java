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
//		String currentPath = System.getProperty("user.dir") +"/src/main/java";
//		System.out.println(currentPath);
//		FileWriter fileWriter = new FileWriter(currentPath +  filename);
//		PrintWriter printWriter = new PrintWriter(fileWriter);
//		printWriter.println(filename);
		int i = 1;
		for (Solution solution: solutions) {
//			printWriter.println(i + ". " + solution.toString() +"\n");
			System.out.println(i + ". " + solution.toString() + "\n");
			i++;
		}
	}

}
