package ambit2.core.processors.structure;

import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;

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
	@Override
	public IStructureRecord[] process(IStructureRecord[] target)
			throws AmbitException {
		try {
			for (int i=0; i < target.length;i++) {
				molecules[i] = reader.process(target[i]);
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
	
	public abstract IStructureRecord[] process(IStructureRecord[] target, IAtomContainer[] molecules) ;

}
