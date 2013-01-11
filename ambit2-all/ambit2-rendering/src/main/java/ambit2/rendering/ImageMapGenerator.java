package ambit2.rendering;

import java.awt.Color;
import java.util.List;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.generators.AtomNumberGenerator;
import org.openscience.cdk.renderer.generators.IGeneratorParameter;

public class ImageMapGenerator extends AtomNumberGenerator {
	@Override
	public IRenderingElement generate(IAtomContainer ac, RendererModel model) {
		ElementGroup numbers = new ElementGroup();

		for (int i= 0; i < ac.getAtomCount(); i++) {
			Point2d p = ac.getAtom(i).getPoint2d();
			numbers.add(new ImageMapAreaElement(p.x, p.y, String.valueOf(i+1), Color.BLACK));
		}
		return numbers;
	}
	
	@Override
	public List<IGeneratorParameter<?>> getParameters() {
		return super.getParameters();
	}
}
