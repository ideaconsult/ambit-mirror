package ambit2.fastox.steps.step4;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.fastox.ModelTools;
import ambit2.fastox.steps.FastoxStepResource;
import ambit2.fastox.steps.FastoxStepResource.params;
import ambit2.fastox.steps.step3.Step3Resource;
import ambit2.fastox.steps.step5.Step5Resource;

/**
 * Run calculations with selected models, display progress indicators
 * @author nina
 *
 */
public class Step4Resource extends FastoxStepResource {
	public static final String resource = "/step4";
	public static final String resourceTab = String.format("%s/{%s}",resource,tab);
	public Step4Resource() {
		super("Confirm",Step3Resource.resource,Step5Resource.resource);
	}
	protected String getTopRef() {
		return resource;
	}
	@Override
	protected String getDefaultTab() {
		return "Confirm";
	}
	@Override
	public void renderFormContent(Writer writer, String key) throws IOException {
		Form form = getRequest().getResourceRef().getQueryAsForm();

		try {
			store = ModelTools.retrieveModels(store,form, MediaType.APPLICATION_RDF_XML);
		} catch (Exception x) {
			form.add(params.errors.toString(),x.getMessage());
		}
		ModelTools.renderModels(store,form, writer, false);
		renderCompounds(writer);
		super.renderFormContent(writer, key);
	}
	@Override
	protected Representation processForm(Representation entity, Variant variant)
			throws ResourceException {
		Form form = new Form(entity);
		getRequest().getResourceRef().setQuery(form.getQueryString());
		return get(variant);			

	}
	@Override
	public void renderResults(Writer writer, String key) throws IOException {
	}
}
