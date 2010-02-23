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
		forms.put("Datasets",new Form());
		//forms.put("Errors",new Form());

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
			writer.write("<br style='clear:both;' clear='all' />\n"); // Safari is not happy otherwise with floating elements
			writer.write(
			"<table>"+
			"<tr>"+
			"<th>"+
			"<label for='text'>Free text search <br>(Enter chemical name, registry identifier, SMILES, InChI, any keywords)</label>"+
			"</th>\n"+
			"</tr><tr>"+
			"<td><input name='text' tabindex='1' type=\"text\" size='80' value='556-82-1'/>"+
			"</td>"+
			"</tr>\n"+			
			"<tr>"+
			"<th>"+
			String.format("<label for='search'>or &nbsp;<input type='button' class='small_button' value='Draw a chemical structure' onClick='startEditor(\"%s\");'></label>",getRootRef())+			
			"</th></tr>\n"+			
			"<tr><td><input name='search' tabindex='10' title='Enter SMILES or use 'Draw' button to launch structure diagram editor' type=\"text\" size='80'/>"+
			"</td><td>"+
			"</td>"+					
			"</tr>\n"+
			"<tr>"+
			"<td>"+
			"<label for='mode'>and search for&nbsp;</label>"+
			"<input type='radio' name='mode' value='structure'>Structure&nbsp;<input type='radio' name='mode' checked='checked' value='substructure'>Substructure&nbsp;<input type='radio' name='mode' value='similarity'>Similarity&nbsp;\n"+
			"</td>"+
			"</tr>"+
			"<tr></tr>"+
			"<tr><td></td><td>"+
			"<label for='max'>Number of hits</label><select name='max'><option value='1'>1</option><option value='3'>2</option><option value='5'>5</option><option value='10'>10</option></select>"+
			"</td><tr>"+
			"</table>"
			
			);
			writer.write(String.format("<input name='type' type='hidden' value='%s'>\n",type==null?"smiles":type));
		} else if ("File".equals(key)) {
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
