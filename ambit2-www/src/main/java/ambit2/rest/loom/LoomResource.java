package ambit2.rest.loom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.rest.algorithm.CatalogResource;

/**
 * Entry point for remote services wrappers. See {@linkplain LoomRouter}
 * This is intended to work with different services
 * /loom/{resourcetype}/{resourceid}
 * @author nina
 *
 */
public class LoomResource extends CatalogResource<String>{
	protected List<String> resourceTypes = new ArrayList<String>();
	public static final String resource = "/loom";
	protected static final String resourceType= "resourcetype";
	protected static final String resourceID = "resourceid";
	protected static final String command = "command";
	
	public LoomResource() {
		super();
		resourceTypes.add(String.format("%s%s/%s",resource,"i5",getRootRef()));
		setHtmlbyTemplate(true);
	}

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
		getVariants().clear();
		getVariants().add(new Variant(MediaType.APPLICATION_JSON));
		getVariants().add(new Variant(MediaType.TEXT_JAVASCRIPT));
		getVariants().add(new Variant(MediaType.APPLICATION_RDF_XML));
		getVariants().add(new Variant(MediaType.TEXT_RDF_N3));
	}	
	
	@Override
	protected Iterator<String> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		
		return resourceTypes.iterator();
	}

}
