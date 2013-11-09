package ambit2.descriptors.constitutional;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;

import ambit2.base.data.Property;

/**
 * Square root molecular weight. Todeschini , Handbook of Molecular descriptors, physico-chemical properties, p.589.
 * @author Elena Urucheva, Nikolay Kochev
 * <b>Modified</b> 2013-10-31
 */
public class SquareRootMolecularWeightDescriptor extends DerivedMolecularWeightDescriptor  {
	
	public SquareRootMolecularWeightDescriptor() {
		super(new String[] {"SquareRootMW"});
	}
	@Override
	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				String.format(Property.AMBIT_DESCRIPTORS_ONTOLOGY,"SquareRootMolecularWeightDescriptor"),
				this.getClass().getName(),
				"$Id: SquareRootMolecularWeightDescriptor.java, v 0.1 2013 Elena Urucheva, Nikolay Kochev",
				"http://ambit.sourceforge.net");
	}
	@Override
	public double calculateDerivedValue(IAtomContainer container,
			double weight) {
		return  Math.sqrt(weight);
	}

}


