package component.resource;

import common.STATUS;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MachineResource extends Resource{
	private double consumeFactor;

	@Builder
	public MachineResource(int id, STATUS status, double cost, double consumeFactor) {
		super(id, status, cost);
		this.consumeFactor = consumeFactor;
	}
}
