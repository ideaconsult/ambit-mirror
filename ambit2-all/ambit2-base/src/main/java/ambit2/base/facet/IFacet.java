package ambit2.base.facet;

import java.io.Serializable;

public interface IFacet<T> extends Serializable {
	T getValue();
	void setValue(T object);
	int getCount();
	void setCount(int value);
	String getResultsURL();
}
