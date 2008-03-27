/**
 * Created on 26-Mar-2005
 *
 */
package ambit2.test.data;

import junit.framework.TestCase;
import ambit2.data.AmbitList;
import ambit2.data.AmbitObject;
import ambit2.data.AmbitObjectChanged;
import ambit2.data.IAmbitObjectListener;
import ambit2.data.descriptors.Descriptor;
import ambit2.data.descriptors.DescriptorFactory;
import ambit2.data.descriptors.DescriptorGroups;
import ambit2.data.descriptors.DescriptorsList;
import ambit2.data.experiment.ExperimentFactory;
import ambit2.data.literature.AuthorEntry;
import ambit2.data.literature.LiteratureEntries;
import ambit2.data.literature.ReferenceFactory;
import ambit2.data.model.Model;
import ambit2.data.model.ModelFactory;
import ambit2.data.molecule.AmbitPoint;
import ambit2.data.molecule.Compound;
import ambit2.data.molecule.CompoundFactory;
import ambit2.data.molecule.SourceDataset;
import ambit2.exceptions.AmbitException;

/**
 * JUnit test for (@link ambit2.data.AmbitObject#clone()} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class ambitObjectCloneTestCase extends TestCase  {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(ambitObjectCloneTestCase.class);
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
	 * Constructor for ambitObjectCloneTestCase.
	 * @param arg0
	 */
	public ambitObjectCloneTestCase(String arg0) {
		super(arg0);
	}
	protected AmbitObject ambitClone(AmbitObject o) {
		AmbitObject newO = null;
		try {
			newO = (AmbitObject) o.clone();
			assertTrue(newO != null);
		} catch (CloneNotSupportedException x) {
			x.printStackTrace();
			fail();
		}
		
		return newO;
	}

	protected boolean ambitEquals(AmbitObject o) {
		AmbitObject newO = ambitClone(o);

		assertEquals(newO,o) ;
		boolean b = true;
		IAmbitObjectListener al = new IAmbitObjectListener() {
			/* (non-Javadoc)
			 * @see ambit2.data.IAmbitObjectListener#ambitObjectChanged(ambit2.data.AmbitObjectChanged)
			 */
			public void ambitObjectChanged(AmbitObjectChanged event) {
				assertTrue(event.getObject().isModified());

			}
		};
		o.addAmbitObjectListener(al);
		if (o instanceof Descriptor) {
			((Descriptor) o).setOrderInModel(((Descriptor) o).getOrderInModel()+1);

		} else  
			if (o instanceof AmbitList) {
				((AmbitList)o).getItem(0).setName("bla");
				boolean m = o.isModified();
				assertTrue(m);
			} else {
				newO.setName("bla");
				assertTrue(newO.isModified());
			}
		o.removeAmbitObjectListener(al);
		assertFalse(newO.equals(o)) ;

		return b;
	}
	
	/*
	 * Class under test for Object clone()
	 */
	public void testClone() {
		ambitEquals(new AmbitObject());

	}
	public void testStudyClone() {
	    try {
		ambitEquals(ExperimentFactory.createLC50Fish14d());
	    } catch (AmbitException x) {
	        x.printStackTrace();
	        fail();
	    }

	}
	public void testExperimentClone() {
	    try {
		ambitEquals(ExperimentFactory.createBenzeneLC50());
	    } catch (AmbitException x) {
	        x.printStackTrace();
	        fail();
	    }
	}
	public void testExperimentListClone() {
	    try {
		ambitEquals(ExperimentFactory.createGlendeExperiments());
	    } catch (AmbitException x) {
	        x.printStackTrace();
	        fail();
	    }
	}
	//References
	public void testAuthorEntryClone() {
		ambitEquals(new AuthorEntry("Nikolova N."));
	}
	public void testAuthorEntriesClone() {
		ambitEquals(ReferenceFactory.createDebnathRefAuthors());
	}		
	public void testJournalEntryClone() {
		ambitEquals(ReferenceFactory.createJournalMutRes());
	}
	public void testLiteratureEntryClone() {
		ambitEquals(ReferenceFactory.createAmesReference());
	}
	public void testLiteratureEntriesClone() {
		LiteratureEntries l = new LiteratureEntries();
		l.addItem(ReferenceFactory.createAmesReference());
		l.addItem(ReferenceFactory.createBCFWinReference());
		ambitEquals(l);
	}		
	//descriptors
	public void testDescriptorClone() {
		ambitEquals(DescriptorFactory.createLogP(ReferenceFactory.createBCFWinReference()));
	}
	public void testDescriptorGroupClone() {
		ambitEquals(DescriptorFactory.createDescriptorPartitionGroup());
	}
	public void testDescriptorGroupsClone() {
		DescriptorGroups g = new DescriptorGroups();
		g.addItem(DescriptorFactory.createDescriptorIndicatorGroup());
		g.addItem(DescriptorFactory.createDescriptorElectronicGroup());		
		ambitEquals(g);
	}		
	public void testDescriptorListClone() {
		DescriptorsList g = new DescriptorsList();
		g.addItem(DescriptorFactory.createLogP(ReferenceFactory.createBCFWinReference()));
		ambitEquals(g);
	}	
	//Model
	public void testModelClone() {
	    try {
		ambitEquals(ModelFactory.createDebnathMutagenicityQSAR());
	    } catch (AmbitException x) {
	        x.printStackTrace();
	        fail();
	    }
	}
	public void testEmptyModelClone() {
		try {
			//this has to fail
			new Model().clone();
			assertTrue(false);
		} catch (CloneNotSupportedException x) {
			assertTrue(true);
			//x.printStackTrace();
		}
	}
	public void testAmbitPoint() {
		double[] x = new double[4];
		AmbitPoint c = new AmbitPoint(CompoundFactory.create2Aminonaphthalene());
		c.setXvalues(x);
		c.getXvalues()[1] = 0.44;
		c.setYObserved("444");
		c.setYPredicted("445");
		ambitEquals(c);
		assertTrue(CompoundFactory.create2Aminonaphthalene().equals(c.getCompound()));
		try {
			AmbitPoint c1 = (AmbitPoint)c.clone();
			assertEquals(444.0,c1.getYObservedDouble(),0.01);
			
			assertEquals(445.0,c1.getYPredictedDouble(),0.01);
			
			assertEquals(c1.getXValue(1),"0.44");			
		} catch (CloneNotSupportedException xx) {
			fail();
		}
	}
	public void testCompoundClone() {
		Compound c = CompoundFactory.create2Aminofluorene(); 
		ambitEquals(c);
		assertFalse(c.equals(CompoundFactory.create2Aminonaphthalene()));
		
	}
	public void testSourceDataSetClone() {
		SourceDataset c = new SourceDataset("ligand_info_subset_111.sdf",ReferenceFactory.createEmptyReference()); 
		ambitEquals(c);
	}

	
}
