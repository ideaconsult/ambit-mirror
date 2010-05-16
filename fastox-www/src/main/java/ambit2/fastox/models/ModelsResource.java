package ambit2.fastox.models;

import java.io.IOException;
import java.io.Writer;

import ambit2.fastox.ModelTools;
import ambit2.fastox.steps.FastoxStepResource;

public class ModelsResource extends FastoxStepResource {
	public static final String resource = "/model";
	public ModelsResource() {
		super(0);
		helpResource = null;
	}
	@Override
	protected boolean isMandatory(String param) {
		return false;
	}
	@Override
	public void renderFormContent(Writer writer, String key) throws IOException {
		try {
			renderRDFModels(writer, session, false, getRequest().getRootRef(),true);
		} catch (Exception x) {
			writer.write(x.getMessage());
		}
	}
	@Override
	public void footer(Writer output) throws IOException {
		output.write(ModelTools.jsIFrame());
		super.footer(output);
	}
}
