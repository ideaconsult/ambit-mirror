/**
 * <b>Filename</b> MutableInt.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-6-17
 * <b>Project</b> ambit
 */
package ambit2.stats.datastructures;

/**
 * A mutable integer object. Used in {@link ambit2.stats.datastructures.Histogram}
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-6-17
 */
public class MutableInt {
    protected int value = 0;
    protected boolean flag = false;
    /**
     * 
     */
    public MutableInt(int value) {
        super();
        this.value = value;
        flag = false;
    }
    

    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }
    public int increment() {
        value++;
        return value;
    }
    public int decrement() {
        value--;
        return value;
    }    
    
    public boolean isFlag() {
        return flag;
    }
    public void setFlag(boolean flag) {
        this.flag = flag;
    }
    @Override
    public String toString() {
    	return Integer.toString(value);
    }
    @Override
    public boolean equals(Object obj) {
    	return value == ((MutableInt)obj).value;
    }
}

