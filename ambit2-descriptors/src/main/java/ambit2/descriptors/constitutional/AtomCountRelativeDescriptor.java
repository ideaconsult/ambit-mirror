package ambit2.descriptors.constitutional;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleArrayResultType;
import org.openscience.cdk.qsar.result.IDescriptorResult;

import ambit2.base.data.Property;

/**
 * Relative atom-type count. Todeschini , Handbook of Molecular descriptors, count descriptors, p.175.
 * @author Elena Urucheva, Nevena Todorova, Nikolay Kochev
 * <b>Modified</b> 2013-10-31
 */
public class AtomCountRelativeDescriptor  extends AbstractAtomCountDescriptor {

	private String elementName = "";

	private String elements[] = {"H","C","O","N","X"};
	private double percentFactor = 1.0;


	public AtomCountRelativeDescriptor()  {
		super( new String[] {"H%","C%","O%","N%","X%"});
	}
	/**
	 * 
	 */
	public String[] getDescriptorNames()	{
		if (!"".equals(elementName)){	
			return new String[] {String.format("%s%%", elementName)};
		} else return super.getDescriptorNames();
	}


	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "elementName";
		return params;
	}


	public Object getParameterType(String name) {
		return "";
	}


	public Object[] getParameters() 
	{
		// return the parameters as used for the descriptor calculation
		return new String[] {elementName};
	}


	public DescriptorSpecification getSpecification() 
	{
		return new DescriptorSpecification(
				String.format(Property.AMBIT_DESCRIPTORS_ONTOLOGY,"AtomCountRelativeDescriptor"),
				this.getClass().getName(),
				"$Id: AtomCountRelativeDescriptor.java, v 0.1 2013 Elena Urucheva, Nevena Todorova, Nikolay Kochev",
				"http://ambit.sourceforge.net");
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


	@Override
	public DescriptorValue calculateCounts(IAtomContainer container) throws CDKException {
		int nAllAtoms = countAtoms(container, "*");
		
		DoubleArrayResult result = new DoubleArrayResult();
		
		if (elementName.equals(""))	{
			for (int i = 0; i < elements.length; i++)
			{
				int nAt = countAtoms(container, elements[i]);
				result.add((percentFactor * nAt)/nAllAtoms);
			}
		}
		else {
			int nAt = countAtoms(container, elementName);
			result.add((percentFactor * nAt)/nAllAtoms);
		}
		
		
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
				result, getDescriptorNames());
	}

	

	private int countAtoms(IAtomContainer container, String atType) throws CDKException {
		int atomCount = 0;
		
		if (atType.equals("*")) 	{ 
			atomCount = container.getAtomCount() + getNumImplicitHAtoms(container);   
			return (atomCount);
		}
		
		if (atType.equals("X")) 		{
			return countHalogens(container);
		}
		
		for (int i = 0; i < container.getAtomCount(); i++) 		{
			if (container.getAtom(i).getSymbol().equals(atType))			
				atomCount += 1;
		}	
		
		if (atType.equals("H")) {
			atomCount += getNumImplicitHAtoms(container);
		}
		
		
		return atomCount;
	}
	
	private int countHalogens(IAtomContainer container) {
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
	
	private int getNumImplicitHAtoms(IAtomContainer container) throws CDKException {
		int n = 0;
		for (int i = 0; i < container.getAtomCount(); i++) 	{
			// we assume that UNSET is equivalent to 0 implicit H's 
			//NJ : no, the assumption is not correct. UNSET should throw an exception
			Integer hcount = container.getAtom(i).getImplicitHydrogenCount();
			if (hcount != CDKConstants.UNSET) 
				n += hcount;
			else throw new CDKException("Unset implicit H");
		}
		
		return n;
	}
	
	/**
	 * Returning results of different size is not very convenient
	 */
	public IDescriptorResult getDescriptorResultType() {
		if (!"".equals(elementName))
			return new DoubleArrayResultType(1);
		else
			return new DoubleArrayResultType(5);
	}
}