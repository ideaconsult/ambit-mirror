package ambit2.descriptors.constitutional;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;

public abstract class AbstractAtomCountDescriptor implements IMolecularDescriptor  {
	protected final String[] descriptorNames;

	public AbstractAtomCountDescriptor(String[] names) {
		super();
		this.descriptorNames = names;
	}

	@Override
	public String[] getDescriptorNames() {
		return descriptorNames;
	}
	
	public void setParameters(Object[] params) throws CDKException	{	
	}


	public Object[] getParameters()	{
		return null;
	}

	public IDescriptorResult getDescriptorResultType() {
		return new IntegerResult(1);
	}


	public String[] getParameterNames()
	{	
		return new String[0];
	}


	public Object getParameterType(String name) {
		return "";
	}

	
	@Override
	public DescriptorValue calculate(IAtomContainer container) {
		if (container == null) 	{
			return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
					new DoubleResult (Double.NaN), getDescriptorNames(),
					new CDKException("The supplied AtomContainer was NULL"));
		}

		if (container.getAtomCount() == 0)		{
			return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
					new DoubleResult(Double.NaN), getDescriptorNames(),
					new CDKException("The supplied AtomContainer did not have any atoms"));
		}
		try {
			return calculateCounts(container);
		} catch (CDKException x) {
			return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
					new DoubleResult(Double.NaN), getDescriptorNames(),
					x);
		}
		
	}
	
	public abstract DescriptorValue calculateCounts(IAtomContainer container) throws CDKException  ;
}	
