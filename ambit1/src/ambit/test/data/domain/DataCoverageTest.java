/*
 * Created on 14-Apr-2005
 *
 * @author Nina Jeliazkova
 */
package ambit.test.data.domain;

import java.util.BitSet;

import junit.framework.TestCase;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;

import Jama.Matrix;
import ambit.data.AmbitObjectChanged;
import ambit.data.IAmbitObjectListener;
import ambit.data.model.ModelFactory;
import ambit.data.molecule.Compound;
import ambit.data.molecule.CompoundFactory;
import ambit.domain.ADomainMethodType;
import ambit.domain.AllData;
import ambit.domain.DataCoverage;
import ambit.domain.DataCoverageAtomEnvironment;
import ambit.domain.DataCoverageDescriptors;
import ambit.domain.DataCoverageDistance;
import ambit.domain.DataCoverageFingerprints;
import ambit.domain.DataModule;
import ambit.domain.FingerprintDistanceType;
import ambit.domain.IDataCoverageStats;
import ambit.domain.QSARDataset;
import ambit.domain.QSARDatasetFactory;
import ambit.exceptions.AmbitException;
import ambit.stats.Tools;
import ambit.stats.datastructures.Sort;

/**
 * TODO add description for DataCoverageTest
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 14-Apr-2005
 */
public class DataCoverageTest extends TestCase {
	DataCoverageDescriptors dc ;
	Fingerprinter fingerprinter;
	public static void main(String[] args) {
		junit.textui.TestRunner.run(DataCoverageTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		dc = new DataCoverageDescriptors();
		fingerprinter = new Fingerprinter(1024);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		dc.clear();
		dc = null;
	}

	/*
	public void xtestAE() {
	    InputStream in = this.getClass().getClassLoader().getResourceAsStream(
		        "ambit/data/domain/demo/kowwin_training.out");
		QSARDataset ds = 
		QSARDatasetFactory.createKOWWinDataset(in);
		assertEquals(2471,ds.getNpoints());
		DataCoverage method = new DataCoverageAtomEnvironment(); 
		ds.setMethod(method);
		ds.estimateCoverage(method);
		ds.assessCoverage(method);
		IDataCoverageStats stats = ds.getCoverage();
		System.out.println(stats);
		ds = null;
	}
	*/
	public void testFP() {
		QSARDataset ds = new QSARDataset();
		String f = "ambit/domain/demo/Debnath_smiles.csv";
		try {
		QSARDatasetFactory.loadDebnathSmilesDataset(ds,
				ModelFactory.createDebnathMutagenicityQSAR(),
				this.getClass().getClassLoader().getResourceAsStream(f),f);
		} catch (Exception e) {
		    e.printStackTrace();
            fail();
        }
		assertEquals(ds.getNpoints(),88);
		SmilesParser sp = new SmilesParser();
		SmilesGenerator gen = new SmilesGenerator(DefaultChemObjectBuilder.getInstance());
		
		for (int i = 0; i < ds.getNpoints(); i++) {
			try {
				String smilesOriginal = ds.getData().getSMILES(i);
				Molecule mol1 = sp.parseSmiles(smilesOriginal);
				BitSet bs1 = fingerprinter.getFingerprint((AtomContainer) mol1);
				
				String smilesNew = gen.createSMILES(mol1);
				Molecule mol2 = sp.parseSmiles(smilesNew);
				BitSet bs2 = fingerprinter.getFingerprint((AtomContainer) mol2);
				if (!bs1.equals(bs2)) {
					//System.err.println("Compounds not same \t" + (i+1) + "\t" + smilesOriginal + "\t" + smilesNew);
					System.err.println(smilesOriginal);
				}
			} catch (NoSuchAtomException x) {
				x.printStackTrace();
			} catch (InvalidSmilesException x) {
				System.err.println("Invalid SMILES \t" + ds.getData().getSMILES(i));
			} catch (Exception x) {
			    x.printStackTrace();
			}
		}
		sp = null;
		gen = null;
	}
	public void testAssessData() {
		IAmbitObjectListener ao = new IAmbitObjectListener() {
			public void ambitObjectChanged(AmbitObjectChanged event) {
				System.err.print("Object changed\t");				
				System.err.println(event.getSource().toString());

			}
		};		
		DataModule dm = new DataModule();
		try {
		    dm.createDemoModel(ao);
		} catch (Exception e) {
		    e.printStackTrace();
            fail();
        }
		try {
		dm.setMethod(ADomainMethodType.methodName[ADomainMethodType._modeRANGE]);
		} catch (AmbitException x) {
		    x.printStackTrace();
		    fail();
		}
		dc = (DataCoverageDescriptors) dm.method;
		dc.setPca(false);
		dm.calcModelCoverage();
		
		System.out.println("Min\t" +dc.getMaxdata()[0] + "\t" + dc.getMindata()[1]);
		System.out.println("Max\t" +dc.getMaxdata()[0] + "\t" + dc.getMaxdata()[1]);
		double[][] cp = new double[2][dm.modelData.getNdescriptors()];
		for (int i =0; i < dm.modelData.getNdescriptors(); i++) {
			cp[0][i] = dc.getMindata()[i];
			cp[1][i] = dc.getMaxdata()[i];			
		}
		Matrix m = dc.inverseTransform(new Matrix(cp));
		System.out.println("*Min\t" +m.get(0,0) + "\t" + m.get(0,1));
		System.out.println("*Max\t" +m.get(1,0) + "\t" + m.get(1,1));
		//TODO DataModule clear func
		//		dm.clear();
		dm = null;
	}

	public void testAssess() {
		double[][] points = new double[4][2];
		for (int i =0; i < 4; i++) {
			points[i][0] = i;
			points[i][1] = i+10+Math.random();
		}
		
		dc.setPca(true);
		dc.estimate(points);
		System.out.println("Min\t" +dc.getMindata()[0] + "\t" + dc.getMindata()[1]);
		System.out.println("Max\t" +dc.getMaxdata()[0] + "\t" + dc.getMaxdata()[1]);
		double[][] cp = new double[2][2];
		for (int i =0; i < 2; i++) {
			cp[0][i] = dc.getMindata()[i];
			cp[1][i] = dc.getMaxdata()[i];			
		}
		Matrix m = dc.inverseTransform(new Matrix(cp));
		System.out.println("*Min\t" +m.get(0,0) + "\t" + m.get(0,1));
		System.out.println("*Max\t" +m.get(1,0) + "\t" + m.get(1,1));		
	}

	public void testAssessAtomEnvironment() {
	    AllData data = new AllData();
	    data.initialize(null);
	    Compound cmp = new Compound();
	    cmp.setSMILES("[Kr]");
	    cmp.updateMolecule();
	    data.addRow(cmp);	    
	    data.addRow(CompoundFactory.createBenzene());
	    data.addRow(CompoundFactory.create4Aminobiphenyl());
	    data.addRow(CompoundFactory.create2Aminonaphthalene());
	    assertEquals(4,data.getNPoints());
	    
	    QSARDataset ds = new QSARDataset();
		ds.setData(data);

		DataCoverage method = new DataCoverageAtomEnvironment(); 
		ds.setMethod(method);
		ds.estimateCoverage(method);
		ds.assessCoverage(method);
		IDataCoverageStats stats = ds.getCoverage();
		System.out.println(stats);
		
	}
	public void testNN() {
		QSARDataset ds = new QSARDataset();
		String f = "ambit/domain/demo/Debnath_smiles.csv";
		//QSARDataset ds = QSARDatasetFactory.createDebnathDataset("data/misc/Debnath_smiles.csv");
		try {
		QSARDatasetFactory.loadDebnathSmilesDataset(ds,
				ModelFactory.createDebnathMutagenicityQSAR(),
				this.getClass().getClassLoader().getResourceAsStream(f),f);
		} catch (Exception e) {
		    e.printStackTrace();
            fail();
        }
		assertEquals(ds.getNpoints(),88);
		DataCoverageFingerprints method = new DataCoverageFingerprints();
		method.setFpComparison(new FingerprintDistanceType("1-Tanimoto"));
		ds.setMethod(method);
		ds.estimateCoverage(method);
		ds.assessCoverage(method);
		IDataCoverageStats stats = ds.getCoverage();
		
		for (int i=0;i < ds.getNpoints();i++) {
		    try {
		    findNN(ds,i);
		    } catch (Exception x) {
		        
		    }
		}
		
	}
	public void findNN(QSARDataset ds , int row) throws Exception {
	    int[] index = new int[] {0}; //,1,2,3};
	    double x[] = new double[4];
	    ds.getXData(row,x,index);
	    System.out.print("This\t#\tde\tdh\tde/dh\tEuclidean distance\tSimilarity\tResidual"+ds.getResidual(row) + "\tx1\tx2\tx3\tx4\n");
	    
	    DataCoverageDistance c = new DataCoverageDistance();
	    
	    double values[] = new double[4];
	    double nn[] = new double[ds.getNpoints()];
	    
	    /*
	    for (int i=0;i < ds.getNpoints();i++) {
	        ds.getXData(i,values,index);
	        nn[i] = c.euclideanDistance(x,values,4);
	    } 
	    */   
	    
	    for (int i=0;i < ds.getNpoints();i++) {
	        ds.getXData(i,values,index);
	        double nearDistance = c.euclideanDistance(x,values,index.length);
	        double de = ds.getResidual(row)-ds.getResidual(i);
	        double dh = ds.getCoverage(row) - ds.getCoverage(i);
	        	        
	        System.out.print(row);
	        System.out.print('\t');
	        System.out.print(i);

	        System.out.print('\t');
	        System.out.print(de);
	        System.out.print('\t');
	        System.out.print(dh);
	        System.out.print('\t');
	        if (dh !=0)
	            System.out.print(de/dh);
	        else
	            System.out.print("0");
	        System.out.print('\t');
	        System.out.print(nearDistance);	        
	        System.out.print('\t');
	        System.out.print(ds.getCoverage(i));	        
	        System.out.print('\t');
	        System.out.print(ds.getResidual(i));
	        for (int j=0; j < index.length;j++) {
		        System.out.print('\t');
		        System.out.print(values[index[j]]);
	        }
	        System.out.print('\n');
	    }
	    System.out.print('\n');
	}

}
