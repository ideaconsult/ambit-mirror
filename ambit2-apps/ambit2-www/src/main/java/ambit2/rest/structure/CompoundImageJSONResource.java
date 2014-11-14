package ambit2.rest.structure;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.NotFoundException;
import net.idea.restnet.c.RepresentationConvertor;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.rest.ChemicalMediaType;

/**
 * /compound/{id}/imagejson
 * /compound/{id}/conformer/{id}/imagejson
 * 2D coordinates in JSON, generated the same way as for 
 * /compound/{id}/image
 * /compound/{id}/conformer/{id}/image
 * @author nina
 *
 */
public class CompoundImageJSONResource extends CompoundResource {
	public static String resource = "/imagejson";
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		getVariants().clear();
		getVariants().add(new Variant(ChemicalMediaType.IMAGE_JSON));  //JSON
		getVariants().add(new Variant(MediaType.APPLICATION_JSON));    //JSON
		getVariants().add(new Variant(MediaType.APPLICATION_JAVASCRIPT)); //JSONP
	}

	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		Form acceptform = getResourceRef(getRequest()).getQueryAsForm();
		String media = acceptform.getFirstValue("accept-header");
		if (media != null) {
			variant.setMediaType(new MediaType(media));
		}		
		
		if ((queryObject == null) && !(variant.getMediaType().equals(MediaType.TEXT_HTML))) 
			throw new NotFoundException();
			
		return createImageStringConvertor(variant);			
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	@Override
	protected Representation process(Representation entity, Variant variant,
			boolean async) throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
}
