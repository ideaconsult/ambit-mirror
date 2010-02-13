package ambit2.fastox.steps.step6;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import ambit2.fastox.DatasetTools;
import ambit2.fastox.ModelTools;
import ambit2.fastox.steps.FastoxStepResource;

public class Step6Resource extends FastoxStepResource {

	public Step6Resource() {
		super(6);
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

		writer.write(params.dataset.htmlInputHidden(dataset));
		
		try {
			store = ModelTools.retrieveModels(store,form, MediaType.APPLICATION_RDF_XML);
		} catch (Exception x) {
			form.add(params.errors.toString(),x.getMessage());
		}
	
		ModelTools.renderModels(store,form, writer, false,getRootRef());
		//todo retrieve dataset once and then only predictions into a single model
		writer.write("<h4>Compounds</h4>");
		String[] models = form.getValuesArray(params.model.toString());
		for (String model:models) {
			String[] uris = form.getValuesArray(model);
			for (String uri:uris) {
				if ("on".equals(uri)) continue;

				writer.write(String.format("<input type='hidden' name='%s'   value='%s'>", model,uri));	

				try {
					store = DatasetTools.retrieveDataset(store,uri);
				} catch (Exception x) {
					form.add(params.errors.toString(),x.getMessage());
				}	
			}
		}	
		try {
			DatasetTools.renderDataset(store,writer,DatasetTools.modelVars,getRequest().getRootRef()); //"UNION { ?f owl:sameAs ?o.}"); //
		} catch (Exception x) {
			form.add(params.errors.toString(),x.toString());
		}		
		//super.renderFormContent(writer, key);
	}
	@Override
	public void renderResults(Writer writer,String key) throws IOException {
	}

	@Override
	protected String getDefaultTab() {
		return "Display results";
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
