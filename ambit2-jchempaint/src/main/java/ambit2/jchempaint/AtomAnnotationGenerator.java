package ambit2.jchempaint;

import java.awt.Color;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jchempaint.renderer.RendererModel;
import org.openscience.jchempaint.renderer.elements.IRenderingElement;
import org.openscience.jchempaint.renderer.elements.TextGroupElement;
import org.openscience.jchempaint.renderer.generators.BasicAtomGenerator;

public class AtomAnnotationGenerator extends BasicAtomGenerator {

	   public IRenderingElement generate(
	            IAtomContainer ac, IAtom atom, RendererModel model) {
	        
		   Object text = atom.getProperty(CompoundImageTools.ATOM_ANNOTATION);
		   if (text != null) {
			   Point2d p = atom.getPoint2d();
	            Color c = Color.black;
	            TextGroupElement textGroup;
	            
	            String[] result = text.toString().split("\\n");
	            double offset = 0.6;
	            if(result.length>1){
	                textGroup = new TextGroupElement(p.x, p.y-offset, result[1], c, Color.white);
	                textGroup.addChild(result[0], TextGroupElement.Position.N);
	            }else{
	                textGroup = new TextGroupElement(p.x, p.y-offset, result[0], c, Color.white);
	            }
	            if(result.length>2)
	                textGroup.addChild(result[0], TextGroupElement.Position.S);
	            
	            return textGroup;
		   } else return null;
	        
	    }    
}
