package ambit2.tautomers.test;

import javax.swing.JOptionPane;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.ui.Panel2D;

public class TautomersVisualisationTest {

	public void show(IAtomContainer mol) throws Exception {
		Panel2D p = new Panel2D();
		p.setAtomContainer(mol);
		JOptionPane.showMessageDialog(null,p);
		
	}
	public static void main(String[] args) {
		TautomersVisualisationTest test = new TautomersVisualisationTest();
		try {
			test.show(MoleculeFactory.make123Triazole());
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
