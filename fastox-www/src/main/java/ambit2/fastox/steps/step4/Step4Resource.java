package ambit2.fastox.steps.step4;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Form;
import org.restlet.data.MediaType;

import ambit2.fastox.ModelTools;
import ambit2.fastox.steps.FastoxStepResource;

/**
 * Run calculations with selected models, display progress indicators
 * @author nina
 *
 */
public class Step4Resource extends FastoxStepResource {
	public Step4Resource() {
		super(4);
	}

	@Override
	protected String getDefaultTab() {
		return "ExperimentalData";
	}
	@Override
	public void renderFormContent(Writer writer, String key) throws IOException {
		writer.write("<h5>Under development: This page will display available experimental data for the selected endpoints</h5>");
		Form form = getRequest().getResourceRef().getQueryAsForm();

		try {
			store = ModelTools.retrieveModels(store,form, MediaType.APPLICATION_RDF_XML);
		} catch (Exception x) {
			form.add(params.errors.toString(),x.getMessage());
		}
		ModelTools.renderModels(store,form, writer, false,getRootRef());
		renderCompounds(writer);
		super.renderFormContent(writer, key);
	}

	@Override
	public void renderResults(Writer writer, String key) throws IOException {
	}
}
