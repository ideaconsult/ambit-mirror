/**
 * <b>Filename</b> HistogramTest.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-6-20
 * <b>Project</b> ambit
 */
package ambit2.similarity.stats.test;

import junit.framework.TestCase;
import ambit2.similarity.stats.ConfusionMatrix;
import ambit2.similarity.stats.DoubleHistogramBin;
import ambit2.similarity.stats.DoubleValuesHistogram;
import ambit2.similarity.stats.Histogram;
import ambit2.similarity.stats.SelfBin;
import ambit2.similarity.stats.ValuePair;

/**
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-6-20
 */
public class HistogramTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(HistogramTest.class);
    }
    public void testHistogram() {
        Histogram<String> h = new Histogram<String>();
        h.addObject("AAA");
        assertEquals(1,h.size());
        h.addObject("BBB");
        assertEquals(2,h.size());
        h.addObject("AAA");
        assertEquals(2,h.size());
        assertEquals(h.getFrequency("AAA"),2);
        assertEquals(h.getFrequency("BBB"),1);
        h.clear();
        assertEquals(0,h.size());
        assertEquals(h.getFrequency("AAA"),0);
    }
    public void testEntropy() {
        Histogram<String> h = new Histogram<String>();
        h.addObject("A");
        h.addObject("B");
        h.addObject("C");
        double e= - 3 * (1.0/3.0) * Math.log(1.0/3.0);
        assertEquals( e * Histogram.ln2_1 , h.entropy(), 1E-10);
		h = null;        
    }
    public void testHellinger() {
        Histogram<String> h1 = new Histogram<String>();
        h1.addObject("-1,-1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,",1);
        
        Histogram<String> h2 = new Histogram<String>();
        h2.addObject("-1,-1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,",1);
        h2.addObject("14,4,1,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,",4);
        h2.addObject("14,4,1,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,",7);
        h2.addObject("16,4,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,4,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,",1);
        h2.addObject("16,4,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,4,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,",1);
        h2.addObject("16,4,1,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,3,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,",3);
        h2.addObject("16,4,1,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,",3);
        h2.addObject("16,4,1,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,",4);
        h2.addObject("18,4,1,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,4,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,",1);
        h2.addObject("19,4,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,",1);
        h2.addObject("19,4,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,",1);
        h2.addObject("20,4,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,",2);
        h2.addObject("5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,",4);
        h2.addObject("7,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,",1);
        h2.addObject("7,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,",3);
        h2.addObject("7,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,",7);
        h2.addObject("7,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,",11);
        h2.addObject("8,-1,2,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,",2);
        
        assertEquals(18,h2.size());
        try {
            double hd = h1.hellinger(h2);
            //System.out.println(hd);            
            assertTrue(hd <= 2);
        } catch (Exception x) {
            x.printStackTrace();
            fail();
        }
    }
    public void testRelativeEntropy() {
        Histogram<String> h = new Histogram<String>();
        h.addObject("A");
        h.addObject("B");
        h.addObject("C");
        
        Histogram<String> h1 = new Histogram<String>();
        h1.addObject("A");
        h1.addObject("B");
        h1.addObject("C");
    	
        h1.addObject("C");        
        
        double hd = 0;
        double third = 1.0/3.0;
        hd +=  Math.pow(Math.sqrt(0.25) + Math.sqrt(third),2);
        hd +=  Math.pow(Math.sqrt(0.25) + Math.sqrt(third),2);
        hd +=  Math.pow(Math.sqrt(0.5) + Math.sqrt(third),2);       
        hd = Math.sqrt(hd);
//        HD += Math.pow(Math.sqrt(prob[i][0])+Math.sqrt(prob[i][1]),2.0);
        try { 
        	assertEquals(hd ,h.hellinger(h1),1E-10);
        } catch (Exception x) {
        	fail();
        }
        h1.clear();

        h1.addObject("D");
        h1.addObject("F");
        h1.addObject("D");        
        try { 
        	assertEquals(1 ,h.hellinger(h1),1E-10);

        } catch (Exception x) {
        	fail();
        }
        
		h = null;    
		h1 = null;    
    }
    public void testConfusionMatrix() {
        Histogram<SelfBin<ValuePair>> h = new Histogram<SelfBin<ValuePair>>();
        h.addObject(new SelfBin(new ValuePair("A","A")));
        h.addObject(new SelfBin(new ValuePair("B","A")));
        h.addObject(new SelfBin(new ValuePair("C","A")));
        h.addObject(new SelfBin(new ValuePair("A","B")));
        h.addObject(new SelfBin(new ValuePair("C","B")));
        h.addObject(new SelfBin(new ValuePair("B","C")));
        h.addObject(new SelfBin(new ValuePair("B","C")));
        h.addObject(new SelfBin(new ValuePair("C","C")));
        ConfusionMatrix cm = new ConfusionMatrix(h);
        String s = cm.toString();
        //System.out.println(s);
        assertEquals(3,cm.getColumnCount());
        assertEquals(3,cm.getRowCount());
        /*
        MutableInt m1 = new MutableInt(1);
        MutableInt m2 = new MutableInt(2);
        assertSame(m1,cm.getValueAt(0,0));
        assertSame("1",cm.getValueAt(0,1).toString());
        assertSame("1",cm.getValueAt(1,0).toString());
        assertSame("1",cm.getValueAt(1,1).toString());
        assertSame("2",cm.getValueAt(2,0).toString());
        assertSame("1",cm.getValueAt(2,1).toString());
        assertSame("1",cm.getValueAt(2,2).toString());
*/
    }
    public void testBin() {
    	Histogram<DoubleHistogramBin> histogram = new Histogram<DoubleHistogramBin>();
    	double start = 0.0;
    	for (int i=1; i <= 10; i ++) {
	    	DoubleHistogramBin d = new DoubleHistogramBin(start,i/10.0);
	    	assertEquals(d.getLow(),start,1E-6);
	    	assertEquals(d.getHigh(),i/10.0,1E-6);
	    	start = i/10.0;
	    	histogram.addObject(d);
    	}
    	histogram.addObject(new DoubleHistogramBin(0.4,0.5));
    	//System.out.println(histogram);
    	
    }
    public void testDoubleValuesHistogram() {
    	DoubleValuesHistogram h = new DoubleValuesHistogram("test");
    	h.addValue("1", "2", 0.3);
    	h.addValue("1", "2", 3.0);
    	h.addValue("1", "2", 0.4);
    	h.addValue("1", "2", 1.3);
    	h.addValue("1", "2", 13.0);
    	h.addValue("1", "2", 0.25);
    	//System.out.println(h);
    }
    public void testCompareBins() {
    	DoubleHistogramBin b = new DoubleHistogramBin(0.1,0.2);
    	DoubleHistogramBin b1 = new DoubleHistogramBin(0.2,0.3);
    	DoubleHistogramBin b2 = new DoubleHistogramBin(0.4,0.5);
    	assertTrue(b1.compareTo(b)>0);
    	assertTrue(b1.compareTo(b2)<0);
    	
    	assertTrue(b2.compareTo(b1)>0);
    	assertTrue(b2.compareTo(b)>0);
    	
    	assertTrue(b.compareTo(b1)<0);
    	assertTrue(b.compareTo(b2)<0);
    	
    	Histogram h = new Histogram();
    	h.addObject(b);
    	h.addObject(b1);
    	h.addObject(b2);
    	//System.out.println(h);
    	
    	DoubleHistogramBin b3 = new DoubleHistogramBin(0.7,0.8);
    	h.addObject(b3);
    	//System.out.println(h);
    	
    	
    }
}

