package ambit2.descriptors.test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

import org.apache.xalan.xsltc.util.IntegerArray;
import org.junit.Test;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.io.MyIteratingMDLReader;
import ambit2.core.io.RawIteratingSDFReader;
import ambit2.descriptors.AtomEnvironment;
import ambit2.descriptors.AtomEnvironmentMatrixDescriptor;
import ambit2.descriptors.processors.AtomEnvironmentGenerator;
import ambit2.descriptors.processors.AtomEnvironmentList;

public class AtomEnvironmentGeneratorTest {
	protected CDKHydrogenAdder hAdder = null;
	   /*
	    public void testAtomEnvironmentDescriptor() throws Exception {
	    	AtomEnvironmentGenerator gen = new AtomEnvironmentGenerator();
	    	gen.setMaxLevels(1);
	    	gen.setUseHydrogens(false);
	    	
		    SmilesParserWrapper sp = SmilesParserWrapper.getInstance();
 	        IAtomContainer mol = sp.parseSmiles("CCCC(O)=O");
			AtomConfigurator typer = new AtomConfigurator();
			typer.process(mol);
			    
			CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(NoNotificationChemObjectBuilder.getInstance());
			hAdder.addImplicitHydrogens(mol);

			mol = gen.process(mol);
			
			Object ae = mol.getProperty(AmbitCONSTANTS.AtomEnvironment);
			
			Assert.assertTrue(ae instanceof AtomEnvironmentList);
			for (AtomEnvironment a : (AtomEnvironmentList) ae) {
				System.out.println(a);
				int[] l0 = a.getLevel(0);
				for (int i:l0) System.out.print(String.format("%d,", i));
				System.out.println();
				int[] l1 = a.getLevel(1);
				for (int i:l1) System.out.print(String.format("%d,", i));
				System.out.println();
			}
			System.out.println(ae);
			    
	    }
	   */
	   @Test
		public void test() throws Exception {
			
			AtomEnvironmentGenerator gen = new AtomEnvironmentGenerator();
			gen.setMaxLevels(1);
			InputStream in = AtomEnvironmentGeneratorTest.class.getClassLoader().getResourceAsStream("ambit2/descriptors/3d/test.sdf");
			RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(in));
			while (reader.hasNext()) {
				IStructureRecord record = reader.nextRecord();
				record = gen.process(record);
				AtomEnvironmentList ae = (AtomEnvironmentList) record.getProperty(gen.getProperty());
				System.out.println();
				System.out.println(ae);
				
				for (AtomEnvironment a : (AtomEnvironmentList) ae) {
					System.out.println(a);
					int[] l0 = a.getLevel(0);
					for (int i:l0) System.out.print(String.format("%d,", i));
					System.out.println();
					int[] l1 = a.getLevel(1);
					for (int i:l1) System.out.print(String.format("%d,", i));
					System.out.println();
				}
				
			}
			reader.close();
			/*
			IStructureRecord record = new StructureRecord();
			record.setContent();
			MoleculeReader reader = new MoleculeReader();
			reader.process(target)
			*/
		}	   
	   
	   @Test
		public void testAtomTypeMatrix() throws Exception {
			
			AtomEnvironmentMatrixDescriptor gen = new AtomEnvironmentMatrixDescriptor();
			InputStream in = AtomEnvironmentGeneratorTest.class.getClassLoader().getResourceAsStream("ambit2/descriptors/3d/test.sdf");
			IIteratingChemObjectReader<IAtomContainer> reader = new MyIteratingMDLReader(new InputStreamReader(in),SilentChemObjectBuilder.getInstance());
			while (reader.hasNext()) {
				IAtomContainer mol = reader.next();
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);				
                //if (useHydrogens) { //always, otherwise atom types are not recognised correctly
                	//for some reason H atoms are added as bond references, but not in atom list - bug?
				try {
	    			if (hAdder == null) hAdder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
	    		    hAdder.addImplicitHydrogens(mol);
				} catch (Exception x) {
					
				}
    			CDKHueckelAromaticityDetector.detectAromaticity(mol);        
				Hashtable<String,Integer> sparseMatrix = gen.doCalculation(mol);
			    System.out.println("Sparse Matrix");
			    System.out.println(sparseMatrix);
			    System.out.println("End Sparse Matrix");
			}
			reader.close();
		}	   	
	   @Test
		public void testAtomTypeMatrixDescriptor() throws Exception {
			
			AtomEnvironmentMatrixDescriptor gen = new AtomEnvironmentMatrixDescriptor();
			InputStream in = AtomEnvironmentGeneratorTest.class.getClassLoader().getResourceAsStream("ambit2/descriptors/3d/test.sdf");
			IIteratingChemObjectReader<IAtomContainer> reader = new MyIteratingMDLReader(new InputStreamReader(in),SilentChemObjectBuilder.getInstance());
			while (reader.hasNext()) {
				IAtomContainer mol = reader.next();
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);				
               //if (useHydrogens) { //always, otherwise atom types are not recognised correctly
               	//for some reason H atoms are added as bond references, but not in atom list - bug?
				try {
	    			if (hAdder == null) hAdder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
	    		    hAdder.addImplicitHydrogens(mol);
				} catch (Exception x) {
					
				}
   			CDKHueckelAromaticityDetector.detectAromaticity(mol);        
   			DescriptorValue value = gen.calculate(mol);
			    System.out.println("Value");
			    for (int i=0; i < value.getNames().length; i++)  
			    	if (((IntegerArrayResult)value.getValue()).get(i)>0) {
			    		System.out.println(value.getNames()[i] + " = " + ((IntegerArrayResult)value.getValue()).get(i));		
			    	}
			    System.out.println("End Value");
			}
			reader.close();
		}	   
}
