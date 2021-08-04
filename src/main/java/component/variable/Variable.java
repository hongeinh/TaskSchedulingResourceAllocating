package component.variable;

import java.io.Serializable;

public interface Variable extends Comparable<Variable>, Cloneable, Serializable {

	 Object getValue();
	 void setValue(Object value);
}
