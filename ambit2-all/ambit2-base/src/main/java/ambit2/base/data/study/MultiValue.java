package ambit2.base.data.study;

import java.util.ArrayList;



public class MultiValue<V extends IValue> extends ArrayList<V>  {
	public MultiValue() {
		super();
	}
	public MultiValue(V value) {
		super();
		add(value);
	}
}
