package ambit2.descriptors.processors;

import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.data.Property;
import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.data.HashIntDescriptorResult;
import ambit2.core.helper.CDKHueckelAromaticityDetector;
import ambit2.core.processors.structure.AbstractPropertyGenerator;
import ambit2.descriptors.AtomEnvironentMatrix;

/**
 * A processor to generate atom environments. Uses
 * {@link ambit2.data.descriptors.AtomEnvironmentMatrixr} for actual
 * generation. The result is contained in
 * {@link HashIntDescriptorResult} and is assigned as a
 * molecule property with a name {@link AmbitCONSTANTS#AtomEnvironment}. <br>
 * Used by {@link ambit2.database.writers.AtomEnvironmentMatrixWriter}
 * 
 */
public class AtomEnvironmentMatrixGenerator extends
		AbstractPropertyGenerator<HashIntDescriptorResult> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7279981279903286909L;
	protected CDKHydrogenAdder hAdder = null;
	protected AtomEnvironentMatrix matrix = null;
	protected FPTable fpmode = FPTable.aematrix;
	
	public AtomEnvironmentMatrixGenerator() throws Exception{
		this(7);
	}
	public AtomEnvironmentMatrixGenerator(int maxLevels) throws Exception{
		super();
		matrix = new AtomEnvironentMatrix(CDKAtomTypeMatcher.getInstance(SilentChemObjectBuilder.getInstance()),"org/openscience/cdk/dict/data/cdk-atom-types.owl",maxLevels);
	}

	@Override
	public HashIntDescriptorResult generateProperty(IAtomContainer atomContainer)
			throws AmbitException {

		try {

			IAtomContainer mol = (IAtomContainer) atomContainer
					.clone();

			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
			// if (useHydrogens) { //always, otherwise atom types are not
			// recognised correctly
			// for some reason H atoms are added as bond references, but not in
			// atom list - bug?
			try {
				if (hAdder == null)
					hAdder = CDKHydrogenAdder
							.getInstance(SilentChemObjectBuilder.getInstance());
				hAdder.addImplicitHydrogens(mol);
				AtomContainerManipulator.suppressHydrogens(mol);
			} catch (Exception x) {

			}
			CDKHueckelAromaticityDetector.detectAromaticity(mol);
			// }
			
			try {
				return matrix.doCalculation(mol);
			} catch (CDKException x) {
				throw new AmbitException(x);
			}
		} catch (Exception x) {
			logger.log(Level.WARNING, x.getMessage(), x);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ambit2.processors.DefaultAmbitProcessor#toString()
	 */
	public String toString() {
		return "Generates atom environments";
	}


	public Integer getMaxLevels() {
		return matrix.getMaxLevel();
	}

	public void setMaxLevels(Integer maxLevels) {
		matrix.setMaxLevel(maxLevels);
	}


	@Override
	public Property getProperty() {
		return Property.getInstance(fpmode.getProperty(), fpmode.getProperty());
	}

	@Override
	protected Property getTimeProperty() {
		return null;
	}
}
