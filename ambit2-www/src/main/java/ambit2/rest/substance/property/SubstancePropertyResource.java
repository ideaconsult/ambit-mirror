package ambit2.rest.substance.property;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.substance.study.ReadSubstanceProperty;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.property.PropertyJSONReporter;
import ambit2.rest.property.PropertyRDFReporter;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.query.QueryResource;

/**
Substance property - serialization as in /feature 
</pre>

 * @author nina
 *
 */
public class SubstancePropertyResource extends QueryResource<IQueryRetrieval<Property>, Property> {
	/**
	 * Parameters, expected in http headers
	 * @author nina
	 *
	 */

	public final static String substanceproperty = "/property";
	public final static String substancepropertyid = "idproperty";
	
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
		customizeVariants(new MediaType[] {
				MediaType.TEXT_HTML,
				MediaType.TEXT_URI_LIST,
				ChemicalMediaType.TEXT_YAML,
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE,
				MediaType.TEXT_RDF_N3,
				MediaType.TEXT_RDF_NTRIPLES,
				MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JAVA_OBJECT
				});	
	}
	
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		String filenamePrefix = getRequest().getResourceRef().getPath();
		if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
				PropertyURIReporter r = new PropertyURIReporter(getRequest(),getDocumentation());
				r.setDelimiter("\n");
				return new StringConvertor(r,MediaType.TEXT_URI_LIST,filenamePrefix);
						
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
				variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES) 
				) {
			return new RDFJenaConvertor<Property, IQueryRetrieval<Property>>(
					new PropertyRDFReporter<IQueryRetrieval<Property>>(getRequest(),variant.getMediaType(),getDocumentation())
					,variant.getMediaType(),filenamePrefix);		
		} else {
			PropertyURIReporter r = new PropertyURIReporter(getRequest(),getDocumentation());
			r.setDelimiter("\n");
			return new OutputWriterConvertor(new PropertyJSONReporter(getRequest()),MediaType.APPLICATION_JSON);
		}	
			
	}

	@Override
	protected IQueryRetrieval<Property> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		Object key = request.getAttributes().get(substancepropertyid);
		if (key==null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		ReadSubstanceProperty query = new ReadSubstanceProperty();
		query.setFieldname(key.toString());
		return query;
	}
	
	@Override
	protected Representation put(Representation entity, Variant variant)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	/**
	 * POST allowed to feature collections only (creates new feature)
	 */
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	
	
	
	
	@Override
	protected QueryURIReporter<Property, IQueryRetrieval<Property>> getURUReporter(
			Request baseReference) throws ResourceException {
		return new PropertyURIReporter(baseReference,getDocumentation());
	}
	

	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
		
	}	

}
