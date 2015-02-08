package ambit2.rest.template;

import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.Dictionary;
import ambit2.base.data.Property;
import ambit2.rest.AmbitResource;
import ambit2.rest.DisplayMode;
import ambit2.rest.OpenTox;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.dataset.MetadatasetResource.search_features;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.rdf.OTEE;

/**
 * Reporter for {@link Dictionary} or {@link Property}
 * 
 * @author nina
 * 
 */
public class OntologyHTMLReporter extends QueryHTMLReporter<Property, IQueryRetrieval<Property>> {

    /**
	 * 
	 */
    private static final long serialVersionUID = -4281274169475316720L;
    protected int count = 0;

    public OntologyHTMLReporter(Request reference, DisplayMode _dmode) {
	super(reference, _dmode);

    }

    @Override
    protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
	return new PropertyURIReporter(request);
    }

    public String toURI(Property record) {
	count++;
	// if (count==1) return "";

	String w = uriReporter.getURI(record);

	boolean isDictionary = record.getClazz().equals(Dictionary.class);
	return String.format("<img src=\"%s/images/%s\">%s<a href=\"%s\">%s</a>&nbsp;<br>",

	uriReporter.getBaseReference().toString(), isDictionary ? "folder.png" : "feature.png", isDictionary ? ""
		: "&nbsp;Feature:&nbsp;", w, isDictionary ? ((Dictionary) record).getTemplate() : record.toString());

    }

    @Override
    public Object processItem(Property record) throws AmbitException {

	try {
	    output.write("<tr>");
	    output.write(String.format("<td>%s</td>", toURI(record)));
	    output.write("<td>");
	    if (record.getClazz().equals(Dictionary.class)) {
		output.write(String.format("<a href='%s/%s?%s=%s'>%s</a>", getUriReporter().getBaseReference(),
			OpenTox.URI.dataset.name(), search_features.feature_sameas.name(),
			Reference.encode(String.format("%s%s", OTEE.NS, record.getName())), "Datasets"));
	    } else
		output.write(String.format("<a href='%s/%s?%s=%s'>%s</a>", getUriReporter().getBaseReference(),
			OpenTox.URI.dataset.name(), search_features.feature_id.name(),
			Reference.encode(String.format("%s%s", OTEE.NS, record.getId())), "Datasets"));

	    output.write("</td>");
	    output.write("</tr>");
	} catch (Exception x) {
	    Context.getCurrentLogger().severe(x.getMessage());
	}
	return null;
    }

    @Override
    public void footer(Writer output, IQueryRetrieval<Property> query) {
	try {
	    output.write("</tbody>");
	    output.write("</table>");
	    AmbitResource.writeHTMLFooter(output, "", uriReporter.getRequest());
	    output.flush();
	} catch (Exception x) {
	}
    };

    @Override
    public void header(Writer w, IQueryRetrieval<Property> query) {
	try {
	    count = 0;
	    AmbitResource.writeHTMLHeader(output, query.toString(), uriReporter.getRequest(),
		    uriReporter.getResourceRef());

	    output.write("<table>");
	    output.write(String.format("<caption>%s</caption>", query.toString()));
	    output.write("<tbody>");
	    output.flush();
	} catch (Exception x) {
	}

    }
}
