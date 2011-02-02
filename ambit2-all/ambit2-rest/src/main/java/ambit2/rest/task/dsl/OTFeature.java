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
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;

public class OTFeature extends OTProcessingResource  {
     protected OTAlgorithm algorithm = null;
     protected boolean isNumeric = false;
     protected boolean isNominal = false;
     
	 protected OTFeature(Reference ref) {
			super(ref);
		 }
	protected OTFeature(String ref) {
		super(ref);
	}
	
	 public OTAlgorithm getAlgorithm() {
		return algorithm;
	}
		public static OTFeature feature() throws Exception  { 
		    return new OTFeature((Reference)null);
	 }	 
	public static OTFeature feature(String ref) throws Exception  { 
		    return new OTFeature(ref);
	 }
	public static OTFeature feature(Reference ref) throws Exception  { 
	    return new OTFeature(ref);
   }
	public OTFeature withFeatureService(Reference uri) throws Exception { 
		  this.service = uri;
		  return this; 
	 }	
	public OTFeature withAlgorithm(OTAlgorithm alg) throws Exception { 
		  this.algorithm = alg;
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
					RDFNode var = solution.get("algorithm");
					if (var.isResource())
						algorithm =  OTAlgorithm.algorithm(((Resource)var).getURI());
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
	 @Override
	public OTObject create() throws Exception {
		try {
			if (service == null) throw new Exception("No feature service");
			//RemoteTask task = new RemoteTask(service,MediaType.APPLICATION_RDF_XML,input,Method.POST,null);
		} catch (Exception x) {
			
		}
		return this;
	}
	 public OTFeature getPage(int page,int pageSize) throws Exception {
		 return feature(OTObject.getPagedReference(uri,page, pageSize));
	 }
	 
	 public OTFeature setSameas(String newLabel) throws Exception {
		OntModel jenaModel = OT.createModel(null, getUri(), MediaType.APPLICATION_RDF_XML);
		
		Resource thisFeature  = jenaModel.createResource(getUri().toString());
		StmtIterator i  =  jenaModel.listStatements(new SimpleSelector(thisFeature,OWL.sameAs,(RDFNode) null));
		Statement st = null;
		while (i.hasNext()) {
			st = i.next();
			break;
		}	
		i.close();
		if (st != null) jenaModel.remove(st);
		jenaModel.add(thisFeature,OWL.sameAs,newLabel);
		put(jenaModel);
		jenaModel.close();
		return this;
	 }
	 

}


