package ambit2.fastox.steps.step5;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import ambit2.fastox.ModelTools;
import ambit2.fastox.steps.FastoxStepResource;
import ambit2.fastox.steps.StepProcessor;

/**
 * Display results
 * @author nina
 *
 */
public class Step5Resource extends FastoxStepResource {
	public static String meta_refresh = 
				"<meta http-equiv=\"refresh\" content=\"5;URL=%s\">\n"+
				"<META HTTP-EQUIV=\"CACHE-CONTROL\" CONTENT=\"no-store, no-cache, must-revalidate\">\n"+
				"<META HTTP-EQUIV=\"CACHE-CONTROL\" CONTENT=\"post-check=0, pre-check=0\">\n"+
				"<META HTTP-EQUIV=\"PRAGMA\" CONTENT=\"NO-CACHE\">\n"+
				"<META HTTP-EQUIV=\"EXPIRES\" CONTENT=\"0\">\n";
				

	public Step5Resource() {
		super(5);
	}

	@Override
	protected String getDefaultTab() {
		return "Estimate";
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		meta = String.format(meta_refresh,getRequest().getResourceRef());
		
	}
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		int running = 0;
		Form form = getRequest().getResourceRef().getQueryAsForm();
		String[] models = form.getValuesArray(params.model.toString());
		for (String model:models) {
			String[] uris = form.getValuesArray(model);
			for (String uri:uris) {
				String[] tasks = form.getValuesArray(uri);
				for (String task:tasks) 
				if (task.equals(Status.SUCCESS_ACCEPTED.getName()) || task.equals(Status.REDIRECTION_SEE_OTHER.getName())) {
					System.out.println(String.format("%s %s %s",model,uri,task));
				    running += verifyTask(model,uri, form); 
				}
			}
		}
		
		getRequest().getResourceRef().setQuery(form.getQueryString());
		if (running == 0) meta = ""; 
		else meta = String.format(meta_refresh,getRequest().getResourceRef());
		
		return super.get(variant);
	}
	
	@Override
	public void renderFormContent(Writer writer, String key) throws IOException {
		Form form = getRequest().getResourceRef().getQueryAsForm();

		writer.write(params.dataset.htmlInputHidden(dataset));
		writer.write("<h3>Models</h3>");
		
		try {
			store = ModelTools.retrieveModels(store,form, MediaType.APPLICATION_RDF_XML);
		} catch (Exception x) {
			form.add(params.errors.toString(),x.getMessage());
		}
		int running = ModelTools.renderModels(store,form, writer, true,getRequest().getRootRef());
		
		String[] values = form.getValuesArray(params.errors.toString());
		writer.write(values.length>0?"<h3>Errors</h3>":"");
		for (String value:values) {
			if (value==null) continue;
			writer.write(value);
			writer.write(String.format("<input type='hidden' name='errors' size='40' value='%s'>", value));
			writer.write("<br>");
		}		
		writer.write(String.format(
				"<INPUT name=\"next\" type=\"submit\" value=\"Next\" tabindex=\"1\" %s >",
				(running==0)?"":"DISABLED")
				);
		

		if (running>0) { //refresh the page in order to check tasks
			Form responseHeaders = (Form) getResponse().getAttributes().get("org.restlet.http.headers"); 
			if (responseHeaders == null) {
				responseHeaders = new Form();  
				getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);  
			} 				
			responseHeaders.add("Cache-Control", "no-store, no-cache, must-revalidate"); //HTTP 1.1
			responseHeaders.add("Cache-Control", "post-check=0, pre-check=0");
			responseHeaders.add("Pragma", "no-cache"); //HTTP 1.0
			responseHeaders.add("Expires", "0"); //prevents caching at the proxy server
			responseHeaders.add("Refresh",String.format("5; url=%s",getRequest().getResourceRef()));				
		}		
	}


	protected int verifyTask(String model, String task, Form form) throws ResourceException {
		Representation r = null;
		try {
			System.out.println(task);
			ClientResource client = new ClientResource(task);
			client.setFollowingRedirects(false);
			r = client.get(MediaType.TEXT_URI_LIST);
			
			Status status = client.getStatus();
			System.out.println(status);
			Reference uri = client.getResponse().getLocationRef();
			form.removeAll(task);
			form.removeAll(model);
			if (status.SUCCESS_OK.equals(status)) {
				form.add(model,task);
				form.add(task,status.getName());
			} else if (uri != null) {
				System.out.println(uri);
				form.add(model,uri.toString());
				form.add(uri.toString(),status.getName());

			} else {
				String text = StepProcessor.readUriList(r.getStream());
				form.add(model,task);
				form.add(text,status.getName());
			}
			return (status.SUCCESS_ACCEPTED.equals(status) || status.REDIRECTION_SEE_OTHER.equals(status))?1:0;
		} catch (ResourceException x) {
			x.printStackTrace();
			form.add(params.errors.toString(),x.toString());
		} catch (Exception x) {
			x.printStackTrace();
			form.add(params.errors.toString(),x.toString());
		} finally {
			try {r.release();} catch (Exception x) {}
		}			
		return 0;
	}	
	@Override
	protected Representation processForm(Representation entity, Variant variant)
			throws ResourceException {
		meta = String.format(meta_refresh,getRequest().getResourceRef());
		return super.processForm(entity, variant);
	}
	@Override
	public void renderResults(Writer writer, String key) throws IOException {
		
	}
}
