package ambit2.fastox;

import java.io.IOException;
import java.io.Writer;

import org.restlet.resource.ResourceException;

import ambit2.fastox.wizard.WizardResource;

public class HelpResource extends WizardResource {
	
	public HelpResource() throws ResourceException {
		super(0);
	}
	
	@Override
	public void renderFormContent(Writer writer, String key) throws IOException {
		writer.write("<h2>ToxPredict Help</h2>");
		writer.write("Sorry, ToxPredict Help is under development.");
		
		writer.write(String.format("<h2><a href='%s'>%s</a></h2>",getRequest().getRootRef(),"Go to ToxPredict"));
		
	}
	@Override
	public void renderResults(Writer writer, String key) throws IOException {
	}


}
