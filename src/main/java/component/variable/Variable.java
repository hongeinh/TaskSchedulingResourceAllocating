package component.variable;

public abstract class Variable implements Comparable<Variable>, Cloneable{

	public abstract Object getValue();
	public abstract void setValue(Object value);
	public Object clone() throws CloneNotSupportedException  {
		return super.clone();
	}
}
