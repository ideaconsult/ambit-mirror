package ambit2.core.processors;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IIsotope;

/**
 * Sets the mass number to the most abundant isotope
 * 
 * @author nina
 * 
 */
public class IsotopesProcessor extends AbstractStructureProcessor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4774343290932854421L;
	protected Isotopes isotopesFactory;

	public IsotopesProcessor(Logger logger) {
		super(logger);
		setAtomtypeasproperties(true);
		setSparseproperties(true);
		try {
			isotopesFactory = Isotopes.getInstance();
			setEnabled(true);
		} catch (IOException x) {
			logger.log(Level.WARNING, x.getMessage());
			isotopesFactory = null;
			setEnabled(false);
		}
	}

	@Override
	protected boolean applicable(IAtom atom) {
		IIsotope isotope = isotopesFactory.getMajorIsotope(atom.getSymbol());
		return atom.getMassNumber() != isotope.getMassNumber();
	}

	@Override
	public IAtomContainer process(IAtomContainer container) throws Exception {
		if (container == null)
			return container;
		if (atomtypeasproperties && !isSparseproperties())
			container.setProperty(at_property, null);

		Set<String> b = null;
		for (IAtom atom : container.atoms())
			if (atom.getMassNumber() != null) {
				IIsotope isotope = isotopesFactory.getMajorIsotope(atom
						.getSymbol());
				if (isotope != null) {
					if ((atom.getMassNumber() != isotope.getMassNumber())) {
						atom.setMassNumber(isotope.getMassNumber());
						atom.setExactMass(isotope.getExactMass());
						atom.setNaturalAbundance(isotope.getNaturalAbundance());
						if (b == null)
							b = new HashSet<>();
						b.add(atom.getAtomTypeName());
					}
				}
			}
		if (b != null && atomtypeasproperties)
			container.setProperty(at_property, b);
		return container;
	}

}
