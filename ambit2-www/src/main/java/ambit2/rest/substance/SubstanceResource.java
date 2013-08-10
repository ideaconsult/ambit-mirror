package ambit2.rest.substance;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.StructureRecord;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.substance.ReadSubstance;
import ambit2.rest.OpenTox;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.StringConvertor;
import ambit2.rest.query.QueryResource;

/**
 * Substances (in the sense of IUCLID5) 
 * @author nina
 *
 * @param <Q>
 */
public class SubstanceResource<Q extends IQueryRetrieval<SubstanceRecord>> extends QueryResource<Q,SubstanceRecord> {
	public final static String substance = OpenTox.URI.substance.getURI();
	public final static String idsubstance = OpenTox.URI.substance.getKey();
	public final static String substanceID = OpenTox.URI.substance.getResourceID();
	
	public SubstanceResource() {
		super();
		setHtmlbyTemplate(true);
	}
	
	@Override
	public String getTemplateName() {
		return "substance.ftl";
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_HTML,
				MediaType.TEXT_URI_LIST,
				MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JAVA_OBJECT,
				MediaType.APPLICATION_JAVASCRIPT,

				});
				
	}
	@Override
	public IProcessor<Q, Representation> createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		/* workaround for clients not being able to set accept headers */
		Form acceptform = getResourceRef(getRequest()).getQueryAsForm();
		String media = acceptform.getFirstValue("accept-header");
		if (media != null) variant.setMediaType(new MediaType(media));

		String filenamePrefix = getRequest().getResourceRef().getPath();
		if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			QueryURIReporter r = (QueryURIReporter)getURIReporter();
			r.setDelimiter("\n");
			return new StringConvertor(
					r,MediaType.TEXT_URI_LIST,filenamePrefix);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
			String jsonpcallback = getParams().getFirstValue("jsonp");
			if (jsonpcallback==null) jsonpcallback = getParams().getFirstValue("callback");
			SubstanceJSONReporter cmpreporter = new SubstanceJSONReporter(getRequest(),getDocumentation(),jsonpcallback);
			return new OutputWriterConvertor<SubstanceRecord, Q>(
					cmpreporter,
					MediaType.APPLICATION_JAVASCRIPT,filenamePrefix);
		} else { //json by default
		//else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
			SubstanceJSONReporter cmpreporter = new SubstanceJSONReporter(getRequest(),getDocumentation(),null);
			return new OutputWriterConvertor<SubstanceRecord, Q>(
					cmpreporter,
					MediaType.APPLICATION_JSON,filenamePrefix);
		}
	}	

	@Override
	protected Q createQuery(Context context, Request request, Response response) throws ResourceException {
		Object key = request.getAttributes().get(idsubstance);
		if (key==null) {
			Form form = getRequest().getResourceRef().getQueryAsForm();
			Object cmpURI = OpenTox.params.compound_uri.getFirstValue(form);
			if (cmpURI!=null) {
				Object id = OpenTox.URI.compound.getId(cmpURI.toString(), request.getRootRef());
				if (id!=null && (id instanceof Integer)) {
					CompositionRelation composition = new CompositionRelation(null,new StructureRecord((Integer)id,-1,null,null),null);
					return (Q)new ReadSubstance(composition);		
				}
			}
			return (Q)new ReadSubstance();
		} else try {
			return (Q)new ReadSubstance(new SubstanceRecord(Integer.parseInt(key.toString())));
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
	}

	protected QueryURIReporter getURIReporter() {
		return new SubstanceURIReporter<Q>(getRequest(),getDocumentation());
	}
}
