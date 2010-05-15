package ambit2.rest.task.dsl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.rest.OpenTox;
import ambit2.rest.rdf.OT;
import ambit2.rest.rdf.OT.OTProperty;
import ambit2.rest.task.CallablePOST;
import ambit2.rest.task.RemoteTask;
import ambit2.rest.task.RemoteTaskPool;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * 
 * @author nina
 *
 */
public class OTModel extends OTProcessingResource {
	 protected OTFeatures independentVariables;
	 public OTFeatures getIndependentVariables() {
		return independentVariables;
	}
	public OTFeatures getPredictedVariables() {
		return predictedVariables;
	}
	protected OTFeatures predictedVariables;
	 
	 public static OTModel model() throws Exception  { 
		    return new OTModel();
	 }
	 public OTModel withUri(Reference uri) throws Exception { 
		  this.uri = uri;
		  return this; 
	 }	 
	 public OTModel withUri(String uri) throws Exception { 
		  return withUri(new Reference(uri)); 
	 }	
	 /**
	  * Reads RDF and initializes variables 
	  * @return
	  * @throws Exception
	  */
	 public OTModel load() throws Exception  {
		 OTFeatures vars = null;
		 String sparqlName = "sparql/ModelIndependentVariables.sparql";
		 
		 OTProperty[] varTypes = new OTProperty[] {OTProperty.independentVariables,OTProperty.predictedVariables};
		 
		 
		 OntModel model = OT.createModel(null, new Reference(uri),MediaType.APPLICATION_RDF_XML);
		 try {
			 for (OTProperty varType:varTypes) {
				 switch (varType) {
				 case independentVariables: { 
					 if (independentVariables == null) independentVariables = OTFeatures.features();
					 vars = independentVariables;
					 break;
				 }
				 case predictedVariables: { 
					 if (predictedVariables == null) predictedVariables = OTFeatures.features();
					 vars = predictedVariables;
					 sparqlName = "sparql/ModelPredictedVariables.sparql";
					 break;
				 }
				 default: throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format("Not supported",varType));
				 }
				 vars.clear();
				 vars.withDatasetService(dataset_service);
				 
			        QueryExecution qe = null;
			        try {
			        	
						Query query = QueryFactory.create(String.format(OTModel.getSparql(sparqlName).toString(),uri,uri));
						qe = QueryExecutionFactory.create(query,model);
						ResultSet results = qe.execSelect();
						
						
						while (results.hasNext()) {
							QuerySolution solution = results.next();
							Resource var = solution.getResource("vars");
							
							vars.add(OTFeature.feature().withUri(var.getURI()));
						}
			        } catch (Exception x) {
			        	throw x;
			        } finally {
			        	try {qe.close();} catch (Exception x) {}

			        }
			   }
		 } catch (Exception x) {
			 throw x;
		 } finally {
	        	try {model.close();} catch (Exception x) {}
		 }
	
	 	return this;
     }	 
	 public OTDataset calculateDescriptors(OTDataset inputDataset) throws Exception  {
		 if (independentVariables==null) load();
		 return calculateDescriptors(independentVariables,inputDataset);
	 }
	 public  OTDataset calculateDescriptors(OTFeatures features, OTDataset inputDataset) throws Exception  {
		

		if (features.size()>0) {
			RemoteTaskPool pool = new RemoteTaskPool();
			OTAlgorithms algorithms = OTAlgorithms.algorithms();
			algorithms.withDatasetService(dataset_service);
			
			OTDatasets datasets = OTDatasets.datasets();
			datasets.withDatasetService(dataset_service);
			
			for (OTFeature feature : features.getItems())
				if (feature!=null) {
					OTDataset subset = inputDataset.filterByFeature(feature,false);
					if ((subset!=null) && subset.isEmpty()) {
						//TODO logger
						//System.out.println("Nothing to calculate");
						datasets.add(inputDataset.addColumns(feature));
					} else {
						//algorithms.add(feature.algorithm().getAlgorithm());
						RemoteTask task = feature.getAlgorithm().processAsync(subset==null?inputDataset:subset);
						pool.add(task);
					}
				}
			if (pool.size()>0) 
				datasets = tasksCompleted(pool).taskResults(pool,datasets);
			//ok, all datasets in place, merge into one to submit for model calculation
			return datasets.merge();
	
		} else return inputDataset;
	 }

	 public OTDataset subsetWithoutPredictedValues(OTDataset inputDataset) throws Exception  {
		 if (predictedVariables==null) load();
		 return inputDataset.filteredSubsetWithoutFeatures(getPredictedVariables());
	 }
	 @Override
	 public OTDataset process(OTDataset inputDataset) throws Exception  {
		 OTDataset subsetToCalculate = subsetWithoutPredictedValues(inputDataset);
		 if (!subsetToCalculate.isEmpty()) {
			 predict(subsetToCalculate);
		 }
		 return OTDataset.dataset().withUri(inputDataset.uri).withDatasetService(dataset_service).
		 		removeColumns().addColumns(getPredictedVariables());		 
	 }	
	 public OTDataset predict(OTDataset subsetToCalculate) throws Exception  {
			 long now = System.currentTimeMillis();
			 OTDataset newdataset = OTDataset.dataset().withDatasetService(dataset_service).copy(subsetToCalculate);
			 RemoteTask task = processAsync(newdataset);
			 wait(task,now);
			 return OTDataset.dataset().withUri(task.getResult());

	 }		 
	 @Override
	 public RemoteTask processAsync(OTDataset inputDataset) throws Exception  { 
		if (inputDataset == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"No input dataset");
		Form params = form==null?new Form():form;
		if (dataset_service!=null) {
			params.removeAll(OpenTox.params.dataset_service.toString());
			params.add(OpenTox.params.dataset_service.toString(),dataset_service.toString());
		}
		params.removeAll(OpenTox.params.model_uri.toString());
		params.removeAll(OpenTox.params.dataset_uri.toString());
		params.add(OpenTox.params.dataset_uri.toString(),inputDataset.toString());
		return new RemoteTask(new Reference(uri),MediaType.TEXT_URI_LIST,params.getWebRepresentation(),Method.POST,authentication);
		 
	 }

	 public OTModel withDatasetService(Reference uri) throws Exception { 
		  this.dataset_service = uri;
		  return this; 
	 }	
	 public OTModel withDatasetService(String uri) throws Exception { 
		  return withDatasetService(new Reference(uri));
	 }	
		public static String getSparql(String name)  throws Exception {
			InputStream in  = CallablePOST.class.getClassLoader().getResourceAsStream(name);
	        StringBuilder sparqlQuery = new StringBuilder();
	        String line;

	        try {
	            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
	            while ((line = reader.readLine()) != null) {
	            	sparqlQuery.append(line).append("\n");
	            }
	        } finally {
	            in.close();
	        }
	        return sparqlQuery.toString();
		}
		
		@Override
		public OTModel withParams(Form form) throws Exception {
			return (OTModel)super.withParams(form);
		}
		@Override
		public OTModel withParams(String name, String value) throws Exception {
			return (OTModel)super.withParams(name, value);
		}
}
