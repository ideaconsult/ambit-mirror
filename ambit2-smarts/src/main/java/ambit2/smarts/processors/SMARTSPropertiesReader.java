package ambit2.smarts.processors;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import ambit2.base.data.Property;
import ambit2.smarts.CMLUtilities;

/**
 * Expects properties to accelerate smarts search , expected in CMLUtilities.SMARTSProp
 * @author nina
 *
 */
public class SMARTSPropertiesReader extends DefaultAmbitProcessor<IAtomContainer, IAtomContainer> {
	protected CMLUtilities util = new CMLUtilities();
	/**
	 * 
	 */
	private static final long serialVersionUID = -2900200524126295738L;

	public IAtomContainer process(IAtomContainer mol)
			throws AmbitException {
		Property p = getProperty();
		if (mol.getProperty(p) == null) return null;
		String[] prop = mol.getProperty(p).toString().split("\n");
		String[] atomprops =prop[0].split(",");
		for (int i=0; i < atomprops.length;i++)
			mol.getAtom(i).setProperty(CMLUtilities.SMARTSProp,atomprops[i]);
		if (prop.length == 1) logger.warning("Bond properties missing!");
		else {
			String[] bondprop =prop[1].split(",");
			
			for (int i=0; i < mol.getAtomCount();i++)
				mol.getAtom(i).setFlag(CDKConstants.ISAROMATIC,false);
			
			for (int i=0; i < bondprop.length;i++) {
				boolean aromatic = bondprop[i].equals("1")?true:false;
				IBond bond = mol.getBond(i);
				bond.setFlag(CDKConstants.ISAROMATIC, aromatic);
				for (int b=0; b < bond.getAtomCount(); b++)
					if (aromatic) bond.getAtom(b).setFlag(CDKConstants.ISAROMATIC, true);
			}
		}
		if (util == null) util = new CMLUtilities();
		util.extractSMARTSProperties((IAtomContainer)mol);

		return mol;
	}
	protected Property getProperty() {
		return Property.getInstance(CMLUtilities.SMARTSProp, CMLUtilities.SMARTSProp);
	}

}
