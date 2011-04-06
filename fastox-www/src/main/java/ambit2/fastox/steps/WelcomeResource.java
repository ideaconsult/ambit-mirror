package ambit2.fastox.steps;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.restlet.data.Reference;

import ambit2.fastox.users.IToxPredictSession;
import ambit2.fastox.users.IToxPredictUser;
import ambit2.fastox.users.ToxPredictUser;
import ambit2.fastox.users.UserResource;
import ambit2.fastox.wizard.Wizard.SERVICE;
import ambit2.fastox.wizard.Wizard.WizardMode;
import ambit2.fastox.wizard.WizardResource;

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
	protected List<String> createTabs() {
		List<String> tabs = new ArrayList<String>();
		tabs.add("Welcome");
		return tabs;
	}
	public void renderFormHeader(Writer writer, String key)  throws IOException {
	}
	public void renderFormFooter(Writer writer,String key)  throws IOException {
		
		writer.write(String.format(
				"\n<script type=\"text/javascript\">$(document).ready(function() {  stats(\"%s%s\",\"%s\"); } );</script>",
				wizard.getService(SERVICE.application).toString(),
				"/stats/structures",
				"#count_struc"));
		
		writer.write(String.format(
				"\n<script type=\"text/javascript\">$(document).ready(function() {  stats(\"%s%s\",\"%s\"); } );</script>",
				wizard.getService(SERVICE.application).toString(),
				"/stats/chemicals_in_dataset",
				"#count_chemicals"));		
		
		writer.write(String.format(
				"\n<script type=\"text/javascript\">$(document).ready(function() {  stats(\"%s%s\",\"%s\"); } );</script>",
				wizard.getService(SERVICE.application).toString(),
				"/stats/dataset",
				"#count_datasets"));	
		
		writer.write(String.format(
				"\n<script type=\"text/javascript\">$(document).ready(function() {  %s; } );</script>",
				"animatedcollapse.show('help_step')"));			

		writer.write(String.format(
				"\n<script type=\"text/javascript\">$(document).ready(function() {  %s } );</script>",
				"for (i=1;i<=6;i++) { animatedcollapse.showH('wiz'+i); }\n"));			

		/*
		writer.write(String.format(
				"\n<script type=\"text/javascript\">$(document).ready( $(\"#%s\").load(\"%s\"); );</script>",
				"count_struc","http://194.141.0.136:8080/ambit2/stats/structures"));		
		;
		*/
	}
	
	@Override
	public void renderFormContent(Writer writer,String key) throws IOException {

		writer.write("<table width='100%'><tr align='left'><td>");
		writer.write("<h2>Estimate toxicological hazard of a chemical structure</h2>");
		writer.write(String.format("<form name='start' method='GET' action='%s/%s/%s/%s%s'>",
				getRootRef(),UserResource.resource,
				Reference.encode(session.getUser().getId()),
				WizardMode.A,"/step1"));	
		writer.write("<ul id=\"mainNav\" class=\"wizardStep\">\n");
		writer.write(String.format("<li id=\"wiz1\" style=\"display:none;\"><a title=\"\"><em>1.&nbsp;Select</em><span>structure(s)</span></a></li>\n<li id=\"wiz2\" style=\"display:none;\"><a title=\"\"><em>2.&nbsp;Verify</em><span>structure(s)</span></a></li>\n<li id=\"wiz3\" style=\"display:none;\"><a title=\"\"><em>3.&nbsp;Select</em><span>model(s)</span></a></li>\n<li id=\"wiz4\" style=\"display:none;\"><a title=\"\"><em>4.&nbsp;Run</em><span> prediction(s)</span></a></li>\n<li class=\"mainNavLast\" id=\"wiz5\" style=\"display:none;\"><a title=\"\"><em>5.&nbsp;Display</em><span>result(s)</span></a></li>\n<li class=\"next\" id=\"wiz6\" style=\"display:none;\"><INPUT name=\"next\" type=\"submit\" value=\"\" tabindex=\"1\" title=\"Click here to start the wizard.\" class=\"button\"></li>"));
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
	@Override
	protected String getHelp() {
		String chemicals = "";
		String datasets = "datasets"; //String.format("<a href='%s/user/admin/report/Stats?header=TRUE' alt='stats' title='dataset statistics' target='_blank'>datasets</a>", getRequest().getRootRef());
		String models = "";
		String endpoints = "";
		
		String help = super.getHelp();
		try {	
			if (session.getAllModels()<=0) session.setAllModels(countObjects("ot:Model"));
		} catch (Exception x) { session.setAllModels(0);}
		models = session.getAllModels()<=0?"":Integer.toString(session.getAllModels());
	
		try {	
			if (session.getAllEndpoints()<=0) session.setAllEndpoints(countEndpoints());
		} catch (Exception x) { session.setAllEndpoints(0);}
		endpoints = session.getAllEndpoints()<=0?"":Integer.toString(session.getAllEndpoints());

		String modelref = String.format("%s/user/%s/model", getRequest().getRootRef(),session.getUser().getId());
		return String.format(help,chemicals,datasets,modelref,models,endpoints);
	}
}
