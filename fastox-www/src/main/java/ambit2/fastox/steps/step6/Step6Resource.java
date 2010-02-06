package ambit2.fastox.steps.step6;

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

import ambit2.fastox.steps.FastoxStepResource;
import ambit2.fastox.steps.step5.Step5Resource;

public class Step6Resource extends FastoxStepResource {
	public static final String resource = "/step6";
	public static final String resourceTab = String.format("%s/{%s}",resource,tab);
	public Step6Resource() {
		super("Display results",Step5Resource.resource,null);
	}
	@Override
	public void renderFormFooter(Writer writer,String key) throws IOException {
	}
	@Override
	public void renderFormHeader(Writer writer,String key) throws IOException {
	}
	@Override
	public void renderFormContent(Writer writer,String key) throws IOException {
		Form form = getRequest().getResourceRef().getQueryAsForm();

		writer.write(params.dataset.htmlInputText(dataset));
		
		renderModels(form, writer, false);
		
		String[] models = form.getValuesArray(params.model.toString());
		for (String model:models) {
			String[] uris = form.getValuesArray(model);
			for (String uri:uris) {
				if ("on".equals(uri)) continue;

				writer.write(String.format("<input type='hidden' name='%s'   value='%s'>", model,uri));	
				//String[] tasks = form.getValuesArray(uri);
				//for (String task:tasks) {
					//if (task.equals(Status.SUCCESS_OK.getName()))
						displayResults(uri,form,writer);
				//}
 
			}
		}	
		super.renderFormContent(writer, key);
	}
	@Override
	public void renderResults(Writer writer,String key) throws IOException {
	}
	protected String getTopRef() {
		return resource;
	}
	@Override
	protected String getDefaultTab() {
		return "Display results";
	}
	@Override
	protected Representation processForm(Representation entity, Variant variant)
			throws ResourceException {
		Form form = new Form(entity);
		getRequest().getResourceRef().setQuery(form.getQueryString());
		return get(variant);		
	
	}
	
	protected void displayResults(String uri, Form form, Writer writer) throws ResourceException {
		Representation r = null;
		try {
			ClientResource client = new ClientResource(new Reference(uri));
			client.setFollowingRedirects(false);
			r = client.get(MediaType.TEXT_CSV);
			//r = client.get(MediaType.TEXT_RDF_N3);

			Status status = client.getStatus();
			if (status.equals(Status.SUCCESS_OK))  {
				renderCSV(r.getStream(), writer);
			}


		} catch (ResourceException x) {
			x.printStackTrace();
			form.add(params.errors.toString(),x.toString());
		} catch (Exception x) {
			x.printStackTrace();
			form.add(params.errors.toString(),x.toString());
		} finally {
			try {r.release();} catch (Exception x) {}
			
		}			
	}	
}
