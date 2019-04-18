package ambit2.rest.substance.templates;

import java.io.Writer;

import org.restlet.Request;

import ambit2.base.json.JSONUtils;
import net.enanomapper.maker.TR;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.db.QueryURIReporter;

public class AssayTemplateEntryJSONReporter<Q extends IQueryRetrieval<TR>> extends QueryURIReporter<TR, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 250468394638470775L;
	protected String comma = null;
	protected String jsonpCallback = null;

	public AssayTemplateEntryJSONReporter(Request request) {
		this(request, null);
	}

	public AssayTemplateEntryJSONReporter(Request request, String jsonpcallback) {
		super(request);
		this.jsonpCallback = JSONUtils.jsonSanitizeCallback(jsonpcallback);
	}

	@Override
	public Object processItem(TR item) throws AmbitException {
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
			output.write("{\"assaytemplate\": [");
		} catch (Exception x) {
		}

	}

	@Override
	public String getURI(String ref, TR item) {
		return null;
	};
}