package ambit2.descriptors.processors;

import java.awt.image.BufferedImage;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.EmptyMoleculeException;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.data.AbstractDescriptorResultType;
import ambit2.core.data.IStructureDiagramHighlights;

public class DescriptorCalculationProcessor extends
		DefaultAmbitProcessor<IAtomContainer, DescriptorValue> implements IStructureDiagramHighlights {
	protected IMolecularDescriptor descriptor;
	public DescriptorCalculationProcessor() {
		this(null);
	}
	public DescriptorCalculationProcessor(IMolecularDescriptor descriptor) {
		setDescriptor(descriptor);
	}

	public IMolecularDescriptor getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(IMolecularDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3399104328409649302L;

	public DescriptorValue process(IAtomContainer target) throws AmbitException {
		if (descriptor == null) 
			throw new AmbitException("Undefined descriptor");
		
		try {
			if ((target==null) || (target.getAtomCount()==0)) throw new EmptyMoleculeException();
				return descriptor.calculate(target);
		} catch (Exception x) {
				return new DescriptorValue(
						descriptor.getSpecification(),
						new String[]{},
						new Object[]{},
						new AbstractDescriptorResultType<Exception>(x),
						new String[] {descriptor.getClass().getName()}
						);
				//throw new AmbitException(getDescriptor().toString(),x);
		
		}
	}
	public BufferedImage getStructureDiagramWithHighlights(IAtomContainer mol,
			String ruleID, int width, int height, boolean atomnumbers)
			throws AmbitException {
		if (descriptor == null) 
			throw new AmbitException("Undefined descriptor");
		if ((mol==null) || (mol.getAtomCount()==0)) throw new EmptyMoleculeException();
		if (descriptor instanceof IStructureDiagramHighlights) {
			return ((IStructureDiagramHighlights)descriptor).getStructureDiagramWithHighlights(mol, ruleID, width, height, atomnumbers);
		} else throw new AmbitException("Not supported");

	}
}
