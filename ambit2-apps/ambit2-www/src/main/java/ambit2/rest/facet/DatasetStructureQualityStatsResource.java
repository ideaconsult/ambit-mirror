package ambit2.rest.facet;

import java.util.Map;

import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.SourceDataset;
import ambit2.db.facets.qlabel.DatasetStructureQLabelFacet;
import ambit2.db.facets.qlabel.DatasetStructureQualityStats;
import ambit2.rest.OpenTox;

public class DatasetStructureQualityStatsResource extends AmbitFacetResource<DatasetStructureQLabelFacet,DatasetStructureQualityStats> {
	public static final String resource = "/label_strucs";
	protected Integer datasetID;
	protected Integer queryResultsID;
	
	@Override
	protected DatasetStructureQualityStats createQuery(Context context,
			Request request, Response response) throws ResourceException {
		DatasetStructureQualityStats q = new DatasetStructureQualityStats(getResourceRef(getRequest()).toString());
		Object id = request.getAttributes().get(OpenTox.URI.dataset.getKey());
		if (id != null) try {
			datasetID = Integer.parseInt(id.toString());
			SourceDataset dataset = new SourceDataset();
			dataset.setId(datasetID);
			q.setValue(dataset);
		} catch (Exception x) {
			/*
			if (id.toString().startsWith("R")) {
				queryResultsID = Integer.parseInt(id.toString().substring(1));
				StoredQuery dataset = new StoredQuery();
				dataset.setId(queryResultsID);
				q.setFieldname(dataset);
				return q;
			} else 
			*/
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Invalid dataset id");
		} 		
		return q;
	}
	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		map.put("facet_title","QA Labels statistics");
	}
}
