/*
 * Created on 2005-12-18
 *
 */
package ambit2.test.dbadmin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;
import ambit2.data.DefaultData;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2005-12-18
 */
public class DefaultDataTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(DefaultDataTest.class);
    }
    public void test() {
        DefaultData d = new DefaultData();
        d.put(DefaultData.DEFAULT_DIR,"test");
        File f = new File("test.conf");
        try {
	        ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(f));
	        o.writeObject(d);
	        o.close();
	        ObjectInputStream i = new ObjectInputStream(new FileInputStream(f));
	        DefaultData d1 = (DefaultData) i.readObject();
	        assertEquals(d1.get(DefaultData.DEFAULT_DIR),"test");
	        
	        f.delete();
        } catch (Exception x) {
            fail();
        }
    }
    public void testXML() {
        DefaultData d = new DefaultData();
        DefaultData d1 = new DefaultData();
        d.put(DefaultData.DEFAULT_DIR,"test");
        try {
            FileOutputStream out = new FileOutputStream("test.xml");
            d.writeXML(out);
            out.close();
            assertTrue(new File("test.xml").exists());
            FileInputStream in = new FileInputStream("test.xml");
            d1.readXML(in);
            in.close();
            assertEquals("test",d1.get(DefaultData.DEFAULT_DIR));
        } catch (IOException x) {
            x.printStackTrace();
        }
    }
}
