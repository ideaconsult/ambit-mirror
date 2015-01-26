package ambit2.rest.facet;

import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.Template;
import ambit2.db.facets.propertyvalue.PropertyDatasetFacet;
import ambit2.db.facets.propertyvalue.PropertyDatasetFacetQuery;
import ambit2.rest.OpenTox;

/**
 * Number of compounds given property value and a dataset
 * @author nina
 *
 */
public class CompoundsByPropertyValueInDatasetResource extends AmbitFacetResource<PropertyDatasetFacet<Property,SourceDataset>,PropertyDatasetFacetQuery> {
	public static final String resource = "/ncompound_value";
	@Override
	protected PropertyDatasetFacetQuery createQuery(Context context,
			Request request, Response response) throws ResourceException {
		
		ISourceDataset dataset = null;
		String uri = getParams().getFirstValue(OpenTox.params.dataset_uri.toString());
		if (uri!=null) {
			int datasetid = (Integer)OpenTox.URI.dataset.getId(uri,getRequest().getRootRef());
			if (datasetid<1) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"No dataset_uri!");
			
			dataset = new SourceDataset(uri);
			dataset.setID(datasetid);
		}
		
		Template profile = getProperty(getParams().getValuesArray(OpenTox.params.feature_uris.toString()),1);
		if ((profile==null) || (profile.size()==0))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"No feature_uris[] !");
		
		PropertyDatasetFacetQuery q = new PropertyDatasetFacetQuery(getResourceRef(getRequest()).toString());
		Iterator<Property> i = profile.getProperties(true);
		while (i.hasNext()) {
			q.setFieldname(i.next());
			break;
		}
		
		q.setValue((SourceDataset)dataset);
		if ((q.getValue()==null) && (q.getFieldname()==null)) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		return q;
	}

	@Override
	protected FacetHTMLReporter getHTMLReporter(Request request) {
		return new FacetHTMLReporter(request) {
			/**
		     * 
		     */
		    private static final long serialVersionUID = 3585043716711167668L;
			@Override
			public void headerBeforeTable(Writer w, IQueryRetrieval query) {
				try {
					PropertyDatasetFacetQuery q = (PropertyDatasetFacetQuery) query;
				w.write(String.format("<h5>Dataset <a href='%s/%s/%s?page=0&pagesize=100' target='_blank'>%s</a> &nbsp; Property <a href='%s/%s/%d'>%s</a></h5>", 
						getRequest().getRootRef(),
						OpenTox.URI.dataset.name(),
						q.getValue().getId(),
						q.getValue(),
						getRequest().getRootRef(),
						OpenTox.URI.feature.name(),
						q.getFieldname().getId(),
						q.getFieldname().getName()
					));
				} catch (Exception x) {
					
				}
				
			}
			@Override
			public void footer(Writer w, IQueryRetrieval query) {
				try {
					String chart = String.format("%s/chart/pie?%s",
							getRequest().getRootRef(),
							getRequest().getResourceRef().getQuery()
							);
					w.write(String.format("<tr><td colspan='2'><a href='%s&w=600&h=500' alt='%s' title='%s'><img src='%s&w=600&h=500' title='%s'></a></td></tr>",
							chart,
							query.toString(),
							query.toString(),
							chart,
							query.toString()
							));
				} catch (Exception x) {}
				
				super.footer(w, query);
			}
		};
	}
	
	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		map.put("facet_title","Compounds");
	}
}
