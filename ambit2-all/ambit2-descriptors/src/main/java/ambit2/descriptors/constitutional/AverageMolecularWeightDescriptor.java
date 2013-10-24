package ambit2.descriptors.constitutional;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.descriptors.molecular.AtomCountDescriptor;

import ambit2.descriptors.MolecularWeight;

public class AverageMolecularWeightDescriptor implements IMolecularDescriptor
{
	public DescriptorSpecification getSpecification()
	{
		return new DescriptorSpecification(
				"AverageMolecularWeightDescriptor",
				this.getClass().getName(),
				"$Id: AverageMolecularWeightDescriptor.java, v 0.1 2013 Elena Urucheva, Nikolay Kochev",
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
		return new String[] {"AMW"};
	}

	public DescriptorValue calculate(IAtomContainer container)
	{
		MolecularWeight descr = new MolecularWeight();
		DescriptorValue dValue = descr.calculate(container);
		IDescriptorResult res = dValue.getValue();		
		double weight = ((DoubleResult)res).doubleValue();

		AtomCountDescriptor descr1 = new AtomCountDescriptor();
		DescriptorValue dValue1 = descr1.calculate(container);
		IDescriptorResult res1 = dValue1.getValue();
		double nA = ((IntegerResult)res1).intValue();

		//System.out.println(" MW = " + weight);
		//System.out.println(" nA = " + nA);

		double averageWeight = 0.0;
		if (nA > 0)
			averageWeight = (weight/nA);

		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
				new DoubleResult(averageWeight), getDescriptorNames());
	}
	
	public IDescriptorResult getDescriptorResultType()
	{
		return new DoubleResult(0.0);
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
