package ambit2.rest.structure.diagram;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.core.io.CompoundImageTools;

/**
 * 2D depiction based on CDK
 * @author nina
 *
 */
public class CDKDepict extends AbstractDepict {

	protected String smiles = null;
	protected CompoundImageTools depict = new CompoundImageTools();

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		depict.setImageSize(new Dimension(410,210));
		this.getVariants().clear();
		this.getVariants().add(new Variant(MediaType.IMAGE_PNG));		
	}
	@Override
	protected BufferedImage getImage(String smiles,int w, int h) throws AmbitException {
		try {
			depict.setImageSize(new Dimension(w,h));
			return depict.generateImage(smiles);
		} catch (Exception x) { throw new AmbitException(x); }
	}
	@Override
	protected String getTitle(Reference ref, String smiles) {
		return String.format("SMILES: %s<br><img src='%s' alt='%s' title='%s'>", smiles,ref,smiles,smiles);
	}
}
