package utils;


import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtil {
	public static int getRandomNumber(int min, int max) {
		return (int) ((Math.random() * (max - min)) + min);
	}

	public static double floor2DecimalPoints(double num) {
		BigDecimal bd = new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
}
