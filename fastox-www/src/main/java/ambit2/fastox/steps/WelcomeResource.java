package ambit2.fastox.steps;

import java.io.IOException;
import java.io.Writer;
import java.util.Hashtable;

import org.restlet.data.Form;

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
	}
	@Override
	protected Hashtable<String, Form> createForms() {
		Hashtable<String, Form> forms = new Hashtable<String, Form>();
		forms.put("Welcome",new Form());		
		return forms;
	}
	public void renderFormHeader(Writer writer, String key)  throws IOException {
		writer.write(mode.toString());
		writer.write(String.format("<form name='%s' method='POST' action='%s/%s%s'>","form",getRootRef(),mode,wizard.nextStep(step)));
	}
	public void renderFormFooter(Writer writer,String key)  throws IOException {
		writer.write(String.format("</form>"));
	}
	
	@Override
	public void renderFormContent(Writer writer,String key) throws IOException {
		
		
		writer.write("<table border='0' width='100%>");
		writer.write("<tr align='center'>");
		writer.write("<td align='left' valign='top'>");
		writer.write("</td>");		
		writer.write("<td align='center'>");
		writer.write("</td>");
		writer.write("<td>");
		writer.write("</td>");		
		writer.write("</tr>");
		writer.write("<tr align='center'>");
		writer.write("<td width='10%'>");
		writer.write("</td>");
		writer.write("<td width='60%' align='center'>");
		writer.write("<input name='text' title='Enter chemical name, registry identifier, smiles, InChI' type=\"text\" size='80' value='556-82-1'/>");
		writer.write("</td>");
		writer.write("<td width='30%' align='left'>");
		writer.write("<INPUT name=\"next\" type=\"submit\" value=\"Start ToxPredict\" title='ToxPredict is a series of steps, starting from structure selection towards prediction of toxicological properties by OpenTox models.' tabindex=\"1\">");		
		writer.write("</td>");		
		writer.write("</tr>");
		writer.write("<tr >");
		writer.write("<td>");
		writer.write("</td>");
		writer.write("<td >");
		writer.write("</td>");	
		writer.write("<td align='left'>");
		writer.write(String.format("<a href='%s%s%s' title='More options for structure selection'>%s</a>",getRootRef(),mode,step.getResource(),"Advanced"));	
		writer.write("</td>");		
		writer.write("</tr>");		
		writer.write("<tr >");
		writer.write("<td>");
		writer.write("</td>");
		writer.write("<td >");
		writer.write("<tr >");
		writer.write("<td>");
			
		writer.write("</td>");
		writer.write("<td >");
		//writer.write("<blockquote>TODO: ToxPredict description</blockquote>");		
		writer.write("</td>");	
		writer.write("<td align='center'>");
		writer.write("</td>");		
		writer.write("</tr>");		
		writer.write("</td>");	
		writer.write("<td align='left'>");
		writer.write("</td>");		
		writer.write("</tr>");		
		writer.write("</table>");
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
