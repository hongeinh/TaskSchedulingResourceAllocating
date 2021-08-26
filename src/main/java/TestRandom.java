import utils.NumberUtil;

import java.util.HashMap;
import java.util.Map;

public class TestRandom {
	public static void main(String[] args) {
		Map<Double, Double> map = new HashMap<>();
		for (int i = 0; i < 100; i++) {
			double rand = NumberUtil.getRandomDoubleNumber(0, 25);
			if (map.containsKey(rand)) {
				double times = map.get(rand);
				map.put(rand, times + 1);
			} else {
				map.put(rand, 1.0);
			}
		}
		for (Map.Entry entry: map.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
	}
}
