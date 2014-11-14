package ambit2.rest.structure.tautomers;

import net.idea.modbcum.i.IQueryRetrieval;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.db.chemrelation.ReadStructureRelation;
import ambit2.db.reporters.CSVReporter;
import ambit2.rest.OpenTox;
import ambit2.rest.query.StructureQueryResource;

public class QueryTautomersResource <Q extends IQueryRetrieval<IStructureRecord>> extends StructureQueryResource<IQueryRetrieval<IStructureRecord>> {
	
	public QueryTautomersResource() {
		super();
		setHtmlbyTemplate(true);
	}
	@Override
	public String getTemplateName() {
		return "relation_compound.ftl";
	}
	
	@Override
	protected Q createQuery(Context context, Request request,Response response) throws ResourceException {
		try {
			setGroupProperties(context, request, response);
			setTemplate(createTemplate(context, request, response));
			
			Form form = getResourceRef(getRequest()).getQueryAsForm();
			try { includeMol = "true".equals(form.getFirstValue("mol")); } catch (Exception x) { includeMol=false;}

			Object datasetURI = OpenTox.params.dataset_uri.getFirstValue(form);
			if (datasetURI != null) {
				String dataset = datasetURI.toString();
				if (dataset.startsWith(request.getRootRef().toString())) {
					if (dataset.indexOf("/conformer/")>0) {
						Object[] ids = OpenTox.URI.conformer.getIds(dataset, getRequest().getRootRef());
						if ((ids!=null) && (ids.length>0) && (((Integer)ids[0])>0)) {
							ReadStructureRelation q =  new ReadStructureRelation(STRUCTURE_RELATION.HAS_TAUTOMER.name(),(Integer)ids[0]);
							return ((Q)q);
						}
					} else if (dataset.indexOf("/compound/")>0) {
						Object id = OpenTox.URI.compound.getId(dataset, getRequest().getRootRef());
						if (id!=null ) {
							ReadStructureRelation q =  new ReadStructureRelation(STRUCTURE_RELATION.HAS_TAUTOMER.name(),(Integer)id);
							return ((Q)q);
						}
					} else if (dataset.indexOf("/dataset/")>0) {
						/*
						Object id = OpenTox.URI.dataset.getId(dataset, getRequest().getRootRef());
						if (id!=null ) {
							ISourceDataset d = new SourceDataset(); d.setID((Integer)id);
							ReadDatasetRelation q =  new ReadDatasetRelation(STRUCTURE_RELATION.HAS_TAUTOMER.name(),d);
							return ((Q)q);
						}
						*/
					}
				}  
				throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED,String.format("Dataset '%s' not supported",datasetURI));
			} else
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Missing parameter dataset_uri");
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
		}		
	}
	
	@Override
	protected CSVReporter createCSVReporter() {
		CSVReporter csvReporter = super.createCSVReporter();
		csvReporter.setSimilarityColumn(Property.getInstance("metric",queryObject==null?"":queryObject.toString(),"http://ambit.sourceforge.net"));
		return csvReporter;
	}

	
}
