package ambit2.processors.descriptors;



import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;

import ambit2.data.descriptors.DescriptorFactory;
import ambit2.data.descriptors.SpherosityDescriptor;
import ambit2.exceptions.AmbitException;

/**
 * Calculates {@link ambit2.data.descriptors.SpherosityDescriptor} and verifies if the value is < {@link #planarityThreshold} <br>
 * If yes, then the molecule is planar and the object is returned; otherwise returns null (skips the molecule) <br> 
 * The default value of the threshold is 0.1
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class PlanarityFilter extends CalculateDescriptors {
	protected SpherosityDescriptor spherosityDescriptor;
	protected double planarityThreshold = 0.1;
	public PlanarityFilter() {
		super();
		spherosityDescriptor = new SpherosityDescriptor();
		lookup.addDescriptorPair(spherosityDescriptor, DescriptorFactory.createEmptyDescriptor());

	}
	public Object process(Object object) throws AmbitException {
		Object o = super.process(object);
		if (o == null) return null;
		try {
			Object value = ((IChemObject) o).getProperty(spherosityDescriptor);
			IDescriptorResult r = ((DescriptorValue)value).getValue();
			if (((DoubleResult) r).doubleValue() < planarityThreshold) return o;
			else return null;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	public String toString() {
		return "Selects only planar compounds (Spherosity < " + planarityThreshold + ")";
	}
    public synchronized double getPlanarityThreshold() {
        return planarityThreshold;
    }
    public synchronized void setPlanarityThreshold(double planarityThreshold) {
        this.planarityThreshold = planarityThreshold;
    }
}
