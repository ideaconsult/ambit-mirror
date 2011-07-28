package ambit2.rest.structure.diagram;

import java.awt.image.BufferedImage;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

public class OpenBabelDepict extends AbstractDepict {

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		this.getVariants().clear();
		this.getVariants().add(new Variant(MediaType.IMAGE_PNG));		
	}
	//obabel -:"CCC(=O)Cl" -O tmp.png -xp 1000
	@Override
	protected BufferedImage getImage(String smiles,int w, int h) throws ResourceException {
		try {
			return null;
		
		} catch (Exception x) {
			throw new ResourceException(x);
		}
	}
	protected String getTitle(Reference ref, String smiles) {
		return String.format("SMILES: %s<br><img src='%s' alt='%s' title='%s'>", smiles,ref,smiles,smiles);
	}
}
