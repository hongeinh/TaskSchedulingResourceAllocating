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
public class TimeFrame {

	private LocalDateTime startDateTime;
	private LocalDateTime endDateTime;
	private double elapsedTime;
}
