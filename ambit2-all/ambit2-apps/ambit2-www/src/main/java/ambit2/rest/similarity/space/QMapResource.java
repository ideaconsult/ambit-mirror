package ambit2.rest.similarity.space;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.db.convertors.OutputWriterConvertor;

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

import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.db.simiparity.space.QMap;
import ambit2.db.simiparity.space.QueryQMap;
import ambit2.rest.OpenTox;
import ambit2.rest.StringConvertor;
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
				QMapURIReporter r = new QMapURIReporter(getRequest(),getDocumentation());
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
		Object qid = request.getAttributes().get(QMapResource.qmapKey);
		if (qid != null)  try {
			query.setValue(new QMap(Integer.parseInt(qid.toString())));
			return query;
		} catch (Exception x) {}
		
		//filter by dataset
		Form form = request.getResourceRef().getQueryAsForm();
		Object datasetURI = OpenTox.params.dataset_uri.getFirstValue(form);
		if (datasetURI!=null) try {
			int id = (Integer)OpenTox.URI.dataset.getId(Reference.decode(datasetURI.toString().trim()),request.getRootRef());
			QMap map = new QMap();
			map.setDataset(new SourceDataset());
			map.getDataset().setID(id);
			query.setValue(map);
		} catch (Exception x) { /* ignore the filter	*/}

		Object propertyURI = OpenTox.params.feature_uris.getFirstValue(form);
		if (propertyURI!=null) try {
			int id = (Integer)OpenTox.URI.feature.getId(Reference.decode(propertyURI.toString().trim()),request.getRootRef());
			if (query.getValue()==null) query.setValue(new QMap());
			Property p = new Property(null);
			p.setId(id);
			query.getValue().setProperty(p);
		} catch (Exception x) { /* ignore the filter	*/}

		
		return query;
	}



}
