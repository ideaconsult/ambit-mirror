package ambit2.rest.structure.diagram;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.cdk.renderer.selection.SingleSelection;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import ambit2.core.data.MoleculeTools;
import ambit2.rendering.CompoundImageTools;
import ambit2.rendering.IAtomContainerHighlights;
import ambit2.rest.query.StructureQueryResource.QueryType;
import ambit2.smarts.query.ISmartsPattern;
import ambit2.smarts.query.SmartsPatternAmbit;

public class DepictionReporterCDK extends DepictionReporter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3724235449725863074L;
	protected CompoundImageTools depict = new CompoundImageTools();

	@Override
	public void processItem(DepictQuery q, BufferedImage output) {
		try {
			String smarts = q.getSmarts();
			String smiles = (q.getSmiles() != null)
					&& (q.getSmiles().length > 0) ? q.getSmiles()[0] : null;
			depict.setImageSize(new Dimension(q.getW(), q.getH()));
			if (q.getqType() != null && QueryType.mol.equals(q.getqType())) {
				IAtomContainer mol = MoleculeTools
						.readMolfile(q.getSmiles()[0]);
				setOutput(depict
						.getImage(
								mol,
								(q.getSmarts() == null)
										|| ("".equals(smarts.trim())) ? null
										: new SmartsPatternSelector(smarts),
								true, false, false));

			} else {
				if (depict.getParser() == null)
					depict.setParser(new SmilesParser(SilentChemObjectBuilder
							.getInstance()));

				if (q.displayMode == null) {
					setOutput(depict.generateImage(smiles, (smarts == null)
							|| ("".equals(smarts.trim())) ? null
							: new SmartsPatternSelector(smarts), false, false,
							null));
				} else
					setOutput(depict.generateImage(smiles, (smarts == null)
							|| ("".equals(smarts.trim())) ? null
							: new SmartsPatternSelector(smarts), false, false,
							q.getDisplayMode()));
			}
		} catch (Exception x) {
			try {
				setOutput(createDefaultImage(q.getW(), q.getH()));
			} catch (Exception xx) {
			}
		}

	}
}


class SmartsPatternSelector implements IAtomContainerHighlights {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2781667048103591227L;
	protected String smarts;

	public SmartsPatternSelector(String smarts) {
		this.smarts = smarts;
	}

	public long getID() {
		return 0;
	}

	public boolean isEnabled() {
		return true;
	}

	public IChemObjectSelection process(IAtomContainer target)
			throws AmbitException {
		ISmartsPattern pattern = new SmartsPatternAmbit(smarts);
		if (pattern.match(target) > 0) {
			IAtomContainer selected = pattern.getMatchingStructure(target);
			return new SingleSelection<IAtomContainer>(selected);
		} else
			return null;
	}

	public void setEnabled(boolean value) {

	}

	@Override
	public void open() throws Exception {
	}

	@Override
	public void close() throws Exception {
	}
}