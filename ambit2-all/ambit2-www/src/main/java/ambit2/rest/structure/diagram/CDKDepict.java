package ambit2.rest.structure.diagram;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jchempaint.renderer.selection.IChemObjectSelection;
import org.openscience.jchempaint.renderer.selection.SingleSelection;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.jchempaint.CompoundImageTools;
import ambit2.smarts.query.ISmartsPattern;
import ambit2.smarts.query.SmartsPatternAmbit;

/**
 * 2D depiction based on CDK
 * @author nina
 *
 */
public class CDKDepict extends AbstractDepict {
	protected CompoundImageTools depict = new CompoundImageTools();

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		depict.setImageSize(new Dimension(410,210));
		this.getVariants().clear();
		this.getVariants().add(new Variant(MediaType.IMAGE_PNG));		
	}
	@Override
	protected BufferedImage getImage(String smiles,int w, int h) throws ResourceException {
		try {
			depict.setImageSize(new Dimension(w,h));
			
			return depict.generateImage(smiles,smarts == null?null:new SmartsPatternSelector(smarts),false,false);
		} catch (ResourceException x) {throw x; 
		} catch (Exception x) { 
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x); 
		}
	}
	@Override
	protected String getTitle(Reference ref, String smiles) {
		return String.format("SMILES: %s<br><img src='%s' alt='%s' title='%s'>", smiles,ref,smiles,smiles);
	}
	

}

class SmartsPatternSelector implements IProcessor<IAtomContainer,IChemObjectSelection> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2781667048103591227L;
	protected String smarts;
	public SmartsPatternSelector(String smarts) {
		this.smarts = smarts;
	}
	public long getID() {
		return 0;
	}

	public boolean isEnabled() {
		return true;
	}

	public IChemObjectSelection process(IAtomContainer target)
			throws AmbitException {
		ISmartsPattern pattern = new SmartsPatternAmbit(smarts);
		if(pattern.match(target)>0) {
			IAtomContainer selected = pattern.getMatchingStructure(target);
			return new SingleSelection<IAtomContainer>(selected);
		} else return null;
	}

	public void setEnabled(boolean value) {
		
	}
	
}
