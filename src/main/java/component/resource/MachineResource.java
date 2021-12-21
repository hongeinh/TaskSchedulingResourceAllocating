package component.resource;

import common.STATUS;
import component.timeframe.TimeSlot;
import lombok.*;

import java.util.List;

@Getter
@Setter
public class MachineResource extends Resource{

	private double consumeFactor;

	@Builder
	public MachineResource(int id, STATUS status, double cost, List<TimeSlot> usedTimeSlots, double consumeFactor) {
		super(id, status, cost, usedTimeSlots);
		this.consumeFactor = consumeFactor;
	}
}
