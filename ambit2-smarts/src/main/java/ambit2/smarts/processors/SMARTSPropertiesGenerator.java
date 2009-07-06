package ambit2.smarts.processors;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.core.processors.structure.AbstractPropertyGenerator;
import ambit2.smarts.CMLUtilities;

public class SMARTSPropertiesGenerator extends AbstractPropertyGenerator<String> {
	protected CMLUtilities utils = new CMLUtilities();
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = -3259442103743490512L;

	@Override
	protected String generateProperty(IAtomContainer atomContainer)
			throws AmbitException {
		StringBuilder b = new StringBuilder();
		utils.setCMLSMARTSProperties((IMolecule)atomContainer);
		for (int i=0; i < atomContainer.getAtomCount();i++) {
			b.append(atomContainer.getAtom(i).getProperty(CMLUtilities.SMARTSProp));
			b.append(',');
		}
		return b.toString();
	}

	@Override
	protected Property getProperty() {
		return Property.getInstance(CMLUtilities.SMARTSProp, CMLUtilities.SMARTSProp);
	}

	@Override
	protected Property getTimeProperty() {
		return null;
	}

}
