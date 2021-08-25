package operator;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public abstract class Operator {

	private Map<Object, Object> parameters;

	public Operator() {
		this.parameters = new HashMap<>();
	}
	public void setParameters(Map<Object, Object> parameters) {
		this.parameters = parameters;
	}

	public Map<Object, Object> getParameters() {
		return this.parameters;
	}

	public abstract Object execute(Object object);

}
