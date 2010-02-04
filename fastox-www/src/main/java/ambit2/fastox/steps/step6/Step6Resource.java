package ambit2.fastox.steps.step6;

import java.io.IOException;
import java.io.Writer;

import ambit2.fastox.steps.step5.Step5Resource;
import ambit2.fastox.wizard.WizardResource;

public class Step6Resource extends WizardResource {
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
		
	}
	@Override
	public void renderResults(Writer writer,String key) throws IOException {
		writer.write("Completed");
	}
	protected String getTopRef() {
		return resource;
	}
	@Override
	protected String getDefaultTab() {
		return "Display results";
	}

}
