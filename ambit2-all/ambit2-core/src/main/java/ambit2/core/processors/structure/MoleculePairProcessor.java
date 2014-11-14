package ambit2.core.processors.structure;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;

/**
 * Reads pair of structures
 * @author nina
 *
 */
public abstract class MoleculePairProcessor extends DefaultAmbitProcessor<IStructureRecord[],IStructureRecord[]> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6601719866474797743L;
	protected MoleculeReader reader = new MoleculeReader();
	protected IAtomContainer[] molecules = new IAtomContainer[] { null, null };
	protected Property smartsProperty = Property.getInstance("SMARTSProp","SMARTSProp");
	//SMARTSPropertiesReader
	@Override
	public IStructureRecord[] process(IStructureRecord[] target)
			throws AmbitException {
		try {
			for (int i=0; i < target.length;i++) {
				molecules[i] = reader.process(target[i]);
				target[i].removeProperty(smartsProperty);
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecules[i]);
				CDKHueckelAromaticityDetector.detectAromaticity(molecules[i]);

			}
			process(target,molecules);
			return target;
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
		
	}
	/*
	protected void readBondProperties(IStructureRecord object) {
		if ("true".equals(Preferences
				.getProperty(Preferences.FASTSMARTS))) {
			Object smartsdata = object.getProperty(smartsProperty);

			if (smartsdata != null) {
				mol.setProperty(smartsProperty, smartsdata);
				if (bondPropertiesReader == null) bondPropertiesReader = new SMARTSPropertiesReader();
				mol = bondPropertiesReader.process(mol);
			} else {
				mol.removeProperty(smartsProperty);
				if (configurator==null) configurator = new AtomConfigurator();
				mol = configurator.process(mol);
				
				CDKHueckelAromaticityDetector.detectAromaticity(mol);

			}
		} else {
			mol.removeProperty(smartsProperty);
			if (configurator==null) configurator = new AtomConfigurator();
			mol = configurator.process(mol);
			CDKHueckelAromaticityDetector.detectAromaticity(mol);

		}
	}
	*/
	public abstract IStructureRecord[] process(IStructureRecord[] target, IAtomContainer[] molecules) ;

}
