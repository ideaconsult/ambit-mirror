package ambit2.fastox.steps.step5;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.fastox.ModelTools;
import ambit2.fastox.steps.FastoxStepResource;

/**
 * Display results
 * @author nina
 *
 */
public class Step5Resource extends FastoxStepResource {
	public static String meta_refresh = 
				"<meta http-equiv=\"refresh\" content=\"10;URL=%s\">\n"+
				"<META HTTP-EQUIV=\"CACHE-CONTROL\" CONTENT=\"no-store, no-cache, must-revalidate\">\n"+
				"<META HTTP-EQUIV=\"CACHE-CONTROL\" CONTENT=\"post-check=0, pre-check=0\">\n"+
				"<META HTTP-EQUIV=\"PRAGMA\" CONTENT=\"NO-CACHE\">\n"+
				"<META HTTP-EQUIV=\"EXPIRES\" CONTENT=\"0\">\n";
				
	protected int running = 0;
	public Step5Resource() {
		super(4);
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
		running = session.pollModels();
		Form form = getRequest().getResourceRef().getQueryAsForm();

		getRequest().getResourceRef().setQuery(form.getQueryString());
		if (running == 0) meta = ""; 
		else meta = String.format(meta_refresh,getRequest().getResourceRef());
		
		Representation r = super.get(variant);
		//if (running > 0)		r.setExpirationDate(new Date(0)); //doesn't seem to work
		return r;
	}
	
	@Override
	public void renderFormContent(Writer writer, String key) throws IOException {
		Form form = getRequest().getResourceRef().getQueryAsForm();

		writer.write("<h3>Models</h3>");
		
		try {
			store = ModelTools.retrieveModels(store,session, MediaType.APPLICATION_RDF_XML);
		} catch (Exception x) {
			session.setError(x);
		}
		//running = ModelTools.renderModels(store,session, writer, true,getRequest().getRootRef());
		
		try {
			running = renderRDFModels(writer, session, true, getRequest().getRootRef());
		} catch (Exception x) {
			writer.write(x.getMessage());
		}
		
		
		writer.write(session.getError()==null?"":"<h3>Errors</h3>");
		writer.write(session.getError()==null?"":session.getError().getMessage());

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
			responseHeaders.add("Refresh",String.format("10; url=%s",getRequest().getResourceRef()));				
		}		
	}


	@Override
	protected Representation processForm(Representation entity, Variant variant)
			throws ResourceException {
		meta = String.format(meta_refresh,getRequest().getResourceRef());
		Representation r = super.processForm(entity, variant);
		//r.setExpirationDate(new Date(0));
		return r;
	}
	@Override
	public void renderResults(Writer writer, String key) throws IOException {
		
	}
}
