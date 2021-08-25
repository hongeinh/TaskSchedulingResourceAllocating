package utils;

import component.variable.Variable;
import component.variable.impl.Task;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import representation.Solution;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileUtil {

	public static final String RESULT_DIRECTORY = System.getProperty("user.dir") + "/src/main/java" + File.separator + "result";

	public PrintWriter createPrintWriter(String dirname, String filename, boolean isAppend) throws IOException {
		String currentPath = System.getProperty("user.dir") + "/src/main/java";
		System.out.println(currentPath);
		FileWriter fileWriter = new FileWriter(currentPath + dirname + filename, isAppend);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		return new PrintWriter(bufferedWriter);
	}

	public static String createResultDirectory(String directoryName) {
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
		Date date = new Date();
		String resultDirectoryName = RESULT_DIRECTORY + File.separator + directoryName;
		File directoryExperiment = new File(resultDirectoryName);
		if (!directoryExperiment.exists()) {
			directoryExperiment.mkdirs();
		}

		String resultFileName = resultDirectoryName + File.separator + df.format(date) + "-results.xlsx";
		File resultFile = new File(resultDirectoryName);
		if (!resultFile.exists()) {
			try {
				XSSFWorkbook workbook = new XSSFWorkbook(resultFileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		writeSolutionHeader(resultFileName, "Overall solutions");

		return resultFileName;
	}


	public static void writeSolutionHeader(String pathFileName, String sheetName) {
		try {

			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet(sheetName);
			XSSFRow headerRow = sheet.createRow(0);

			for (int i = 0; i < CSVHeaders.SOLUTION_HEADERS.length; i++) {
				XSSFCell cell = headerRow.createCell(i);
				cell.setCellValue(CSVHeaders.SOLUTION_HEADERS[i]);
			}

			OutputStream fileOut = new FileOutputStream(pathFileName);
			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void writeVariableHeader(String pathFileName, String variableSheetName) {
		try {
			InputStream inputStream = new FileInputStream(pathFileName);
			XSSFWorkbook workbook = (XSSFWorkbook) WorkbookFactory.create(inputStream);
			XSSFSheet sheet = workbook.createSheet(variableSheetName);

			XSSFRow headerRow = sheet.createRow(0);

			for (int i = 0; i < CSVHeaders.VARIABLE_HEADERS.length; i++) {
				XSSFCell cell = headerRow.createCell(i);
				cell.setCellValue(CSVHeaders.VARIABLE_HEADERS[i]);
			}
			FileOutputStream fileOutputStream = new FileOutputStream(pathFileName);
			workbook.write(fileOutputStream);
			fileOutputStream.flush();
			fileOutputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeSolutionResult(String pathFileName, List<Solution> solutions) {
		int numberOfRows = solutions.size() + 1;
		try {
			String sheetName = "Overall solutions";
			InputStream inputStream = new FileInputStream(pathFileName);
			Workbook workbook = WorkbookFactory.create(inputStream);
			XSSFSheet sheet = (XSSFSheet) workbook.getSheet(sheetName);

			for (int i = 1; i < numberOfRows; i++) {
				XSSFRow solutionRow = sheet.createRow(i);

				XSSFCell idCell = solutionRow.createCell(0);
				idCell.setCellValue(solutions.get(i - 1).getId());

				XSSFCell rankCell = solutionRow.createCell(1);
				rankCell.setCellValue(solutions.get(i - 1).getFitness()[0]);

				XSSFCell distanceCell = solutionRow.createCell(2);
				distanceCell.setCellValue(solutions.get(i - 1).getFitness()[1]);

				XSSFCell firstObjectiveCell = solutionRow.createCell(3);
				firstObjectiveCell.setCellValue(solutions.get(i - 1).getObjectives()[0]);

				XSSFCell secondObjectiveCell = solutionRow.createCell(4);
				secondObjectiveCell.setCellValue(solutions.get(i - 1).getObjectives()[1]);

				XSSFCell thirdObjectiveCell = solutionRow.createCell(5);
				thirdObjectiveCell.setCellValue(solutions.get(i - 1).getObjectives()[3]);
			}

			FileOutputStream fileOutputStream = new FileOutputStream(pathFileName);
			workbook.write(fileOutputStream);
			fileOutputStream.flush();
			fileOutputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
//			for (Solution solution : solutions) {
//				String variableSheetName = "solution #" + solution.getId();
//				writeVariableHeader(pathFileName, variableSheetName);
//				writeVariableResult(pathFileName, variableSheetName, solution.getVariables());
//			}
		}
	}


	public static void writeVariableResult(String pathFileName, String sheetName, List<Variable> variables) {
		List<Task> joinTasks = joinVariables(variables);
		int numberOfRows = joinTasks.size() + 1;
		try {
			InputStream inputStream = new FileInputStream(pathFileName);
			Workbook workbook = WorkbookFactory.create(inputStream);
			XSSFSheet sheet = (XSSFSheet) workbook.getSheet(sheetName);

			for (int i = 1; i < numberOfRows; i++) {
				XSSFRow solutionRow = sheet.createRow(i);

				Task task = joinTasks.get(i - 1);

				XSSFCell orderIdCell = solutionRow.createCell(0);
				orderIdCell.setCellValue(task.getOrderId());
				XSSFCell taskIdCell = solutionRow.createCell(1);
				taskIdCell.setCellValue(task.getId());

				XSSFCell startCell = solutionRow.createCell(2);
				startCell.setCellValue(task.getStart());

				XSSFCell durationCell = solutionRow.createCell(3);
				durationCell.setCellValue(task.getDuration());

				XSSFCell humanResouceCell = solutionRow.createCell(4);
				humanResouceCell.setCellValue(task.getHumanResourceString());

				XSSFCell machineResourceCell = solutionRow.createCell(5);
				machineResourceCell.setCellValue(task.getMachineResourceString());
			}

			FileOutputStream fileOutputStream = new FileOutputStream(pathFileName);
			workbook.write(fileOutputStream);
			fileOutputStream.flush();
			fileOutputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static List<Task> joinVariables(List<Variable> variables) {
		List<Task> tasks = new ArrayList<>();
		for (Variable variable : variables) {
			tasks.addAll((List<Task>) variable.getValue());
		}
		return tasks;
	}
}
