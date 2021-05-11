package component;

public abstract class Variable implements Comparable<Variable>, Cloneable{

	public abstract Object get();
	public abstract void set(Object value);
	public Object clone() throws CloneNotSupportedException  {
		return super.clone();
	}
}
