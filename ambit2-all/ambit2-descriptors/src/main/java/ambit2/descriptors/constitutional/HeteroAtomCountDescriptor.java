package ambit2.descriptors.constitutional;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.IntegerResult;

import ambit2.base.data.Property;

/**
 * Number of atoms which are not hydrogens and carbons .
 * @author Elena Urucheva, Nikolay Kochev
 * <b>Modified</b> 2013-10-31
 */
public class HeteroAtomCountDescriptor extends AbstractAtomCountDescriptor {

	public HeteroAtomCountDescriptor() 	{	
		super(new String[] {"nHeteroAtom"});
	}

	public DescriptorSpecification getSpecification()
	{
		return new DescriptorSpecification(
				String.format(Property.AMBIT_DESCRIPTORS_ONTOLOGY,"HeteroAtomCountDescriptor"),
				this.getClass().getName(),
				"$Id: HeteroAtomCountDescriptor.java, v 0.1 2013 Elena Urucheva, Nikolay Kochev",
				"http://ambit.sourceforge.net");
	} 

	
	@Override
	public DescriptorValue calculateCounts(IAtomContainer container) throws CDKException {
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


}	

