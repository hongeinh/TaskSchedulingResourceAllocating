package component;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Task implements Variable{

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


	public String toString() {
		return "Task " + this.id + ":" +
				"\n\tDuration: " + this.duration +
				"\n\tScheduled start: " + this.scheduledTime +
				"\n\tStart: " + this.start;
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
			this.duration =  (Integer) parameters.get("duration");
			this.scheduledTime = (Integer) parameters.get("scheduledTime");
		}

	}

}
