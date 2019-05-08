package ambit2.rest.facet;

import java.io.Writer;
import java.util.logging.Level;

import org.restlet.Request;

import ambit2.base.ro.AbstractAnnotator;
import ambit2.db.substance.study.facet.FacetAnnotator;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.i.facet.IFacet;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.db.QueryURIReporter;

public class FacetJSONReporter<Q extends IQueryRetrieval<IFacet>> extends QueryReporter<IFacet, Q, Writer> {
	protected QueryURIReporter<IFacet, IQueryRetrieval<IFacet>> uriReporter;
	protected String jsonp = null;
	protected String comma = null;
	AbstractAnnotator <IFacet,IFacet> annotator;
	//protected AbstractAnnotator<IFacet,IFacet> annotator = null;

	public AbstractAnnotator<IFacet,IFacet> getAnnotator() {
		return annotator;
	}

	public void setAnnotator(AbstractAnnotator<IFacet,IFacet> annotator) {
		this.annotator = annotator;
	}

	public FacetJSONReporter(Request baseRef) {
		this(baseRef, null);
	}

	public FacetJSONReporter(Request baseRef, String jsonp) {
		this(baseRef,jsonp,null);
	}
	public FacetJSONReporter(Request baseRef, String jsonp, FacetAnnotator annotator) {
		super();
		uriReporter = new FacetURIReporter<IQueryRetrieval<IFacet>>(baseRef);
		this.jsonp = jsonp;
		this.annotator=annotator;
	}

	@Override
	public void open() throws DbAmbitException {

	}

	@Override
	public void header(Writer output, Q query) {
		try {
			if (jsonp != null) {
				output.write(jsonp);
				output.write("(");
			}
			output.write("{\"facet\": [");
			// "Name,Count,URI,Subcategory\n");
		} catch (Exception x) {
		}

	}

	@Override
	public void footer(Writer output, Q query) {
		try {
			output.write("\n]\n}");

			if (jsonp != null) {
				output.write(");");
			}
			output.flush();
		} catch (Exception x) {
		}
	}

	@Override
	public Object processItem(IFacet item) throws AmbitException {
		try {
			if (item == null)
				return item;
			if (comma != null)
				getOutput().write(comma);
			String subcategory = null;
			if ((uriReporter != null) && (uriReporter.getBaseReference() != null))
				subcategory = uriReporter.getBaseReference().toString();
			if (annotator!=null)
				annotator.process(item);
			output.write(item.toJSON(item == null ? null : uriReporter.getURI(item), subcategory));
			comma = ",";
		} catch (Exception x) {
			logger.log(Level.WARNING, x.getMessage(), x);
		}
		return item;
	}

}