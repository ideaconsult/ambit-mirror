package ambit2.rest.similarity.space;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.simiparity.space.QMap;
import ambit2.db.simiparity.space.QueryQMap;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.query.QueryResource;

public class QMapResource extends QueryResource<IQueryRetrieval<QMap>, QMap> {
	public final static String qmap = "/qmap";
	public final static String qmapKey = "qmapid";
	
	public QMapResource() {
		super();
		setHtmlbyTemplate(true);
	}
	@Override
	public String getTemplateName() {
		return "qmap.ftl";
	}
	@Override
	public IProcessor<IQueryRetrieval<QMap>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		String filenamePrefix = getRequest().getResourceRef().getPath();
		if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
				PropertyURIReporter r = new PropertyURIReporter(getRequest(),getDocumentation());
				r.setDelimiter("\n");
				return new StringConvertor(r,MediaType.TEXT_URI_LIST,filenamePrefix);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
					return new OutputWriterConvertor(new QMapJSONReporter(getRequest()),MediaType.APPLICATION_JSON);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
			return new OutputWriterConvertor(new QMapJSONReporter<IQueryRetrieval<QMap>>(getRequest()),MediaType.APPLICATION_JSON);
		}			
		throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);		
	}	

	@Override
	protected IQueryRetrieval<QMap> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		QueryQMap query = new QueryQMap();
		return query;
	}



}
