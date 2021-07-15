package component.variable;

import java.io.Serializable;

public abstract class Variable implements Comparable<Variable>, Cloneable, Serializable {

	public abstract Object getValue();
	public abstract void setValue(Object value);
	public Object clone() throws CloneNotSupportedException  {
		return super.clone();
	}
}
