package component.resource;

import common.STATUS;
import component.timeframe.TimeFrame;
import lombok.*;

import java.util.List;

@Getter
@Setter
public class MachineResource extends Resource{

	private double consumeFactor;
	private List<TimeFrame> usedTimeFrames;

	@Builder
	public MachineResource(int id, STATUS status, double cost, List<TimeFrame> usedTimeFrames, double consumeFactor) {
		super(id, status, cost, usedTimeFrames);
		this.consumeFactor = consumeFactor;
	}
}
