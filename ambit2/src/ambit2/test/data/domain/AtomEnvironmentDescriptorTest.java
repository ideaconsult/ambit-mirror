package ambit2.test.data.domain;

import junit.framework.TestCase;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.SmilesParser;

import ambit2.smiles.SmilesParserWrapper;
import ambit2.data.descriptors.AtomEnvironment;
import ambit2.data.descriptors.AtomEnvironmentDescriptor;

public class AtomEnvironmentDescriptorTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AtomEnvironmentDescriptorTest.class);
	}
	public void test() {
		SmilesParserWrapper p = SmilesParserWrapper.getInstance();
		try {
			IMolecule mol = p.parseSmiles("Nc1ccccc1(C(=O)O)");
			AtomEnvironmentDescriptor ae = new AtomEnvironmentDescriptor();
			Object params[] = new Object[1];
			for (int i=0; i < mol.getAtomCount();i++) {
				params[0] = new Integer(i);
				ae.setParameters(params);
				int[] result = new int[ae.getAtomFingerprintSize()];
				ae.doCalculation(mol, result);
				//DescriptorValue value = ae.calculate(mol);
				
				System.out.print(ae.atomTypeToString(result[1]));
				System.out.print(' ');
				System.out.println(AtomEnvironment.atomFingerprintToString(result,','));
			}
			for (int i=0; i < 33;i++)
				System.out.println(ae.getAtomType(i).getAtomTypeName());
			
		} catch (Exception x) {
			x.printStackTrace();
			fail();
			
		}
	}
}
