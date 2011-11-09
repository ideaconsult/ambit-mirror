package ambit2.rest.algorithm;

import java.io.InputStream;
import java.io.Writer;
import java.util.Iterator;
import java.util.Properties;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.processors.search.AbstractFinder;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.AlgorithmType;
import ambit2.rest.AmbitResource;
import ambit2.rest.OpenTox;
import ambit2.rest.ResourceDoc;

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
	protected String sparql = null;
	
	public AlgorithmHTMLReporter(Request ref, boolean collapsed,ResourceDoc doc) {
		super(ref,doc);
		this.collapsed = collapsed;
		sparql = getOntologyServiceURI();
	}
	@Override
	public void header(Writer output, Iterator<Algorithm> query) {
		try {
			AmbitResource.writeHTMLHeader(output, "AMBIT", getRequest(),
					getResourceRef(),getDocumentation()
					);//,"<meta http-equiv=\"refresh\" content=\"10\">");
			output.write(AmbitResource.jsTableSorter("algorithms","pager"));
			output.write(String.format("<table %s id='algorithms' border='0' cellpadding='1' cellspacing='2'>",collapsed?"class='tablesorter'":""));
			if (collapsed) {
				output.write("<caption>");
				int c = 0;
				for (AlgorithmType type: AlgorithmType.values()) {
					output.write(String.format("&nbsp;<a href=?type=%s>%s</a>%s",
							Reference.encode(type.name()),
							type.getTitle(),
							(c % 12)==11?"<br>":""));
					c++;
				}
				
				output.write("<hr>");
				String alphabet = "abcdefghijklmnopqrstuvwxyz";  
				try {
					output.write(String.format("<a href='?search=' title='List all algorithms'>%s</a>&nbsp","All"));
					output.write(String.format("<a href='' title='Refresh this page'>%s</a>&nbsp","Refresh"));
					output.write("|&nbsp;");
					for (int i=0; i < alphabet.length(); i++) {
						String search = alphabet.substring(i,i+1);
						output.write(String.format("<a href='?search=%s' title='Search for algorithms with name staring with %s'>%s</a>&nbsp",
									search.toUpperCase(),search.toUpperCase(),search.toUpperCase()));
					}
					output.write("|&nbsp;");
					for (int i=0; i < alphabet.length(); i++) {
						String search = alphabet.substring(i,i+1);
						output.write(String.format("<a href='?search=%s' title='Search for algorithms with name staring with %s'>%s</a>&nbsp",
									search.toLowerCase(),search.toLowerCase(),search.toLowerCase()));
					}
				} catch (Exception x) {
					
				}
				output.write("</caption>\n");

				output.write("<thead>");
				output.write("<tr><th align=\"left\">Name</th><th>Description</th><th>Type</th><th>Implementation of</th></tr>"); 
				output.write("</thead>");
			}
			output.write("<tbody>");
		} catch (Exception x) {
			
		}
	}
	public void processItem(Algorithm item, Writer output) {
		try {
			String t = super.getURI(item);
			if (collapsed) {
				String query = item.getImplementationOf()==null?"":
					String.format("<a href='%s?uri=%s' target='ontology'>%s</a>",sparql,Reference.encode(item.getImplementationOf()),item.getImplementationOf());
				
				output.write(String.format("<tr><td align=\"left\"><a href='%s'>%s</a></td><td>%s</td><td  align='right'><a href=?type=%s>%s</a></td><td>%s</td></tr>", 
						t,item.getName(),
						item.isDataProcessing()?"Processes a dataset":"Generates a model"
						,Reference.encode(item.getType()[0])
						,item.getType()[0],
						query
						               ));
			} else {
				
				String target = item.isSupervised()?"<td><label for='prediction_feature'>Target&nbsp;</label></td><td><input type='text' name='prediction_feature' size='60' value='Enter feature URL'></td>":"";
				String features = "<td><label for='feature_uris[]'>X variables&nbsp;</label></td><td><textarea rows='2' cols='45'name='feature_uris[]' alt='independent variables'></textarea></td>";
				
				if (item.hasType(AlgorithmType.Finder)) {
					output.write("<caption>Find structures by querying online services by compound identifier</caption>");
					output.write(String.format("<form action='' method='%s' name='form'>","POST"));
					output.write(String.format("<tr><th>Dataset URI</td><td><input type='text'  size='120'  name='%s' value='' title='URI of the dataset, e.g. http://host/ambit2/dataset/1'></th></tr>",OpenTox.params.dataset_uri));
					output.write(String.format("<tr><th>Dataset column, containing the identifier (OpenTox Feature URI)</th><td><input type='text' size='120' title='URI of the dataset feature (e.g. http://host/ambit2/feature/2), containing the identifier (e.g. CAS)' name='%s' value=''></td></tr>",OpenTox.params.feature_uris));
					//site
					output.write(String.format("<tr><th>Web site</th><td>"));
					output.write("<select name='search'>");
					for (AbstractFinder.SITE site : AbstractFinder.SITE.values())
						output.write(String.format("<option value='%s' %s %s title='%s'>%s</option>",
								site.name(),
								AbstractFinder.SITE.CIR.equals(site)?"selected":"",
								site.isEnabled()?"":"disabled",
								site.getURI(),		
								site.getTitle()));
					output.write("</select>");
					output.write("</td></tr>");					
					//mode
					output.write(String.format("<tr><th>How to process and store the results</th><td>"));
					output.write("<select name='mode'>");
					for (AbstractFinder.MODE mode : AbstractFinder.MODE.values())
						output.write(String.format("<option value='%s' %s>%s</option>",
								mode.name(),
								AbstractFinder.MODE.emptyonly.equals(mode)?"selected":"",
								mode.getDescription()));
					output.write("</select>");
					output.write("</td></tr>");
					output.write(String.format("<tr><td><input type='submit' name='launch' value='%s'></td></tr>","Find"));
					output.write("</form>");
					
				} else if (item.hasType(AlgorithmType.SuperService)) {
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
				} else if (item.hasType(AlgorithmType.SuperBuilder)) {
					output.write("<tr><th>Model builder</th><th>Calculate descriptors, prepares a dataset and builds the model</th></tr>");
					output.write(String.format("<form action='' method='%s' name='form'>","POST"));
					output.write(String.format("<tr><td>Dataset URI</td><td><input type='text'  size='120'  name='%s' value=''></td></tr>",OpenTox.params.dataset_uri));
					output.write(String.format("<tr><td>Prediction feature URI</td><td><input type='text' size='120' name='%s' value=''></td></tr>",OpenTox.params.target));
					output.write(String.format("<tr><td title='If no learning algorithm specified, only builds a dataset with all features'>Learning algorithm URI (e.g. regression)</td><td><input type='text' size='120' name='%s' value=''></td></tr>",OpenTox.params.model_learning));
					for ( int i=0; i < 10; i++)
						output.write(String.format("<tr><td>Descriptor calculation algorithm URI</td><td><input type='text' size='120' name='%s' value=''></td></tr>",OpenTox.params.feature_calculation));
					
					output.write(String.format("<tr><td>Dataset service URI</td><td><input type='text' size='120' name='%s' value=''></td></tr>",OpenTox.params.dataset_service));
					output.write(String.format("<tr><td><input type='submit' name='launch' value='%s'></td></tr>","Run"));
					output.write("</form>");		

				} else	if (item.isDataProcessing()) {
						String dataset = item.isRequiresDataset()?"<td><label for='dataset_uri'>Dataset&nbsp;</label></td><td><input type='text' name='dataset_uri' size='60' value='Enter dataset URL'></td>":"";
						output.write(String.format(
								"<tr><form action=\"\" method=\"POST\"><tr><td>Algorithm:&nbsp;<a href='%s'>%s</a></td><td><table><tr>%s</tr><tr>%s</tr></table></td><td><input align='bottom' type=\"submit\" value=\"Run\"></td></form></tr>",
								t,item.getName(),
								dataset,
								target));
				} else {//create a model
					String dataset = item.isRequiresDataset()?"<td><label for='dataset_uri'>Training dataset&nbsp;</label></td><td><input type='text' name='dataset_uri' size='60' value='Enter dataset URL'></td>":"";					
					output.write(String.format(
						"<tr><form action=\"\" method=\"POST\"><tr><td>Algorithm:&nbsp;<a href='%s'>%s</a></td><td><table><tr>%s</tr><tr>%s</tr><tr>%s</tr></table></td><td><input type=\"submit\" value=\"Create model\"></td></form></tr>",
						t,item.getName(),
						dataset,
						features,
						target
						));
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
	
	public synchronized String getOntologyServiceURI()  {
		try {
			Properties properties = new Properties();
			InputStream in = AlgorithmHTMLReporter.class.getClassLoader().getResourceAsStream("ambit2/rest/config/ambit2.pref");
			properties.load(in);
			in.close();	
			return properties.getProperty("service.ontology");
		} catch (Exception x) {
			return null;
		}
	}		
}
