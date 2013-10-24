package ambit2.descriptors.constitutional;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.qsar.result.IntegerResult;

public class AtomCountHybridizationDescriptor implements IMolecularDescriptor
{
	private String names[];
	
	public AtomCountHybridizationDescriptor()
	{
		names = new String[] {"nSp3" ,"nSp2", "nSp1"};
	}


	public String[] getDescriptorNames() 
	{
		return names;
	}

	public String[] getParameterNames()
	{
		return null;
	}


	public Object getParameterType(String arg0)
	{
		return null;
	}

	public Object[] getParameters() 
	{
		return null;
	}

	public DescriptorSpecification getSpecification() 
	{
		return new DescriptorSpecification(
				"AtomCountHybridizationDescriptor",
				this.getClass().getName(),
				"$Id: AtomCountHybridizationDescriptor.java, v 0.1 2013 Elena Urucheva, Nikolay Kochev",
				"http://ambit.sourceforge.net");
	}

	public void setParameters(Object[] arg0) throws CDKException
	{
	}


	public DescriptorValue calculate(IAtomContainer container)
	{
		int nSp3 = 0;
		int nSp2 = 0;
		int nSp1 = 0;

		if (container == null)
		{
			return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
					new IntegerResult((int) Double.NaN), getDescriptorNames(),
					new CDKException("The supplied AtomContainer was NULL"));
		}

		if (container.getAtomCount() == 0) 
		{
			return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
					new IntegerResult((int) Double.NaN), getDescriptorNames(),
					new CDKException("The supplied AtomContainer did not have any atoms"));
		}

		for (int i = 0; i < container.getAtomCount(); i++) 
		{
			if ( container.getAtom(i).getHybridization() == null)
				continue;

			container.getAtom(i).getHybridization();
			if (container.getAtom(i).getHybridization() == Hybridization.SP3)
				nSp3 += 1;

			container.getAtom(i).getHybridization();
			if (container.getAtom(i).getHybridization() == Hybridization.SP2)
				nSp2 += 1;

			container.getAtom(i).getHybridization();
			if (container.getAtom(i).getHybridization() == Hybridization.SP1)
				nSp1 += 1;
		}

		IntegerArrayResult result = new IntegerArrayResult(3);
		result.add(nSp3);
		result.add(nSp2);
		result.add(nSp1);

		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
				result, getDescriptorNames());
	}

	public IDescriptorResult getDescriptorResultType()
	{
		return new IntegerArrayResult(3);
	}

}
