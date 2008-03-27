package ambit2.test.data.experiments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import junit.framework.TestCase;
import ambit2.data.experiment.DSSToxCarcinogenicityTemplate;
import ambit2.data.experiment.DSSToxERBindingTemplate;
import ambit2.data.experiment.DSSToxLC50Template;
import ambit2.data.experiment.LLNATemplate;
import ambit2.data.experiment.StudyTemplate;

public class StudyTemplateTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(StudyTemplateTest.class);
	}
	public void roundtrip(StudyTemplate t, String filename) {
		try {
			FileOutputStream out = new FileOutputStream(filename);
			t.save(out);
			
			out.close();
			assertTrue(new File(filename).exists());
			
			FileInputStream in = new FileInputStream(filename);
			StudyTemplate newt = new StudyTemplate("");
			newt.load(in);
			in.close();
			assertEquals(t,newt);
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
	}
	public void testDSSToxLC50Template() {
	    roundtrip(new DSSToxLC50Template("LC50"),"DSSTox_LC50_template.xml");
	}
	public void testDSSToxCarcinogenicityTemplate() {
	    roundtrip(new DSSToxCarcinogenicityTemplate("Carcinogenicity"),"DSSTox_Carcinogenicity_template.xml");
	}
	public void testDSSToxERBindingTemplate() {
	    roundtrip(new DSSToxERBindingTemplate("ERBinding"),"DSSTox_ERBinding_template.xml");
	}	
	public void testLLNATemplate() {
	    roundtrip(new LLNATemplate("LLNA"),"LLNA_template.xml");
	}
	public void test() {
	    String filename = "data/templates/DSSTox_LC50_template.xml";
		try {
			FileInputStream in = new FileInputStream(filename);
			StudyTemplate newt = new StudyTemplate("");
			newt.load(in);
			assertEquals("DSSTox-EPAFHM",newt.getName());
			in.close();
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
	    
	}
	
}
