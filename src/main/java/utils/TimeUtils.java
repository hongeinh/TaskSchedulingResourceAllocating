package utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeUtils {
	public static long calculateTimeDifferenceWithTimeUnit (LocalDateTime firstDateTime, LocalDateTime secondDateTime, ChronoUnit unit) {
		long time = 0;
		if (firstDateTime.isBefore(secondDateTime)) {
			time = firstDateTime.until(secondDateTime, unit);
		} else if (firstDateTime.isAfter(secondDateTime)){
			time = secondDateTime.until(firstDateTime, unit) * (-1);
		}
		return time;
	}



//	public static void main(String[] args) {
//		LocalDateTime first = LocalDateTime.now();
//		LocalDateTime second = LocalDateTime.of(2021, 07, 31, 10, 22);
//		System.out.println((first, second));
//	}
}



