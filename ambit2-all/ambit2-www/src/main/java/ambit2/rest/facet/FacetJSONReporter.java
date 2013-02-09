package ambit2.rest.facet;

import java.io.Writer;
import java.util.logging.Level;

import org.restlet.Request;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.facet.IFacet;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;
import ambit2.rest.QueryURIReporter;


public class FacetJSONReporter<Q extends IQueryRetrieval<IFacet>> extends QueryReporter<IFacet, Q, Writer> {
	protected QueryURIReporter<IFacet, IQueryRetrieval<IFacet>> uriReporter;
	protected String jsonp = null;
	protected String comma = null;
	
	public FacetJSONReporter(Request baseRef) {
		this(baseRef,null);
	}
	public FacetJSONReporter(Request baseRef, String jsonp) {
		super();
		uriReporter = new FacetURIReporter<IQueryRetrieval<IFacet>>(baseRef);
		this.jsonp = jsonp;
	}
	@Override
	public void open() throws DbAmbitException {
		
	}

	@Override
	public void header(Writer output, Q query) {
		try {
			if (jsonp!=null) {
				output.write(jsonp);
				output.write("(");
			}
			output.write("{\"facet\": [");
				//"Name,Count,URI,Subcategory\n");
		} catch (Exception x) {}
		
	}

	@Override
	public void footer(Writer output, Q query) {
		try {
			output.write("\n]\n}");
			
			if (jsonp!=null) {
				output.write(");");
			}
			output.flush();
			} catch (Exception x) {}
	}


	@Override
	public Object processItem(IFacet item) throws AmbitException {
		try {
			if (comma!=null) getOutput().write(comma);
			String subcategory = null;
			if ((uriReporter!=null) && (uriReporter.getBaseReference()!=null))
				subcategory = uriReporter.getBaseReference().toString();
			output.write(String.format("\n\t{\n\t\"value\":\"%s\",\n\t\"count\":%d,\n\t\"uri\":\"%s\",\n\t\"subcategory\":%s%s%s\n\t}",
					item.getValue(),
					item.getCount(),
					uriReporter.getURI(item),
					subcategory==null?"":"\"",
					item.getSubCategoryURL(subcategory),
					subcategory==null?"":"\""
					));
			comma = ",";
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
		return item;
	}

}