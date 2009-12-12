package ambit2.rest.reference;

import java.io.Writer;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.update.AbstractUpdate;
import ambit2.db.update.reference.CreateReference;
import ambit2.db.update.reference.ReadReference;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.propertyvalue.PropertyValueReporter;
import ambit2.rest.query.QueryResource;

/**
 * Reference resource, according to  http://opentox.org/development/wiki/feature. 
 * <br>
 * Supported operations:
 * <ul>
 * <li>GET 	 /reference/{id} 	 returns returns text/uri-list or text/xml or text/html
 * <li>POST 	 /reference (as specified)
 * <li>PUT not yet supported
 * </ul>
 * @author nina
 *
 * @param <Q>
 */
public class ReferenceResource	extends QueryResource<ReadReference,ILiteratureEntry> {
	/**
	 * Parameters, expected in http headers
	 * @author nina
	 *
	 */
	public enum headers  {
			name {
				@Override
				public boolean isMandatory() {
					return true;
				}
			},
			algorithm_id,
			parameters,
			experimental_protocol; 
			public boolean isMandatory() {
				return false;
			}
	};
	public final static String reference = "/reference";
	public final static String idreference = "idreference";
	//public final static String referenceID = String.format("%s/{%s}",reference,idreference);
	

	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
			
			return new StringConvertor(new PropertyValueReporter());
			} else if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
				return new DocumentConvertor(new ReferenceDOMReporter(getRequest()));
			} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
				return new StringConvertor(	new ReferenceURIReporter<IQueryRetrieval<ILiteratureEntry>>(getRequest()) {
					@Override
					public void processItem(ILiteratureEntry dataset) throws AmbitException  {
						super.processItem(dataset);
						try {
							output.write('\n');
						} catch (Exception x) {}
					}
				},MediaType.TEXT_URI_LIST);
			} else 
				return new OutputWriterConvertor(
						new ReferenceHTMLReporter(getRequest(),queryObject.getValue()==null),
						MediaType.TEXT_HTML);
	}

	@Override
	protected ReadReference createQuery(Context context, Request request, Response response)
			throws ResourceException {
		Object idref = request.getAttributes().get(idreference);
		try {
			if (idref==null) {
				/*
				Form form = request.getResourceRef().getQueryAsForm();
				Object key = form.getFirstValue(QueryResource.search_param);
				if (key != null) {
					RetrieveFieldNamesByAlias q = new RetrieveFieldNamesByAlias(Reference.decode(key.toString()));
					q.setCondition(StringCondition.getInstance(StringCondition.C_SOUNDSLIKE));
					return q;
				} else 
				*/
					return new ReadReference();
			}			
			else return new ReadReference(new Integer(Reference.decode(idref.toString())));
		} catch (Exception x) {
			throw new ResourceException(
					Status.CLIENT_ERROR_BAD_REQUEST,
					String.format("Invalid resource id %d",idref),
					x
					);
		}
	} 
	@Override
	protected Representation post(Representation entity)
			throws ResourceException {
		if (getRequest().getAttributes().get(idreference)==null)
			createNewObject(entity);
		else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		return getResponse().getEntity();
	}
	@Override
	protected QueryURIReporter<ILiteratureEntry, ReadReference> getURUReporter(
			Request baseReference) throws ResourceException {
		return new ReferenceURIReporter<ReadReference>(baseReference);
	}
	/**
<pre<
Description  	Method  	URI  	Parameters  	Result  	Status codes
create a new reference  	 POST  	 /reference  	 name:String, algorithm_id:String, parameters:String, experimental_protocol:String  	 URI of new reference  	200,400,404,503
</pre>
	 */
	protected LiteratureEntry createObjectFromHeaders(Form requestHeaders, Representation entity) throws ResourceException {
		String name = getParameter(requestHeaders,headers.name.toString(),headers.name.isMandatory());
		String url = getParameter(requestHeaders,headers.algorithm_id.toString(),headers.algorithm_id.isMandatory());  	
		return LiteratureEntry.getInstance(name, url);
	}
	@Override
	protected AbstractUpdate createUpdateObject(
			ILiteratureEntry entry) throws ResourceException {
		return new CreateReference(entry);
	}

	/*
	if (MediaType.TEXT_XML.equals(entity.getMediaType())) {
		ReferenceDOMParser parser = new ReferenceDOMParser() {
			@Override
			public void processItem(LiteratureEntry item)
					throws AmbitException {
				System.out.println(item);
				
			}
		};
		try {
			parser.parse(entity.getReader());
		} catch (Exception x) {
			throw new ResourceException(new Status(Status.CLIENT_ERROR_BAD_REQUEST,x));
		}
	}
	*/
}
