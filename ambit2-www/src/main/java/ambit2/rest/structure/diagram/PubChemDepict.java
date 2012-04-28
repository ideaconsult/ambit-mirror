package ambit2.rest.structure.diagram;

import java.awt.image.BufferedImage;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.HttpException;
import ambit2.pubchem.rest.PUGRestImageRequest;
import ambit2.pubchem.rest.PUGRestImageRequest.IMAGE_SIZE;
import ambit2.pubchem.rest.PUGRestImageRequest.RECORD_TYPE;
import ambit2.pubchem.rest.PUGRestRequest.COMPOUND_DOMAIN_INPUT;

public class PubChemDepict extends AbstractDepict {
	protected PUGRestImageRequest depict = new PUGRestImageRequest();
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		this.getVariants().clear();
		this.getVariants().add(new Variant(MediaType.IMAGE_PNG));		
	}
	
	@Override
	protected BufferedImage getImage(String smiles, int w, int h,
			String recordType) throws ResourceException {
		depict.setWidth(w);
		depict.setHeight(h);
		depict.setImageSize(IMAGE_SIZE.dimension);
		depict.setInput(COMPOUND_DOMAIN_INPUT.smiles);
		try {
			depict.setRecordType(RECORD_TYPE.D3.toString().equals(recordType)?RECORD_TYPE.D3:RECORD_TYPE.D2);
		} catch (Exception x) {}
		if (smiles.startsWith("InChI")) depict.setInput(COMPOUND_DOMAIN_INPUT.inchi);
		else try  { //CID
			Integer.parseInt(smiles);
			depict.setInput(COMPOUND_DOMAIN_INPUT.cid);
		} catch (Exception x) {}
		try {
			if (smiles==null) return null;
			return depict.process(smiles);
		} catch (HttpException x) {
			throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,x.getMessage(),x);
		} catch (Exception x) {
			throw new ResourceException(x);
		}
	}
	protected String getTitle(Reference ref, String smiles) throws ResourceException {
		return String.format("SMILES: %s<br><img src='%s' alt='%s' title='%s'>", smiles,ref,smiles,smiles);
	}
}