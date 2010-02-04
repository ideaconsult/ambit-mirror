package ambit2.fastox.steps;

import java.io.IOException;
import java.io.Writer;
import java.util.Hashtable;

import org.restlet.data.Form;

import ambit2.fastox.steps.step1.Step1Resource;
import ambit2.fastox.wizard.WizardResource;

/**
 * Entry point
 * @author nina
 *
 */
public class WelcomeResource extends WizardResource {
	public static final String resource = "/";

	public WelcomeResource() {
		super("Fastox",null,WelcomeResource.resource);
	}
	@Override
	protected Hashtable<String, Form> createForms() {
		Hashtable<String, Form> forms = new Hashtable<String, Form>();
		forms.put("Welcome",new Form());		
		return forms;
	}
	@Override
	public void renderFormHeader(Writer writer,String key) throws IOException {
	}
	@Override
	public void renderFormFooter(Writer writer,String key) throws IOException {
	}
	@Override
	public void renderFormContent(Writer writer,String key) throws IOException {
		writer.write(String.format("<a href='%s%s'>%s</a>",getRootRef(),Step1Resource.resource,"Start"));
	}
	@Override
	public void renderResults(Writer writer, String key) throws IOException {

	}
	@Override
	public String toString() {
		return "Fastox";
	}
	protected String getTopRef() {
		return resource;
	}
}
