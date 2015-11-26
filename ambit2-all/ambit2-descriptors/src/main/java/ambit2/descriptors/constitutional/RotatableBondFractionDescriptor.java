package ambit2.descriptors.constitutional;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.RotatableBondsCountDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;

import ambit2.base.data.Property;

/**
 * Rotatable bond fraction. Todeschini , Handbook of Molecular descriptors,
 * flexibility indices, p.313.
 * 
 * @author Elena Urucheva, Nikolay Kochev <b>Modified</b> 2013-10-31
 */
public class RotatableBondFractionDescriptor implements IMolecularDescriptor {
	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				String.format(Property.AMBIT_DESCRIPTORS_ONTOLOGY,
						"RotatableBondFractionDescriptor"),
				this.getClass().getName(),
				"$Id: RotatableBondFractionDescriptor.java, v 0.1 2013 Elena Urucheva, Nikolay Kochev",
				"http://ambit.sourceforge.net");
	}

	@Override
	public void initialise(IChemObjectBuilder arg0) {

	}

	public void setParameters(Object[] params) throws CDKException {

	}

	public Object[] getParameters() {
		return null;
	}

	public String[] getDescriptorNames() {
		return null;
	}

	public DescriptorValue calculate(IAtomContainer container) {
		RotatableBondsCountDescriptor descr = new RotatableBondsCountDescriptor();
		DescriptorValue dValue = descr.calculate(container);
		IDescriptorResult res = dValue.getValue();
		double nRotB = ((IntegerResult) res).intValue();
		// System.out.println("nRotB = " +nRotB);

		double nB = container.getBondCount();
		// System.out.println("nB = " +nB);

		double RBFD = 0.0;
		if (nB > 0)
			RBFD = nRotB / nB;
		;

		return new DescriptorValue(getSpecification(), getParameterNames(),
				getParameters(), new DoubleResult(RBFD), getDescriptorNames());
	}

	public IDescriptorResult getDescriptorResultType() {
		return new DoubleResult(0.0);
	}

	public String[] getParameterNames() {
		return null;
	}

	public Object getParameterType(String name) {
		return true;
	}

}