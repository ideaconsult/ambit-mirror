package ambit2.descriptors.processors.test;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

public class MoleculeFactory {

	public static IAtomContainer makeAlkane(int num) {
		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		StringBuilder smiles = new StringBuilder();
		for (int i = 0; i < num; i++)
			smiles.append("C");
		try {
			return p.parseSmiles(smiles.toString());
		} catch (Exception x) {
			return null;
		}
	}

	public static IAtomContainer makeFromSmiles(String smiles) {
		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());

		try {
			return p.parseSmiles(smiles);
		} catch (Exception x) {
			return null;
		}
	}

	public static IAtomContainer makeBenzene() {
		return makeFromSmiles("c1ccccc1");

	}

	public static IAtomContainer make123Triazole() {
		return makeFromSmiles("C1=CN=NN1");

	}

}
