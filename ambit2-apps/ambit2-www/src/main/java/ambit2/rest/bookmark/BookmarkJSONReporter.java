package ambit2.rest.bookmark;

import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;

import ambit2.base.data.Bookmark;
import ambit2.base.json.JSONUtils;

public class BookmarkJSONReporter<Q extends IQueryRetrieval<Bookmark>> extends QueryURIReporter<Bookmark, Q> {

    /**
     * 
     */
    private static final long serialVersionUID = 6499082929972747222L;
    protected String comma = null;
    protected String jsonpCallback = null;

    public BookmarkJSONReporter(Request request) {
	this(request, null);
    }

    public BookmarkJSONReporter(Request request, String jsonpcallback) {
	super(request);
	this.jsonpCallback = JSONUtils.jsonSanitizeCallback(jsonpcallback);
    }


    @Override
    public Object processItem(Bookmark item) throws AmbitException {
	try {
	    Writer writer = getOutput();
	    if (comma != null)
		writer.write(comma);
	    writer.write(item.toJSON());
	    comma = ",";
	} catch (Exception x) {
	    logger.log(java.util.logging.Level.SEVERE, x.getMessage(), x);
	}
	return item;
    }
    @Override
    public void footer(Writer output, Q query) {
	try {
	    output.write("\n]\n}");
	} catch (Exception x) {
	}

	try {
	    if (jsonpCallback != null) {
		output.write(");");
	    }
	} catch (Exception x) {
	}
    };

    @Override
    public void header(Writer output, Q query) {
	try {
	    if (jsonpCallback != null) {
		output.write(jsonpCallback);
		output.write("(");
	    }
	} catch (Exception x) {
	}
	try {
	    output.write("{\"ontobucket\": [");
	} catch (Exception x) {
	}

    }

    @Override
    public String getURI(String ref, Bookmark item) {
	return null;
    };
}
