package ambit2.test.processors;

import java.util.Enumeration;

import junit.framework.TestCase;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.AromaticAtomsCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.AromaticBondsCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.KappaShapeIndicesDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.RotatableBondsCountDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.RuleOfFiveDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.WienerNumbersDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.ZagrebIndexDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.data.descriptors.Descriptor;
import ambit2.data.descriptors.DescriptorFactory;
import ambit2.data.descriptors.DescriptorsHashtable;
import ambit2.exceptions.AmbitException;
import ambit2.processors.descriptors.CalculateDescriptors;

public class CalculateDescriptorsTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(CalculateDescriptorsTest.class);
	}
	public void test() {
		Molecule mol = MoleculeFactory.makeBenzene();

		try {
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
			CDKHydrogenAdder a = CDKHydrogenAdder.getInstance(DefaultChemObjectBuilder.getInstance());			
			a.addImplicitHydrogens(mol);
			CDKHueckelAromaticityDetector.detectAromaticity(mol);

		} catch (Exception x) {
			
		}
		DescriptorsHashtable hashtable = new DescriptorsHashtable();
		IMolecularDescriptor d1 = new XLogPDescriptor();
		hashtable.addDescriptorPair(d1, DescriptorFactory.createEmptyDescriptor());

		hashtable.addDescriptorPair(new KappaShapeIndicesDescriptor(), DescriptorFactory.createEmptyDescriptor());		
		hashtable.addDescriptorPair(new ZagrebIndexDescriptor(), DescriptorFactory.createEmptyDescriptor());
		hashtable.addDescriptorPair(new WienerNumbersDescriptor(), DescriptorFactory.createEmptyDescriptor());
		hashtable.addDescriptorPair(new RuleOfFiveDescriptor(), DescriptorFactory.createEmptyDescriptor());		
		hashtable.addDescriptorPair(new RotatableBondsCountDescriptor(), DescriptorFactory.createEmptyDescriptor());
		hashtable.addDescriptorPair(new AromaticAtomsCountDescriptor(), DescriptorFactory.createEmptyDescriptor());
		hashtable.addDescriptorPair(new AromaticBondsCountDescriptor(), DescriptorFactory.createEmptyDescriptor());
		//hashtable.addDescriptorPair(new HBondAcceptorCountDescriptor(), DescriptorFactory.createEmptyDescriptor());		
		//hashtable.addDescriptorPair(new HBondDonorCountDescriptor(), DescriptorFactory.createEmptyDescriptor());		
		
		
		CalculateDescriptors p = new CalculateDescriptors(hashtable);
		try {
			if (p.process(mol) != null) {
				Object prop = mol.getProperty(d1);
				assertNotNull(prop);
				assertTrue(prop instanceof DescriptorValue);
				DescriptorValue dv = (DescriptorValue)prop;
				assertEquals("org.openscience.cdk.qsar.XLogPDescriptor",
						(dv.getSpecification().getImplementationTitle()));
				//assertEquals(12.438,((DoubleResult)dv.getValue()).doubleValue() ,0.0001);
				
				Enumeration keys = hashtable.keys();
				while (keys.hasMoreElements()) {
					Descriptor d = (Descriptor) keys.nextElement();
					prop = mol.getProperty(d);
					assertNotNull(prop);
					assertTrue(prop instanceof DescriptorValue);
					dv = (DescriptorValue)prop;
					assertEquals(d.getClass().getName(),
							(dv.getSpecification().getImplementationTitle()));
					System.out.println(dv.getSpecification().getImplementationTitle());
					
					if (dv.getValue() instanceof DoubleArrayResult) {
						DoubleArrayResult r = (DoubleArrayResult)dv.getValue();
						for (int i=0; i < r.length();i++)
							System.out.println(r.get(i));
					} else if (dv.getValue() instanceof DoubleResult) {
						DoubleResult r = (DoubleResult)dv.getValue();
						System.out.println(r.doubleValue());
					} else if (dv.getValue() instanceof IntegerResult) {
						IntegerResult r = (IntegerResult)dv.getValue();
						System.out.println(r.intValue());
					}
				}
				
				//assertEquals(12.438,((DoubleResult)dv.getValue()).doubleValue() ,0.0001);
				
				
			}
		} catch (AmbitException x) {
			x.printStackTrace();
			fail();
		}
	}
}
