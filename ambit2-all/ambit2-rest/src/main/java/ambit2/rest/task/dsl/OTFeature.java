package ambit2.rest.task.dsl;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.rest.rdf.OT;
import ambit2.rest.task.RemoteTask;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;

public class OTFeature extends OTProcessingResource {
     protected OTAlgorithm algorithm = null;
     
	 public OTAlgorithm getAlgorithm() {
		return algorithm;
	}
	public static OTFeature feature() throws Exception  { 
		    return new OTFeature();
	 }
	 public OTFeature withUri(String uri) throws Exception { 
		  return withUri(new Reference(uri));

	 }	 
	 public OTFeature withUri(Reference uri) throws Exception { 
		  this.uri = uri;
		  return this; 
	 }	 
	 /**
	  * Retrieve algorithms from ot:hasSource property
	  * @return
	  * @throws Exception
	  */
	 public OTFeature algorithm() throws Exception  { 
		 if (algorithm ==null) {
	        QueryExecution qe = null;
	        OntModel model = null;
	     
	        try {
	        	model = OT.createModel(null, uri,MediaType.APPLICATION_RDF_XML);	
				
				Query query = QueryFactory.create(OTModel.getSparql("sparql/FeatureAlgorithm.sparql"));
				qe = QueryExecutionFactory.create(query,model);
				ResultSet results = qe.execSelect();
				while (results.hasNext()) {
					QuerySolution solution = results.next();
					Resource var = solution.getResource("algorithm");
					algorithm =  OTAlgorithm.algorithm().withUri(var.getURI());
				}
	        }  catch (Exception x) {
	        	throw x;
	        } finally {
	        	try {qe.close();} catch (Exception x) {}
	        	try {model.close();} catch (Exception x) {}
	        }
		 }  
	     return this;
	}		 
 	 /**
 	  * 
 	  * @param inputDataset
 	  * @param dataset_service
 	  * @return
 	  * @throws Exception
 	  */
	 @Override
	 public RemoteTask processAsync(OTDataset inputDataset) throws Exception {
		 algorithm();
		 return algorithm==null?null:algorithm.processAsync(inputDataset);
	 }
	 @Override
	 public OTDataset process(OTDataset inputDataset) throws Exception {
		 algorithm();
		 return (algorithm==null)?null:algorithm.process(inputDataset);	 
	 }		 
}


