package ambit2.descriptors.constitutional;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;

import ambit2.descriptors.MolecularWeight;

/**
 * Parent class for molecular weight related descriptors
 * @author nina
 *
 */
public abstract class DerivedMolecularWeightDescriptor  extends MolecularWeight {
	protected final String[] names;

	public DerivedMolecularWeightDescriptor(String[] names) {
		super();
		this.names = names;
	}

	@Override
	public String[] getDescriptorNames() {
		return names;
	}
	
	@Override
	public DescriptorValue calculate(IAtomContainer container) {
	
		DescriptorValue dValue = super.calculate(container);
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
				new DoubleResult(calculateDerivedValue(container, ((DoubleResult)dValue.getValue()).doubleValue())), 
				getDescriptorNames());
	}
	
	public abstract double calculateDerivedValue(IAtomContainer container, double molweight);

}
