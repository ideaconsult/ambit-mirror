/*
 * Created on 2006-2-18
 *
 */
package ambit.test.io;

import java.io.FileInputStream;

import junit.framework.TestCase;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.setting.IOSetting;

import ambit.data.descriptors.Descriptor;
import ambit.data.descriptors.DescriptorsList;
import ambit.data.literature.LiteratureEntry;
import ambit.data.literature.ReferenceFactory;
import ambit.data.molecule.Compound;
import ambit.domain.AllData;
import ambit.io.AmbitSettingsListener;
import ambit.io.IColumnTypeSelection;
import ambit.io.InteractiveIteratingMDLReader;
import ambit.io.IteratingDelimitedFileReader;
import ambit.io.MolPropertiesIOSetting;
import ambit.misc.AmbitCONSTANTS;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-2-18
 */
public class AllDataIOTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AllDataIOTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for AllDataIOTest.
     * @param arg0
     */
    public AllDataIOTest(String arg0) {
        super(arg0);
    }
    public void testListenerCSV() {
        try {
            DescriptorsList expected = new DescriptorsList();
            LiteratureEntry ref = ReferenceFactory.createEmptyReference();
            //these are automatically recognised types, not the true parameter meaning
            expected.addItem(new Descriptor("Code",0,IColumnTypeSelection._ctX,ref));
            expected.addItem(new Descriptor("Compound",2,IColumnTypeSelection._ctX,ref));
            expected.addItem(new Descriptor("CAS",2,IColumnTypeSelection._ctCAS,ref));
            expected.addItem(new Descriptor(AmbitCONSTANTS.SMILES,2,IColumnTypeSelection._ctSMILES,ref));
            
            expected.addItem(new Descriptor("Obs",0,IColumnTypeSelection._ctYobserved,ref));
            expected.addItem(new Descriptor("Pred",0,IColumnTypeSelection._ctX,ref));
            expected.addItem(new Descriptor("Dev.",0,IColumnTypeSelection._ctX,ref));
            expected.addItem(new Descriptor("log_P",0,IColumnTypeSelection._ctX,ref));
            expected.addItem(new Descriptor("eLumo",0,IColumnTypeSelection._ctX,ref));
            expected.addItem(new Descriptor("eHomo",0,IColumnTypeSelection._ctX,ref));
            expected.addItem(new Descriptor("IL",0,IColumnTypeSelection._ctX,ref));
            expected.addItem(new Descriptor("abs dev",0,IColumnTypeSelection._ctX,ref));
            
            
			IIteratingChemObjectReader reader = new IteratingDelimitedFileReader(
					new FileInputStream("data/misc/Debnath_smiles.csv"));
			runTestListener(reader,expected,88,9);
			reader.close();
        } catch (Exception x) {
            x.printStackTrace();
            fail();
        }   
    }
    public void testListenerSDF() {
        try {
            DescriptorsList expected = new DescriptorsList();
            LiteratureEntry ref = ReferenceFactory.createEmptyReference();
            expected.addItem(new Descriptor(AmbitCONSTANTS.SMILES,2,IColumnTypeSelection._ctSMILES,ref));
            expected.addItem(new Descriptor("ID",0,IColumnTypeSelection._ctRowID,ref));
            expected.addItem(new Descriptor("WEIGHT",0,IColumnTypeSelection._ctX,ref));
            expected.addItem(new Descriptor("FORMULA",2,IColumnTypeSelection._ctUnknown,ref));
            expected.addItem(new Descriptor("Drug",2,IColumnTypeSelection._ctUnknown,ref));
            expected.addItem(new Descriptor("NAME",2,IColumnTypeSelection._ctChemName,ref));

            
			IIteratingChemObjectReader reader = new InteractiveIteratingMDLReader(
			        new FileInputStream("data/misc/test_properties.sdf"), DefaultChemObjectBuilder.getInstance());
	        runTestListener(reader,expected,7,1);
	        reader.close();
        } catch (Exception x) {
            x.printStackTrace();
            fail();
        }   
    }    
    public void runTestListener(IIteratingChemObjectReader reader, 
            	DescriptorsList expected, int nPoints, int nDescriptors) {
        	
        	AllData data = new AllData();
			reader.addChemObjectIOListener(new AmbitSettingsListener(null,IOSetting.LOW) {
			    /* (non-Javadoc)
                 * @see ambit.io.AmbitSettingsListener#processIOSettingQuestion(org.openscience.cdk.io.setting.IOSetting)
                 */
                public void processIOSettingQuestion(IOSetting setting) {
                   // super.processIOSettingQuestion(setting);
                }
			}
			);
			int c = 0;
			Object o; 
			IMolecule mol ;
			DescriptorsList descriptors = null;
			while (reader.hasNext()) {
			    o = reader.next();
			    if (o instanceof IMolecule) {
			        mol = (IMolecule) o;
			        if (descriptors == null) {
			            descriptors = ((MolPropertiesIOSetting) reader.getIOSettings()[0]).getSelectedProperties();
			            data.initialize(descriptors);
			        }
			        
			        data.addRow(new Compound(mol),descriptors,mol.getProperties(),false);
			        c++;
			    }
			}
			assertTrue(c>0);
			
			assertEquals(nPoints,data.getNPoints());
			assertEquals(nDescriptors,data.getNDescriptors());
			
			
			DescriptorsList dlist = ((MolPropertiesIOSetting) reader.getIOSettings()[0]).getSelectedProperties();			
			if  (expected != null) {
			    

				assertEquals(expected.size(),dlist.size());
				for (int i=0; i < dlist.size();i++) {
				    assertEquals(dlist.getDescriptor(i).getName(),expected.getDescriptor(i).getName());
				    assertEquals(dlist.getDescriptor(i).getTypeInModel(),expected.getDescriptor(i).getTypeInModel());
				}
			} else System.out.println(dlist);
			
			//AmbitObjectDialog aod = new AmbitObjectDialog(data);
			//aod.show(true);
        
    }
    
}
