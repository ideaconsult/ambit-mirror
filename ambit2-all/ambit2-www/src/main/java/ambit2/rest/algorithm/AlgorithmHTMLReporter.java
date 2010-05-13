package ambit2.rest.algorithm;

import java.io.Writer;
import java.util.Iterator;

import org.restlet.Request;

import ambit2.core.data.model.Algorithm;
import ambit2.rest.AmbitResource;
import ambit2.rest.OpenTox;

/**
 * Generates HTML output for {@link AllAlgorithmsResource}
 * @author nina
 *
 */
public class AlgorithmHTMLReporter extends AlgorithmURIReporter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7544605965468875232L;
	protected boolean collapsed = false;
	public AlgorithmHTMLReporter(Request ref, boolean collapsed) {
		super(ref);
		this.collapsed = collapsed;
	}
	@Override
	public void header(Writer output, Iterator<Algorithm> query) {
		try {
			AmbitResource.writeHTMLHeader(output, "AMBIT", getRequest());//,"<meta http-equiv=\"refresh\" content=\"10\">");
			output.write(AmbitResource.jsTableSorter("algorithms","pager"));
			output.write("<table class='tablesorter' id='algorithms'>");
			if (collapsed) {
				output.write("<thead>");
				output.write("<tr><th align=\"left\">Name</th><th>Description</th><th>Type</th></tr>"); 
				output.write("</thead>");
			}
			output.write("<tbody>");
		} catch (Exception x) {
			
		}
	}
	public void processItem(Algorithm item, Writer output) {
		try {
			String t = super.getURI(item);
			if (collapsed)
				output.write(String.format("<tr><td align=\"left\"><a href='%s'>%s</a></td><td>%s</td><td  align='right'>%s</td></tr>", 
						t,item.getName(),
						item.isDataProcessing()?"Processes a dataset":"Generates a model"
						,item.getType()[0]));
			else {
				
				String target = item.isSupervised()?"<td><label for='prediction_feature'>Target&nbsp;</label></td><td><input type='text' name='prediction_feature' size='60' value='Enter feature URL'></td>":"";
				if (item.isDataProcessing()) {
					String dataset = item.isRequiresDataset()?"<td><label for='dataset_uri'>Dataset&nbsp;</label></td><td><input type='text' name='dataset_uri' size='60' value='Enter dataset URL'></td>":"";
					output.write(String.format(
							"<tr><form action=\"\" method=\"POST\"><tr><td>Algorithm:&nbsp;<a href='%s'>%s</a></td><td><table><tr>%s</tr><tr>%s</tr></table></td><td><input type=\"submit\" value=\"Run\"></td></form></tr>",
							t,item.getName(),
							dataset,
							target));
				} else if (item.hasType(Algorithm.typeSuperService)) {
					output.write("<tr><th>Model launcher</th><th>Calculate descriptors, prepares a dataset and runs the model</th></tr>");
					output.write(String.format("<form action='' method='%s' name='form'>","POST"));
					output.write(String.format("<tr><td>Dataset URI</td><td><input type='text'  size='120'  name='%s' value=''></td></tr>",OpenTox.params.dataset_uri));
					output.write(String.format("<tr><td>Model URI</td><td><input type='text' size='120' name='%s' value=''></td></tr>",OpenTox.params.model_uri));
					output.write(String.format("<tr><td>Algorithm URI</td><td><input type='text' size='120' name='%s' value=''></td></tr>",OpenTox.params.algorithm_uri));
					output.write(String.format("<tr><td>Algorithm URI</td><td><input type='text' size='120' name='%s' value=''></td></tr>",OpenTox.params.algorithm_uri));
					output.write(String.format("<tr><td>Algorithm URI</td><td><input type='text' size='120' name='%s' value=''></td></tr>",OpenTox.params.algorithm_uri));
					output.write(String.format("<tr><td>Algorithm URI</td><td><input type='text' size='120' name='%s' value=''></td></tr>",OpenTox.params.algorithm_uri));
					output.write(String.format("<tr><td>Dataset service URI</td><td><input type='text' size='120' name='%s' value=''></td></tr>",OpenTox.params.dataset_service));
					output.write(String.format("<tr><td><input type='submit' name='launch' value='%s'></td></tr>","Run"));
					output.write("</form>");
				} else  {//create a model
					String dataset = item.isRequiresDataset()?"<td><label for='dataset_uri'>Training dataset&nbsp;</label></td><td><input type='text' name='dataset_uri' size='60' value='Enter dataset URL'></td>":"";					
					output.write(String.format(
						"<tr><form action=\"\" method=\"POST\"><tr><td>Algorithm:&nbsp;<a href='%s'>%s</a></td><td><table><tr>%s</tr><tr>%s</tr></table></td><td><input type=\"submit\" value=\"Create model\"></td></form></tr>",
						t,item.getName(),
						dataset,
						target));
				}
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	};
	@Override
	public void footer(Writer output, Iterator<Algorithm> query) {
		try {
			output.write("</tbody></table>");
			AmbitResource.writeHTMLFooter(output, AllAlgorithmsResource.algorithm, getRequest());
			output.flush();
		} catch (Exception x) {
			
		}
	}
}
