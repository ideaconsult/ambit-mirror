package ambit2.descriptors.test;

import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.io.RawIteratingSDFReader;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.smiles.SmilesParserWrapper;
import ambit2.descriptors.AtomEnvironment;
import ambit2.descriptors.processors.AtomEnvironmentGenerator;
import ambit2.descriptors.processors.AtomEnvironmentList;

public class AtomEnvironmentGeneratorTest {

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
			gen.setMaxLevels(3);
			InputStream in = AtomEnvironmentGeneratorTest.class.getClassLoader().getResourceAsStream("ambit2/descriptors/3d/test.sdf");
			RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(in));
			while (reader.hasNext()) {
				IStructureRecord record = reader.nextRecord();
				record = gen.process(record);
				AtomEnvironmentList ae = (AtomEnvironmentList) record.getProperty(gen.getProperty());
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
}
