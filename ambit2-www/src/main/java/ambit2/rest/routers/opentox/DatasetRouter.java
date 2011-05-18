package ambit2.rest.routers.opentox;

import org.restlet.Context;
import org.restlet.routing.Router;

import ambit2.rest.dataEntry.DataEntryResource;
import ambit2.rest.dataset.DatasetCompoundResource;
import ambit2.rest.dataset.DatasetResource;
import ambit2.rest.dataset.DatasetStructuresResource;
import ambit2.rest.dataset.MetadatasetResource;
import ambit2.rest.property.PropertiesByDatasetResource;
import ambit2.rest.query.QLabelQueryResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.query.SmartsQueryResource;
import ambit2.rest.routers.MyRouter;
import ambit2.rest.routers.misc.DataEntryRouter;
import ambit2.rest.similarity.SimilarityResource;
import ambit2.rest.structure.CompoundResource;

/**
 *  OpenTox dataset   /dataset/{id}
 */
public class DatasetRouter extends MyRouter {
	
	
	public DatasetRouter(Context context,DataEntryRouter tupleRouter, Router smartsRouter, Router similarityRouter) {
		super(context);
		init(tupleRouter, smartsRouter, similarityRouter);
	}
	
	/**
	 *  OpenTox dataset
	 * @return
	 */
	protected void init(DataEntryRouter tupleRouter, Router smartsRouter, Router similarityRouter) {
		attachDefault(DatasetResource.class);
		//this is for backward compatibility

		attach(PropertiesByDatasetResource.featuredef,PropertiesByDatasetResource.class);
		attach(String.format("%s/{%s}",PropertiesByDatasetResource.featuredef,PropertiesByDatasetResource.idfeaturedef),PropertiesByDatasetResource.class);

		/**
		 *  Metadata  /metadata
		 */
		attach(MetadatasetResource.metadata, MetadatasetResource.class);

		/**
		 *  Features of this dataset   /feature
		 *  TODO support for  /features
		 */
		attach(PropertiesByDatasetResource.featuredef,PropertiesByDatasetResource.class);	


		attach(CompoundResource.compoundID, DatasetCompoundResource.class);
		attach(CompoundResource.compound, DatasetStructuresResource.class);
		
		/**
		 * Data entries /dataEntry
		 */
		attach(DataEntryResource.resourceTag,tupleRouter);
	
		
		/**
		 * Smarts/similarity within a dataset
		 */
		attach(SmartsQueryResource.resource,smartsRouter);
		attach(SimilarityResource.resource,similarityRouter);	
		
		/**
		 * Quality label 
		 * TODO refactor it as query with dataset_uri as parameter
		 */
		attach(String.format("%s%s",QueryResource.query_resource,QLabelQueryResource.resource),QLabelQueryResource.class);
		
	}	
}
