package ambit2.rest.task;

import java.util.concurrent.Callable;

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
public class CallablePOST implements Callable<Reference>{
	protected MediaType media; 
	protected Representation input;
	protected Status status;
	protected Reference applicationRootReference;
	
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
		long now = System.currentTimeMillis();
		Form form = new Form(input);
		String dataset_service = form.getFirstValue(OpenTox.params.dataset_service.toString());
		if (dataset_service==null) dataset_service = String.format("%s/dataset", applicationRootReference);
		String datasetURI = form.getFirstValue(OpenTox.params.dataset_uri.toString());
		
		String modelURI = form.getFirstValue(OpenTox.params.model_uri.toString());
		String[] algoURIs = form.getValuesArray(OpenTox.params.algorithm_uri.toString());
		
		OTDataset results = null;
		
		try { 
			if (modelURI != null) 
				results = OTSuperModel.model()
						.withUri(modelURI)
						.withDatasetService(dataset_service)
						.withParams(form)
						.process(OTDataset.dataset().withUri(datasetURI));
			else if (algoURIs != null) {
				
				OTAlgorithms algorithms = OTAlgorithms.algorithms();
				algorithms.withDatasetService(dataset_service);
				
				for (String algoUri : algoURIs)
					if (algoUri!=null) 
						algorithms.add(OTAlgorithm.algorithm().withUri(algoUri).withParams(form));
				
				results = algorithms.process(OTDataset.dataset().withUri(datasetURI).withDatasetService(dataset_service));
			}
			return results.getUri();
			
		} catch (Exception x) {
			//x.printStackTrace();
			throw x;
		} finally {
			System.out.println(String.format("Elapsed %s", System.currentTimeMillis()-now));
		}
	}




}
