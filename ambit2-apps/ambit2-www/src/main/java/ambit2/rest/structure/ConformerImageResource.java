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

public class ConformerImageResource extends ConformerResource {
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		getVariants().clear();
		getVariants().add(new Variant(MediaType.IMAGE_PNG));
		getVariants().add(new Variant(MediaType.IMAGE_GIF));

	}

	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		/* workaround for clients not being able to set accept headers */
		if (!variant.getMediaType().equals(MediaType.IMAGE_PNG)) {
			setTemplate(createTemplate(getContext(),getRequest(),getResponse()));
			setGroupProperties(getContext(),getRequest(),getResponse());
		}
		Form acceptform = getResourceRef(getRequest()).getQueryAsForm();
		String media = acceptform.getFirstValue("accept-header");
		if (media != null) {
			variant.setMediaType(new MediaType(media));
		}		
		
		if ((queryObject == null) && !(variant.getMediaType().equals(MediaType.TEXT_HTML))) 
			throw new NotFoundException();
			
		if (variant.getMediaType().equals(MediaType.IMAGE_PNG) ||
				variant.getMediaType().equals(MediaType.IMAGE_BMP) ||
				variant.getMediaType().equals(MediaType.IMAGE_JPEG) ||
				variant.getMediaType().equals(MediaType.IMAGE_TIFF) ||
				variant.getMediaType().equals(MediaType.IMAGE_GIF) 
				) {
			return createImageConvertor(variant);
		} 
		throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
		
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
