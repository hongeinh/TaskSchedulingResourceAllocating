package operator;

import java.util.Map;

public abstract class Operator {

	private Map<Object, Object> parameters;

	public void setParameters(Map<Object, Object> parameters) {
		this.parameters = parameters;
	}

	public Map<Object, Object> getParameters() {
		return this.parameters;
	}

	public abstract Object execute(Object object);

}
