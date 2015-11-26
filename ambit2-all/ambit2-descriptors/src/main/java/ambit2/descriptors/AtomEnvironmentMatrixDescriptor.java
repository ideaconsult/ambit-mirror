/**
 * <b>Filename</b> AtomEnvironmentDescriptor.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-6-15
 * <b>Project</b> ambit
 */
package ambit2.descriptors;

import org.openscience.cdk.atomtype.IAtomTypeMatcher;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerArrayResultType;

import ambit2.base.data.Property;
import ambit2.core.data.HashIntDescriptorResult;

/**
 * @author Nina Jeliazkova <br>
 *         <b>Modified</b> 2013-4-22
 */
public class AtomEnvironmentMatrixDescriptor extends AtomEnvironentMatrix implements IMolecularDescriptor {


	/**
     * 
     */
	public AtomEnvironmentMatrixDescriptor() throws Exception {
			super();
	}
	
	public AtomEnvironmentMatrixDescriptor(IAtomTypeMatcher matcher, String factoryResource,int maxLevels) throws Exception{
		super(matcher,factoryResource,maxLevels);
	}

	@Override
	public void initialise(IChemObjectBuilder arg0) {

	}

	/**
	 * 
	 * @see org.openscience.cdk.qsar.IDescriptor#getSpecification()
	 */
	public DescriptorSpecification getSpecification() {
		// TODO Define specification !!!!
		return new DescriptorSpecification(
				String.format(Property.AMBIT_DESCRIPTORS_ONTOLOGY,
						"AtomEnvironmentMatrix"),
				this.getClass().getName(),
				"$Id: AtomTypeMatrixDescriptor.java,v 1.3 2013/04/16 11:41:0 jeliazkova.nina@gmail.com",
				"http://ambit.sourceforge.net");
	}

	public IDescriptorResult getDescriptorResultType() {
		return new IntegerArrayResultType(1);
	}

	/**
	 * Gets the parameterNames attribute of the AtomHybridizationDescriptor
	 * object
	 * 
	 * @return The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[_param.values().length];
		for (int i = 0; i < params.length; i++)
			params[i] = _param.values()[i].toString();
		return params;
	}

	/**
	 * 
	 * @see org.openscience.cdk.qsar.IDescriptor#getParameterType(java.lang.String)
	 */
	public Object getParameterType(String arg0) {
		Object[] paramTypes = new Object[3];
		paramTypes[0] = new Integer(1);
		// TODO
		paramTypes[1] = "AtomTypeFactory";
		paramTypes[2] = "AtomTypeMatcher";
		return paramTypes;
	}

	/**
	 * Sets the parameters attribute of the AtomEnvironmentDescriptor object
	 * 
	 * @param params
	 *            - parameters, as below params[0] MaxLevel, optional , default
	 *            3 params[1] perceiveCyclicAtoms, optional , default false
	 *            params[2] AtomTypeFactory factory, optional, default
	 *            AtomTypeFactory.getInstance(
	 *            "org/openscience/cdk/dict/data/sybyl-atom-types.owl")
	 *            params[3] AtomTypeMatcher atm, optional , default
	 *            HybridizationStateATMatcher
	 */
	public void setParameters(Object[] allParams) throws CDKException {

		AtomTypeFactory afactory = null;
		IAtomTypeMatcher amatcher = null;

		for (int i = 0; i < allParams.length; i++) {
			switch (i) {
			case 0: {
				if (!(allParams[i] instanceof Integer))
					throw new CDKException(String.format(
							"The %d parameter must be of type Integer", i + 1));
				setMaxLevel(((Integer) allParams[i]).intValue());
				break;
			}
			case 1: {
				if (!(allParams[i] instanceof Boolean))
					throw new CDKException(String.format(
							"The %d parameter must be of type AtomTypeFactory",
							i + 1));
				afactory = (AtomTypeFactory) allParams[i];
				break;
			}
			case 2: {
				if (!(allParams[i] instanceof Boolean))
					throw new CDKException(
							String.format(
									"The %d parameter must be of type IAtomTypeMatcher",
									i + 1));
				amatcher = (IAtomTypeMatcher) allParams[i];
				break;
			}
			default: {
				throw new CDKException(String.format(
						"%s only expects %d parameters", getClass().getName(),
						allParams.length));
			}
			}
		}
		;

		try {
			descriptorNames = initAtomTypes(afactory, amatcher);
		} catch (Exception x) {
			throw new CDKException(x.getMessage());
		}
	}

	/**
	 * 
	 * @see org.openscience.cdk.qsar.IDescriptor#getParameters()
	 */
	public Object[] getParameters() {
		Object[] params = new Object[6];
		params[0] = new Integer(getMaxLevel());
		params[1] = getFactory(); // TODO should it be factory.clone() ?
		params[2] = getAtm(); // TODO should it be atm.clone() ?
		return params;
	}

	/**
	 * This method calculates the atom environment of an atom.
	 * 
	 * @param container
	 *            Parameter is the atom container.
	 * @return int[]
	 * @exception CDKException
	 *                Description of the Exception
	 */
	public DescriptorValue calculate(IAtomContainer container) {
		HashIntDescriptorResult r = null;
		try {
			r = doCalculation(container);
			return new DescriptorValue(getSpecification(), getParameterNames(),
					getParameters(), r, getDescriptorNames());
		} catch (Exception e) {
			return new DescriptorValue(getSpecification(), getParameterNames(),
					getParameters(), r, new String[] { getClass().getName() },
					e);
		}
	}

	public String[] getDescriptorNames() {
		return descriptorNames;
	}


}
