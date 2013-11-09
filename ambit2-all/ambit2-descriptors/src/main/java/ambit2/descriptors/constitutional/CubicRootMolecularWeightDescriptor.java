package ambit2.descriptors.constitutional;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;

import ambit2.base.data.Property;

/**
 * Cubic root molecular weight. Todeschini , Handbook of Molecular descriptors, physico-chemical properties, p.589.
 * @author Elena Urucheva, Nikolay Kochev
 * <b>Modified</b> 2013-10-31
 */
public class CubicRootMolecularWeightDescriptor extends DerivedMolecularWeightDescriptor {
	
	public CubicRootMolecularWeightDescriptor() {
		super(new String[] {"CubicRootMolecularWeight"});
	}
	@Override
	public DescriptorSpecification getSpecification() 
	{
		return new DescriptorSpecification(
				String.format(Property.AMBIT_DESCRIPTORS_ONTOLOGY,"CubicRootMolecularWeightDescriptor"),
				this.getClass().getName(),
				"$Id: CubicRootMolecularWeightDescriptor.java, v 0.1 2013 Elena Urucheva, Nikolay Kochev",
				"http://ambit.sourceforge.net");
	}

	@Override
	public double calculateDerivedValue(IAtomContainer container,
			double weight) {
		return Math.exp((1.0/3)* Math.log(weight));

	}
}


