package ambit2.rest.aa.opensso.policy;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import net.idea.modbcum.i.exceptions.DbAmbitException;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.json.JSONUtils;
import ambit2.rest.reporters.CatalogURIReporter;

public class PolicyJSONReporter extends CatalogURIReporter<Policy> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4566136103208284105L;
	protected String comma = null;
	protected Request request;
	protected String jsonpCallback = null;
	
	public Request getRequest() {
		return request;
	}
	public void setRequest(Request request) {
		this.request = request;
	}
	protected Reference baseReference;
	public Reference getBaseReference() {
		return baseReference;
	}

	public PolicyJSONReporter(Request request,String jsonpcallback) {
		this.baseReference = (request==null?null:request.getRootRef());
		setRequest(request);
		this.jsonpCallback = jsonpcallback==null?null:JSONUtils.jsonSanitizeCallback(jsonpcallback);
		
	}
	/**
	 *  inspired from 
	 *  https://github.com/lgriffin/JSONPL/blob/master/Resources/JSONPL/1-req.json 
	 */
	

	private static String format = "\n{\n\t\"id\":%s,\n\t\"uri\": %s,\n\t\"xml\": %s\n}";
	/*
			"{\n"+
			"   \"subject\": {\n"+
			"       \"category\": \"access-subject\",\n"+
			"       \"role\": %s\n"+
			"   },\n\"+
			"   \"resource\": {\n"+
			"       \"resource-id\": %s\n"+
			"   },\n\"+
			"   \"action\": {\n"+
			"       \"action-type\": \"write\"\n"+
			"   }\n"+
			"}";
			*/
	@Override
	public void processItem(Policy item, Writer output) {
		try {
			if (comma!=null) output.write(comma);
			
			output.write(String.format(format,
					JSONUtils.jsonQuote(JSONUtils.jsonEscape(item.getId())),
					JSONUtils.jsonQuote(JSONUtils.jsonEscape(item.getUri())),
					JSONUtils.jsonQuote(JSONUtils.jsonEscape(item.getXml())))
					);
			
			comma = ",";
		} catch (IOException x) {
			Context.getCurrentLogger().severe(x.getMessage());
		}
	}	
	@Override
	public void footer(Writer output, Iterator<Policy> query) {
		try {
			output.write("\n]\n}");
		} catch (Exception x) {}
	};
	@Override
	public void header(Writer output, Iterator<Policy> query) {
		try {
			output.write("{\"policy\": [");
		} catch (Exception x) {}
		
	};
	
	public void open() throws DbAmbitException {
		
	}
	@Override
	public void close() throws Exception {
		setRequest(null);
		super.close();
	}
	@Override
	public String getFileExtension() {
		return null;
	}
}