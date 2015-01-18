package ambit2.base.data.study;

import java.util.ArrayList;

public class MultiValue<V extends IValue> extends ArrayList<V> {
    public MultiValue() {
	super();
    }

    public MultiValue(V value) {
	super();
	add(value);
    }

    public String toHumanReadable() {
	StringBuilder b = new StringBuilder();
	for (int i=0; i < size(); i++) {
	    b.append(get(i).toHumanReadable());
	    b.append(" ");
	}
	return b.toString();
    }
}
