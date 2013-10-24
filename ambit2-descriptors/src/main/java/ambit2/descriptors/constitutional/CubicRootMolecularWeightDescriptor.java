package ambit2.descriptors.constitutional;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import ambit2.descriptors.MolecularWeight;


public class CubicRootMolecularWeightDescriptor 
{
	public DescriptorSpecification getSpecification() 
	{
		return new DescriptorSpecification(
				"CubicRootMolecularWeightDescriptor",
				this.getClass().getName(),
				"$Id: CubicRootMolecularWeightDescriptor.java, v 0.1 2013 Elena Urucheva, Nikolay Kochev",
				"http://ambit.sourceforge.net");
	}

	public void setParameters(Object[] params) throws CDKException 
	{	
	}

	public Object[] getParameters() 
	{
		return null;
	}

	public String[] getDescriptorNames()
	{
		return null;
	}

	public DescriptorValue calculate(IAtomContainer container)
	{
		MolecularWeight descr = new MolecularWeight();
		DescriptorValue dValue = descr.calculate(container);
		IDescriptorResult res = dValue.getValue();
		double weight = ((DoubleResult)res).doubleValue();
		
		double CMWD = Math.exp((1.0/3)* Math.log(weight));;
		
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
				new DoubleResult(CMWD), getDescriptorNames());

	}

	public IDescriptorResult getDescriptorResultType() 
	{
		return new DoubleResult(1);
	}


	public String[] getParameterNames()
	{
		return null;
	}

	public Object getParameterType(String name) 
	{
		return "";
	}
}


