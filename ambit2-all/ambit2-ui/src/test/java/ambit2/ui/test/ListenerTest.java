/*
 * Created on 2006-2-18
 *
 */
package ambit2.ui.test;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.setting.IOSetting;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.core.io.InteractiveIteratingMDLReader;
import ambit2.core.io.IteratingDelimitedFileReader;
import ambit2.core.io.IteratingXLSReader;
import ambit2.ui.AmbitSettingsListener;

/**
 * Tests AmbitSettingsListener
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-2-18
 */
public class ListenerTest  {

    @Before public void setUp() throws Exception {
        
    }

    
    @After public void tearDown() throws Exception {
        
    }

    @Test public void testListenerXLS() throws Exception{
		String[] labels = {"Code","Compound","SMILES","MolWeigth",
				"CasRN","log_P","eLumo","eHomo","IL","Dev.","Pred","abs dev","Obs"};
		IIteratingChemObjectReader reader = new IteratingXLSReader(
				getClass().getClassLoader().getResourceAsStream("Debnath_smiles.xls")
				,0);
	    readFile(reader,labels);
}
    
    @Test public void testListenerCSV() throws Exception{
    		String[] labels = {"Code","Compound","SMILES","MolWeigth",
    				"CasRN","log_P","eLumo","eHomo","IL","Dev.","Pred","abs dev","Obs"};
			IIteratingChemObjectReader reader = new IteratingDelimitedFileReader(
					getClass().getClassLoader().getResourceAsStream("Debnath_smiles.csv"));
		    readFile(reader,labels);
    }
    
    public void readFile(IIteratingChemObjectReader reader, String[] labels) throws Exception {
    	//IF MEDIUM or LOW, the dialog box for field selections will appear
		AmbitSettingsListener listener = new AmbitSettingsListener(IOSetting.HIGH); 	
		reader.addChemObjectIOListener(listener);
		int record = 0;
		while (reader.hasNext()) {
			IChemObject o = (IChemObject) reader.next();
			
			for (Property s : listener.getProperties().values()) 
				if (s.isEnabled())
					if (!s.getName().equals(s.getLabel())) {
						Object value = o.getProperty(s.getName());
						o.removeProperty(s.getName());
						o.setProperty(s.getLabel(),value);
					} else {} 
				else o.removeProperty(s.getName());
			//System.out.println(o.getProperties());
			//System.out.println(++record);
		}
        Assert.assertNotNull(listener.getProperties());
        Profile props = listener.getProperties();
        Assert.assertEquals(labels.length,props.size());
        /*
        for (String s: labels) {
        	Assert.assertNotNull(props.get(s));
        }	
        */
}    
    
    @Test public void testListenerSDF() throws Exception {
			IIteratingChemObjectReader reader = new InteractiveIteratingMDLReader(
					getClass().getClassLoader().getResourceAsStream("test_properties.sdf"), DefaultChemObjectBuilder.getInstance(),true);
			String[] labels = {"ID","SMILES","WEIGHT","FORMULA","NAME","Drug" };
			readFile(reader,labels);
			reader.close();

    }    

    
}
