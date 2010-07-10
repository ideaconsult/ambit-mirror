package ambit2.rest.task.dsl;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;

import ambit2.rest.OpenTox;
import ambit2.rest.task.RemoteTask;
import ambit2.rest.task.dsl.interfaces.IOTAlgorithm;

public class OTAlgorithm extends OTProcessingResource implements IOTAlgorithm  {
	
	
	protected OTAlgorithm(Reference ref) {
		super(ref);
	}
	protected OTAlgorithm(String ref) {
		super(ref);
	}
	 public static OTAlgorithm algorithm(Reference datasetURI) throws Exception  { 
			    return new OTAlgorithm(datasetURI);
	}

	public static OTAlgorithm algorithm(String datasetURI) throws Exception  { 
			    return new OTAlgorithm(datasetURI);
	 }	 

	 public RemoteTask processAsync(OTDataset inputDataset,OTFeature feature) throws Exception {
			Form params = form==null?new Form():form;
			if (dataset_service!=null) {
				params.removeAll(OpenTox.params.dataset_service.toString());
				params.add(OpenTox.params.dataset_service.toString(),dataset_service.toString());
			}
			params.removeAll(OpenTox.params.dataset_uri.toString());
			params.add(OpenTox.params.dataset_uri.toString(),inputDataset.toString());
			params.removeAll(OpenTox.params.target.toString());
			if (feature!=null)
				params.add(OpenTox.params.target.toString(),feature.toString());
			return processAsync(params);
	 }
	 @Override
	 public RemoteTask processAsync(OTDataset inputDataset) throws Exception {
		 return processAsync(inputDataset,null);
	 }

	 public RemoteTask processAsync(Form params) throws Exception {

			return new RemoteTask(new Reference(uri),MediaType.TEXT_URI_LIST,params.getWebRepresentation(),Method.POST,authentication);
	 }
	 @Override
	 public OTDataset process(OTDataset inputDataset) throws Exception {
			long now = System.currentTimeMillis();
			RemoteTask task = processAsync(inputDataset);
			task = wait(task,now);
			return OTDataset.dataset(task.getResult());		 
	 }	 
	 
	 public OTModel process(OTDataset inputDataset,OTFeature feature) throws Exception {
			long now = System.currentTimeMillis();
			RemoteTask task = processAsync(inputDataset,feature);
			task = wait(task,now);
			return OTModel.model(task.getResult());		 
	 }		 
	 @Override
	public OTAlgorithm withParams(Form form) throws Exception {
		return (OTAlgorithm)super.withParams(form);
	}
	 @Override
	public OTAlgorithm withParams(String name, String value) throws Exception {
		return (OTAlgorithm)super.withParams(name, value);
	}
	 
	public String report(String ontologyURI) throws Exception  { 
			String a = String.format("<%s>", uri);
			String query = String.format(OTModel.getSparql("sparql/AlgorithmReport.sparql").toString(),"",a,a);
					
		    OTOntologyService<String> ontology = new OTOntologyService<String>(ontologyURI);
		    
		    return ontology.report(query);		
	}	 
}
