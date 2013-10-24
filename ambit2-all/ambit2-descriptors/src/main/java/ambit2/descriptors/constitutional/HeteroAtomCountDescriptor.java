package ambit2.descriptors.constitutional;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;

public class HeteroAtomCountDescriptor implements IMolecularDescriptor 
{

	public static final String[] names = 
	{
		"nHeteroAtom"
	};
	

	public HeteroAtomCountDescriptor()
	{	
	}

	public DescriptorSpecification getSpecification()
	{
		return new DescriptorSpecification(
				"HeteroAtomCountDescriptor",
				this.getClass().getName(),
				"$Id: HeteroAtomCountDescriptor.java, v 0.1 2013 Elena Urucheva, Nikolay Kochev",
				"http://ambit.sourceforge.net");
	} 


	public void setParameters(Object[] params) throws CDKException 
	{	
	}


	public Object[] getParameters() 
	{
		// return the parameters as used for the descriptor calculation
		return null;
	}

	public String[] getDescriptorNames()
	{
		return names;
	}


	public DescriptorValue calculate(IAtomContainer container)
	{	
		//int nAtom = countAtoms(container);
		
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

		int nHeteroAtom = 0; 
		for (int i = 0; i < container.getAtomCount(); i++)
		{
			if (container.getAtom(i).getSymbol().equals("H"))
				continue;
			
			if (container.getAtom(i).getSymbol().equals("C"))
				continue;
			
			nHeteroAtom +=1;
		}		

		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
				new IntegerResult(nHeteroAtom), getDescriptorNames());
	}	


	public IDescriptorResult getDescriptorResultType()
	{
		return new IntegerResult(1);
	}


	public String[] getParameterNames()
	{	
		return new String[0];
	}


	public Object getParameterType(String name) 
	{
		return "";
	}
}

