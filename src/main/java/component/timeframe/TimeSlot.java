package component.timeframe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlot implements Comparable<TimeSlot> {

	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;

	@Override
	public int compareTo(TimeSlot o) {
		if (this.startDateTime.isBefore(o.getStartDateTime()) ||
				(this.startDateTime.isEqual(o.getStartDateTime()) && this.endDateTime.isBefore(o.getEndDateTime()))) {
			return 1;
		} else if (this.startDateTime.isAfter(o.getStartDateTime()) ||
				(this.startDateTime.isEqual(o.getStartDateTime()) && this.endDateTime.isAfter(o.getEndDateTime()))) {
			return -1;
		} else {
			return 0;
		}
	}
}
