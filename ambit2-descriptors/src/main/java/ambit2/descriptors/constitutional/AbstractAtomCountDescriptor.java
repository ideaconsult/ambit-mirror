package ambit2.descriptors.constitutional;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.qsar.AbstractMolecularDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;

import ambit2.base.data.Property;

public abstract class AbstractAtomCountDescriptor extends
		AbstractMolecularDescriptor {
	protected final String[] descriptorNames;

	public AbstractAtomCountDescriptor(String[] names) {
		super();
		this.descriptorNames = names;
	}

	@Override
	public String[] getDescriptorNames() {
		return descriptorNames;
	}

	public void setParameters(Object[] params) throws CDKException {
	}

	public Object[] getParameters() {
		return null;
	}

	public IDescriptorResult getDescriptorResultType() {
		return new IntegerResult(1);
	}

	public String[] getParameterNames() {
		return new String[0];
	}

	public Object getParameterType(String name) {
		return "";
	}
	@Override
	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				String.format(Property.AMBIT_DESCRIPTORS_ONTOLOGY,
						"AbstractAtomCountDescriptor"),
				this.getClass().getName(),
				"$Id: AbstractAtomCountDescriptor.java, v 0.1 2013 Elena Urucheva, Nikolay Kochev",
				"http://ambit.sourceforge.net");
	}
	
	@Override
	public DescriptorValue calculate(IAtomContainer container) {
		if (container == null) {
			return new DescriptorValue(getSpecification(), getParameterNames(),
					getParameters(), new DoubleResult(Double.NaN),
					getDescriptorNames(), new CDKException(
							"The supplied AtomContainer was NULL"));
		}

		if (container.getAtomCount() == 0) {
			return new DescriptorValue(
					getSpecification(),
					getParameterNames(),
					getParameters(),
					new DoubleResult(Double.NaN),
					getDescriptorNames(),
					new CDKException(
							"The supplied AtomContainer did not have any atoms"));
		}
		try {
			return calculateCounts(container);
		} catch (CDKException x) {
			return new DescriptorValue(getSpecification(), getParameterNames(),
					getParameters(), new DoubleResult(Double.NaN),
					getDescriptorNames(), x);
		}

	}

	public abstract DescriptorValue calculateCounts(IAtomContainer container)
			throws CDKException;

	@Override
	public void initialise(IChemObjectBuilder arg0) {

	}
}
