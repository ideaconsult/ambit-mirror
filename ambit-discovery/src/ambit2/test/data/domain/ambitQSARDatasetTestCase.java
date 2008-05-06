/**
 * Created on 2005-3-29
 *
 */
package ambit2.test.data.domain;

import java.util.Vector;

import junit.framework.TestCase;
import ambit2.data.AmbitList;
import ambit2.data.AmbitObject;
import ambit2.data.descriptors.Descriptor;
import ambit2.data.descriptors.DescriptorFactory;
import ambit2.data.literature.ReferenceFactory;
import ambit2.data.model.Model;
import ambit2.data.model.ModelFactory;
import ambit2.data.molecule.AmbitPoint;
import ambit2.data.molecule.Compound;
import ambit2.domain.ADomainMethodType;
import ambit2.domain.AllData;
import ambit2.domain.DataCoverage;
import ambit2.domain.DataCoverageDensity;
import ambit2.domain.DataCoverageDescriptors;
import ambit2.domain.DataCoverageFingerprints;
import ambit2.domain.DataCoverageStats;
import ambit2.domain.DatasetCenterPoint;
import ambit2.domain.IDataCoverageStats;
import ambit2.domain.QSARDataset;
import ambit2.domain.QSARDatasetFactory;
import ambit2.exceptions.AmbitException;


/**
 * JUnit test for {@link ambit2.domain.QSARDataset} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class ambitQSARDatasetTestCase extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(ambitQSARDatasetTestCase.class);
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
	public void testADdatasetCreate() {

		try {
			Model model = ModelFactory.createDebnathMutagenicityQSAR();		    
			Model m1 = (Model) model.clone();
			QSARDataset ds = new QSARDataset(m1);
			assertEquals(model,ds.getModel());
		} catch (CloneNotSupportedException x) {
			assertTrue(false);
	    } catch (AmbitException x) {
	        x.printStackTrace();
	        fail();
	    }
	}

	public void testLoadDebnath() {
	    try {
		QSARDataset ds = QSARDatasetFactory.createDebnathDataset("data/misc/Debnath_smiles.csv");
		        //"d:\\src\\ambit\\src\\ambit\\data\\domain\\demo\\Debnath.csv");
		assertEquals(88,ds.getNpoints());
		assertEquals(4,ds.getNdescriptors());
		} catch (Exception e) {
		    e.printStackTrace();
            fail();
        }
	}	
	public void testLoadGrammatica() {
	    try {
		QSARDataset ds = QSARDatasetFactory.createBCFGrammaticadataset("src\\ambit\\domain\\demo\\BCFPaolaGrammatica.csv");
		assertEquals(238,ds.getNpoints());
		assertEquals(6,ds.getNdescriptors());
		} catch (Exception e) {
		    e.printStackTrace();
            fail();
        }
	}
	public void testAssess() {
	    try {
		QSARDataset ds = QSARDatasetFactory.createDebnathDataset("src\\ambit\\domain\\demo\\Debnath.csv");
		assertEquals(88,ds.getNpoints());
		assertEquals(4,ds.getNdescriptors());
		
		DataCoverageDescriptors coverage = new DataCoverageDescriptors(0);
		ds.estimateCoverage(coverage);
		
		IDataCoverageStats s = ds.getCoverage();
		assertEquals(88,s.getNoIn());
		assertEquals(0.83,s.getRmseIn(),0.1);

		coverage.setMode(ADomainMethodType._modeEUCLIDEAN);
		coverage.setCmode(DatasetCenterPoint._cmodeMean);
		ds.estimateCoverage(coverage);
		
		s = ds.getCoverage();
		assertEquals(88,s.getNoIn());
		assertEquals(0.83,s.getRmseIn(),0.1);
		} catch (Exception e) {
		    e.printStackTrace();
            fail();
        }
	}
	public void testAssessDensity() {
	    try {
		QSARDataset ds = QSARDatasetFactory.createDebnathDataset("src\\ambit\\domain\\demo\\Debnath.csv");
		assertEquals(88,ds.getNpoints());
		assertEquals(4,ds.getNdescriptors());
		
		DataCoverageDescriptors coverage = new DataCoverageDensity();

		assertEquals(coverage.getAppDomainMethodType().getId(),ADomainMethodType._modeDENSITY);
		double p = 0.75;

		coverage.setPThreshold(p);
		assertEquals(coverage.getPThreshold(),p,0.001);
		ds.estimateCoverage(coverage);
		assertEquals(coverage.getPThreshold(),p,0.001);		
		IDataCoverageStats s = ds.getCoverage();
		assertEquals(88*p,s.getNoIn(),0.1);
		assertEquals(0.86,s.getRmseIn(),0.1);
		} catch (Exception e) {
		    e.printStackTrace();
            fail();
        }
	}
/*
	public void testADdatasetCreateBCFWIN() {
		try {
			QSARDataset ds = QSARDatasetFactory.createBCFWinDataset(
			"D:\\nina\\LRI_Ambit\\Ecosar\\nonionic.out");
			assertTrue(true);
		} catch (Exception x) {
			x.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void xtestADdatasetCreateKOWWIN() {
		try {
			QSARDataset ds = QSARDatasetFactory.createKOWWinDataset(
			"D:\\nina\\LRI_Ambit\\DATA\\kowwin_training_fragments.OUT");
			assertTrue(true);
		} catch (Exception x) {
			x.printStackTrace();
			assertTrue(false);
		}
	}
	*/
	public void testDefaultNewQSARDataset() {
		Model m = new Model();
		m.addDescriptor(new Descriptor("X",ReferenceFactory.createEmptyReference()));	
		QSARDataset ds = new QSARDataset(m);
		assertEquals(1,ds.getNdescriptors());
		
		QSARDataset ds1 = new QSARDataset(ds.getModel());
		assertEquals(1,ds1.getNdescriptors());		
	}
	public void testTestDatasetAsModel() {
		QSARDataset ds = new QSARDataset();
		String filename = "data/misc/Debnath_smiles.csv";
		try {
		QSARDatasetFactory.loadQSARdataset(ds,filename, 
				DescriptorFactory.createDebnathFileDescriptors());
		} catch (Exception e) {
		    e.printStackTrace();
            fail();
        }
		assertEquals(88,ds.getNpoints());
		assertEquals(4,ds.getNdescriptors());
		
		QSARDataset ds1 = new QSARDataset(ds.getModel());
		assertEquals(4,ds1.getNdescriptors());
		assertEquals(0,ds1.getNpoints());
		double [] x = new double[4];
		x[0] = 0; x [1] = 0; x[2] = 0; x[3] = 5;
		ds1.getData().addRow(new Compound(),
				ds1.getModel().getDescriptors(),x);
		ds1.getData().setObservedValue(0,1);
		ds1.getData().setPredictedValue(0,11);
		//ds1.getData().setResidualValue(0,10);		
		
		DataCoverageDescriptors coverage = new DataCoverageDescriptors(0);
		ds.estimateCoverage(coverage);
		
		IDataCoverageStats s = ds.getCoverage();
		assertEquals(88,s.getNoIn());
		assertEquals(0.83,s.getRmseIn(),0.1);
		
		ds1.assessCoverage(coverage);
		s = ds1.getCoverage();
		assertEquals(0,s.getNoIn());
		assertEquals(1,s.getNoOut());		
		
		assertEquals(10.0,s.getRmseOut(),0.01);
		
		System.out.println(s.getRmseIn());		
		System.out.println(s.getRmseOut());

		
	}	
	public void testDefaultReadDataAddEmptyItem() {
		QSARDataset ds = new QSARDataset();
//		Model m = ds.getModel();
//		m.addDescriptor(new Descriptor("X",ReferenceFactory.createEmptyReference()));
		
//		assertEquals(1,m.getN_Descriptors());
		
		AllData data = ds.getData();
//		data.initialize(m.getDescriptors());
		
//		assertEquals(1,data.getNDescriptors());		
		
		try {
            AmbitPoint o = data.createNewItem();
			((AmbitPoint) o).setYPredicted("0.44");
			assertTrue(o instanceof AmbitPoint);
			data.addItem(o);
			assertEquals(data.size(),1);
			assertEquals("0.44",data.getYPredictedString(0));
			
			AmbitPoint newp = (AmbitPoint) data.getItem(0);
			newp.setYObserved("0.99");
			newp.setXValue(0,"0.88");
			
			//assertEquals("0.99",data.getYObservedToString(0));
			if (data.getNDescriptors() > 0)
			assertEquals("0.88",Double.toString(data.getX(0,0)));
			
			
		} catch (Exception x){
			x.printStackTrace();
			fail();
		}
	}
	public void testDefaultReadDataGetItem() {
	    try {
			QSARDataset ds = QSARDatasetFactory.createDebnathDataset("src\\ambit\\domain\\demo\\Debnath.csv");
			assertEquals(88,ds.getNpoints());
			assertEquals(4,ds.getNdescriptors());
			
			AllData data = ds.getData();
			AmbitPoint p = (AmbitPoint) data.getItem(0);
			String yp = p.getYPredictedString();
			String yo = p.getYObservedString();
			
			
			assertFalse(p.getName().equals(""));
			
			assertFalse(p.getYObservedString().equals(""));
			AmbitList list = data;
			try {
				AmbitObject newp = (AmbitObject) p.clone();
				int s = list.size();
				list.addItem(newp);
	 
				assertEquals(s+1,list.size());
				
				AmbitPoint o = (AmbitPoint) list.getItem(list.size()-1);
	
				assertEquals(yo,o.getYObservedString());			
				assertEquals(yp,o.getYPredictedString());
				
			} catch (CloneNotSupportedException x) {
				fail();
			} catch (Exception x) {
				x.printStackTrace();
				fail();
			}	
		} catch (Exception e) {
		    e.printStackTrace();
            fail();
        }
		
	}
	public void testAddingNullValue() {
		Vector v = new Vector();
		double[] val = null;
		v.add(val);
		
	}
	public void testEstimateCoverageFigerprints() {
	    try {
			QSARDataset ds = QSARDatasetFactory.createGlendeDataset();
			assertEquals(4,ds.getNpoints());
			ds.getData().addRow(new Compound());
			assertEquals(5,ds.getNpoints());
			double[] p = new double[6];
			p[0] = 0.5; p[1] = 0.75; p[2] = 0.8; p[3] = 0.9;p[4] = 0.99;p[5] = 1;		
			estimateCoverageFigerprints(ds,16,p,0);
	    } catch (AmbitException x) {
	        x.printStackTrace();
	        fail();
	    }
	}
	public void testEstimateCoverageTanimoto() {
	    try {
			QSARDataset ds = QSARDatasetFactory.createGlendeDataset();
			assertEquals(4,ds.getNpoints());
			ds.getData().addRow(new Compound());
			assertEquals(5,ds.getNpoints());
			double[] p = new double[6];
			p[0] = 0.5; p[1] = 0.75; p[2] = 0.8; p[3] = 0.9;p[4] = 0.99;p[5] = 1;		
			estimateCoverageFigerprints(ds,16,p,1);
	    } catch (AmbitException x) {
	        x.printStackTrace();
	        fail();
	    }		
	}	
	/*
	public void xtestEstimateCoverageFigerprintsKOWWIN() {
		QSARDataset ds = QSARDatasetFactory.createKOWWinDataset(
				"D:\\nina\\LRI_Ambit\\DATA\\kowwin_training_fragments.OUT");
		int n = ds.getNpoints();		
		double[] p = new double[1];
		//p[0] = 1-3.0/n;p[1] = 1-2.0/n;p[2] = 1-1.0/n;p[3] = 1;
		p[0] = 0.75;
		estimateCoverageFigerprints(ds,1024,p,1);
	}
	
	public void xtestEstimateCoverageFigerprintsBCFWIN() {
		QSARDataset ds = QSARDatasetFactory.createBCFWinDataset(
		"D:\\nina\\LRI_Ambit\\DATA\\BCFWIN_training_nonionic.OUT");
		double[] p = new double[4];
		p[0] = 1-3.0/607.0;p[1] = 1-2.0/607.0;p[2] = 1-1.1/607.0;p[3] = 1;
		assertEquals(608,ds.getNpoints());
		estimateCoverageFigerprints(ds,1024,p,0);
	}
	*/
/*
	public void xtestEstimateCoverageFigerprintsBCFWINionic() {
		QSARDataset ds = QSARDatasetFactory.createBCFWinDataset(
		"D:\\nina\\LRI_Ambit\\DATA\\BCFWIN_training_ionic.OUT");
		int n = ds.getNpoints();
		double[] p = new double[4];
		p[0] = 1-3.0/n;p[1] = 1-2.0/n;p[2] = 1-1.1/n;p[3] = 1;
		estimateCoverageFigerprints(ds,1024,p,0);
	}
	*/	
	protected void estimateCoverageFigerprints(QSARDataset ds, 
					int fpLength, double[] pThreshold, int fpMode) {
		try {
			System.out.println();
			System.out.println(ds.getName());
			DataCoverageStats s = new DataCoverageStats();
			DataCoverage method = new DataCoverageFingerprints(fpMode);
			((DataCoverageFingerprints) method).setFingerPrintLength(fpLength);
			
			double[][] hist = null;
			for (int i = 0; i < pThreshold.length; i++) {
				method.setPThreshold(pThreshold[i]);
				s.setMethod(method);
				if (s.estimate(ds)) {
					int bins = 10;
					int n = ((DataCoverageFingerprints) method).getFingerprintsCalculated();					
					if (n < bins) bins = n;
					
					hist = ((DataCoverageFingerprints) method).profileToHistogram(bins); 
					System.out.println("Histogram (compound frequency:number of fingerprints set)\n"+ 
							((DataCoverageFingerprints) method).histToString( hist));					

					System.out.println(((DataCoverageFingerprints) method).profileToString());
					System.out.println("Threshold\t"+100*pThreshold[i]+"%\tIn\t"+s.getNoIn()+"\tOut\t"+s.getNoOut()+"\tNA\t"+s.getNoNotAssessed());
					if (pThreshold[i] == 1) assertEquals(n,s.getNoIn(),0.5);
					
				} else fail();
			}	
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
	}
	
}
