package ambit2.rest.bookmark;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Bookmark;
import ambit2.db.update.bookmark.ReadAnnotation;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.query.QueryResource;

public class OntoBucketResource extends QueryResource<ReadAnnotation, Bookmark> {
    @Override
    public String getTemplateName() {
        return "ontobucket.ftl";
    }
    public OntoBucketResource() {
	super();
	setHtmlbyTemplate(true);
    }
    @Override
    public IProcessor<ReadAnnotation, Representation> createConvertor(Variant variant) throws AmbitException,
	    ResourceException {
	String filenamePrefix = getRequest().getResourceRef().getPath();
	if (variant.getMediaType().equals(MediaType.TEXT_CSV)) {
	    return new StringConvertor(new BookmarkCSVReporter<IQueryRetrieval<Bookmark>>(getRequest()),
		    MediaType.TEXT_CSV, filenamePrefix);
	} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
	    return new StringConvertor(new BookmarkURIReporter<IQueryRetrieval<Bookmark>>(getRequest()),
		    MediaType.TEXT_URI_LIST, filenamePrefix);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
	    return new StringConvertor(new BookmarkJSONReporter<IQueryRetrieval<Bookmark>>(getRequest()),
		    MediaType.APPLICATION_JSON, filenamePrefix);

	} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML)
		|| variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE)
		|| variant.getMediaType().equals(MediaType.TEXT_RDF_N3)
		|| variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)) {

	    return new RDFJenaConvertor(
		    new BookmarkRDFReporter<IQueryRetrieval<Bookmark>>(getRequest(), variant.getMediaType()),
		    variant.getMediaType(), filenamePrefix);

	} else
	    return new StringConvertor(new BookmarkJSONReporter<IQueryRetrieval<Bookmark>>(getRequest()),
		    MediaType.APPLICATION_JSON, null);
    }

    private enum _type {
	all {
	  @Override
	protected String getParam() {
	    return null;
	}  
	},
	label,
	subclass,
	endpoint,
	hash,
	protocol;
	protected String getParam() {
	    return name();
	}
    }
    @Override
    protected ReadAnnotation createQuery(Context context, Request request, Response response) throws ResourceException {

	Form form = getResourceRef(request).getQueryAsForm();
	Object key = form.getFirstValue(QueryResource.search_param);
	
	_type type = _type.label;
	if (type!=null)
	try {
	    type = _type.valueOf(form.getFirstValue("type").toString());
	} catch (Exception x) {
	}
	if (key != null) {
	    ReadAnnotation q = new ReadAnnotation(key.toString());
	    q.setPageSize(100);
	    q.setValue(type.getParam());
	    setPaging(form, queryObject);
	    return q;
	} else
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

    }

    @Override
    protected Representation post(Representation entity, Variant variant) throws ResourceException {
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    @Override
    protected Representation put(Representation entity, Variant variant) throws ResourceException {
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    @Override
    protected Representation delete() throws ResourceException {
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }
}
