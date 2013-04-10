package ambit2.rendering;

import java.awt.Color;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.TextElement;
import org.openscience.cdk.renderer.generators.AtomNumberGenerator;

public class AtomLabelGenerator extends AtomNumberGenerator {
	
	@Override
	public IRenderingElement generate(IAtomContainer ac, RendererModel arg1) {
		ElementGroup numbers = new ElementGroup();

		for (int i= 0; i < ac.getAtomCount(); i++) {
			Point2d p = ac.getAtom(i).getPoint2d();
			Object label = getLabel(ac.getAtom(i));
			Object labelfile = getLabelFile(ac.getAtom(i));
			//System.out.println(String.format("label\t%s\t#%d\tlabel %s\tlabelfile %s\t%s",ac.hashCode(),i+1,label,labelfile,ac.getAtom(i).getSymbol()));
			numbers.add(new TextElement(p.x, p.y,label==null?"?":(i+1)+"."+label.toString() , Color.BLACK));
		}
		return numbers;
	}
	
	protected Object getLabel(IAtom atom) {
		return atom.getProperty("LABEL");
	}

	protected Object getLabelFile(IAtom atom) {
		return atom.getProperty("LABELFILE");
	}
}
