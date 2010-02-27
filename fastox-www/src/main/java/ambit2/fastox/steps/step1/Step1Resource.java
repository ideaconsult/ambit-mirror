package ambit2.fastox.steps.step1;

import java.io.IOException;
import java.io.Writer;
import java.util.Hashtable;

import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import ambit2.fastox.steps.FastoxStepResource;
import ambit2.fastox.users.UserResource;

/**
 * Define structure
 * @author nina
 *
 */
public class Step1Resource extends FastoxStepResource {

	public Step1Resource() {
		super(1);
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		/** This determines if similarity searching will be done via smiles or via URL **/

	}
	@Override
	protected String getDefaultTab() {
		return "Search";
	}
	protected Hashtable<String, Form> createForms() {
		Hashtable<String, Form> forms = new Hashtable<String, Form>();
		forms.put("Search",new Form());
		forms.put("File",new Form());
		forms.put("Structure",new Form());
		forms.put("Datasets",new Form());

		return forms;
	}	

	public void renderFormHeader(Writer writer, String key)  throws IOException {
		writer.write(String.format("<form name='%s' method='POST' %s action='%s/%s/%s/%s%s'>",
				"form",
				key.equals("File")?"enctype='multipart/form-data'":"",
				getRootRef(),
				UserResource.resource,
				Reference.encode(session.getUser().getId()),mode,wizard.nextStep(step)));
	}
	@Override
	public void renderFormContent(Writer writer, String key) throws IOException {

		writer.write("<br style='clear:both;' clear='all' />\n"); // Safari is not happy otherwise with floating elements
		if ("Search".equals(key)) {
		
			writer.write(
			"<table>"+
			"<tr>"+
			"<th>"+
			"<label for='text'>Free text search <br>(Enter chemical name, registry identifier, SMILES, InChI, any keywords)</label>"+
			"</th>\n"+
			"</tr><tr>"+
			"<td><input name='text' tabindex='1' type=\"text\" size='80' value='556-82-1'/>"+
			"</td>"+
			"<td>"+
			"<label for='max'>Number of hits</label><select name='max'><option value='1'>1</option><option value='3'>2</option><option value='5'>5</option><option value='10'>10</option></select>"+
			"</td><tr>"+
			"</table>"
			
			);

		} else if ("File".equals(key)) {
			
			writer.write(
			"<table>"+
			"<tr>"+
			"<th>"+
			"<label for='file'>File upload</label>"+
			"</th>\n"+
			"</tr><tr>"+
			"<td><input type='file' name='file' accept='chemical/x-mdl-sdfile' size='80'>"+
			"</td><tr>"+
			"</table>"
			
			);
		} else if ("Structure".equals(key)) {

			writer.write("<table>");
			writer.write("<tr><td>");
			writer.write(String.format(
			"<applet code=\"JME.class\" name=\"JME\" codebase=\"%s/jme\" archive=\"JME.jar\" width=\"540\" height=\"360\">"+
			"<param name=\"options\" value=\"query,nohydrogens\">"+
			"You have to enable Java and JavaScript on your machine !"+ 
			"</applet>",
			
			getRequest().getRootRef()
			));
			
			writer.write("<input name='search' type='hidden'>");
			writer.write("</td><td>");
			writer.write("<label for='mode'>search for&nbsp;</label><br>");
			writer.write("<input type='radio' name='mode' checked='checked' value='structure'>Structure<br><input type='radio' name='mode' value='substructure'>Substructure<br><input type='radio' name='mode' value='similarity'>Similarity<br>\n");
			writer.write("<label for='max'>Number of hits</label><select name='max'><option value='1'>1</option><option value='3'>2</option><option value='5'>5</option><option value='10'>10</option><option value='20'>20</option></select>");			
			writer.write("</td></tr>");
			writer.write("</table>");
		} else if ("Datasets".equals(key)) {
			writer.write("Under development");
		}
		writer.write("<p>");
		super.renderFormContent(writer, key);
	}
	@Override
	public void renderResults(Writer writer, String key) throws IOException {
	}
	@Override
	protected boolean isMandatory(String param) {
		return false;
	}
}
