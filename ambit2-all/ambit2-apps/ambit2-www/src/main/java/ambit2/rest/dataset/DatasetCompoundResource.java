package ambit2.rest.dataset;

import net.idea.modbcum.i.IQueryRetrieval;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import ambit2.base.data.AbstractDataset;
import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.QueryCombined.COMBINE;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.rest.DBConnection;
import ambit2.rest.OpenTox;
import ambit2.rest.error.InvalidResourceIDException;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.structure.CompoundResource;

/**
 * Compound from a dataset; if id is valid, but the compound doesn't belong to the dataset , it will not be a hit
 * 
 * @author nina
 *
 */
public class DatasetCompoundResource extends CompoundResource {
	//public final static String resource = String.format("%s%s",DatasetsResource.datasetID,CompoundResource.compoundID);
	protected String prefix = "";
	
	public DatasetCompoundResource() {
		super();
	}
	
	@Override
	protected void doInit() throws ResourceException {
		DBConnection dbc = new DBConnection(getContext());
		configureDatasetMembersPrefixOption(dbc.dataset_prefixed_compound_uri());
		super.doInit();
	}
	@Override
	public String getCompoundInDatasetPrefix() {
		return dataset_prefixed_compound_uri?prefix:"";
		/*
		if (dataset_prefixed_compound_uri)
			return
				datasetID!=null?String.format("%s/%d", dataset,datasetID):
					queryResultsID!=null?String.format("%s/R%d", dataset,queryResultsID):"";
			else return "";		
			*/
	}
	
	@Override
	protected String getDefaultTemplateURI(Context context, Request request,Response response) {
		Object id = request.getAttributes().get(DatasetResource.datasetKey);
		if (id != null)
			//on riap://  the request.getRootRef() is null and all URI generation is consequently broken
			//return String.format("riap://application/dataset/%s%s",id,PropertyResource.featuredef);
			return String.format("%s%s/%s%s",
					getRequest().getRootRef(),OpenTox.URI.dataset.getURI(),id,PropertyResource.featuredef);
		else 
			return super.getDefaultTemplateURI(context,request,response);
			
	}
	@Override
	protected IQueryRetrieval<IStructureRecord> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		setTemplate(createTemplate(context, request, response));
		IQueryRetrieval<IStructureRecord> q = super.createQuery(context, request, response);
		if (q == null) return null;
		
		
		DatasetStructuresResource ds = new DatasetStructuresResource();
		ds.init(context, request, response);
		ds.configureDatasetMembersPrefixOption(dataset_prefixed_compound_uri);
		ds.setIncludeMol(includeMol);
		
		IQueryRetrieval<IStructureRecord> datasetQuery = ds.createQuery(context, request, response);
		if (datasetQuery == null) return null;
		
		prefix = ds.getCompoundInDatasetPrefix();
		
		if (q instanceof QueryStructureByID) {
			Object id = request.getAttributes().get(DatasetResource.datasetKey);
			if (id==null) throw new InvalidResourceIDException("No dataset");
			ISourceDataset dataset = getDataset(id.toString());
			if (dataset==null) throw new InvalidResourceIDException("No dataset");
			((QueryStructureByID)q).setFieldname(dataset);
			return q;
		}
		//else some other query

		QueryCombinedStructure combinedQuery = new QueryCombinedStructure();
		combinedQuery.add(q);
		combinedQuery.add(datasetQuery);
		combinedQuery.setCombine_queries(COMBINE.AND);
		return combinedQuery;
	}
	
	protected ISourceDataset getDataset(String key) throws ResourceException {
		try {
			int id = Integer.parseInt(Reference.decode(key.toString()));
			ISourceDataset dataset = new SourceDataset();
			dataset.setID(id);
			return dataset;
		} catch (NumberFormatException x) {
			if (key.startsWith(DatasetStructuresResource.QR_PREFIX)) {
				key = key.substring(DatasetStructuresResource.QR_PREFIX.length());
				try {
					int queryResultsID = Integer.parseInt(key.toString());
					ISourceDataset dataset = new AbstractDataset();
					dataset.setID(queryResultsID);
					return dataset;
				} catch (NumberFormatException xx) {
					throw new InvalidResourceIDException(key);
				}
			}
		} 
		throw new InvalidResourceIDException(key);
	}
}
