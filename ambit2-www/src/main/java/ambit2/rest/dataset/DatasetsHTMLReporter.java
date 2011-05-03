package ambit2.rest.dataset;

import java.io.StringWriter;
import java.io.Writer;
import java.net.URLEncoder;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.AbstractDataset._props;
import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.core.processors.structure.key.IStructureKey;
import ambit2.core.processors.structure.key.IStructureKey.Matcher;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;
import ambit2.rest.facet.DatasetsByEndpoint;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.structure.CompoundResource;

/**Generates html page for {@link QueryDatasetResource}
 * @author nina
 *
 */
public class DatasetsHTMLReporter extends QueryHTMLReporter<ISourceDataset, IQueryRetrieval<ISourceDataset>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;
	public static String fileUploadField = "file";
	public DatasetsHTMLReporter(ResourceDoc doc) {
		this(null,true,doc);
	}
	public DatasetsHTMLReporter(Request baseRef,boolean collapsed,ResourceDoc doc) {
		this(baseRef,baseRef,collapsed,doc);
	}
	public DatasetsHTMLReporter(Request baseRef,Request originalRef,boolean collapsed,ResourceDoc doc) {
		super(baseRef,collapsed,doc);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
		return new DatasetURIReporter<IQueryRetrieval<ISourceDataset>>(request,doc);
	}
	@Override
	public void header(Writer w, IQueryRetrieval<ISourceDataset> query) {
		super.header(w, query);
		/**
		 * /dataset
		 */
		if (collapsed) { 
			uploadUI("",w, query);
			try {
				w.write(String.format("<a href='%s/query%s?%s=%s&condition=startswith' title='List datasets by endpoints'>%s</a><br>",
						uriReporter.getBaseReference(),
						DatasetsByEndpoint.resource,
						MetadatasetResource.search_features.feature_sameas,
						URLEncoder.encode("http://www.opentox.org/echaEndpoints.owl"),
						"Datasets by endpoints"));
			} catch (Exception x) {
				
			}
			
			String alphabet = "abcdefghijklmnopqrstuvwxyz";  
			try {
				w.write(String.format("<a href='?search=' title='List all datasets'>%s</a>&nbsp","All"));
				w.write(String.format("<a href='' title='Refresh this page'>%s</a>&nbsp","Refresh"));
				w.write("|&nbsp;");
				for (int i=0; i < alphabet.length(); i++) {
					String search = alphabet.substring(i,i+1);
					w.write(String.format("<a href='?search=^%s' title='Search for datasets with name staring with %s'>%s</a>&nbsp",
								search.toUpperCase(),search.toUpperCase(),search.toUpperCase()));
				}
				w.write("|&nbsp;");
				for (int i=0; i < alphabet.length(); i++) {
					String search = alphabet.substring(i,i+1);
					w.write(String.format("<a href='?search=^%s' title='Search for datasets with name staring with %s'>%s</a>&nbsp",
								search.toLowerCase(),search.toLowerCase(),search.toLowerCase()));
				}
				w.write("|&nbsp;");
				for (int i=0; i < 10; i++) {
					w.write(String.format("<a href='?search=^%s' title='Search for datasets with name staring with %s'>%s</a>&nbsp",
								i,i,i));
				}			
				w.write("<hr>");
			} catch (Exception x) {
				
			}
		} 
		/**
		 * else /dataset/{id}/metadata
		 */
		
			
	}
	@Override
	public void footer(Writer output, IQueryRetrieval<ISourceDataset> query) {
		super.footer(output, query);
	}
	public void uploadUI(String uri, Writer output, IQueryRetrieval<ISourceDataset> query) {		
		try {
			String[][] methods = new String[][] {
					{"post","Add new dataset","Adds all compounds and data from the file, even empty structures."},
					{"put","Import properties","Import properties only for compounds from the file, which could be found in the database"}
			};
			output.write("<table width='95%' border='1' border-style='solid' >");
			output.write("<tr>");
			for (int i=0; i < methods.length;i ++) {
				String[] method = methods[i];
				output.write("<td width='50%'>\n");
				output.write("<table border='0' width='95%'>");
				output.write("<caption>");
				output.write(String.format("<label accesskey='F' title='%s'>%s</label>",
						method[2],
						String.format("%s (SDF, MOL, SMI, CSV, TXT, ToxML (.xml) file)",method[1])
				)); 	
				output.write("</caption>");
				output.write("<tbody>");
				output.write(String.format("<form method=\"post\" action=\"%s?method=%s\" ENCTYPE=\"multipart/form-data\">",uri,method[0]));
				//file
				output.write("<tr>");
				output.write("<th>File<label title='Mandatory'>*</label></th>");
				output.write("<td>");
				output.write(String.format("<input type=\"file\" name=\"%s\" accept=\"%s\" title='%s' size=\"60\">",
						fileUploadField,
						ChemicalMediaType.CHEMICAL_MDLSDF.toString(),
						String.format("%s (SDF, MOL, SMI, CSV, TXT, ToxML (.xml) file)",method[1]))); 
				output.write("</td>");
				output.write("</tr>");
				//title
				output.write("<tr>");
				output.write("<th>Dataset name</th>");
				output.write("<td>");
				output.write(String.format("<input type=\"text\" name='title' title='%s' size=\"60\">","Dataset name (dc:title)")); 
				output.write("</td>");
				output.write("</tr>");
				//match
				output.write("<tr>");
				output.write("<th>Match</th>");
				output.write("<td>");
				output.write("<select name='match'>");
				for (Matcher matcher : IStructureKey.Matcher.values())
					output.write(String.format("<option title='%s' value='%s' %s>%s</option>",
							    "On import, finds the same compound in the database by matching with the selected criteria \""+matcher.getDescription()+"\"\n",
								matcher.toString(),
								IStructureKey.Matcher.CAS.equals(matcher)?"selected":"",
								(matcher.getDescription().length()>60)?matcher.getDescription().substring(0,60)+"...":matcher.getDescription()));
				output.write("</select>");
				output.write("</td>");
				output.write("</tr>");

				//URL
				output.write("<tr>");
				output.write("<th>URL</th>");
				output.write("<td>");
				output.write(String.format("<input type=\"text\" name='seeAlso' title='%s' size=\"60\">","Related URL (rdfs:seeAlso)")); 
				output.write("</td>");
				
				output.write("</tr>");
				
				output.write("<tr>");
				output.write("<th>License</th>");
				output.write("<td>");
				output.write("<select name='license'>");
				for (ISourceDataset.license license : ISourceDataset.license.values())
					output.write(String.format("<option title='%s' value='%s'>%s</option>",
							    license.getTitle(),
								license.getURI(),
								license.getURI()));
				output.write("</select>");
				output.write("</td>");
				output.write("</tr>");

				output.write("<tr><td align='right'><input type='submit' value='Submit'></td></tr>");
				output.write("</form>");
				output.write("</tbody>");
				output.write("</table>");
				
				output.write("</td>\n");
			}
			output.write("</tr>");
			output.write("</table>");
			output.write("<hr>");
			//if (collapsed) {

				
				/*
				output.write("<div class=\"actions\"><span class=\"center\">");
				output.write("<form method=\"post\" action=\"?method=put\" ENCTYPE=\"multipart/form-data\">");

				output.write(String.format("<label accesskey=F>%s&nbsp;<input type=\"file\" name=\"%s\" accept=\"%s\" size=\"80\"></label>",
						"Import properties (SDF, MOL, SMI, CSV, TXT, ToxML (.xml) file)",
						fileUploadField,
						ChemicalMediaType.CHEMICAL_MDLSDF.toString())); 
				
				output.write("<select name='match'>");
				for (Matcher matcher : IStructureKey.Matcher.values())
					output.write(String.format("<option value='%s' %s>%s</option>",
							matcher.toString(),
							IStructureKey.Matcher.CAS.equals(matcher)?"selected":"",
							matcher.getDescription()));
				output.write("</select>");
				
				output.write("<br><input type='submit' value='Submit'>");
				output.write("</form>");
				output.write("</span></div>\n");	
				*/					
			//}
		} catch (Exception x) {}
		
	}
	@Override
	public Object processItem(ISourceDataset dataset) throws AmbitException {
		try {
			StringWriter w = new StringWriter();
			uriReporter.setOutput(w);
			uriReporter.processItem(dataset);
			

			
			if (collapsed) {
				
				output.write("<div id=\"div-1b\">");

				output.write("<div class=\"rowwhite\"><span class=\"left\">");
				output.write("&nbsp;");
				output.write(String.format(
						"<a href=\"%s%s?max=100\"><img src=\"%s/images/table.png\" alt=\"compounds\" title=\"Browse compounds\" border=\"0\"/></a>",
						w.toString(),
						CompoundResource.compound,
						uriReporter.getBaseReference().toString()));	
				
				output.write("&nbsp;");
				output.write(String.format(
						"<a href=\"%s%s\"><img src=\"%s/images/feature.png\" alt=\"features\" title=\"Retrieve feature definitions\" border=\"0\"/></a>",
						w.toString(),
						PropertyResource.featuredef,
						uriReporter.getBaseReference().toString()));	

				
				output.write("&nbsp;");
				output.write(String.format(
						"<a href=\"%s%s?max=100\"><img src=\"%s/images/search.png\" alt=\"/smarts\" title=\"Search compounds by SMARTS\" border=\"0\"/></a>",
						w.toString(),
						"/smarts",
						uriReporter.getBaseReference().toString()));
				output.write("&nbsp;");
				
				output.write(String.format(
						"<a href=\"%s%s?max=100\"><img src=\"%s/images/search.png\" alt=\"/similarity\" title=\"Search for similar compounds within this dataset\" border=\"0\"/></a>",
						w.toString(),
						"/similarity",
						uriReporter.getBaseReference().toString()));
				output.write("&nbsp;");				
				
				output.write("</span><span class=\"center\">");
				MediaType[] mimes = {ChemicalMediaType.CHEMICAL_MDLSDF,

						ChemicalMediaType.CHEMICAL_CML,
						ChemicalMediaType.CHEMICAL_SMILES,						
						MediaType.TEXT_URI_LIST,
						MediaType.APPLICATION_PDF,
						MediaType.TEXT_CSV,
						ChemicalMediaType.WEKA_ARFF,
						MediaType.APPLICATION_RDF_XML
						};
				String[] image = {
						"sdf.jpg",
						"cml.jpg",
						"smi.png",						
						"link.png",
						"pdf.png",
						"excel.png",
						"weka.jpg",
						"rdf.gif"
						
				};		
				for (int i=0;i<mimes.length;i++) {
					MediaType mime = mimes[i];
					output.write("&nbsp;");
					output.write(String.format(
							"<a href=\"%s%s?media=%s&max=100\"  ><img src=\"%s/images/%s\" alt=\"%s\" title=\"%s\" border=\"0\"/></a>",
							w.toString(),
							"",
							//CompoundResource.compound,
							Reference.encode(mime.toString()),
							uriReporter.getBaseReference().toString(),
							image[i],
							mime,
							mime));	
				}				
	

				output.write("&nbsp;");	
				
				output.write(String.format(
						"&nbsp;<a href=\"%s%s\">[Metadata]</a>",
						w.toString(),
						"/metadata"));
				/*
				if (dataset.getLicenseURI()!= null)
					output.write(String.format(
							"&nbsp;<a href=\"%s\" target=_blank title='%s'>[License]</a>",
							dataset.getLicenseURI(),
							dataset.getLicenseURI()
							));			
				else 
					output.write(String.format(
							"&nbsp;<label title='%s'>[License]</label>",
							ISourceDataset.license.Unknown
							));		
				*/
				output.write(String.format(
						"&nbsp;<a href=\"%s?max=100\">%s</a>",
						w.toString(),
						(dataset.getName()==null)||(dataset.getName().equals(""))?Integer.toString(dataset.getID()):dataset.getName()
						));
				output.write("</span></div>");
				output.write("</div>");
			}  else 
				renderMetadata(dataset, w.toString(),null);

			/*
			output.write(String.format(
					"<form method=POST action='%s?method=DELETE'><input type='submit' value='Delete'></form>",
					w.toString()
					));
					*/			

		} catch (Exception x) {
			Context.getCurrentLogger().severe(x.getMessage());
		}
		return null;
	}

	protected Object renderMetadata(ISourceDataset dataset,String uri,IQueryRetrieval<ISourceDataset> query)  throws AmbitException  {
		try {
			//output.write("<h4>Dataset metadata</h4>");
			output.write("<table width='90%'>");
			output.write(String.format("<tr><th>%s</th><td>%s</td></tr>", "Dataset URI",uri));
			output.write(String.format("<tr><th>%s</th><td>%s</td></tr>", "Dataset name",dataset.getName()));
			
			String licenseLabel = dataset.getLicenseURI();
			try {
				if (dataset.getLicenseURI()!=null)
				for (ISourceDataset.license license : ISourceDataset.license.values()) 
					if (license.getURI().equals(dataset.getLicenseURI())) {
						licenseLabel = license.getTitle();
					}
				
			} catch (Exception x) {}
			
			if (dataset.getLicenseURI()!=null)
				output.write(String.format("<tr><th>%s</th><td><a href='%s' target='_blank'>%s</a></td></tr>", 
							"License",
							licenseLabel==null?dataset.getLicenseURI():licenseLabel,
							dataset.getLicenseURI()==null?"NA":dataset.getLicenseURI()));
			else
				output.write("<tr><th>License</th><td>NA</td></tr>"); 
			
			if (dataset.getrightsHolder()!=null)
				output.write(String.format("<tr><th>%s</th><td><a href='%s' target='_blank'>%s</a></td></tr>", 
							"Rights holder",
							dataset.getrightsHolder(),
							dataset.getrightsHolder()));

			else
				output.write("<tr><th>Rights holder</th><td>NA</td></tr>"); 			
						
			output.write(String.format("<tr><th>%s</th><td>%s</td></tr>", "Source",dataset.getSource()));
			
			output.write(String.format("<tr><th>%s</th><td>%s</td></tr>", "<p>",""));
			
			output.write(String.format("<tr><th>%s</th><td><a href='%s'>%s</a></td></tr>", "Browse the dataset",uri,uri));
			output.write(String.format("<tr><th>%s</th><td><a href='%s/compounds'>%s/compounds</a></td></tr>", "Browse the compounds only",uri,uri));
			output.write(String.format("<tr><th>%s</th><td><a href='%s/feature'>%s/feature</a></td></tr>", "Browse the dataset features",uri,uri));
			
			output.write(String.format("<tr><th>%s</th><td>%s</td></tr>", "<p>",""));
			output.write(String.format("<tr><th>%s</th><td><a href='%s/similarity?search=c1ccccc1'>%s/similarity</a></td></tr>", "Search for similar compounds within this dataset",uri,uri));
			output.write(String.format("<tr><th>%s</th><td><a href='%s/smarts?search=c1ccccc1'>%s/smarts</a></td></tr>", "Search compounds by SMARTS",uri,uri));

			//don't write ip addresses
			if ((dataset instanceof SourceDataset) && ((SourceDataset)dataset).getURL().startsWith("http"))
				output.write(String.format("<tr><th>%s</th><td>%s</td></tr>", "See also",((SourceDataset)dataset).getURL()));

			output.write(String.format("<tr><th></th><td colspan='2'><a href='%s/chart/bar?dataset_uri=%s&param=sk1024'  target='_blank'>%s</a></td></tr>", 
					uriReporter.getBaseReference(),uri,"Structure fragments bar chart"));
			output.write(String.format("<tr><th></th><td colspan='2'><a href='%s/chart/bar?dataset_uri=%s&param=fp1024' target='_blank'>%s</a></td></tr>", 
					uriReporter.getBaseReference(),uri,"Hashed fingerprints bar chart"));			
			output.write("</table>");
			
			output.write("<hr>");
			//output.write("<h4>Add more data to this dataset</h4>");
			//uploadUI(uri,output, query);
		} catch (Exception x) {
			
		}
		return dataset;
	}

}
/*
public String getReCaptchaHtml() {
ReCaptcha recaptcha = createReCaptcha();
return recaptcha.createRecaptchaHtml("You did not type the captcha correctly", new Properties());
}

private ReCaptcha createReCaptcha() {
String publicKey = //your public key
String privateKey = //your private key
return ReCaptchaFactory.newReCaptcha(publicKey, privateKey, true);
}

@ValidationMethod(on = "submit")
public void captchaValidation(ValidationErrors errors) {
    ReCaptchaResponse response = createReCaptcha().checkAnswer(context.getRequest().getRemoteAddr(),
            context.getRequest().getParameter("recaptcha_challenge_field"),
            context.getRequest().getParameter("recaptcha_response_field"));
    if (!response.isValid()) {
        errors.add("Captcha", new SimpleError("You didn't type the captcha correctly!"));
    }
}
*/