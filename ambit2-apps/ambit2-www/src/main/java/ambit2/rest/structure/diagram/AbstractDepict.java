package ambit2.rest.structure.diagram;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.imageio.ImageIO;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.r.AbstractReporter;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.engine.util.Base64;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.rendering.CompoundImageTools.Mode2D;
import ambit2.rest.AmbitResource;
import ambit2.rest.ProtectedResource;
import ambit2.rest.StringConvertor;
import ambit2.rest.query.QueryResource;
import ambit2.rest.query.StructureQueryResource.QueryType;

/**
 * Returns PNG given a smiles
 * 
 * @author nina
 * 
 */
public class AbstractDepict extends ProtectedResource {
    public static final String resource = "/depict";
    protected Form params;
    protected String[] smiles;
    protected String smarts;
    protected String smirks;
    protected String recordType = null;
    protected QueryType qType = QueryType.smiles;
    protected boolean headless = false;
    int w = 400;
    int h = 200;
    public final static String gplus = "<g:plusone size='small' href='%s'></g:plusone>";
    private static final String header_gplus = "<script type='text/javascript' src='https://apis.google.com/js/plusone.js'></script>";
    private final static String gplus_snippet = "<span style='display:none;' itemprop=\"name\">%s</span><span  style='display:none;' itemprop=\"description\">%s</span><img itemprop=\"image\" src=\"%s\">";

    protected static String printGPlusSnippet(String title, String description, String image) {
	return String.format(gplus_snippet, title, description, image == null ? "" : image);
    }

    /**
     * Might be ignored, currently only CDK depict considers the flags
     */
    protected Mode2D displayMode = null;

    public String getSmirks() {
	return smirks;
    }

    public void setSmirks(String smirks) {
	this.smirks = smirks;
    }

    @Override
    protected void doInit() throws ResourceException {
	super.doInit();
	this.getVariants().add(new Variant(MediaType.TEXT_HTML));
    }

    protected BufferedImage getImage(String smiles, int width, int height, String recordType, QueryType type)
	    throws ResourceException {
	return null;
    }

    protected Form getParams() {
	if (params == null)
	    if (Method.GET.equals(getRequest().getMethod()))
		params = getResourceRef(getRequest()).getQueryAsForm();
	    // if POST, the form should be already initialized
	    else
		params = getRequest().getEntityAsForm();
	try {
	    headless = Boolean.parseBoolean(params.getFirstValue("headless"));
	} catch (Exception x) {
	    headless = false;
	}
	return params;
    }

    protected String getTitle(Reference ref, String... smiles) throws ResourceException {

	String style = "depictBox";
	StringBuilder b = new StringBuilder();
	b.append("<table width='100%'>");
	b.append("<tr><td>");

	Reference root = (Reference) ref.clone();
	root.setQuery(null);
	String uri;
	String displaySmiles = smiles != null && (smiles.length > 0) ? smiles[0] : "";
	/*
	 * uri = String.format("%s/daylight",root);
	 * 
	 * b.append(AmbitResource.printWidget( String.format(
	 * "<a href='%s?search=%s'>%s</a>&nbsp;<span style='float:right;'>%s</span>"
	 * ,
	 * uri,smiles==null?"":Reference.encode(smiles[0]),"Daylight depiction"
	 * ,String.format(gplus,uri)), String.format(
	 * "<img id='daylight' src='%s?search=%s' alt='%s' title='%s' onError=\"hideDiv('daylight')\">"
	 * , uri, Reference.encode(displaySmiles), displaySmiles,displaySmiles
	 * ), style )); b.append("</td>");
	 * 
	 * b.append("<td>");
	 */
	uri = String.format("%s/cdk", root);
	b.append(AmbitResource.printWidget(String.format(
		"<a href='%s?search=%s%s%s'>%s</a>&nbsp;<span style='float:right;'>%s</span>", uri, smiles == null ? ""
			: Reference.encode(smiles[0]), smarts == null ? "" : "&smarts=", smarts == null ? ""
			: Reference.encode(smarts), "CDK depiction", String.format(gplus, uri)), String.format(
		"<img id='cdk' src='%s/any?search=%s&smarts=%s' alt='%s' title='%s' onError=\"hideDiv('cdk')\">", uri,
		Reference.encode(displaySmiles), smarts == null ? "" : Reference.encode(smarts), displaySmiles,
		displaySmiles), style));
	b.append("</td>");
	b.append("<td>");
	uri = String.format("%s/cactvs", root);
	b.append(AmbitResource.printWidget(String.format(
		"<a href='%s?search=%s'>%s</a>&nbsp;<span style='float:right;'>%s</span>", uri,
		Reference.encode(displaySmiles), "Cactvs depiction", String.format(gplus, uri)), String.format(
		"<img id='cactvs' src='%s?search=%s' alt='%s' title='%s' onError=\"hideDiv('cactvs')\">", uri,
		Reference.encode(displaySmiles), displaySmiles, displaySmiles), style));
	b.append("</td></tr><tr><td>");
	uri = String.format("%s/obabel", root);
	b.append(AmbitResource.printWidget(
		String.format("<a href='%s?search=%s'>%s</a>&nbsp;<span style='float:right;'>%s</span>", uri,
			Reference.encode(displaySmiles), "Open Babel depiction", String.format(gplus, uri)),
		String.format(
			"<img id='obabel' src='%s?search=%s&w=%d&h=%d' alt='%s' title='%s' onError=\"hideDiv('obabel')\" width='%d' heigth='%d'>",
			uri, Reference.encode(displaySmiles), h, h, displaySmiles, displaySmiles, h, h), style));
	b.append("</td>");

	b.append("<td>");

	String recordTypeOption = recordType == null ? "" : String.format("&record_type=%s", recordType);
	uri = String.format("%s/pubchem", root);
	b.append(AmbitResource.printWidget(String.format(
		"<a href='%s?search=%s%s'>%s</a>&nbsp;<span style='float:right;'>%s</span>", uri,
		Reference.encode(displaySmiles), recordTypeOption, "PubChem depiction", String.format(gplus, uri)),
		String.format(
			"<img id='pubchem' src='%s?search=%s%s' alt='%s' title='%s' onError=\"hideDiv('pubchem')\">",
			uri, Reference.encode(displaySmiles), recordTypeOption, displaySmiles, displaySmiles), style));
	b.append("</td><td>");
	b.append("</td></tr>");
	b.append("</table>");
	b.append(getGPlusSnippet());
	return b.toString();

    }

    @Override
    public Representation get(Variant variant) {
	setFrameOptions("SAMEORIGIN");
	return process(variant);
    }

    public Representation process(Variant variant) {

	try {

	    Form form = getParams();
	    w = 400;
	    h = 200;
	    recordType = "2d";
	    try {
		w = Integer.parseInt(form.getFirstValue("w"));
	    } catch (Exception x) {
		w = 400;
	    }
	    try {
		h = Integer.parseInt(form.getFirstValue("h"));
	    } catch (Exception x) {
		h = 200;
	    }
	    try {
		recordType = form.getFirstValue("record_type");
	    } catch (Exception x) {
	    }
	    try {
		qType = QueryType.valueOf(form.getFirstValue("type"));
	    } catch (Exception x) {
		qType = QueryType.smiles;
	    }
	    switch (qType) {
	    case mol: { // base64 encoded mol files
		smiles = form.getValuesArray(QueryResource.b64search_param);
		if (smiles != null)
		    for (int i = 0; i < smiles.length; i++)
			smiles[i] = new String(Base64.decode(smiles[i]));
		break;
	    }
	    default: {
		smiles = form.getValuesArray(QueryResource.search_param);
		if ((smiles == null) || (smiles.length < 1))
		    smiles = new String[] { null };
		else
		    smiles[0] = smiles[0] == null ? "" : smiles[0].trim();
	    }
	    }

	    setSmarts(form.getFirstValue("smarts"));
	    setSmirks(null);
	    String[] smirks_patterns = form.getValuesArray("smirks");
	    for (String sm : smirks_patterns)
		if (sm != null) {
		    setSmirks(sm);
		    break;
		}

	    if (variant.getMediaType().equals(MediaType.TEXT_HTML) || (smiles == null)) {
		StringConvertor convertor = new StringConvertor(new AbstractReporter<String, Writer>() {
		    /**
				     * 
				     */
		    private static final long serialVersionUID = -6791292888357737097L;

		    public void close() throws Exception {
		    };

		    public Writer process(String target) throws AmbitException {
			try {
			    if (headless)
				output.write(target);
			    else {

				AmbitResource.writeTopHeader(output, smiles[0] == null ? "2D structural diagram"
					: smiles[0], getRequest(), getResourceRef(getRequest()), header_gplus);
				writeSearchForm(output, smiles[0] == null ? "N/A" : smiles[0], getRequest(), "",
					Method.GET, params);
				output.write(target);

				AmbitResource.writeHTMLFooter(output, smiles[0] == null ? "" : smiles[0], getRequest());
			    }
			} catch (Exception x) {
			}
			return output;
		    };
		}, MediaType.TEXT_HTML);
		return convertor.process(getTitle(getResourceRef(getRequest()), smiles));
	    }
	    final BufferedImage image;
	    if (smiles[0] != null) {
		image = getImage(smiles[0], w, h, recordType, qType);
		if (image == null) {
		    getResponse()
			    .setStatus(Status.CLIENT_ERROR_BAD_REQUEST, String.format("Invalid smiles %s", smiles));
		    return null;
		}
	    } else
		image = createDefaultImage(w, h);
	    return new OutputRepresentation(MediaType.IMAGE_PNG) {
		@Override
		public void write(OutputStream out) throws IOException {
		    try {
			ImageIO.write(image, "PNG", out);
		    } catch (IOException x) {
			throw x;
		    } catch (Exception x) {

		    } finally {
			try {
			    out.flush();
			} catch (Exception x) {
			}
			try {
			    out.close();
			} catch (Exception x) {
			}
		    }
		}
	    };

	} catch (ResourceException x) {
	    getResponse().setStatus(x.getStatus(), x, x.getMessage());
	    return null;
	} catch (Exception x) {
	    getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, x, x.getMessage());
	    return null;

	}
    }

    @Override
    protected Representation post(Representation entity, Variant variant) throws ResourceException {
	if (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType())) {
	    if (params == null)
		params = new Form(entity);
	    return get(variant);
	} else
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, String.format("%s not supported",
		    entity == null ? "" : entity.getMediaType()));
    }

    public void writeSearchForm(Writer w, String title, Request request, String meta, Method method, Form params)
	    throws IOException {
	Reference baseReference = request == null ? null : request.getRootRef();
	w.write("<table width='100%' bgcolor='#ffffff'>");
	w.write("<tr>");
	w.write("<td align='left' width='256px'>");
	w.write(String
		.format("<a href=\"http://ambit.sourceforge.net/intro.html\"><img itemprop=\"image\" src='%s/images/ambit-logo.png' width='256px' alt='%s' title='%s' border='0'></a>\n",
			baseReference, "AMBIT", baseReference));
	w.write("</td>");
	w.write("<td align='center'>");
	String query_smiles = "";
	try {
	    Form form = getParams(params, request);
	    if ((form != null) && (form.size() > 0)) {
		query_smiles = form.getFirstValue(QueryResource.search_param);
		if (query_smiles != null)
		    query_smiles = query_smiles.trim();
	    } else
		query_smiles = null;
	} catch (Exception x) {
	    query_smiles = "";
	}

	w.write(String.format("<form action='' method='%s'>\n", method));
	w.write("<table width='100%'>");
	w.write("<tr>");
	w.write(String.format("<th><label for='%s'>%s</label>", QueryResource.search_param, "SMILES or InChI"));
	/*
	 * w.write(String.format(
	 * "&nbsp;<input type='image' src=\"%s/images/edit.png\" title='Draw molecule' onClick='startEditor(\"%s\");' tabindex='3'>"
	 * , request.getRootRef(),request.getRootRef()));
	 */

	w.write("</th>");
	w.write("<td>");
	w.write(String.format("<input name='%s' size='70' value='%s' tabindex='0'>", QueryResource.search_param,
		query_smiles == null ? "" : query_smiles.trim()));

	w.write("</td>");

	w.write("<td>");

	w.write("</td>");
	w.write("</tr>\n");

	w.write("<tr>");
	w.write(String.format("<th><label for='%s'>%s</label></th>", "smarts", "SMARTS (optional)"));
	w.write("<td>");
	w.write(String
		.format("<input name='%s' size='70' value='%s' title='Highlights the substructure, specified by SMARTS' tabindex='1'>",
			"smarts", getSmarts() == null ? "" : getSmarts()));
	w.write("</td>");
	w.write("<td>");
	w.write("<input type='submit' value='Display' tabindex='2'>");
	w.write("</td>");
	w.write("</tr>\n");

	w.write("</table>");
	// w.write(baseReference.toString());

	w.write("</form>\n");

	w.write("</td>");
	w.write("<td align='right' valign='bottom' width='256px'>");
	// w.write("<span style='float:right;'>");
	w.write(String.format(gplus, String.format("%s%s?search=%s", baseReference, resource, smiles)));
	// w.write("<br>");
	// w.write(String.format(AmbitResource.facebook,String.format("%s%s?smiles=%s",baseReference,resource,smiles)));
	// w.write("</span>");
	// w.write(AmbitResource.disclaimer);
	w.write("</td>");
	w.write("</tr></table>");

	w.write("<hr>");

    }

    protected static Form getParams(Form params, Request request) {
	if (params == null)
	    if (Method.GET.equals(request.getMethod()))
		params = request.getResourceRef().getQueryAsForm();
	    // if POST, the form should be already initialized
	    else
		params = request.getEntityAsForm();
	return params;
    }

    public String getSmarts() {
	return smarts;
    }

    public void setSmarts(String smarts) {
	this.smarts = smarts;

    }

    protected BufferedImage createDefaultImage(int w, int h) {
	BufferedImage buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

	Graphics2D g = buffer.createGraphics();
	g.setColor(new Color(0x00ffffff, true));// white transparent);
	g.fillRect(0, 0, w, h);
	return buffer;
    }

    protected String getGPlusSnippet() {
	return printGPlusSnippet("Chemical structure diagram comparison",
		"Chemical structure diagram, generated by different providers.", null
	// String.format("%s%s%s?search=%s",getRequest().getRootRef(),resource,"/cactvs",smiles)
	// this shows one more image!!
	);
    }
}