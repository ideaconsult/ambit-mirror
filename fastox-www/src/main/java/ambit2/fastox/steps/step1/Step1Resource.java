package ambit2.fastox.steps.step1;

import java.io.IOException;
import java.io.Writer;
import java.util.Hashtable;

import org.restlet.data.Form;
import org.restlet.resource.ResourceException;

import ambit2.fastox.steps.FastoxStepResource;

/**
 * Define structure
 * @author nina
 *
 */
public class Step1Resource extends FastoxStepResource {

	protected String type = "";
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
		forms.put("Errors",new Form());
		forms.put("Help",new Form());		
		return forms;
	}	

	@Override
	public void renderFormContent(Writer writer, String key) throws IOException {
		Form form = forms.get(key);
		if ("Search".equals(key)) {
			type = "";
			try {

				type = form.getFirstValue("type");
			} catch (Exception x) {
				type = "smiles";
			}			
			writer.write(
			"<table>"+
			"<tr>"+
			"<td>"+
			"<label for='text'>Keywords</label></td><td colspan='2'><input name='text' type=\"text\" size='80'/>"+
			"</td>"+
			"</tr>\n"+
			"<tr>"+
			"<td>"+
			"<label for='search'>SMARTS</label></td><td><input name='search' type=\"text\" size='80'/>"+
			"</td><td>"+
			String.format("&nbsp;<input type='button' value='Draw molecule' onClick='startEditor(\"%s\");'>",
					getRootRef())+
			"</td>"+					
			"</tr>\n"+
			"</table>"
			
			);
			writer.write(String.format("<input name='type' type='hidden' value='%s'>\n",type==null?"smiles":type));
		} else if ("File".equals(key)) {
			writer.write("Under development");
		} else if ("Structure".equals(key)) {
			writer.write("Under development");
		}
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
