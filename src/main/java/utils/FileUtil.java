package utils;

import component.variable.Variable;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import representation.Solution;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FileUtil {

	public static final String RESULT_DIRECTORY = "experiment/ga-experiment";

	public PrintWriter createPrintWriter(String dirname, String filename, boolean isAppend) throws IOException {
		String currentPath = System.getProperty("user.dir") + "/src/main/java";
		System.out.println(currentPath);
		FileWriter fileWriter = new FileWriter(currentPath + dirname + filename, isAppend);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		return new PrintWriter(bufferedWriter);
	}

	public static void createExperimentDirectory() {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date date = new Date();
		String directoryName = dateFormat.format(date);
		String directoryExperimentPath = RESULT_DIRECTORY + File.separator + directoryName;
		File directoryExperiment = new File(directoryExperimentPath);
		if (!directoryExperiment.exists()) {
			directoryExperiment.mkdirs();
		}

	}

	public static void writeSolutionHeader(String pathFileName, String sheetName) {
		try	{
			InputStream inputStream = new FileInputStream(pathFileName);
			Workbook workbook = WorkbookFactory.create(inputStream);
			XSSFSheet sheet = (XSSFSheet) workbook.getSheet(sheetName);

			XSSFRow headerRow = sheet.createRow(0);

			XSSFCell idCell = headerRow.createCell(0);
			idCell.setCellValue(CSVHeaders.SOLUTION_HEADERS[0]);

			XSSFCell rankCell = headerRow.createCell(1);
			rankCell.setCellValue(CSVHeaders.SOLUTION_HEADERS[1]);

			XSSFCell distanceCell = headerRow.createCell(2);
			distanceCell.setCellValue(CSVHeaders.SOLUTION_HEADERS[2]);

			XSSFCell firstObjectiveCell = headerRow.createCell(3);
			firstObjectiveCell.setCellValue(CSVHeaders.SOLUTION_HEADERS[3]);

			XSSFCell secondObjectiveCell = headerRow.createCell(4);
			secondObjectiveCell.setCellValue(CSVHeaders.SOLUTION_HEADERS[4]);

			XSSFCell thirdObjectiveCell = headerRow.createCell(5);
			thirdObjectiveCell.setCellValue(CSVHeaders.SOLUTION_HEADERS[5]);

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

	public static void writeSolutionResult(String pathFileName, String sheetName, List<Solution> solutions) {
		int numberOfRows = solutions.size() + 1;
		try	{
			InputStream inputStream = new FileInputStream(pathFileName);
			Workbook workbook = WorkbookFactory.create(inputStream);
			XSSFSheet sheet = (XSSFSheet) workbook.getSheet(sheetName);

			for (int i = 1; i < numberOfRows; i++) {
				XSSFRow solutionRow = sheet.createRow(i);

				XSSFCell idCell = solutionRow.createCell(0);
				idCell.setCellValue(solutions.get(i).getId());

				XSSFCell rankCell = solutionRow.createCell(1);
				rankCell.setCellValue(solutions.get(i).getFitness()[0]);

				XSSFCell distanceCell = solutionRow.createCell(2);
				distanceCell.setCellValue(solutions.get(i).getFitness()[1]);

				XSSFCell firstObjectiveCell = solutionRow.createCell(3);
				firstObjectiveCell.setCellValue(solutions.get(i).getObjectives()[0]);

				XSSFCell secondObjectiveCell = solutionRow.createCell(4);
				secondObjectiveCell.setCellValue(solutions.get(i).getObjectives()[1]);

				XSSFCell thirdObjectiveCell = solutionRow.createCell(5);
				thirdObjectiveCell.setCellValue(solutions.get(i).getObjectives()[0]);
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
			for (Solution solution: solutions) {
				String variableSheetName = "solution #" + solution.getId();
				writeVariableResult(pathFileName, variableSheetName, solution.getVariables() );
			}
		}
	}

	public static void writeVariableResult(String pathFileName, String sheetName, List<Variable> variables) {

	}
}
