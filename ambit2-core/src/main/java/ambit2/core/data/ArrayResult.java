package ambit2.core.data;

import org.openscience.cdk.qsar.result.IDescriptorResult;

public class ArrayResult<T> implements IDescriptorResult {
    /**
     * 
     */
    private static final long serialVersionUID = 8938268348726747293L;
    private T[] result;

    public ArrayResult() {
	super();
	result = null;
    }

    public ArrayResult(T[] array) {
	super();
	result = array;
    }

    public T get(int index) {
	return result[index];
    }

    public void set(int index, T value) {
	result[index] = value;
    }

    public int size() {
	if (result == null)
	    return 0;
	else
	    return result.length;
    }

    public String toString() {
	if (result == null)
	    return "";
	StringBuffer b = new StringBuffer();
	for (int i = 0; i < result.length; i++) {
	    b.append(result[i]);
	    b.append("\t");
	}
	return b.toString();
    }

    public int length() {
	return result.length;
    }
}
