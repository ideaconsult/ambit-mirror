package ambit2.rest.dataset;

import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Logger;

import org.restlet.data.MediaType;
import org.restlet.data.Request;

import ambit2.base.exceptions.AmbitException;
import ambit2.db.SourceDataset;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.query.QueryResource;
import ambit2.rest.structure.CompoundResource;

/**Generates html page for {@link QueryDatasetResource}
 * @author nina
 *
 */
public class DatasetsHTMLReporter extends QueryHTMLReporter<SourceDataset, IQueryRetrieval<SourceDataset>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;
	public static String fileUploadField = "fileUpload";
	public DatasetsHTMLReporter() {
		this(null,true);
	}
	public DatasetsHTMLReporter(Request baseRef,boolean collapsed) {
		this(baseRef,baseRef,collapsed);
	}
	public DatasetsHTMLReporter(Request baseRef,Request originalRef,boolean collapsed) {
		super(baseRef,collapsed);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request) {
		return new DatasetURIReporter<IQueryRetrieval<SourceDataset>>(request);
	}
	@Override
	public void footer(Writer output, IQueryRetrieval<SourceDataset> query) {
		try {
			//if (collapsed) {
				output.write("\n<hr>");
				output.write("<div class=\"actions\"><span class=\"center\">");
				output.write("<form method=\"post\" ENCTYPE=\"multipart/form-data\">");

				output.write(String.format("<label accesskey=F>%s&nbsp;<input type=\"file\" name=\"%s\" accept=\"%s\" size=\"80\"></label>",
						"Add new dataset (SDF file)",
						fileUploadField,
						ChemicalMediaType.CHEMICAL_MDLSDF.toString())); 
				output.write("<br><input type='submit' value='Submit'>");
				output.write("</form>");
				output.write("</span></div>\n");		
			//}
		} catch (Exception x) {}
		super.footer(output, query);
	}
	@Override
	public void processItem(SourceDataset dataset) throws AmbitException {
		try {
			StringWriter w = new StringWriter();
			uriReporter.setOutput(w);
			uriReporter.processItem(dataset);
			
			//output.write("<br>");
			output.write("<div id=\"div-1b\">");

			
			if (!collapsed) {
				output.write("&nbsp;");
				output.write(String.format(
						"<a href=\"%s%s\"><img src=\"%s/images/table.png\" alt=\"compounds\" title=\"Browse compounds\" border=\"0\"/></a>",
						w.toString(),
						CompoundResource.compound,
						uriReporter.getBaseReference().toString()));	
				
				output.write("&nbsp;");
				output.write(String.format(
						"<a href=\"%s%s\"><img src=\"%s/images/feature.png\" alt=\"feature_definition\" title=\"Retrieve feature definitions\" border=\"0\"/></a>",
						w.toString(),
						PropertyResource.featuredef,
						uriReporter.getBaseReference().toString()));	

				
				output.write("&nbsp;");
				output.write(String.format(
						"<a href=\"%s%s\"><img src=\"%s/images/search.png\" alt=\"query/smarts\" title=\"Search compounds with smarts\" border=\"0\"/></a>",
						w.toString(),
						QueryResource.query_resource+"/smarts",
						uriReporter.getBaseReference().toString()));
				output.write("&nbsp;");
				MediaType[] mimes = {ChemicalMediaType.CHEMICAL_MDLSDF,

						ChemicalMediaType.CHEMICAL_CML,
						ChemicalMediaType.CHEMICAL_SMILES,						
						MediaType.TEXT_URI_LIST,
						MediaType.TEXT_XML,
						MediaType.APPLICATION_PDF,
						MediaType.TEXT_CSV,
						ChemicalMediaType.WEKA_ARFF						
						};
				String[] image = {
						"sdf.jpg",
						"cml.jpg",
						"smi.png",						
						"link.png",
						"xml.png",
						"pdf.png",
						"excel.png",
						"weka.jpg"						
						
				};		
				for (int i=0;i<mimes.length;i++) {
					MediaType mime = mimes[i];
					output.write("&nbsp;");
					output.write(String.format(
							"<a href=\"%s%s?accept-header=%s\"  ><img src=\"%s/images/%s\" alt=\"%s\" title=\"%s\" border=\"0\"/></a>",
							w.toString(),
							"",
							//CompoundResource.compound,
							mime,
							uriReporter.getBaseReference().toString(),
							image[i],
							mime,
							mime));	
				}				
	
			
			} else {
			
			}
			output.write(String.format(
					"&nbsp;<a href=\"%s\">%s</a>",
					w.toString(),
					dataset.getName()));
			
			/*
			output.write(String.format(
					"&nbsp;<a href=\"%s\">%s</a>",
					w.toString(),
					dataset.getReference().getTitle()));			
			*/
			output.write("</div>");
		} catch (Exception x) {
			x.printStackTrace();
			Logger.getLogger(getClass().getName()).severe(x.getMessage());
		}
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