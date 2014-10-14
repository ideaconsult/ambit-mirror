package ambit2.i5rest.resource;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.ideaconsult.iuclidws.types.Types.DocumentReferencePK;
import org.ideaconsult.iuclidws.types.Types.DocumentReferencePKType;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.i5rest.convertors.I5DocumentReporter;
import ambit2.i5rest.convertors.I5OutputStreamConvertor;
import ambit2.rest.StringConvertor;

public class DocumentResource<T extends Serializable> extends I5Resource<DocumentReferencePK, T> {

	private final static Logger LOGGER = Logger.getLogger(I5OutputStreamConvertor.class);
	public static final String resourceKey = "key";
	public static final String resource = "/document";
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_XML
		});
	}
	public static String getKey(Request request) {
		Form form = request.getResourceRef().getQueryAsForm();
		String k = form.getFirstValue(resourceKey);
		if (k==null) return null;
		else return Reference.decode(k);
	}		
	@Override
	protected DocumentReferencePK createQuery(Context context, Request request, Response response)
			throws ResourceException {
		try {
			String key = getKey(request);
			if (key==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			DocumentReferencePK documentReferencePK = new DocumentReferencePK();
			DocumentReferencePKType type = new DocumentReferencePKType();
			type.setDocumentReferencePKType(key);
			documentReferencePK.setUniqueKey(type);		
			return documentReferencePK;
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x);
		} finally {
			LOGGER.info("test looking for all query definition - end\n");
		}
	}
	@Override
	public IProcessor<DocumentReferencePK, Representation> createConvertor(Variant variant) throws AmbitException,
			ResourceException {
		//TODO use specific media type for IUCLID
		return new StringConvertor(new I5DocumentReporter(getRequest(),getSession()),MediaType.APPLICATION_XML);
	}

}
