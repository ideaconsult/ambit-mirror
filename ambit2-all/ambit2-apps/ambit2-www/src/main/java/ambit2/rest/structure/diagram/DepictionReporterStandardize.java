package ambit2.rest.structure.diagram;

import java.awt.image.BufferedImage;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

import ambit2.core.data.MoleculeTools;
import ambit2.rendering.CompoundImageTools;
import ambit2.rest.query.StructureQueryResource.QueryType;
import ambit2.tautomers.processor.StructureStandardizer;

public class DepictionReporterStandardize extends DepictionReporter<DepictQueryStandardize> {
	protected CompoundImageTools depict = new CompoundImageTools();
	/**
	 * 
	 */
	private static final long serialVersionUID = -6506809418999661854L;

	
	@Override
	public void processItem(DepictQueryStandardize q, BufferedImage output) {
		try {
			IAtomContainer mol = null;
			if (q.getqType() != null && QueryType.mol.equals(q.getqType())) {
				mol = MoleculeTools.readMolfile(q.getSmiles()[0]);
			} else {
				String smiles = (q.getSmiles() != null) && (q.getSmiles().length > 0) ? q.getSmiles()[0] : null;
				if (smiles != null) {
					mol = new SmilesParser(SilentChemObjectBuilder.getInstance()).parseSmiles(smiles);
				}
			}
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
			
			IAtomContainer stdmol =  q.getStandardizer().process(mol);

			setOutput(depict.getImage(stdmol, null, true, false, false));
		} catch (Exception x) {
			try {
				setOutput(createDefaultImage(q.getW(), q.getH()));
			} catch (Exception xx) {
			}
		}

	}

}
