package ambit2.rest.dataset;

import java.io.StringWriter;
import java.io.Writer;

import org.restlet.data.Reference;

import ambit2.db.SourceDataset;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.query.QueryResource;
import ambit2.rest.query.XMLTags;
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
	public DatasetsHTMLReporter(Reference baseRef,boolean collapsed) {
		this(baseRef,baseRef,collapsed);
	}
	public DatasetsHTMLReporter(Reference baseRef,Reference originalRef,boolean collapsed) {
		super(baseRef,collapsed);
	}
	@Override
	protected QueryURIReporter createURIReporter(Reference reference) {
		return new DatasetURIReporter<IQueryRetrieval<SourceDataset>>(reference);
	}
	@Override
	public void footer(Writer output, IQueryRetrieval<SourceDataset> query) {
		try {
			if (collapsed) {
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
			}
		} catch (Exception x) {}
		super.footer(output, query);
	}
	@Override
	public void processItem(SourceDataset dataset, Writer output) {
		try {
			StringWriter w = new StringWriter();
			uriReporter.processItem(dataset, w);
			
			output.write("<br>");
			output.write(String.format(
						"<a href=\"%s\">%s</a>",
						w.toString(),
						dataset.getName()));
			if (!collapsed) {
				output.write("&nbsp;");
				output.write(String.format(
						"<a href=\"%s%s\"><img src=\"%s/images/structures.gif\" alt=\"compounds\" title=\"Browse compounds\" border=\"0\"/></a>",
						w.toString(),
						CompoundResource.compound,
						uriReporter.getBaseReference().toString()));	
				
				output.write("&nbsp;");
				output.write(String.format(
						"<a href=\"%s%s\"><img src=\"%s/images/search.png\" alt=\"query\" title=\"Search compounds\" border=\"0\"/></a>",
						w.toString(),
						QueryResource.query_resource,
						uriReporter.getBaseReference().toString()));
				
			
			} else {
			
			}
		
		} catch (Exception x) {
			
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