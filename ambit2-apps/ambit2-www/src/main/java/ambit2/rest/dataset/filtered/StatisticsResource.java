package ambit2.rest.dataset.filtered;

import java.util.HashMap;
import java.util.Map;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.facet.IFacet;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Template;

import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.model.QueryCountModels;
import ambit2.db.substance.QueryCountEndpoints;
import ambit2.db.substance.QueryCountInterpretationResults;
import ambit2.db.substance.QueryCountProtocolApplications;
import ambit2.db.substance.QueryCountSubstances;
import ambit2.db.update.dataset.QueryCount;
import ambit2.db.update.dataset.QueryCountChemicalInDataset;
import ambit2.db.update.dataset.QueryCountDataset;
import ambit2.db.update.dataset.QueryCountDatasetIntersection;
import ambit2.db.update.dataset.QueryCountProperties;
import ambit2.db.update.dataset.QueryCountValues;
import ambit2.rest.OpenTox;
import ambit2.rest.dataset.DatasetStructuresResource;
import ambit2.rest.facet.AmbitFacetResource;


public class StatisticsResource<FACET extends IFacet<String>,Q extends QueryCount<FACET>> extends AmbitFacetResource<FACET,Q>  {
	public static String resource = "/stats";
	public static String resourceKey = "mode";
	protected StatsMode mode;
	public enum StatsMode {
		structures {
			@Override
			public String getURL() {
				return "/compound";
			}
		},
		chemicals_in_dataset,
		dataset_intersection,
		properties {
			@Override
			public String getURL() {
				return "/feature";
			}
		},
		values,
		dataset {
			@Override
			public String getURL() {
				return "/dataset";
			}
		},
		models {
			@Override
			public String getURL() {
				return "/model";
			}			
		},
		substances {
			@Override
			public String getURL() {
				return "/substance";
			}
		},
		experiment_endpoints {
			@Override
			public String getURL() {
				return "/substance";
			}
		},
		protocol_applications {
			@Override
			public String getURL() {
				return "/substance";
			}
			public String getTemplateName() {
				return "facets/protocol_applications.ftl";
			}			
		},
		interpretation_result {
			@Override
			public String getURL() {
				return "/substance";
			}
	
		}
		;
		
		public String getURL() {
			return null;
		}
		public String getTemplateName() {
			return "facet_statistics.ftl";
		}
	}
	public StatisticsResource() {
		super();
		setHtmlbyTemplate(true);
	}
	
	@Override
	public String getTemplateName() {
		return mode.getTemplateName();
	}
	
	@Override
	public IProcessor<Q, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		return super.createConvertor(variant); 	
	}
	
	protected StatsMode getSearchMode() {
		try {
			return StatsMode.valueOf(getRequest().getAttributes().get(resourceKey).toString());
		} catch (Exception x) {
			return StatsMode.dataset;
		}
	}

	@Override
	protected Q createQuery(Context context,
			Request request, Response response) throws ResourceException {
		
		String[] datasetsURI =  getParams().getValuesArray(OpenTox.params.dataset_uri.toString());
		Template t = new Template(String.format("%s%s/{%s}",getRequest().getRootRef(),DatasetStructuresResource.dataset,DatasetStructuresResource.datasetKey));
		setStatus(Status.SUCCESS_OK);
		mode = getSearchMode();
		switch (mode) {
		case dataset_intersection: {
			QueryCountDatasetIntersection q = null;
			for (int i=0; i < datasetsURI.length;i++ ) {
				if (q==null) q = new QueryCountDatasetIntersection(null);
				String datasetURI = datasetsURI[i];
				Map<String, Object> vars = new HashMap<String, Object>();
				t.parse(datasetURI, vars);
				if (i==0) q.setFieldname(vars.get(DatasetStructuresResource.datasetKey).toString());
				else q.setValue(vars.get(DatasetStructuresResource.datasetKey).toString());
			}	
			if (q==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Missing dataset_uri parameters!");
			return (Q)q;
		}
		case structures: {
			return (Q)new QueryCount<FACET>(mode.getURL());
		}
		case properties: {
			return (Q)new QueryCountProperties(mode.getURL());
		}		
		case values: {
			return (Q) new QueryCountValues(mode.getURL());
		}		
		case dataset: {
			return (Q)new QueryCountDataset(mode.getURL());
		}	
		case models: {
			return (Q)new QueryCountModels(mode.getURL());
		}
		case substances: {
			return (Q)new QueryCountSubstances(mode.getURL());
		}		
		case experiment_endpoints: {
			QueryCountEndpoints q = new QueryCountEndpoints(mode.getURL());
			q.setFieldname(getParams().getFirstValue("top"));
			q.setValue(getParams().getFirstValue("category"));
			q.setEndpoint(getParams().getFirstValue("search"));
			return (Q)q;
		}		
		case protocol_applications: {
			QueryCountProtocolApplications q = new QueryCountProtocolApplications(mode.getURL());
			q.setPageSize(1000);
			//?bundle_uri=
			//Object bundleURI = OpenTox.params.bundle_uri.getFirstValue(getParams());
			Object bundleURI = getParams().getFirstValue("filterbybundle");
			if (bundleURI!=null) {
				Integer idbundle = getIdBundle(bundleURI, request);
				q.setBundle(new SubstanceEndpointsBundle(idbundle));
			}	
			q.setFieldname(getParams().getFirstValue("topcategory"));
			q.setValue(getParams().getFirstValue("category"));
			return (Q)q;
		}
		case interpretation_result: {
			QueryCountInterpretationResults q = new QueryCountInterpretationResults(mode.getURL());
			q.setFieldname(getParams().getFirstValue("top"));
			q.setValue(getParams().getFirstValue("category"));
			q.setInterpretation_result(getParams().getFirstValue("search"));
			return (Q)q;
		}		
		case chemicals_in_dataset: {
			QueryCountChemicalInDataset q = null;
			for (int i=0; i < datasetsURI.length;i++ ) {
				if (q==null) q = new QueryCountChemicalInDataset(mode.getURL());
				String datasetURI = datasetsURI[i];
				Map<String, Object> vars = new HashMap<String, Object>();
				t.parse(datasetURI, vars);
				q.setFieldname(vars.get(DatasetStructuresResource.datasetKey).toString());
			}	
			if (q==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Missing dataset_uri parameters!");
			return (Q)q;
		}
		default: {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		}

	}

	
	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		map.put("facet_title","Statistics");/*
		map.put("facet_tooltip","");
		map.put("facet_group","");
		map.put("facet_subgroup","");
		map.put("facet_count","count");
		*/
	}
}

