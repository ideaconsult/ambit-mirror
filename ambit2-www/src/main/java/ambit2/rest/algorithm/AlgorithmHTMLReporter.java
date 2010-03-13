package ambit2.rest.algorithm;

import java.io.Writer;
import java.util.Iterator;

import org.restlet.Request;

import ambit2.core.data.model.Algorithm;
import ambit2.rest.AmbitResource;

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
			output.write("<table>");
		} catch (Exception x) {
			
		}
	}
	public void processItem(Algorithm item, Writer output) {
		try {
			String t = super.getURI(item);
			if (collapsed)
				output.write(String.format("<tr class='results_odd'><th align=\"left\"><a href='%s'>%s</a></th><td  class='results_col'>%s</td><td  align='right'>%s</td></tr>", 
						t,item.getName(),
						item.isDataProcessing()?"Processes a dataset":"Generates a model"
						,item.getType()[0]));
			else {
				
				String target = item.isSupervised()?"<td><label for='target'>Target&nbsp;</label></td><td><input type='text' name='target' size='60' value='Enter feature URL'></td>":"";
				if (item.isDataProcessing()) {
					String dataset = item.isRequiresDataset()?"<td><label for='dataset_uri'>Dataset&nbsp;</label></td><td><input type='text' name='dataset_uri' size='60' value='Enter dataset URL'></td>":"";
					output.write(String.format(
							"<tr><form action=\"\" method=\"POST\"><tr><th>Algorithm:&nbsp;<a href='%s'>%s</a></th><td><table><tr>%s</tr><tr>%s</tr></table></td><td><input type=\"submit\" value=\"Run\"></td></form></tr>",
							t,item.getName(),
							dataset,
							target));

				} else  {//create a model
					String dataset = item.isRequiresDataset()?"<td><label for='dataset_uri'>Training dataset&nbsp;</label></td><td><input type='text' name='dataset_uri' size='60' value='Enter dataset URL'></td>":"";					
					output.write(String.format(
						"<tr><form action=\"\" method=\"POST\"><tr><th>Algorithm:&nbsp;<a href='%s'>%s</a></th><td><table><tr>%s</tr><tr>%s</tr></table></td><td><input type=\"submit\" value=\"Create model\"></td></form></tr>",
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
			output.write("</table>");
			AmbitResource.writeHTMLFooter(output, AllAlgorithmsResource.algorithm, getRequest());
			output.flush();
		} catch (Exception x) {
			
		}
	}
}
