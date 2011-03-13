package ambit2.rest.dataset;

import java.io.StringWriter;
import java.io.Writer;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.ISourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.core.processors.structure.key.IStructureKey;
import ambit2.core.processors.structure.key.IStructureKey.Matcher;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;
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
	@Override
	public void footer(Writer output, IQueryRetrieval<ISourceDataset> query) {
		try {
			//if (collapsed) {
				output.write("\n<p>");
				output.write("<div class=\"actions\"><span class=\"center\">");
				output.write("<form method=\"post\" ENCTYPE=\"multipart/form-data\">");

				output.write(String.format("<label accesskey=F>%s&nbsp;<input type=\"file\" name=\"%s\" accept=\"%s\" size=\"80\"></label>",
						"Add new dataset (SDF, MOL, SMI, CSV, TXT file)",
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
				
				output.write("<div class=\"actions\"><span class=\"center\">");
				output.write("<form method=\"post\" action=\"?method=put\" ENCTYPE=\"multipart/form-data\">");

				output.write(String.format("<label accesskey=F>%s&nbsp;<input type=\"file\" name=\"%s\" accept=\"%s\" size=\"80\"></label>",
						"Import properties (SDF, MOL, SMI, CSV, TXT file)",
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
			//}
		} catch (Exception x) {}
		super.footer(output, query);
	}
	@Override
	public Object processItem(ISourceDataset dataset) throws AmbitException {
		try {
			StringWriter w = new StringWriter();
			uriReporter.setOutput(w);
			uriReporter.processItem(dataset);
			
			//output.write("<br>");
			output.write("<div id=\"div-1b\">");

			output.write("<div class=\"rowwhite\"><span class=\"left\">");
			
			if (!collapsed) {
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
						"<a href=\"%s%s?max=100\"><img src=\"%s/images/search.png\" alt=\"/smarts\" title=\"Search compounds with SMARTS\" border=\"0\"/></a>",
						w.toString(),
						"/smarts",
						uriReporter.getBaseReference().toString()));
				output.write("&nbsp;");
				
				output.write(String.format(
						"<a href=\"%s%s?max=100\"><img src=\"%s/images/search.png\" alt=\"/similarity\" title=\"Search for similarcompounds within this dataset\" border=\"0\"/></a>",
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
				
			} else {
			
			}
			
			output.write(String.format(
					"&nbsp;<a href=\"%s?max=100\">%s</a>",
					w.toString(),
					(dataset.getName()==null)||(dataset.getName().equals(""))?Integer.toString(dataset.getID()):dataset.getName()
					));
			/*
			output.write(String.format(
					"<form method=POST action='%s?method=DELETE'><input type='submit' value='Delete'></form>",
					w.toString()
					));
					*/			
			output.write("</span></div>");
			/*
			output.write(String.format(
					"&nbsp;<a href=\"%s\">%s</a>",
					w.toString(),
					dataset.getReference().getTitle()));			
			*/
			output.write("</div>");
		} catch (Exception x) {
			Context.getCurrentLogger().severe(x.getMessage());
		}
		return null;
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