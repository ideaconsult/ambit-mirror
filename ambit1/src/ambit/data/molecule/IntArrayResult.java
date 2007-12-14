/**
 * <b>Filename</b> IntArrayResult.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-6-15
 * <b>Project</b> ambit
 */
package ambit.data.molecule;

import org.openscience.cdk.qsar.result.IDescriptorResult;

/**
 * Array of integers implementing {@link org.openscience.cdk.qsar.result.IDescriptorResult}.
 * It appears that {@link org.openscience.cdk.qsar.result.IntegerArrayResult} doesn't have methods for changing element values.
 * TODO ask CDK list why
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-6-15
 */
public class IntArrayResult implements IDescriptorResult {
    private int[] result;
    /**
     * 
     */
    public IntArrayResult() {
        super();
        result = null;
    }
    public IntArrayResult(int size) {
        super();
        result = new int[size];
    }
    public IntArrayResult(int[] array) {
        super();
        result = array;
    }    
    public int get(int index) {
        return result[index];
    }
    public void set(int index, int value) {
        result[index] = value;
    }    
    public int size() {
        if (result == null) return 0;
        else return result.length;
    }
    public String toString() {
        if (result == null) return "";
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < result.length; i++ ) {
            b.append(result[i]);
            b.append("\t");
        }    
        return b.toString();
    }
}

