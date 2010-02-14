package ambit2.fastox;

import java.io.IOException;
import java.io.Writer;

import org.restlet.resource.ResourceException;

import ambit2.fastox.wizard.WizardResource;
import ambit2.fastox.wizard.Wizard.SERVICE;

public class AdminResource extends WizardResource {

	public AdminResource() throws ResourceException {
		super(0);
	}

	@Override
	public void renderFormContent(Writer writer, String key) throws IOException {
		writer.write("<h2>ToxPredict services configuration</h2>");
		writer.write("ToxPredict uses remote OpenTox services to retrieve and store data and perform QSAR estimations.");
		
		writer.write(String.format("<table>"));
		writer.write(String.format("<caption>Services</caption>"));
		for (SERVICE service : SERVICE.values()) {
			writer.write(String.format("<tr><th>%s</th><td><a href='%s' target='_blank'>%s</a></td></tr>",
					service.toString(),
					wizard.getService(service),
					wizard.getService(service)));	
		}
		writer.write(String.format("</table>"));
		
		writer.write(String.format("<h2><a href='%s'>%s</a></h2>",getRequest().getRootRef(),"Go to ToxPredict"));
		
	}
	@Override
	public void renderResults(Writer writer, String key) throws IOException {
	}
	@Override
	public String toString() {

		return "ToxPredict configuration";
	}

}
