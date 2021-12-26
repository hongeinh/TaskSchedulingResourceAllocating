package utils;

public class DataRandomizeUtils {

	public static int numberOfOrders = 3;

	public static double[][] randomTreq(int numberOfTasks, int numberOfSkills) {
		double[][] treq = new double[numberOfTasks][numberOfSkills];

		for (int i = 0; i < numberOfTasks; i++) {
			for (int j = 0; j < numberOfSkills; j++) {
				double rand = Math.random();
				treq[i][j] = rand > 0.5 ? (double) Math.round(rand * 100) / 100 : 0;
			}
		}
		return treq;
	}

	public static double[][] randomLexp(int numberOfEmployees, int numberOfSkills) {
		double[][] treq = new double[numberOfEmployees][numberOfSkills];

		for (int i = 0; i < numberOfEmployees; i++) {
			for (int j = 0; j < numberOfSkills; j++) {
				double rand = Math.random();
				treq[i][j] = rand > 0.5 ? (double) Math.round(rand * 100) / 100 : 0;
			}
		}
		return treq;
	}
}
