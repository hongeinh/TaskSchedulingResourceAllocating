package utils;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

public class NumberUtil {
	public static int getRandomIntNumber(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max);
	}

	public static double floor2DecimalPoints(double num) {
		BigDecimal bd = new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static double getRandomDoubleNumber(double min, double max) {
		return ThreadLocalRandom.current().nextDouble(min, max + 1);
	}
}
