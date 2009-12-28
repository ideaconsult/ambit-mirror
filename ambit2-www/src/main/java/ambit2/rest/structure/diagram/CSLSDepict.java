package ambit2.rest.structure.diagram;

import java.awt.image.BufferedImage;

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.pubchem.CSLSImageRequest;

/**
 * http://cactus.nci.nih.gov/chemical/structure/c1ccccc1/image
 * @author nina
 *
 */
public class CSLSDepict extends AbstractDepict {
	protected CSLSImageRequest depict = new CSLSImageRequest();
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		this.getVariants().clear();
		this.getVariants().add(new Variant(MediaType.IMAGE_PNG));		
	}
	@Override
	protected BufferedImage getImage(String smiles,int w, int h) throws AmbitException {
		depict.setWidth(w);
		depict.setHeight(h);
		return depict.process(smiles);
	}
	protected String getTitle(Reference ref, String smiles) {
		return String.format("SMILES: %s<br><img src='%s' alt='%s' title='%s'>", smiles,ref,smiles,smiles);
	}
}
