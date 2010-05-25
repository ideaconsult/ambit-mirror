package ambit2.fastox.steps.step6;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.restlet.data.Form;

import ambit2.fastox.steps.FastoxStepResource;
import ambit2.rest.OpenTox;

public class Step6Resource extends FastoxStepResource {

	public Step6Resource() {
		super(5);
		helpResource = "step5.html";
	}
	@Override
	public void renderFormFooter(Writer writer,String key) throws IOException {
	}
	@Override
	public void renderFormHeader(Writer writer,String key) throws IOException {
	}
	

		
	@Override
	public void renderFormContent(Writer writer,String key) throws IOException {
/*
		try {
			store = ModelTools.retrieveModels(store,session, MediaType.APPLICATION_RDF_XML);
		} catch (Exception x) {
			session.setError(key,x);
		}	
		*/		
		try {
			renderCompoundsNew(writer,key,true,false);
		} catch (Exception x) {
			session.setError(key,x);
		}
	}
	/*		
	@Override
	public void renderFormContent(Writer writer,String key) throws IOException {

		try {
			store = ModelTools.retrieveModels(store,session, MediaType.APPLICATION_RDF_XML);
		} catch (Exception x) {
			session.setError(key,x);
		}
	

		try {
			store = DatasetTools.retrieveDataset(store,session.getSearchQuery());
		} catch (Exception x) {
			
		}
		Iterator<String> models = session.getModels();
		if (models != null)
		while (models.hasNext())  {
			String model = models.next();
			String uri = session.getModelResultsURI(model);
			if (uri != null)
				try {
					store = DatasetTools.retrieveDataset(store,new Reference(uri));
				} catch (Exception x) {
					session.setError(key,x);
				}	
		}	
		try {
			//DatasetTools.renderDataset(store,writer,DatasetTools.modelVars,getRequest().getRootRef()); //"UNION { ?f owl:sameAs ?o.}"); //
			DatasetTools.renderDataset1(session,store,writer,"",getRequest().getRootRef(),session.getSearch(),session.getCondition());
		} catch (Exception x) {
			session.setError(key,x);
		}		
		//super.renderFormContent(writer, key);
	}
	*/
	@Override
	public void renderResults(Writer writer,String key) throws IOException {
	}

	@Override
	protected List<String> createTabs() {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		if (form.getFirstValue(OpenTox.params.dataset_uri.toString()) != null) { //usually hidden,currently for testing only 
			session.setDatasetURI(form.getFirstValue(OpenTox.params.dataset_uri.toString()));
		} 
		if (form.getFirstValue(OpenTox.params.model_uri.toString()) != null) { //usually hidden,currently for testing only 
			session.addModel(form.getFirstValue(OpenTox.params.model_uri.toString()),
					Boolean.TRUE);
		} 			
		return super.createTabs();
	}
	@Override
	protected String getDefaultTab() {
		return "Display results";
	}


}
