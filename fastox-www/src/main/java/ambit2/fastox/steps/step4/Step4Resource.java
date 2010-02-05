package ambit2.fastox.steps.step4;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.fastox.steps.FastoxStepResource;
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
		String[] compounds = form.getValuesArray("compound");
		writer.write("<h3>Compound(s)</h3>");
		for (String compound:compounds) {
			writer.write(String.format("<input type='text' name='compound' value='%s'>", compound));
			writer.write("<br>");
		}	
		renderModels(form, writer, true);
		super.renderFormContent(writer, key);
	}
	@Override
	protected Representation processForm(Representation entity, Variant variant)
			throws ResourceException {
		Form form = new Form(entity);
		getRequest().getResourceRef().setQuery(form.getQueryString());
		return get(variant);			

	}
}
