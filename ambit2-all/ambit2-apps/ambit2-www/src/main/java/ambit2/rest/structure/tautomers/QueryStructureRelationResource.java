package ambit2.rest.structure.tautomers;

import java.awt.Dimension;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.r.QueryAbstractReporter;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.base.relation.StructureRelation;
import ambit2.db.chemrelation.ReadDatasetRelation;
import ambit2.db.reporters.CSVReporter;
import ambit2.db.search.StoredQuery;
import ambit2.rest.OpenTox;
import ambit2.rest.dataset.DatasetStructuresResource;
import ambit2.rest.error.InvalidResourceIDException;
import ambit2.rest.similarity.AbstractPairwiseResource;
import ambit2.rest.similarity.StructureRelationJSONReporter;

public class QueryStructureRelationResource<Q extends IQueryRetrieval<StructureRelation>> extends AbstractPairwiseResource<StructureRelation,Q> { 
	public final static String resource = "/relation";
	public final static String relationKey =  "relation";
	public final static String resourceID =  String.format("/{%s}",relationKey);
	protected STRUCTURE_RELATION relation = STRUCTURE_RELATION.HAS_TAUTOMER;
	
	public QueryStructureRelationResource() {
		super();
		setHtmlbyTemplate(true);
	}
	
	@Override
	protected QueryAbstractReporter createHTMLReporter(Dimension d) {
		return null;
	}
	@Override
	protected QueryAbstractReporter createJSONReporter(String callback) {
		return new StructureRelationJSONReporter(getRequest());
	}
	@Override
	public String getTemplateName() {
		
		return "relation.ftl";
	}
	
	@Override
	protected CSVReporter createCSVReporter() {
		CSVReporter csvReporter = new CSVReporter(getRequest().getRootRef().toString(),null,null,
				String.format("%s%s",getRequest().getRootRef(),getCompoundInDatasetPrefix())
				);
		csvReporter.setSimilarityColumn(Property.getInstance("metric",queryObject==null?"":queryObject.toString(),"http://ambit.sourceforge.net"));
		return csvReporter;
	}
	
	@Override
	protected Q createQuery(Context context, Request request, Response response)
			throws ResourceException {

		Form form = getResourceRef(getRequest()).getQueryAsForm();
		setTemplate(createTemplate(request.getResourceRef().getQueryAsForm()));
		Object rid = request.getAttributes().get(relationKey);
		
		if (rid!=null) try {
			relation = STRUCTURE_RELATION.valueOf(rid.toString().toUpperCase());
		} catch (Exception x) {}
		
		Object datasetURI = OpenTox.params.dataset_uri.getFirstValue(form);
		if (datasetURI != null) {
			String dataset = datasetURI.toString();
			if (dataset.startsWith(request.getRootRef().toString())) {
				if (dataset.indexOf("/dataset/")>0) {
					Object id = OpenTox.URI.dataset.getId(dataset, getRequest().getRootRef());
					if (id==null) ; 
					else if (id instanceof Integer) {
						return  getQueryById((Integer)id);
					} else  {
						return  getQueryById(id.toString());
						
					}	
					
				}
			}  
			throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED,String.format("Dataset '%s' not supported",datasetURI));
		} else
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Missing parameter dataset_uri");
		/*
		if (id != null)  try {
			
			id = Reference.decode(id.toString());
			return getQueryById(new Integer(id.toString()));

		} catch (NumberFormatException x) {
			return getQueryById(id.toString());
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
		}
		return null;
		*/
	}
	
	@Override
	protected Q getQueryById(Integer key) throws ResourceException {
		Q query = null;
		datasetID = key;
		ReadDatasetRelation q = new ReadDatasetRelation();
		q.setFieldname(relation.name());
		SourceDataset dataset = new SourceDataset();
		dataset.setID(key);
		q.setValue(dataset);
		Form form = getResourceRef(getRequest()).getQueryAsForm();
		setPaging(form, q);
		return (Q)q;
	}
	
	@Override
	protected Q getQueryById(String key) throws ResourceException {
		if (key.startsWith(DatasetStructuresResource.QR_PREFIX)) {
			key = key.substring(DatasetStructuresResource.QR_PREFIX.length());
			try {
				queryResultsID = Integer.parseInt(key.toString());
			} catch (NumberFormatException x) {
				throw new InvalidResourceIDException(key);
			}
			ReadDatasetRelation q = new ReadDatasetRelation();
			q.setFieldname(relation.name());
			StoredQuery dataset = new StoredQuery();
			dataset.setID(queryResultsID);
			q.setValue(dataset);
			Form form = getResourceRef(getRequest()).getQueryAsForm();
			setPaging(form, q);
			return (Q)q;
		} //else return getDatasetByName(key);
		throw new InvalidResourceIDException(key);
	}
}
