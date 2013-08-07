package ambit2.rest.substance;

import java.io.Writer;
import java.util.logging.Level;

import org.restlet.Request;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.json.JSONUtils;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.ResourceDoc;

public class SubstanceJSONReporter<Q extends IQueryRetrieval<SubstanceRecord>> extends SubstanceURIReporter<Q> {
	protected String comma = null;
	protected String jsonpCallback = null;
	enum jsonSubstance {
		URI,
		i5uuid,
		documentType,
		format,
		name,
		publicname,
		content;
		
		public String jsonname() {
			return name();
		}
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 2315457985592934727L;
	public SubstanceJSONReporter(Request request, ResourceDoc doc,String jsonpCallback) {
		super(request, doc);
		this.jsonpCallback = jsonpCallback;
	}
	public void header(java.io.Writer output, Q query) {
		try {
			if (jsonpCallback!=null) {
				output.write(jsonpCallback);
				output.write("(");
			}
			output.write("{\n");
			output.write("\n\"substance\":[");
			
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
	};
	
	public void footer(java.io.Writer output, Q query) {
		try {
			output.write("\n\t]");
		} catch (Exception x) {}
		try {
			output.write("\n}\n");
			
			if (jsonpCallback!=null) {
				output.write(");");
			}
		} catch (Exception x) {}
	};
	
	@Override
	public Object processItem(SubstanceRecord item) throws AmbitException {
		try {
			Writer writer = getOutput();
			String uri = getURI(item);
						
			StringBuilder builder = new StringBuilder();
			if (comma!=null) builder.append(comma);
			
			builder.append("\n\t{\n");
			builder.append(String.format("\t\t\"%s\":\"%s\",\n",jsonSubstance.URI.name(),JSONUtils.jsonEscape(uri)));
			builder.append(String.format("\t\t\"%s\":\"%s\",\n",jsonSubstance.i5uuid.name(),JSONUtils.jsonEscape(item.getI5UUID())));
			builder.append(String.format("\t\t\"%s\":\"%s\",\n",jsonSubstance.name.name(),JSONUtils.jsonEscape(item.getName())));
			builder.append(String.format("\t\t\"%s\":\"%s\",\n",jsonSubstance.publicname.name(),JSONUtils.jsonEscape(item.getPublicName())));
			builder.append(String.format("\t\t\"%s\":\"%s\"\n",jsonSubstance.format.name(),JSONUtils.jsonEscape(item.getFormat())));
			//builder.append(String.format("\t\t\"%s\":%s,\n",jsonSubstance.documentType.name(),item()));
			builder.append("\t}");
			writer.write(builder.toString());
			comma = ",";
		} catch (Exception x) {
			logger.log(java.util.logging.Level.SEVERE,x.getMessage(),x);
		}
		return item;
	}


}
