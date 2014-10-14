package ambit2.rest.facet;

import java.io.Writer;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;

import org.restlet.Request;

import ambit2.base.facet.IFacet;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.reporters.QueryReporter;
import ambit2.rest.QueryURIReporter;

public class FacetCSVReporter<Q extends IQueryRetrieval<IFacet>> extends QueryReporter<IFacet, Q, Writer> {
	protected QueryURIReporter<IFacet, IQueryRetrieval<IFacet>> uriReporter;
	public FacetCSVReporter(Request baseRef) {
		super();
		uriReporter = new FacetURIReporter<IQueryRetrieval<IFacet>>(baseRef);
	}
	@Override
	public void open() throws DbAmbitException {
		
	}

	@Override
	public void header(Writer output, Q query) {
		try {
		output.write("Name,Count,URI,Subcategory\n");
		} catch (Exception x) {}
		
	}

	@Override
	public void footer(Writer output, Q query) {
		try {
			output.flush();
			} catch (Exception x) {}
	}

	@Override
	public Object processItem(IFacet item) throws AmbitException {
		try {
			String subcategory = null;
			if ((uriReporter!=null) && (uriReporter.getBaseReference()!=null))
				subcategory = uriReporter.getBaseReference().toString();
			output.write(String.format("%s,%s,%s,%s\n",
					item.getValue(),
					item.getCount(),
					uriReporter.getURI(item),
					item.getSubCategoryURL(subcategory)));
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		}
		return item;
	}

}
