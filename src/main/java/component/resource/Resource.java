package component.resource;

import common.STATUS;
import component.timeframe.TimeFrame;
import lombok.*;

import java.io.Serializable;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Resource implements Serializable {

	protected int id;
	protected STATUS status;
	protected double cost;
	protected List<TimeFrame> usedTimeFrames;
}
