package ambit2.smarts.processors;

import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;

import ambit2.base.data.Property;
import ambit2.core.processors.structure.AbstractPropertyGenerator;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.smarts.CMLUtilities;

public class SMARTSPropertiesGenerator extends AbstractPropertyGenerator<String> {
	protected CMLUtilities utils = new CMLUtilities();
	protected AtomConfigurator config = new AtomConfigurator();
	/**
	 * 
	 */
	private static final long serialVersionUID = -3259442103743490512L;

	@Override
	protected String generateProperty(IAtomContainer atomContainer)
			throws AmbitException {
		if ((atomContainer==null) || (atomContainer.getAtomCount()==0))
			throw new AmbitException("Empty molecule");
		try {
			
			IAtomContainer c = config.process(atomContainer);
			try {
				CDKHueckelAromaticityDetector.detectAromaticity(c);
			} catch (Exception x) { logger.log(Level.WARNING,x.getMessage(),x); }
			StringBuilder b = new StringBuilder();
			utils.setCMLSMARTSProperties((IMolecule)atomContainer);
			for (int i=0; i < atomContainer.getAtomCount();i++) {
				b.append(atomContainer.getAtom(i).getProperty(CMLUtilities.SMARTSProp));
				b.append(',');
			}
			b.append('\n');
			for (int i=0; i < atomContainer.getBondCount();i++) {
				b.append(atomContainer.getBond(i).getFlag(CDKConstants.ISAROMATIC)?1:0);
				b.append(',');
			}			
			return b.toString();
		} catch (AmbitException x) {
			return "";
		} catch (Exception x) {
			return ""; //otherwise atomproperties is null and the thing cycles forever
			//throw new AmbitException(x.getMessage());
		}
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
