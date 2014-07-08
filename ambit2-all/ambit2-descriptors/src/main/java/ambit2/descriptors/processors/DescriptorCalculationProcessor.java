package ambit2.descriptors.processors;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;

import ambit2.base.exceptions.EmptyMoleculeException;
import ambit2.core.data.IStructureDiagramHighlights;

public class DescriptorCalculationProcessor extends AbstractDescriptorCalculationProcessor<IAtomContainer,IMolecularDescriptor> implements IStructureDiagramHighlights {
	
	protected Dimension imageSize = new Dimension(150,150);
	public Dimension getImageSize() {
		return imageSize;
	}
	public void setImageSize(Dimension imageSize) {
		this.imageSize = imageSize;
	}
	public DescriptorCalculationProcessor() {
		this(null);
	}
	public DescriptorCalculationProcessor(IMolecularDescriptor descriptor) {
		setDescriptor(descriptor);
	}

	public IMolecularDescriptor getDescriptor() {
		return descriptor;
	}

	public synchronized void setDescriptor(IMolecularDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3399104328409649302L;
	@Override
	public DescriptorValue calculate(IAtomContainer target) throws AmbitException {
		if (descriptor == null) 
			throw new AmbitException("Undefined descriptor");
		
		try {
			if ((target==null) || (target.getAtomCount()==0)) throw new EmptyMoleculeException();
				return descriptor.calculate(target);
		} catch (Exception x) {
				return new DescriptorValue(
						descriptor.getSpecification(),
						descriptor.getParameterNames(),
						descriptor.getParameters(),
						null,
						descriptor.getDescriptorNames(),
						x
						);
				//throw new AmbitException(getDescriptor().toString(),x);
		
		}
	}
	public BufferedImage getImage(IAtomContainer mol) throws AmbitException  {
		return getImage(mol, null,150,150,false);
	}
	
	public BufferedImage getImage(IAtomContainer mol,
			String ruleID, int width, int height, boolean atomnumbers)
			throws AmbitException {
		if (descriptor == null) 
			throw new AmbitException("Undefined descriptor");
		if ((mol==null) || (mol.getAtomCount()==0)) throw new EmptyMoleculeException();
		if (descriptor instanceof IStructureDiagramHighlights) {
			return ((IStructureDiagramHighlights)descriptor).getImage(mol, ruleID, width, height, atomnumbers);
		} else throw new AmbitException("Not supported");

	}
	@Override
	public BufferedImage getLegend(int width, int height) throws AmbitException {
		if (descriptor == null) 
			throw new AmbitException("Undefined descriptor");
		if (descriptor instanceof IStructureDiagramHighlights) {
			return ((IStructureDiagramHighlights)descriptor).getLegend(width, height);
		} else {
			DescriptorSpecification spec = descriptor.getSpecification();
			return writeMessages(new String[] {spec.getImplementationTitle()}, width, height);
		}
	}
	protected BufferedImage writeMessages(String[] msg,int width, int height) throws AmbitException {
		BufferedImage buffer = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		Graphics2D g = buffer.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, width,height);
		RenderingHints rh = g.getRenderingHints ();
		rh.put (RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.addRenderingHints(rh);
		g.setColor(new Color(81,99,115));
		int h = (int) (height*14/75); //looks nice at size 14 h=75
		g.setFont(new Font("Arial",Font.BOLD,h<8?8:h));
		
		for (int i = 0; i < msg.length;i++) {
			if (msg[i]==null) continue;
			g.drawString(msg[i].toString(), 
					3,
					h+h*i);
		}
		return buffer;
	}
}
