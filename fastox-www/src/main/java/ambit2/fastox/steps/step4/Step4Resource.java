package ambit2.fastox.steps.step4;

import java.io.IOException;
import java.io.Writer;

import ambit2.fastox.steps.FastoxStepResource;

/**
 * Run calculations with selected models, display progress indicators
 * @author nina
 *
 */
public class Step4Resource extends FastoxStepResource {
	public Step4Resource() {
		super(-1);
	}

	@Override
	protected String getDefaultTab() {
		return "ExperimentalData";
	}
	@Override
	public void renderFormContent(Writer writer, String key) throws IOException {
		writer.write("<h5>Under development: This page will display available experimental data for the selected endpoints</h5>");

		try {
			renderRDFModels(writer, session, false, getRequest().getRootRef(),false);
		} catch (Exception x) {
			writer.write(x.getMessage());
		}

		//renderCompounds(writer,key);
		super.renderFormContent(writer, key);
	}

	@Override
	public void renderResults(Writer writer, String key) throws IOException {
	}
}
