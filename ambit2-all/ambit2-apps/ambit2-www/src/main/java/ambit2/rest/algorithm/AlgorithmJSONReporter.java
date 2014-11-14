package ambit2.rest.algorithm;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import net.idea.restnet.c.ResourceDoc;

import org.restlet.Context;
import org.restlet.Request;

import ambit2.base.json.JSONUtils;
import ambit2.core.data.model.Algorithm;

public class AlgorithmJSONReporter extends AlgorithmURIReporter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 207326039207770977L;
	protected String comma = null;
	protected String jsonpCallback = null;
	public AlgorithmJSONReporter(Request request,ResourceDoc doc) {
		this(request,doc,null);
	}
	public AlgorithmJSONReporter(Request request,ResourceDoc doc,String jsonpcallback) {
		super(request,doc);
		this.jsonpCallback = JSONUtils.jsonSanitizeCallback(jsonpcallback);
	}
	
	private static String format = "\n{\n\t\"uri\":\"%s\",\n\t\"id\": \"%s\",\n\t\"name\": \"%s\",\n\t\"content\": \"%s\",\n\t\"endpoint\": \"%s\",\n\t\"description\": \"%s\",\n\t\"format\": \"%s\",\n\t\"implementationOf\": \"%s\",\n\t\"isDataProcessing\": %s,\n\t\"requiresDataset\": %s,\n\t\"isSupevised\": %s,\n\t\"requires\": \"%s\",\n\t\"type\": [\n%s]\n}";
	
	@Override
	public void processItem(Algorithm algorithm, Writer output) {
		try {
			if (comma!=null) output.write(comma);

			String uri = getURI(algorithm);
			StringBuilder b = new StringBuilder();
			for (int i=0; i < algorithm.getType().length; i++) {
				if (i>0) b.append(",\n");
				b.append("\t\t\"");
				b.append(algorithm.getType()[i]);
				b.append("\"");
			}
			output.write(String.format(format,
					uri,
					algorithm.getId(),
					algorithm.getName()==null?"":algorithm.getName(),
					algorithm.getContent()==null?"":algorithm.getContent(),
					algorithm.getEndpoint()==null?"":algorithm.getEndpoint(),
					algorithm.getDescription()==null?"":algorithm.getDescription(),
					algorithm.getFormat()==null?"":algorithm.getFormat(),
					algorithm.getImplementationOf()==null?"":algorithm.getImplementationOf(),
					algorithm.isDataProcessing(),
					algorithm.isRequiresDataset(),
					algorithm.isSupervised(),
					algorithm.getRequirement()==null?"":algorithm.getRequirement(),
					b.toString()
					));
			comma = ",";
		} catch (IOException x) {
			Context.getCurrentLogger().severe(x.getMessage());
		}
	}	
	@Override
	public void footer(Writer output, Iterator<Algorithm> query) {
		try {
			output.write("\n]\n}");
		} catch (Exception x) {}
		
		try {
			if (jsonpCallback!=null) {
				output.write(");");
			}
		} catch (Exception x) {}		
	};
	@Override
	public void header(Writer output, Iterator<Algorithm> query) {
		try {
			if (jsonpCallback!=null) {
				output.write(jsonpCallback);
				output.write("(");
			}
		} catch (Exception x) {	}
		try {
			output.write("{\"algorithm\": [");
		} catch (Exception x) {}
		
	};
}
