package ambit2.descriptors.constitutional;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.molecular.AtomCountDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;

import ambit2.base.data.Property;

/**
 * Average molecular weight. Todeschini , Handbook of Molecular descriptors, physico-chemical properties, p.589.
 * @author Elena Urucheva, Nikolay Kochev
 * <b>Modified</b> 2013-10-31
 */
public class AverageMolecularWeightDescriptor extends DerivedMolecularWeightDescriptor {
	
	public AverageMolecularWeightDescriptor() {
		super( new String[] {"AMW"});
	}
	public DescriptorSpecification getSpecification()
	{
		return new DescriptorSpecification(
				String.format(Property.AMBIT_DESCRIPTORS_ONTOLOGY,"AverageMolecularWeightDescriptor"),
				this.getClass().getName(),
				"$Id: AverageMolecularWeightDescriptor.java, v 0.1 2013 Elena Urucheva, Nikolay Kochev",
				"http://ambit.sourceforge.net");
	}

	@Override
	public double calculateDerivedValue(IAtomContainer container,
			double weight) {
		AtomCountDescriptor descr1 = new AtomCountDescriptor();
		DescriptorValue dValue1 = descr1.calculate(container);
		IDescriptorResult res1 = dValue1.getValue();
		double nA = ((IntegerResult)res1).intValue();

		//System.out.println(" MW = " + weight);
		//System.out.println(" nA = " + nA);

		double averageWeight = 0.0;
		if (nA > 0)
			averageWeight = (weight/nA);
		return averageWeight;
	}
	
}
