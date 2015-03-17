package ambit2.rest.substance.property;

import java.io.File;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.c.RepresentationConvertor;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.substance.SubstanceProperty;
import ambit2.db.substance.study.ReadSubstanceProperty;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.property.PropertyJSONReporter;
import ambit2.rest.property.PropertyRDFReporter;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.query.AmbitDBResource;

/**
 * Substance property - serialization as in /feature </pre>
 * 
 * @author nina
 * 
 */
public class SubstancePropertyResource extends AmbitDBResource<IQueryRetrieval<Property>, Property> {
    /**
     * Parameters, expected in http headers
     * 
     * @author nina
     * 
     */

    public final static String substanceproperty = "/property";
    public final static String substancepropertyid = "idproperty";
    public final static String topcategory = "topcategory";
    public final static String endpointcategory = "endpointcategory";
    public final static String endpoint = "endpoint";

    public SubstancePropertyResource() {
	super();
	setHtmlbyTemplate(true);
    }

    @Override
    public String getTemplateName() {
	return "feature.ftl";
    }

    @Override
    protected void doInit() throws ResourceException {
	super.doInit();
	customizeVariants(new MediaType[] { MediaType.TEXT_HTML, MediaType.TEXT_URI_LIST, ChemicalMediaType.TEXT_YAML,
		MediaType.APPLICATION_RDF_XML, MediaType.APPLICATION_RDF_TURTLE, MediaType.TEXT_RDF_N3,
		MediaType.TEXT_RDF_NTRIPLES, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JAVA_OBJECT });
    }

    @Override
    public RepresentationConvertor createConvertor(Variant variant) throws AmbitException, ResourceException {
	String filenamePrefix = getRequest().getResourceRef().getPath();
	if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
	    PropertyURIReporter r = new PropertyURIReporter(getRequest());
	    return new StringConvertor(r, MediaType.TEXT_URI_LIST, filenamePrefix);

	} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML)
		|| variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE)
		|| variant.getMediaType().equals(MediaType.TEXT_RDF_N3)
		|| variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)) {
	    return new RDFJenaConvertor<Property, IQueryRetrieval<Property>>(
		    new PropertyRDFReporter<IQueryRetrieval<Property>>(getRequest(), variant.getMediaType()),
		    variant.getMediaType(), filenamePrefix);
	} else {
	    PropertyURIReporter r = new PropertyURIReporter(getRequest());
	    return new OutputWriterConvertor(new PropertyJSONReporter(getRequest()), MediaType.APPLICATION_JSON);
	}

    }

    @Override
    protected IQueryRetrieval<Property> createQuery(Context context, Request request, Response response)
	    throws ResourceException {
	if (Method.GET.equals(request.getMethod())) {
	    Object topcategory = request.getAttributes().get(SubstancePropertyResource.topcategory);
	    Object endpointcategory = request.getAttributes().get(SubstancePropertyResource.endpointcategory);
	    Object endpoint = request.getAttributes().get(SubstancePropertyResource.endpoint);
	    Object key = request.getAttributes().get(substancepropertyid);
	    if (topcategory == null || endpointcategory == null || endpoint == null || key == null)
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

	    ILiteratureEntry ref = LiteratureEntry.getInstance(Reference.decode(endpoint.toString()), "DEFAULT");
	    SubstanceProperty p = new SubstanceProperty(Reference.decode(topcategory.toString()),
		    Reference.decode(endpointcategory.toString()), Reference.decode(endpoint.toString()), null, ref);
	    p.setIdentifier(Reference.decode(key.toString()));
	    ReadSubstanceProperty query = new ReadSubstanceProperty();
	    query.setFieldname(p);
	    return query;
	} else
	    return null;
    }

    @Override
    protected Representation put(Representation entity, Variant variant) throws ResourceException {
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    /**
     * POST allowed to feature collections only (creates new feature)
     */

    @Override
    protected QueryURIReporter<Property, IQueryRetrieval<Property>> getURIReporter(Request baseReference)
	    throws ResourceException {
	return new PropertyURIReporter(baseReference);
    }

    @Override
    protected Representation delete(Variant variant) throws ResourceException {
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);

    }

    @Override
    public String getConfigFile() {
	return "ambit2/rest/config/ambit2.pref";
    }

    // JSON , RDF , already as file
    @Override
    protected CallableProtectedTask<String> createCallable(Method method, File file, MediaType mediaType, Property item)
	    throws ResourceException {
	PropertyURIReporter reporter = new PropertyURIReporter(getRequest());
	return new CallableSubstancePropertyCreator(reporter, method, file, mediaType, item, getToken());
    }

    @Override
    protected CallableProtectedTask<String> createCallable(Method method, Form form, Property item)
	    throws ResourceException {
	PropertyURIReporter reporter = new PropertyURIReporter(getRequest());
	return new CallableSubstancePropertyCreator(reporter, method, form, item, getToken());
    }

    @Override
    protected String getObjectURI(Form queryForm) throws ResourceException {
	return null;
    }

    @Override
    protected boolean isAllowedMediaType(MediaType mediaType) throws ResourceException {
	return (MediaType.APPLICATION_JSON.equals(mediaType) || MediaType.APPLICATION_RDF_XML.equals(mediaType)
		|| MediaType.TEXT_RDF_N3.equals(mediaType) || MediaType.APPLICATION_WWW_FORM.equals(mediaType));

    }

    @Override
    protected IQueryRetrieval<Property> createUpdateQuery(Method method, Context context, Request request,
	    Response response) throws ResourceException {
	Object topcategory = request.getAttributes().get(SubstancePropertyResource.topcategory);
	if (Method.POST.equals(method)) {
	    return null;
	}
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

}
