package ambit2.rest.task;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;

import ambit2.rest.OpenTox;
import ambit2.rest.legacy.OTAlgorithm;
import ambit2.rest.legacy.OTAlgorithms;
import ambit2.rest.legacy.OTDataset;
import ambit2.rest.legacy.OTSuperModel;

/**
 * Wrapper for remote tasks to be used as super service
 * @author nina
 *
 */
public class CallablePOST<USERID> extends CallableProtectedTask<USERID> {
	protected MediaType media; 
	protected Representation input;
	protected Status status;
	protected Reference applicationRootReference;

	protected OTSuperModel superModel;
	
	protected String referer;
	
	public CallablePOST(Form form,Reference root,USERID token,String referer) throws Exception {
		this(MediaType.TEXT_URI_LIST,form.getWebRepresentation(),root,token,referer);
	}	
	

	public CallablePOST(MediaType media, 
			  Representation input,
			  Reference root,
			  USERID token,String referer) {
		this(media,input,1500,root,token,referer);
	}
	public CallablePOST(MediaType media, 
			  Representation input,
			  long pollInterval,
			  Reference root,
			  USERID token,String referer) {
		super(token);
		this.media = media;
		this.input = input;
		this.applicationRootReference = root;
		this.referer= referer;
	}
	protected String getDatasetService(Form form) {
		String dataset_service = form.getFirstValue(OpenTox.params.dataset_service.toString());
		if (dataset_service==null) dataset_service = String.format("%s/dataset", applicationRootReference);
		else dataset_service = dataset_service.trim();
		return dataset_service;
	}
	protected String getDatasetURI(Form form) {
		String datasetURI = form.getFirstValue(OpenTox.params.dataset_uri.toString());
		if (datasetURI!=null) datasetURI = datasetURI.trim();
		return datasetURI;
	}	
	protected String[] getAlgorithms(Form form, String tag) {
		String[] algoURIs = form.getValuesArray(tag);
		//removing algorithm_uri parameters, no need to pass to algorithms 
		form.removeAll(tag);
		return algoURIs;
	}	
	@Override
	public TaskResult doCall() throws Exception {

		long now = System.currentTimeMillis();
		Form form = new Form(input);
		String dataset_service = getDatasetService(form);
		String datasetURI = getDatasetURI(form);
		
		String modelURI = form.getFirstValue(OpenTox.params.model_uri.toString());
		if (modelURI!=null) modelURI=modelURI.trim();
		String[] algoURIs = getAlgorithms(form,OpenTox.params.algorithm_uri.toString());
			
		OTDataset results = null;
		
		try { 
			if (modelURI != null) {

				results = OTSuperModel.model(modelURI,referer)
						.withDatasetService(dataset_service)
						.withParams(form)
						.process(OTDataset.dataset(datasetURI).withDatasetService(dataset_service));
			} else if (algoURIs != null) {
				
				OTAlgorithms algorithms = OTAlgorithms.algorithms(null,referer);
				algorithms.withDatasetService(dataset_service);
				
				for (String algoUri : algoURIs)
					if (algoUri!=null) 
						algorithms.add(OTAlgorithm.algorithm(algoUri.trim(),referer).withParams(form));
				
				results = algorithms.process(OTDataset.dataset(datasetURI).withDatasetService(dataset_service));
			}
			return new TaskResult(results.getUri().toString());
			
		} catch (Exception x) {

			throw x;
		} finally {

		}
	}




}
