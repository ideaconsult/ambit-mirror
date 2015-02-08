package ambit2.rest.property.annotations;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.db.convertors.OutputWriterConvertor;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.db.update.propertyannotations.ReadPropertyAnnotations;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.error.InvalidResourceIDException;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.query.QueryResource;

public class PropertyAnnotationResource extends QueryResource<IQueryRetrieval<PropertyAnnotation>, PropertyAnnotation> {
    public final static String annotation = "/annotation";

    public PropertyAnnotationResource() {
	super();
	setHtmlbyTemplate(true);
    }

    @Override
    public String getTemplateName() {
	return "feature_annotation.ftl";
    }

    @Override
    public IProcessor<IQueryRetrieval<PropertyAnnotation>, Representation> createConvertor(Variant variant)
	    throws AmbitException, ResourceException {

	if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
	    PropertyAnnotationURIReporter r = new PropertyAnnotationURIReporter(getRequest());
	    return new StringConvertor(r, MediaType.TEXT_URI_LIST);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
	    return new OutputWriterConvertor(new PropertyAnnotationJSONReporter(getRequest()),
		    MediaType.APPLICATION_JSON);
	} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML)
		|| variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE)
		|| variant.getMediaType().equals(MediaType.TEXT_RDF_N3)
		|| variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)) {
	    return new RDFJenaConvertor<PropertyAnnotation, IQueryRetrieval<PropertyAnnotation>>(
		    new PropertyAnnotationRDFReporter(getRequest(), variant.getMediaType()), variant.getMediaType());
	} else
	    return new OutputWriterConvertor(new PropertyAnnotationJSONReporter(getRequest()),
		    MediaType.APPLICATION_JSON);
    }

    @Override
    protected IQueryRetrieval<PropertyAnnotation> createQuery(Context context, Request request, Response response)
	    throws ResourceException {
	Object o = request.getAttributes().get(PropertyResource.idfeaturedef);
	if (o == null)
	    throw new InvalidResourceIDException(request.getResourceRef());
	try {
	    int id = Integer.parseInt(o.toString());
	    ReadPropertyAnnotations q = new ReadPropertyAnnotations();
	    Property p = Property.getInstance("", "");
	    p.setId(id);
	    q.setFieldname(p);
	    return q;
	} catch (NumberFormatException x) {
	    throw new InvalidResourceIDException(o);
	} catch (Exception x) {
	    throw new InvalidResourceIDException(x);
	} finally {
	}

    }

}
