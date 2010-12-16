package ambit2.rest.task;

import java.util.UUID;

import org.restlet.data.ChallengeResponse;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;

import ambit2.rest.OpenTox;
import ambit2.rest.task.dsl.OTAlgorithm;
import ambit2.rest.task.dsl.OTAlgorithms;
import ambit2.rest.task.dsl.OTDataset;
import ambit2.rest.task.dsl.OTSuperModel;

/**
 * Wrapper for remote tasks to be used as super service
 * @author nina
 *
 */
public class CallablePOST implements CallableTask{
	protected MediaType media; 
	protected Representation input;
	protected Status status;
	protected Reference applicationRootReference;
	protected UUID uuid;
	
	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	protected OTSuperModel superModel;
	
	
	public CallablePOST(Form form,Reference root) throws Exception {
		this(MediaType.TEXT_URI_LIST,form.getWebRepresentation(),root);
	}	
	
	public CallablePOST(MediaType media, 
			  Representation input,Reference root) {
		this(media,input,null,root);
	}		
	public CallablePOST(MediaType media, 
			  Representation input,
			  ChallengeResponse authentication,
			  Reference root) {
		this(media,input,authentication,1500,root);
	}
	public CallablePOST(MediaType media, 
			  Representation input,
			  ChallengeResponse authentication, long pollInterval,
			  Reference root) {

		this.media = media;
		this.input = input;
		this.applicationRootReference = root;
	}
	public Reference call() throws Exception {
		//System.out.println(getClass().getName());
		long now = System.currentTimeMillis();
		Form form = new Form(input);
		String dataset_service = form.getFirstValue(OpenTox.params.dataset_service.toString());
		if (dataset_service==null) dataset_service = String.format("%s/dataset", applicationRootReference);
		else dataset_service = dataset_service.trim();
		String datasetURI = form.getFirstValue(OpenTox.params.dataset_uri.toString());
		if (datasetURI!=null) datasetURI = datasetURI.trim();
		
		String modelURI = form.getFirstValue(OpenTox.params.model_uri.toString());
		if (modelURI!=null) modelURI=modelURI.trim();
		String[] algoURIs = form.getValuesArray(OpenTox.params.algorithm_uri.toString());
		
		OTDataset results = null;
		
		try { 
			if (modelURI != null) {
				//System.out.println(modelURI);
				//System.out.println(datasetURI);
				results = OTSuperModel.model(modelURI)
						.withDatasetService(dataset_service)
						.withParams(form)
						.process(OTDataset.dataset(datasetURI).withDatasetService(dataset_service));
			} else if (algoURIs != null) {
				
				OTAlgorithms algorithms = OTAlgorithms.algorithms();
				algorithms.withDatasetService(dataset_service);
				
				for (String algoUri : algoURIs)
					if (algoUri!=null) 
						algorithms.add(OTAlgorithm.algorithm(algoUri.trim()).withParams(form));
				
				results = algorithms.process(OTDataset.dataset(datasetURI).withDatasetService(dataset_service));
			}
			return results.getUri();
			
		} catch (Exception x) {
			//x.printStackTrace();
			throw x;
		} finally {
			//System.out.println(String.format("Elapsed %s", System.currentTimeMillis()-now));
		}
	}




}
