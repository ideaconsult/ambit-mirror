package ambit2.rest.facet;

import java.io.Writer;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.i.facet.IFacet;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;


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
			if (item==null) return item;
			if (comma!=null) getOutput().write(comma);
			String subcategory = null;
			if ((uriReporter!=null) && (uriReporter.getBaseReference()!=null))
				subcategory = uriReporter.getBaseReference().toString();
			output.write(item.toJSON(item==null?null:uriReporter.getURI(item),subcategory));
			comma = ",";
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
		return item;
	}

}