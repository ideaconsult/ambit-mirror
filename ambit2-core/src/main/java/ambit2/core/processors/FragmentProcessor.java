package ambit2.core.processors;

import java.util.logging.Logger;

import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;

/**
 * Keeps the largest fragment
 * 
 * @author nina
 * 
 */
public class FragmentProcessor extends AbstractStructureProcessor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4826290923350893404L;

	public FragmentProcessor(Logger logger) {
		super(logger);
	}
	
	@Override
	public IAtomContainer process(IAtomContainer container) throws Exception {
		if (container == null)
			return container;

		int atomsOriginal = container.getAtomCount();

		if (atomtypeasproperties && container.getProperty(at_property) != null) {

			if (!isSparseproperties()) {
				if (atomtypeasproperties) {
					if (container.getProperty(at_property_removed) == null)
						container.setProperty(at_property_removed, null);
					if (container.getProperty(at_property_added) == null)
						container.setProperty(at_property_added, null);
				}
			}
			if (container.getProperty(at_property) == null)
				container = atomtypes2property(at_property, container, null,
						isSparseproperties());
		}
		IAtomContainerSet fragments = ConnectivityChecker
				.partitionIntoMolecules(container);

		IAtomContainer largest = null;
		if (fragments.getAtomContainerCount() > 1)
			for (IAtomContainer mol : fragments.atomContainers()) {
				if ((largest == null)
						|| (mol.getAtomCount() > largest.getAtomCount()))
					largest = mol;
			}

		if (largest != null && (largest.getAtomCount() < atomsOriginal)) {
			if (isAtomtypeasproperties())
				largest = atomtypes2property(largest,
						container.getProperty(at_property),
						isSparseproperties());
			return largest;
		} else
			return container;

	}

}
