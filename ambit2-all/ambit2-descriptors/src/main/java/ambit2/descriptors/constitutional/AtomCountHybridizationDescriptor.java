package ambit2.descriptors.constitutional;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;

import ambit2.base.data.Property;

/**
 * Atomistic topological indices. Todeschini , Handbook of Molecular descriptors, count descriptors, p.175.
 * @author Elena Urucheva, Nikolay Kochev
 * <b>Modified</b> 2013-10-31
 */
public class AtomCountHybridizationDescriptor extends AbstractAtomCountDescriptor {

	
	public AtomCountHybridizationDescriptor()	{
		super(new String[] {"nSp3" ,"nSp2", "nSp1"});
	}

	public DescriptorSpecification getSpecification() 
	{
		return new DescriptorSpecification(
				String.format(Property.AMBIT_DESCRIPTORS_ONTOLOGY,"AtomCountHybridizationDescriptor"),
				this.getClass().getName(),
				"$Id: AtomCountHybridizationDescriptor.java, v 0.1 2013 Elena Urucheva, Nikolay Kochev",
				"http://ambit.sourceforge.net");
	}

	@Override
	public DescriptorValue calculateCounts(IAtomContainer container) {
		int nSp3 = 0;
		int nSp2 = 0;
		int nSp1 = 0;

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

	public IDescriptorResult getDescriptorResultType()	{
		return new IntegerArrayResult(3);
	}

}
