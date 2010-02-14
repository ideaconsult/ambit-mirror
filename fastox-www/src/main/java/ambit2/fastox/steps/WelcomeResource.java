package ambit2.fastox.steps;

import java.io.IOException;
import java.io.Writer;
import java.util.Hashtable;

import org.restlet.data.Form;

import ambit2.fastox.UserResource;
import ambit2.fastox.wizard.WizardResource;
import ambit2.fastox.wizard.Wizard.WizardMode;

/**
 * Entry point
 * @author nina
 *
 */
public class WelcomeResource extends WizardResource {
	public static final String resource = "/";

	public WelcomeResource() {
		super(0);
	}
	@Override
	protected Hashtable<String, Form> createForms() {
		Hashtable<String, Form> forms = new Hashtable<String, Form>();
		forms.put("Welcome",new Form());		
		return forms;
	}
	public void renderFormHeader(Writer writer, String key)  throws IOException {
		writer.write(String.format("<form name='%s' method='POST' action='%s/%s/%s/%s%s'>","form",
				getRootRef(),UserResource.resource,user_name,mode,wizard.nextStep(step)));
	}
	public void renderFormFooter(Writer writer,String key)  throws IOException {
		writer.write(String.format("</form>"));
	}
	
	@Override
	public void renderFormContent(Writer writer,String key) throws IOException {
		
		
		writer.write("<h2><ul>");
		writer.write("<li>");
		writer.write(String.format("<a href='%s/%s/%s/%s%s' title='Specify structure'>%s</a>",
				getRootRef(),UserResource.resource,user_name,WizardMode.A,"/step1","Structure"));	
		writer.write("<li>");
		writer.write(String.format("<a href='%s/%s/%s/%s%s' title='Select endpoint'>%s</a>",
				getRootRef(),UserResource.resource,user_name,WizardMode.B,"/step1","Endpoints"));			
		writer.write("</ul></h2>");		
	
		//writer.write("</FIELDSET>");
	}
	@Override
	public void renderResults(Writer writer, String key) throws IOException {

	}
	@Override
	public String toString() {
		return "ToxPredict";
	}
	protected String getTopRef() {
		return resource;
	}
}
