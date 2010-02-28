package ambit2.fastox.steps;

import java.io.IOException;
import java.io.Writer;
import java.util.Hashtable;

import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.fastox.users.IToxPredictSession;
import ambit2.fastox.users.IToxPredictUser;
import ambit2.fastox.users.ToxPredictUser;
import ambit2.fastox.users.UserResource;
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
		helpResource = "welcome.html";
	}
	@Override
	protected Hashtable<String, Form> createForms() {
		Hashtable<String, Form> forms = new Hashtable<String, Form>();
		forms.put("Welcome",new Form());		
		return forms;
	}
	public void renderFormHeader(Writer writer, String key)  throws IOException {
	}
	public void renderFormFooter(Writer writer,String key)  throws IOException {
	}
	
	@Override
	public void renderFormContent(Writer writer,String key) throws IOException {

		writer.write("<table width='90%'><tr align='left'><td>");
		writer.write("<h2>Estimate toxicological hazard of a chemical structure</h2>");
		writer.write(String.format("<form name='start' method='GET' action='%s/%s/%s/%s%s'>",
				getRootRef(),UserResource.resource,
				Reference.encode(session.getUser().getId()),
				WizardMode.A,"/step1"));	
		writer.write("<ul id=\"mainNav\" class=\"wizardStep\">\n");
		writer.write(String.format("<li class=\"next\"><INPUT name=\"next\" type=\"submit\" value=\"GO!\" tabindex=\"1\" title='Click here for the next step' class=\"button\"></li>"));
		writer.write(String.format("</ul>"));
		writer.write(String.format("</form>"));
		writer.write("</td></tr></table>");
//		writer.write(String.format("<a href='%s/%s/%s/%s%s' title='Specify structure'>%s</a>",
//				getRootRef(),UserResource.resource,user_name,WizardMode.A,"/step1","Estimate toxicological hazard of a chemical structure"));	
/*
		writer.write("<li>");
		writer.write(String.format("<a href='%s/%s/%s/%s%s' title='Select endpoint'>%s</a>",
				getRootRef(),UserResource.resource,user_name,WizardMode.B,"/step1","Browse available endpoints and models and estimate toxicological hazard"));	
					*/
	
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
	@Override
	protected IToxPredictSession getSession(String id) {
		IToxPredictSession session = null;
		IToxPredictUser user = null;
		try {
			session = super.getSession(id);
			if (session != null) return session;
		} catch (Exception x) {}
		try {
			user = new ToxPredictUser(id);
		} catch (Exception x) {
			user = new ToxPredictUser();
		}
		return addSession(user);
	}		
}
