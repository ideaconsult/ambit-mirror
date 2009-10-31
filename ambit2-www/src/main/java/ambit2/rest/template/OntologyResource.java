package ambit2.rest.template;

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

import ambit2.base.data.Dictionary;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.DictionaryObjectQuery;
import ambit2.db.search.DictionaryQuery;
import ambit2.db.search.StringCondition;
import ambit2.db.search.property.QueryOntology;
import ambit2.db.update.dictionary.DeleteDictionary;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.StringConvertor;
import ambit2.rest.query.QueryResource;

/**
 * 
 * A resource wrapper fot {@link QueryOntology}
 * @author nina
 *
 */
public class OntologyResource<T extends Object> extends QueryResource<IQueryRetrieval<T>, T> {
	
	public static String resource = "/template";
	public static String resourceParent = "subject";
	public static String resourceKey = "object";
	public static String resourceID = String.format("%s/{%s}/{%s}",resource,resourceParent,resourceKey);
	public static String resourceTree = String.format("%s/{%s}/{%s}/view/{tree}",resource,resourceParent,resourceKey);
	protected boolean isRecursive = false;
	public boolean isRecursive() {
		return isRecursive;
	}
	public void setRecursive(boolean isRecursive) {
		this.isRecursive = isRecursive;
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_HTML,
				MediaType.TEXT_XML,
				MediaType.TEXT_URI_LIST,
				});

				
	}
	@Override
	public IProcessor<IQueryRetrieval<T>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		if (variant.getMediaType().equals(MediaType.TEXT_XML)) 
			return new DocumentConvertor(new OntologyDOMReporter(getRequest(),isRecursive()));
		else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
				return new StringConvertor(	new OntologyURIReporter(getRequest()) {
					@Override
					public void processItem(Object item) throws AmbitException  {
						super.processItem(item);
						try {
						output.write('\n');
						} catch (Exception x) {}
					}
				},MediaType.TEXT_URI_LIST);
				
		} else 
			return new OutputStreamConvertor(
					new OntologyHTMLReporter(getRequest(),!isRecursive())
					,MediaType.TEXT_HTML);
	}

	@Override
	protected IQueryRetrieval<T> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		try {
			Form form = getRequest().getResourceRef().getQueryAsForm();
			form.getFirstValue("search");
		} catch (Exception x) {
			
		}			
		try {
			Object view = request.getAttributes().get("tree");
			setRecursive("tree".equals(view));	
		} catch (Exception x) {
			setRecursive(false);
		}		
		Object key = request.getAttributes().get(resourceKey);
		QueryOntology q = new QueryOntology();
		q.setIncludeParent(false);
		if (key != null) 	
			q.setValue(key==null?null:new Dictionary(Reference.decode(key.toString()),null));
		else {
			key =  request.getAttributes().get(resourceParent);
			DictionaryQuery qd = new DictionaryObjectQuery();
			qd.setCondition(StringCondition.getInstance(StringCondition.C_EQ));
			qd.setValue(key==null?null:Reference.decode(key.toString(),null));
			
			return (IQueryRetrieval<T>) qd;
		}
		return (IQueryRetrieval<T>)q;
	}
	
	@Override
	protected Representation delete() throws ResourceException {
		try {
			deleteObject(getRequestEntity());
			return null;
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		}

	}	
	@Override
	protected T createObjectFromHeaders(Form requestHeaders,
			Representation entity) throws ResourceException {
		Object key = getRequest().getAttributes().get(resourceKey);
		if (key == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		Object parent = getRequest().getAttributes().get(resourceParent);
		if (parent == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		Dictionary dictionary = new Dictionary(parent.toString(),key.toString());
		return (T)dictionary;
		
	}
	
	protected ambit2.db.update.AbstractUpdate createDeleteObject(T entry) throws ResourceException {
		DeleteDictionary delete = new DeleteDictionary((Dictionary)entry);
		return delete;
	};
	@Override
	protected QueryURIReporter<T, IQueryRetrieval<T>> getURUReporter(
			Request baseReference) throws ResourceException {
		return (QueryURIReporter) new OntologyURIReporter(getRequest());
	}
}
