package ambit2.rest.structure;

import java.awt.Dimension;
import java.io.IOException;
import java.io.Writer;
import java.net.URLEncoder;
import java.sql.Connection;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.RetrieveGroupedValuesByAlias;
import ambit2.db.readers.RetrieveProfileValues;
import ambit2.db.readers.RetrieveProfileValues.SearchMode;
import ambit2.rest.AmbitResource;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DisplayMode;
import ambit2.rest.OpenTox;
import ambit2.rest.QueryStructureHTMLReporter;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.query.QueryResource;

/**
 * Generates HTML file with links to structures . TODO - make use of a template
 * engine
 * 
 * @author nina
 * 
 * @param <Q>
 */
@Deprecated
public class CompoundHTMLReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryStructureHTMLReporter<Q> {
    /**
	 * 
	 */
    private static final long serialVersionUID = -7776155843790521467L;
    protected PropertyURIReporter pReporter;
    protected DisplayMode _dmode = DisplayMode.table;
    protected int count = 0;
    protected String hilightPredictions = null;
    protected Dimension cellSize = new Dimension(150, 150);
    protected Form featureURI = null;
    protected String imgMime = "image/png";
    boolean hierarchy = false;

    // protected RetrieveFieldPropertyValue fieldQuery;

    public CompoundHTMLReporter(Request request, DisplayMode _dmode, QueryURIReporter urireporter, boolean headless) {
	this(request, _dmode, urireporter, null, headless);
    }

    public CompoundHTMLReporter(Request request, DisplayMode _dmode, QueryURIReporter urireporter, Template template,
	    boolean headless) {
	this(request, _dmode, urireporter, template, null, null, headless);
    }

    public CompoundHTMLReporter(Request request, DisplayMode _dmode, QueryURIReporter urireporter, Template template,
	    Profile groupedProperties, Dimension d, boolean headless) {
	this("", request, _dmode, urireporter, template, groupedProperties, d, headless);
    }

    public CompoundHTMLReporter(String prefix, Request request, DisplayMode _dmode, QueryURIReporter urireporter,
	    Template template, Profile groupedProperties, Dimension d, boolean headless) {
	super(prefix, request, _dmode, headless);

	Reference f = request.getResourceRef().clone();
	f.setQuery(null);
	featureURI = new Form();

	String[] features = request.getResourceRef().getQueryAsForm()
		.getValuesArray(OpenTox.params.feature_uris.toString());
	if ((features == null) || (features.length == 0)) {
	    if (f.toString().indexOf("/dataset") > 0)
		featureURI.add(OpenTox.params.feature_uris.toString(), f.addSegment("feature").toString());
	    else if (f.toString().indexOf("/compound") > 0)
		featureURI.add(OpenTox.params.feature_uris.toString(), f.addSegment("feature").toString());
	} else
	    for (String ff : features)
		featureURI.add(OpenTox.params.feature_uris.toString(), ff);

	if (d != null)
	    cellSize = d;
	setGroupProperties(groupedProperties);
	setTemplate(template == null ? new Template(null) : template);
	if (urireporter != null)
	    this.uriReporter = urireporter;

	hilightPredictions = request.getResourceRef().getQueryAsForm().getFirstValue("model_uri");
	pReporter = new PropertyURIReporter(request);

	// table = isCollapsed();
	this._dmode = headless ? DisplayMode.properties : isCollapsed() ? DisplayMode.table : _dmode;
	getProcessors().clear();
	if ((getGroupProperties() != null) && (getGroupProperties().size() > 0))
	    getProcessors().add(
		    new ProcessorStructureRetrieval(new RetrieveGroupedValuesByAlias(getGroupProperties())) {
			/**
			     * 
			     */
			private static final long serialVersionUID = 9004511979943993256L;

			@Override
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
			    ((RetrieveGroupedValuesByAlias) getQuery()).setRecord(target);
			    return super.process(target);
			}
		    });
	if (getTemplate().size() > 0)
	    getProcessors().add(
		    new ProcessorStructureRetrieval(new RetrieveProfileValues(SearchMode.idproperty, getTemplate(),
			    true)) {
			/**
			     * 
			     */
			private static final long serialVersionUID = 5429970636459346674L;

			@Override
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
			    ((RetrieveProfileValues) getQuery()).setRecord(target);
			    return super.process(target);
			}
		    });

	getProcessors().add(new DefaultAmbitProcessor<IStructureRecord, IStructureRecord>() {
	    /**
		     * 
		     */
	    private static final long serialVersionUID = 7308733066555175709L;

	    public IStructureRecord process(IStructureRecord target) throws AmbitException {
		processItem(target);
		return target;
	    };
	});

    }

    @Override
    public Writer getOutput() throws Exception {
	Writer w = super.getOutput();
	pReporter.setOutput(w);
	uriReporter.setOutput(w);
	return w;
    }

    public CompoundHTMLReporter(Request request,  DisplayMode _dmode, Template template,
	    boolean headless) {
	this(request,  _dmode, null, template, headless);
    }

    public CompoundHTMLReporter(Request request, DisplayMode _dmode, boolean headless) {
	this(request,  _dmode, null, null, headless);

    }

    @Override
    protected QueryURIReporter createURIReporter(Request request,ResourceDoc doc) {
	return new CompoundURIReporter<IQueryRetrieval<IStructureRecord>>(prefix, request,false);
    }

    @Override
    public Object processItem(IStructureRecord record) throws AmbitException {

	try {
	    switch (_dmode) {
	    case table: {
		// output.write("<tr>");
		output.write(toURITable(record));
		break;
		// output.write("</tr>\n");
	    }
	    case properties: {
		if (!headless)
		    output.write(String.format("<a href='%s'><img src='%s'></a>", uriReporter.getURI(record),
			    getImageUri(record)));
		output.write(printProperties(record));
		break;
	    }
	    default: {
		// output.write("<div>");
		output.write(toURI(record));
		// output.write("</div>");
	    }
	    }
	    count++;
	} catch (Exception x) {
	    Context.getCurrentLogger().severe(x.getMessage());
	}
	return null;

    }

    protected String printProperties(IStructureRecord record) throws IOException {

	List<Property> props = template2Header(getTemplate(), true);
	Hashtable<_type, StringBuilder> map = new Hashtable<_type, StringBuilder>();

	for (Property p : props) {
	    Object value = record.getProperty(p);
	    if (value == null)
		continue;
	    StringBuilder b = map.get(p.getReference().getType());
	    if (b == null) {
		b = new StringBuilder();
		b.append(String.format("<h3>%s</h3>", p.getReference().getType()));
		b.append("<div><table>");
		map.put(p.getReference().getType(), b);
	    }
	    String type = p.getReference().getName();
	    String name = p.getName().replace("http://www.opentox.org/api/1.1#", "");
	    switch (p.getReference().getType()) {
	    case Algorithm: {
		String uri = String.format("%s/%s/%s'", uriReporter.getBaseReference(), OpenTox.URI.algorithm, p
			.getReference().getName());
		type = String.format("<a href='%s' title='Calculated by OpenTox algorithm \n%s' target=_blank>%s</a>",
			uri, uri, name);
		name = "";
		break;
	    }
	    case Model: {
		String uri = String.format("%s/%s/%s'", uriReporter.getBaseReference(), OpenTox.URI.model, p
			.getReference().getName());
		type = String.format("<a href='%s' title='Calculated by OpenTox model \n%s' target=_blank>%s</a>", uri,
			uri, name);
		name = "";
		break;
	    }
	    case Dataset: {
		if (p.getReference().getURL().startsWith("http"))
		    type = String.format("<a href='%s' target='_blank' title='See also %s'>%s</a>", p.getReference()
			    .getURL(), p.getReference().getURL(), p.getReference().getName());
		break;
	    }
	    case BibtexEntry: {
		if (p.getReference().getTitle().equals("Default")) {
		    type = "";
		    break;
		}
		if (p.getReference().getURL().startsWith("http"))
		    type = String.format("<a href='%s' target='_blank' title='See also %s'>%s</a>", p.getReference()
			    .getURL(), p.getReference().getURL(), p.getReference().getName());
		break;
	    }
	    default: {
		break;
	    }
	    }
	    String v = value.toString().replace("\n", "<br>");
	    b.append(String.format("<tr><td>%s</td><th>%s <i>%s</i></th><td title='%s'>%s</td></tr>", type, name,
		    p.getUnits(), v, v.length() > 100 ? v.substring(0, 100) : v));
	}
	StringBuilder all = new StringBuilder();
	all.append("<div class='accordion'>");
	Enumeration types = map.keys();
	while (types.hasMoreElements()) {
	    all.append(map.get(types.nextElement()).toString());
	    all.append("</table></div>");
	}
	all.append("</div>");
	return all.toString();
    }

    protected String content(String left, String right) throws IOException {
	return String.format(
		"<div class=\"rowwhite\"><span class=\"left\">%s</span><span class=\"center\">%s</span></div>", left,
		right);

    }

    protected String downloadLinks() throws IOException {
	StringBuilder w = new StringBuilder();
	MediaType[] mimes = { ChemicalMediaType.CHEMICAL_MDLSDF, ChemicalMediaType.CHEMICAL_CML,
		ChemicalMediaType.CHEMICAL_SMILES, ChemicalMediaType.CHEMICAL_INCHI, MediaType.TEXT_URI_LIST,
		MediaType.APPLICATION_PDF, MediaType.TEXT_CSV, MediaType.TEXT_PLAIN, ChemicalMediaType.WEKA_ARFF,
		ChemicalMediaType.THREECOL_ARFF, MediaType.APPLICATION_RDF_XML };
	String[] image = { "sdf.jpg", "cml.jpg", "smi.png", "inchi.png", "link.png", "pdf.png", "excel.png",
		"excel.png", "weka.png", "weka.png", "rdf.gif"

	};
	String q = uriReporter.getResourceRef().getQuery();
	for (int i = 0; i < mimes.length; i++) {
	    MediaType mime = mimes[i];
	    w.append("&nbsp;");
	    w.append(String.format(
		    "<a href=\"?%s%smedia=%s\"  ><img src=\"%s/images/%s\" alt=\"%s\" title=\"%s\" border=\"0\"/></a>",
		    q == null ? "" : q, q == null ? "" : "&", Reference.encode(mime.toString()), uriReporter
			    .getBaseReference().toString(), image[i], mime, mime));

	}
	return w.toString();
    }

    public String resultsForm(Q query) {
	return "";
    }

    public void header(Writer w, Q query) {
	try {
	    String query_smiles = "";
	    String query_text = "";
	    Form form = uriReporter.getResourceRef().getQueryAsForm();
	    try {

		query_text = form.getFirstValue("text");
	    } catch (Exception x) {
		query_text = "";
	    }
	    try {

		query_smiles = form.getFirstValue(QueryResource.search_param);
	    } catch (Exception x) {
		query_smiles = "";
	    }
	    String query_property = "";
	    try {

		query_property = form.getFirstValue("property");
	    } catch (Exception x) {
		query_property = "";
	    }
	    String query_threshold = "";
	    try {

		query_threshold = form.getFirstValue("threshold");
	    } catch (Exception x) {
		query_threshold = "0.9";
	    }
	    String maxhits = "";
	    try {

		maxhits = form.getFirstValue(QueryResource.max_hits);
	    } catch (Exception x) {
		maxhits = "1000";
	    }
	    String page = "";
	    try {

		page = form.getFirstValue("page");
	    } catch (Exception x) {
		page = "0";
	    }
	    String pagesize = "";
	    try {

		pagesize = form.getFirstValue("pagesize");
	    } catch (Exception x) {
		pagesize = maxhits;
	    }
	    /**
	     * This determines if similarity searching will be done via smiles
	     * or via URL
	     **/
	    String type = "";
	    try {

		type = form.getFirstValue("type");
	    } catch (Exception x) {
		type = "smiles";
	    }

	    Reference baseReference = uriReporter.getBaseReference();
	    if (!headless) {
		AmbitResource.writeTopHeader(w, isCollapsed() ? "Chemical compounds" : "Chemical compound",
			uriReporter.getRequest(), uriReporter.getResourceRef(), "");

		;

		w.write("<table bgcolor='#ffffff'>");

		w.write("<tr>");
		w.write("<td align='left' width='256px'>");
		w.write(String
			.format("<a href=\"http://ambit.sourceforge.net/intro.html\"><img src='%s/images/ambit-logo.png' width='256px' alt='%s' title='%s' border='0'></a>\n",
				baseReference, "AMBIT", baseReference));
		w.write("</td>");
		w.write("<td align='center'>");

		w.write("<form action='' name='form' method='get'>\n");
		w.write(String.format("<input name='type' type='hidden' value='%s'>\n", type == null ? "smiles" : type));

		String hint = "";
		if (uriReporter.getResourceRef().toString().indexOf("similarity") > 0) {
		    w.write(String.format("<label for='%s'>SMILES</label>&nbsp;", QueryResource.search_param));
		    w.write(String
			    .format("&nbsp;<input type='image' src=\"%s/images/edit.png\" title='Draw molecule' onClick='startEditor(\"%s\");'>",
				    uriReporter.getBaseReference(), uriReporter.getBaseReference()));
		    w.write(String.format("<input name='%s' type='text' size='40' title='Enter SMILES' value='%s'>\n",
			    QueryResource.search_param, query_smiles == null ? "" : query_smiles));

		    w.write("&nbsp;");
		    w.write(String.format("<label for='threshold'>Threshold</label>&nbsp;"));
		    w.write(String
			    .format("<input name='threshold' type='text' title='Tanimoto coefficient threshold [0,1], default 0.9' size='20' value='%s'>\n",
				    query_threshold == null ? "0.9" : query_threshold));

		    hint = "Draw structure and search for similar compounds";
		    // w.write("<input type='submit' value='Search'><br>");

		} else if (uriReporter.getResourceRef().toString().indexOf("compound") > 0) {
		    w.write(String
			    .format("<input name='property' type='text' title='Enter property name (optional)'  size='20' value='%s'>\n",
				    query_property == null ? "" : query_property));
		    w.write("&nbsp;");
		    w.write(String
			    .format("<input name='%s' type='text' title='Enter molecule identifier, name or property value (e.g. benzene)'  size='40' value='%s'>\n",
				    QueryResource.search_param, query_smiles == null ? "" : query_smiles));
		    hint = "Search by property or identifier name (optional) and value";
		    // w.write("<input type='submit' value='Search'><br>");
		} else {
		    w.write("<table>");

		    w.write("<tr><th>");
		    w.write(String
			    .format("<label for='%s' title='Substructure pattern defined by SMARTS language. Enter manually, or use Draw button on the right'>SMARTS</label>&nbsp;",
				    QueryResource.search_param));

		    w.write(String
			    .format("&nbsp;<input type='image' src=\"%s/images/edit.png\" title='Draw substructure' onClick='startEditor(\"%s\");'>",
				    uriReporter.getBaseReference(), uriReporter.getBaseReference()));
		    w.write("</th>");
		    w.write("<td>");
		    w.write(String.format("<input name='%s' type='text'   size='60' value='%s'>\n",
			    QueryResource.search_param, query_smiles == null ? "" : query_smiles));

		    w.write("</td></tr>\n");
		    w.write("<tr><th>");
		    w.write(String
			    .format("<label for='text' title='Any text, compound identifiers, property names and values, test names and values'>Keywords</label>"));
		    w.write("</th><td>");

		    w.write(String
			    .format("<input name='text' type='text' title='Enter text to search for'  size='60' value='%s'><br>\n",
				    query_text == null ? "" : query_text));
		    w.write("</td></tr>\n");
		    hint = "Search for substructure and properties";
		    w.write("</table>");
		}

		// w.write(templates(baseReference));

		// w.write(baseReference.toString());

		// w.write("</form>\n"); moved in footer
		// w.write(hint);
		w.write("<br>\n<b><i>");
		w.write(hint);
		w.write("</i></b>");
		w.write("</td>");
		w.write("<td align='left' valign='center' width='256px'>");
		// w.write(String.format("<a href=\"http://opentox.org\"><img src=\"%s/images/logo.png\" width=\"256\" alt=\"%s\" title='%s' border='0'></a>\n",baseReference,"AMBIT",baseReference));
		w.write("<input type='submit' value='Search'>");
		w.write("</td></tr>\n");
		w.write("</table>");

	    }
	    if (hilightPredictions != null)
		w.write(String
			.format("<div><span class=\"center\"><h4>Atoms highlighted by the model <a href=%s target=_blank>%s</a></h4></span></div>",
				hilightPredictions, hilightPredictions));

	    // output.write(AmbitResource.printWidgetHeader("&nbsp;"));
	    // output.write(AmbitResource.printWidgetContentHeader(""));
	    output.write("<div class=\"ui-widget \" style=\"margin-top: 20px; padding: 0 .7em;\">");
	    switch (_dmode) {
	    case properties: {
		break;
	    }
	    case table: {

		// output.write(String.format("<div><span class=\"left\">%s</span></div>",templates(baseReference)));

		output.write("<div class=\"rowwhite\"><span class=\"center\">");

		// output.write(AmbitResource.jsTableSorter("results","pager"));
		output.write("<table id='results' class='datatable'>");

		output.write(String
			.format("<CAPTION CLASS=\"results\">Search results <input type='text' value='%s' readonly> &nbsp;Download as %s&nbsp;Page:%s&nbsp;%s</CAPTION>",
				query.toString(),
				downloadLinks(),
				String.format(
					"<input name='page' type='text' title='Page' size='10' value='%s'>&nbsp;"
						+ "Page size<input name='pagesize' type='text' title='Page size' size='10' value='%s'>&nbsp;",
					page == null ? "0" : page, pagesize == null ? "100" : pagesize), String.format(
					"<a href='%s' title='%s'>License</a>", getLicenseURI(), getLicenseURI())));// resultsForm(query)
		// ,resultsForm(query)
		output.write("<thead><tr>");
		output.write(String.format("<th width='20'>#</th><th width='%d'>Compound</th>", cellSize.width)); // ECB42C

		List<Property> props = template2Header(getTemplate(), true);
		int hc = 0;

		hierarchy = props.size() > 15;

		if (hierarchy) {
		    output.write("<th>Properties</th>"); // one single cell and
							 // properties written
							 // vertically within
		    output.write("</tr>\n</thead><tbody>");
		} else {
		    for (Property p : props) {
			hc++;
			int max = 10;
			int dot = 0;
			int end = p.getTitle().indexOf("Descriptor");
			if (end > 0) {
			    dot = p.getTitle().lastIndexOf(".");
			    if (dot < 0)
				dot = 0;
			    else
				dot++;
			} else
			    end = p.getTitle().length();
			if ((end - dot) > max)
			    end = dot + max;

			output.write(String.format("<th width='%d'><a href='%s' title='%s'>%s</a></th>", max,
				p.getUrl(), p.getTitle(), p.getTitle().substring(dot, end)));
		    }
		    output.write("</tr>\n</thead><tbody><tr>");
		    output.write("<th ></th><th ></th>");

		    hc = 0;
		    for (Property p : props) {
			String dataset = "";
			String finder = "";
			try {
			    Reference r = uriReporter.getResourceRef().clone();
			    r.setQuery(null);
			    dataset = URLEncoder.encode(r.toString());
			} catch (Exception x) {
			    x.printStackTrace();
			}
			String feature = "";
			try {
			    feature = URLEncoder.encode(pReporter.getURI(p));
			} catch (Exception x) {
			    x.printStackTrace();
			}

			if (p.getLabel().equals(Property.opentox_Name)
				|| p.getLabel().equals(Property.opentox_IupacName)) {
			    finder = String
				    .format("&nbsp;<a href='%s/algorithm/finder?feature_uris[]=%s&search=NAME2STRUCTURE&mode=emptyadd&dataset_uri=%s' title='Retrieve structure by name'>[Find]</a>",
					    baseReference, feature, dataset);
			} else if (p.getLabel().equals(Property.opentox_CAS)) {
			    finder = String
				    .format("&nbsp;<a href='%s/algorithm/finder?feature_uris[]=%s&search=CIR&mode=emptyadd&dataset_uri=%s' title='Retrieve structure by CAS'>[Find]</a>",
					    baseReference, feature, dataset);
			}

			hc++;
			output.write(String.format("<th align='center'><a href='%s' title='%s'>%s %s</a>%s</th>",
				pReporter.getURI(p), p.getName(), p.getName(), p.getUnits(), finder));
		    }
		    output.write("</tr>\n");
		}

		break;

	    }
	    default: {
		w.write(downloadLinks());
		w.append("<h4><div class=\"actions\"><span class=\"right\">");
		w.write(resultsForm(query));
		w.append("</span></div></h4>\n");

		w.write("<table>");

	    }

	    } // switch

	} catch (Exception x) {
	    logger.log(Level.WARNING, x.getMessage(), x);
	}
    };

    public void footer(Writer output, Q query) {
	try {
	    switch (_dmode) {
	    case properties: {
		break;
	    }
	    case table: {
		output.write("</tbody></table>");
		output.write("</span></div>");
		break;
	    }
	    default: {
		output.write("</table>");
	    }
	    }
	    output.write("</form>\n");

	    output.write("</div>");
	    // output.write(AmbitResource.printWidgetContentFooter());
	    // output.write(AmbitResource.printWidgetFooter());

	    if (headless)
		return;
	    AmbitResource.writeHTMLFooter(output, "", uriReporter.getRequest());

	    output.flush();
	} catch (Exception x) {
	}
    };

    public void open() throws DbAmbitException {
	// TODO Auto-generated method stub

    }

    public String toURITable(IStructureRecord record) {
	String w = uriReporter.getURI(record);
	StringBuilder b = new StringBuilder();

	b.append("<tr>");

	b.append(String.format(
		"<td >%d<br>%s<br>%s<br>%s</td>\n",
		count + 1,
		String.format(
			"<a href='%s/query/similarity?search=%s&type=url&threshold=0.85' title='Find similar compounds'><img src=\"%s/images/search.png\" border='0' alt='Find similar' title='Find similar'></a>",
			uriReporter.getBaseReference(), Reference.encode(w), uriReporter.getBaseReference()),
		String.format(
			"<a href='%s/query/smarts?search=%s&type=url&max=100' title='Find substructure'><img src=\"%s/images/search.png\" border='0' alt='Find substructure' title='Find substructure'></a>",
			uriReporter.getBaseReference(), Reference.encode(w), uriReporter.getBaseReference()),
		String.format(
			"<script type='text/javascript' src='http://chemapps.stolaf.edu/jmol/jmol.php?source=%s&link=3D'></script>",
			Reference.encode(String.format("%s?media=chemical/x-mdl-sdfile", w)))

	));

	String imguri = getImageUri(record);

	b.append(String
		.format("<td valign='top'><a href=\"%s?media=text/html%s\"><img src=\"%s&w=%d&h=%d\" width='%d' height='%d' alt=\"%s\" title=\"%d\"/></a></td>",

			w,
			hilightPredictions == null ? "" : String.format("&%s=%s", OpenTox.params.model_uri,
				hilightPredictions), imguri, cellSize.width, cellSize.height, cellSize.width,
			cellSize.height, w, record.getIdchemical()));

	List<Property> props = template2Header(getTemplate(), true);
	Property prevProperty = null;
	int col = 0;

	if (hierarchy) {
	    b.append("<td>");
	    // b.append("<table border='1' width='90%'>"); //style='border:1px
	    // dotted blue;'
	}

	for (Property property : props) {
	    col++;
	    Object value = record.getProperty(property);

	    if (value instanceof Number) {
		NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setGroupingUsed(false);
		value = nf.format(((Number) value).doubleValue());
	    }
	    boolean isLong = (value == null) ? false : value.toString().length() > 255;

	    if (hierarchy) {
		if (value == null)
		    continue; // don't write non existing values
	    } else
		b.append("<td>");

	    value = value == null ? "" : value.toString();

	    boolean isHTML = (value == null) ? false : value.toString().indexOf("<html>") >= 0;
	    String searchValue = (value == null) ? null : (isHTML ? null : value.toString().length() > 255 ? value
		    .toString().substring(0, 255) : value.toString());

	    StringBuilder f = new StringBuilder();
	    for (String ff : featureURI.getValuesArray(OpenTox.params.feature_uris.toString())) {
		f.append(String.format("&%s=%s", OpenTox.params.feature_uris, ff));
	    }
	    if (hierarchy) {

		b.append("ToXML".equals(property.getUrl()) ? beautifyToXMLField(property, prevProperty) : String
			.format("\n<br><a href='%s' target=_blank>%s</a>&nbsp;", pReporter.getURI(property),
				property.getName()));

		if (Property.opentox_Name.equals(property.getLabel()))
		    b.append(String
			    .format("<label title='%s'><i><a href='%s/query/compound/search/all?search=%s' target='_search'>%s</a></i></label>",
				    value, uriReporter.getBaseReference(), Reference.encode(searchValue), searchValue));
		else
		    b.append(String.format("<label title='%s'><i><font color='black'>%s</font></i></label>", value,
			    searchValue));
		// b.append(value);

		prevProperty = property;
	    } else {
		b.append("<div>");

		b.append(String.format("<a href='?property=%s%s/%d&search=%s%s'>%s</a>",
		// uriReporter.getBaseReference(),

			// OpenTox.params.feature_uris.toString(),
			uriReporter.getBaseReference(), PropertyResource.featuredef, property.getId(),
			// Reference.encode(property.getName()),
			searchValue == null ? "" : Reference.encode(searchValue.toString()), f, value));

		/*
		 * b.append(String.format(
		 * "<a href=\"%s/compound?%s=%s%s/%d&property=%s&search=%s%s\">%s</a>"
		 * , uriReporter.getBaseReference(),
		 * OpenTox.params.feature_uris.toString(),
		 * uriReporter.getBaseReference(), PropertyResource.featuredef,
		 * property.getId(), Reference.encode(property.getName()),
		 * searchValue
		 * ==null?"":Reference.encode(searchValue.toString()),
		 * 
		 * f, value ));
		 */
	    }
	    if (hierarchy) {
		// b.append("<br>");
	    } else
		b.append("</div>\n");

	    if (!hierarchy)
		b.append("</td>\n");
	}
	if (hierarchy) {

	    b.append("</td>\n");
	}
	b.append("</tr>\n");

	return b.toString();
    }

    /**
     * Some magic to show ToXML hierarchical fields, until we find a better
     * solution. Fields come sorted.
     * 
     * @param p
     * @param previous
     * @return
     */
    protected String beautifyToXMLField(Property p, Property previous) {
	StringBuilder b = new StringBuilder();

	String title = p.getTitle().replace("http://opentox.org/toxml.owl#", "");

	int pos = title.lastIndexOf(".");

	b.append("<br>");
	if (pos > 0) {
	    if (previous != null) {
		String prevTitle = previous.getTitle().replace("http://opentox.org/toxml.owl#", "");
		int posPrev = prevTitle.lastIndexOf(".");
		if ((posPrev > 0) && title.substring(0, pos).equals(prevTitle.substring(0, posPrev))) {
		    // same path, one level down the hierarchy
		    posPrev = previous.getName().lastIndexOf(".");
		    int npos = p.getName().lastIndexOf(".");
		    if ((npos > 0) && (posPrev > 0)
			    && !p.getName().substring(0, npos).equals(previous.getName().substring(0, posPrev))) {
			b.append("<br>");
		    }
		    b.append(getPropertyTitle(p, title.substring(pos + 1)));
		} else {
		    b.append("<hr>");
		    b.append(String.format("<h5>%s</h5>", title.substring(0, pos).replace(".", "\u00BB")));
		    b.append(getPropertyTitle(p, title.substring(pos + 1)));
		}
	    } else {
		b.append("<hr>");
		b.append(String.format("<h5>%s</h5>", title.substring(0, pos).replace(".", "\u00BB")));
		b.append(getPropertyTitle(p, title.substring(pos + 1)));
	    }

	} else {
	    b.append(getPropertyTitle(p, title));
	}
	return b.toString();
    }

    protected String getPropertyTitle(Property p, String title) {
	StringBuilder space = new StringBuilder();
	for (int i = 0; i < p.getName().length() - 1; i++)
	    if (".".equals(p.getName().substring(i, i + 1)))
		space.append("&nbsp;");

	return String.format("%s<a href='%s' title='%s'>%s%s%s</a>&nbsp;", space.toString(), pReporter.getURI(p),
		p.getName(), title, "".equals(p.getUnits()) ? "" : ",", p.getUnits());

    }

    @Override
    protected List<Property> template2Header(Template template, boolean propertiesOnly) {
	List<Property> h = super.template2Header(template, propertiesOnly);

	Collections.sort(h, new Comparator<Property>() {
	    public int compare(Property o1, Property o2) {

		return o1.getOrder() - o2.getOrder();
		/*
		 * String n1[] = o1.getName().replace(".",":").split(":");
		 * String n2[] = o2.getName().replace(".",":").split(":");
		 * 
		 * int c = n1.length<n2.length?n1.length:n2.length; int r = 0;
		 * for (int i=0; i < c;i++) { r = n1[i].compareTo(n2[i]); if
		 * (r==0) continue; return r>0?(i+1):-(i+1); } return
		 * r>0?(c+1):-(c+1);
		 */

	    }
	});

	return h;
    }

    protected String getImageUri(IStructureRecord record) {
	String w = uriReporter.getURI(record);
	if (hilightPredictions != null)
	    return String.format("%s?%s=%s&media=%s", hilightPredictions, OpenTox.params.dataset_uri.toString(), w,
		    Reference.encode(imgMime));
	else
	    return String.format("%s?media=%s", w, Reference.encode(imgMime));
    }

    public String toURI(IStructureRecord record) {
	String w = uriReporter.getURI(record);
	StringBuilder b = new StringBuilder();

	int p = w.indexOf("conformer");

	String imguri = getImageUri(record);

	// tabs

	String moleculeURI = String.format(
		"<img src=\"%s&w=%d&h=%d\" width='%d' height='%d' alt=\"%s\" title=\"%d\"/>", imguri, cellSize.width,
		cellSize.height, cellSize.width, cellSize.height, w, record.getIdchemical());

	b.append("<tr>");
	b.append("<td valign='top'>");
	b.append(String.format("<a href='%s' alt='%s' title='%s'>%s</a>", w, w, w, moleculeURI));
	b.append(String.format("<div id='structype_%d'></div>", record.getIdstructure()));
	b.append(String.format("<div id='consensus_%d'></div>", record.getIdstructure()));
	b.append(String.format(
		"<script>$('#structype_%d').load('%s/comparison');$('#consensus_%d').load('%s/consensus');</script>",
		record.getIdstructure(), w, record.getIdstructure(), w));
	b.append(String.format("<br><a href=\"%s%s\" title='All'>All structures</a>&nbsp;", p >= 0 ? w.substring(0, p)
		: String.format("%s/", w), OpenTox.URI.conformer));
	b.append(String
		.format("<script type='text/javascript' src='http://chemapps.stolaf.edu/jmol/jmol.php?source=%s&link=3D'></script><br>",
			Reference.encode(String.format("%s?media=chemical/x-mdl-sdfile", w))));
	b.append(String
		.format("<a href='%s/query/similarity?search=%s&type=url&threshold=0.75' title='Find similar compounds'>Find similar</a>&nbsp;",
			uriReporter.getBaseReference(), Reference.encode(w)));
	b.append(String.format(
		"<a href='%s/query/smarts?search=%s&type=url&max=100' title='Find substructure'>Find substructure</a>",
		uriReporter.getBaseReference(), Reference.encode(w)));

	b.append("</td>");
	b.append("<td valign='top'>");

	b.append("<div class='tabs'>\n<ul>");
	b.append(String
		.format("<li><a href='%s%s%s/url/all?headless=true&search=%s&media=%s' title='Identifiers'>Identifiers</a></li>",
			uriReporter.getBaseReference(), QueryResource.query_resource, CompoundLookup.resource,
			Reference.encode(w), Reference.encode("text/html")));

	b.append(String.format("<li><a href='%s%s?headless=true&media=%s' title='Datasets'>Datasets</a></li>",
		String.format("%s/", w), "datasets", Reference.encode("text/html")));

	/*
	 * b.append(String.format(
	 * "<li><a href='%s/query%s?%s=%s&condition=startswith&%s=%s&headless=true&media=%s' title='Datasets_by_endpoints'>Datasets by endpoints</a></li>"
	 * , uriReporter.getBaseReference(), DatasetsByEndpoint.resource,
	 * MetadatasetResource.search_features.feature_sameas,
	 * URLEncoder.encode("http://www.opentox.org/echaEndpoints.owl"),
	 * OpenTox.params.compound_uri, URLEncoder.encode(w),
	 * Reference.encode("text/html") ));
	 */

	/*
	 * 
	 * b.append(String.format(
	 * "<li><a href='%s/%s?media=%s&headless=true' title='Features'>Features</a></li>"
	 * , w, OpenTox.URI.feature, Reference.encode("text/html") ));
	 */

	List<Property> props = template2Header(getTemplate(), true);
	if (props.size() > 0) {
	    b.append(String.format("<li><a href='#props_%d'>Properties</a></li>", record.getIdstructure()));
	} else {
	    String propertyUri = String.format("%s?%s=%s&headless=true",
	    // p>=0?w.substring(0,p):String.format("%s/",w),
		    w, OpenTox.params.feature_uris, Reference.encode(String.format("%s%s", p >= 0 ? w.substring(0, p)
			    : String.format("%s/", w), OpenTox.URI.feature)));
	    b.append(String.format("<li><a href='%s&media=%s' title='Values'>Properties</a></li>", propertyUri,
		    Reference.encode("text/html")));
	}

	b.append("</ul>");
	if (props.size() > 0) {
	    b.append(String.format("<div id='props_%d'><p><table>", record.getIdstructure()));
	    for (int i = 0; i < props.size(); i++) {
		Property property = props.get(i);
		if (property.getId() > 0) {
		    Object value = record.getProperty(property);
		    if (value != null)
			b.append(String.format("<tr><td>%s<td><td>%s</td></tr>", property.getName(), value == null ? ""
				: value.toString()));
		}
	    }
	    b.append("</table></p></div>");
	}
	b.append("</div>");

	b.append("</td></tr>");
	return b.toString();
    }

    @Override
    public void close() throws Exception {
	super.close();
    }

    @Override
    public void setConnection(Connection connection) throws DbAmbitException {
	super.setConnection(connection);
    }
}
