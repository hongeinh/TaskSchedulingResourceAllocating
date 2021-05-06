package component;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Task extends Variable{

	private int id;
	private double scheduledTime;
	private double duration;
	private double start;
	private double idle;
	private List<Variable> predecessors;
	private List<Variable> descendants;
	private List<Skill> skills;
	private List<Resource> resources;

	public Task() {
		this.predecessors = new ArrayList<>();
		this.descendants = new ArrayList<>();
		this.skills = new ArrayList<>();
		this.resources = new ArrayList<>();
	}


	@Override
	public Object get() {
		Map<String, Object> fields = new HashMap<>();
		fields.put("id", this.id);
		fields.put("scheduledTime", this.scheduledTime);
		fields.put("duration", this.duration);
		fields.put("start", this.start);
		fields.put("idle", this.idle);
		fields.put("predecessors", this.predecessors);
		fields.put("descendants", this.descendants);
		fields.put("skills", this.skills);
		fields.put("resources", this.resources);
		return fields;
	}

	@Override
	public void set(Object value) {
		if (value instanceof HashMap ) {
			HashMap<String, Object> parameters = (HashMap <String, Object>) value;
			this.id = (Integer) parameters.get("id");
			this.duration =  (Double) parameters.get("duration");
			this.scheduledTime = (Double) parameters.get("scheduledTime");
		}
	}


	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("Task " + this.id + ":" +
				"\n\tDuration: " + this.duration +
				"\n\tScheduled start: " + this.scheduledTime +
				"\n\tStart: " + this.start +
				"\n\tResource: ");
		for (Resource resource: resources) {
			if (resource.getStatus() == Resource.STATUS.ASSIGNED)
				stringBuilder.append(resource.getId() + "\t");
		}
		return stringBuilder.toString();
	}
}
