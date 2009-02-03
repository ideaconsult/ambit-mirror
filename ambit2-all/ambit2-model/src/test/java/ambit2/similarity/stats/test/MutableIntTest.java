/**
 * <b>Filename</b> MutableIntTest.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-6-20
 * <b>Project</b> ambit
 */
package ambit2.similarity.stats.test;

import junit.framework.TestCase;
import ambit2.similarity.stats.MutableInt;

/**
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-6-20
 */
public class MutableIntTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(MutableIntTest.class);
    }

    public void testMutableInt() {
        MutableInt mi = new MutableInt(1);
        assertEquals(mi.getValue(),1);
        mi = null;
    }

    public void testSetValue() {
        MutableInt mi = new MutableInt(1);
        assertEquals(mi.getValue(),1);
        mi.setValue(2);
        assertEquals(mi.getValue(),2);
        mi = null;
        
    }

    public void testIncrement() {
        MutableInt mi = new MutableInt(0);        
        assertEquals(mi.getValue(),0);
        mi.increment();
        assertEquals(mi.getValue(),1);
        mi.increment();
        assertEquals(mi.getValue(),2);        
        mi = null;
    }

    public void testDecrement() {
        MutableInt mi = new MutableInt(10);        
        assertEquals(mi.getValue(),10);
        mi.decrement();
        assertEquals(mi.getValue(),9);
        mi.decrement();
        assertEquals(mi.getValue(),8);        
        mi = null;        
    }

}

