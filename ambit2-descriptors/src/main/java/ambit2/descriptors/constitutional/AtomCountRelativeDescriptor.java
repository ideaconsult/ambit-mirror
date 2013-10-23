package ambit2.descriptors.constitutional;


import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleArrayResultType;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;

public class AtomCountRelativeDescriptor  implements IMolecularDescriptor  
{
	private String elementName;
	private String names[];
	private String elements[];
	private double percentFactor = 1.0;


	public AtomCountRelativeDescriptor()
	{
		elementName = "";
		elements = new String[] {"H","C","O","N","X"};
		
		names = new String[elements.length];
		for (int i = 0; i<elements.length; i++)
			names[i] = elements[i] + "%";
	}
	
	public String[] getDescriptorNames() 
	{
		
		if (!elementName.equals(""))
		{	
			String name = elementName + "%";
			names = new String[] {name};
		}	 
		
		return names;
	}


	public String[] getParameterNames() 
	{
		String[] params = new String[1];
		params[0] = "elementName";
		return params;
	}


	public Object getParameterType(String name)
	{
		return "";
	}


	public Object[] getParameters() 
	{
		// return the parameters as used for the descriptor calculation
		Object[] params = new Object[1];
		params[0] = elementName;
		return params;
	}


	public DescriptorSpecification getSpecification() 
	{
		return new DescriptorSpecification(
				"http://",
				this.getClass().getName(),
				"$Id:  $",
				"The Chemistry Development Kit");
	}


	public void setParameters(Object[] params) throws CDKException 
	{
		if (params.length > 1) {
			throw new CDKException("AtomCountRelative only expects one parameter");
		}
		if (!(params[0] instanceof String)) {
			throw new CDKException("The parameter must be of type String");
		}
		elementName = (String) params[0];
	}



	public DescriptorValue calculate(IAtomContainer container)
	{
		if (container == null) 
		{
			return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
					new DoubleResult (Double.NaN), getDescriptorNames(),
					new CDKException("The supplied AtomContainer was NULL"));
		}

		if (container.getAtomCount() == 0) 
		{
			return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
					new DoubleResult(Double.NaN), getDescriptorNames(),
					new CDKException("The supplied AtomContainer did not have any atoms"));
		}
		
		int nAllAtoms = countAtoms(container, "*");
		
		DoubleArrayResult result = new DoubleArrayResult();
		
		if (elementName.equals(""))
		{
			for (int i = 0; i < elements.length; i++)
			{
				int nAt = countAtoms(container, elements[i]);
				result.add((percentFactor * nAt)/nAllAtoms);
			}
		}
		else
		{
			int nAt = countAtoms(container, elementName);
			result.add((percentFactor * nAt)/nAllAtoms);
		}
		
		
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
				result, getDescriptorNames());
	}

	

	private int countAtoms(IAtomContainer container, String atType)
	{
		int atomCount = 0;
		
		if (atType.equals("*"))
		{
			atomCount = container.getAtomCount() + getNumImplicitHAtoms(container);   
			return (atomCount);
		}
		
		if (atType.equals("X"))
		{
			return countHalogens(container);
		}
		
		for (int i = 0; i < container.getAtomCount(); i++) 
		{
			if (container.getAtom(i).getSymbol().equals(atType))			
				atomCount += 1;
		}	
		
		if (atType.equals("H"))
		{
			atomCount += getNumImplicitHAtoms(container);
		}
		
		
		return atomCount;
	}
	
	private int countHalogens(IAtomContainer container)
	{
		int halNum = 0;
		
		for (int i = 0; i < container.getAtomCount(); i++) 
		{
			if (container.getAtom(i).getSymbol().equals("Cl"))			
				halNum += 1;
			else
				if (container.getAtom(i).getSymbol().equals("F"))			
					halNum += 1;
				else
					if (container.getAtom(i).getSymbol().equals("Br"))			
						halNum += 1;
					else
						if (container.getAtom(i).getSymbol().equals("I"))			
							halNum += 1;
			
		}
		
		return halNum;
	}
	
	
	private int getNumImplicitHAtoms(IAtomContainer container)
	{
		int n = 0;
		for (int i = 0; i < container.getAtomCount(); i++) 
		{
			// we assume that UNSET is equivalent to 0 implicit H's
			Integer hcount = container.getAtom(i).getImplicitHydrogenCount();
			if (hcount != CDKConstants.UNSET) 
				n += hcount;
		}
		
		return n;
	}
	

	public IDescriptorResult getDescriptorResultType()
	{
		return new DoubleArrayResultType(5);
	}
}