package ambit2.rest.facet;

import java.io.Writer;
import java.util.Map;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.SourceDataset;
import ambit2.db.facets.qlabel.DatasetChemicalsQualityStats;
import ambit2.db.facets.qlabel.DatasetConsensusLabelFacet;
import ambit2.rest.OpenTox;

public class DatasetChemicalsQualityStatsResource extends AmbitFacetResource<DatasetConsensusLabelFacet,DatasetChemicalsQualityStats> {
	public static final String resource = "/label_compounds";
	protected Integer datasetID;
	protected Integer queryResultsID;
	
	@Override
	protected DatasetChemicalsQualityStats createQuery(Context context,
			Request request, Response response) throws ResourceException {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		DatasetChemicalsQualityStats q = new DatasetChemicalsQualityStats(getResourceRef(getRequest()).toString());
		try {q.setSummary(Boolean.parseBoolean(form.getFirstValue("summary").toString()));} catch (Exception x) {q.setSummary(true);}
		
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
	protected FacetHTMLReporter getHTMLReporter(Request request) {
		return new FacetHTMLReporter(request) {
			/**
		     * 
		     */
		    private static final long serialVersionUID = 1576818116709958251L;

			@Override
			public void headerBeforeTable(Writer w, IQueryRetrieval query) {
				super.headerBeforeTable(w, query);
				try {
					w.write("<a href='?summary=true'>All</a>&nbsp;");
					w.write("<a href='?summary=false'>Per dataset</a>");
				} catch (Exception x) {
					
				}
			}
		};

	}
	
	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		map.put("facet_title","QA labels statistics");
	}

}