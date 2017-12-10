package ambit2.rest.structure.diagram;

import java.awt.image.BufferedImage;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import ambit2.core.data.MoleculeTools;
import ambit2.rendering.CompoundImageTools;
import ambit2.rest.query.StructureQueryResource.QueryType;
import ambit2.tautomers.processor.StructureStandardizer;

public class DepictionReporterStandardize extends DepictionReporter {
	protected CompoundImageTools depict = new CompoundImageTools();
	/**
	 * 
	 */
	private static final long serialVersionUID = -6506809418999661854L;

	
	@Override
	public void processItem(DepictQuery q, BufferedImage output) {
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
			StructureStandardizer std = new StructureStandardizer();
			std.setNeutralise(true);
			std.setImplicitHydrogens(true);
			std.setClearIsotopes(true);
			std.setSplitFragments(true);
			std.setGenerateTautomers(true);
			IAtomContainer stdmol = std.process(mol);
			setOutput(depict.getImage(stdmol, null, true, false, false));
		} catch (Exception x) {
			try {
				setOutput(createDefaultImage(q.getW(), q.getH()));
			} catch (Exception xx) {
			}
		}

	}

}
