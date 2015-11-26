package ambit2.descriptors.processors;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IDescriptor;

import ambit2.base.data.Property;
import ambit2.core.data.AbstractDescriptorResultType;


public abstract class AbstractDescriptorCalculationProcessor<ITEM, DESCRIPTOR extends IDescriptor> extends DefaultAmbitProcessor<ITEM, DescriptorValue>  {
	protected DESCRIPTOR descriptor;
	/**
	 * 
	 */
	private static final long serialVersionUID = 471269379672309986L;

	public AbstractDescriptorCalculationProcessor() {
		this(null);
	}
	public AbstractDescriptorCalculationProcessor(DESCRIPTOR descriptor) {
		setDescriptor(descriptor);
	}

	public DESCRIPTOR getDescriptor() {
		return descriptor;
	}

	public synchronized void setDescriptor(DESCRIPTOR descriptor) {
		this.descriptor = descriptor;
	}
	
	
	public DescriptorValue process(ITEM target) throws AmbitException {
		if (descriptor == null) 
			throw new AmbitException("Undefined descriptor");
		
		try {
			return calculate(target);
		} catch (Exception x) {
				return new DescriptorValue(
						(DescriptorSpecification)descriptor.getSpecification(),
						new String[]{},
						new Object[]{},
						new AbstractDescriptorResultType<Exception>(x),
						new String[] {descriptor.getClass().getName()}
						);
				//throw new AmbitException(getDescriptor().toString(),x);
		
		}
	}
	
	protected abstract DescriptorValue calculate(ITEM target) throws Exception;
}
