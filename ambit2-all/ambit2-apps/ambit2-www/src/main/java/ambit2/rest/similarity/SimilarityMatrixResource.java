package ambit2.rest.similarity;

import java.awt.Dimension;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.r.QueryAbstractReporter;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.relation.SimilarityRelation;
import ambit2.db.reporters.CSVReporter;
import ambit2.rest.DisplayMode;

/**
 * Returns similarity matrix of a dataset
 * @author nina
 *
 * @param <Q>
 */
public class SimilarityMatrixResource<Q extends IQueryRetrieval<SimilarityRelation>> extends AbstractPairwiseResource<SimilarityRelation,Q> { 
	public final static String resource =  "/matrix";

	@Override
	protected QueryAbstractReporter createHTMLReporter(Dimension d) {
		return new PairwiseSimilarityHTMLReporter(getRequest(),DisplayMode.table,null);
	}

	@Override
	protected QueryAbstractReporter createJSONReporter(String callback) {
		return new PairwiseSimilarityJSONReporter(getRequest());
	}
	
	@Override
	protected CSVReporter createCSVReporter() {
		CSVReporter csvReporter = new CSVReporter(getRequest().getRootRef().toString(),null,null,
				String.format("%s%s",getRequest().getRootRef(),getCompoundInDatasetPrefix())
				);
		csvReporter.setSimilarityColumn(Property.getInstance("metric",queryObject==null?"":queryObject.toString(),"http://ambit.sourceforge.net"));
		return csvReporter;
	}
	@Override
	protected Template createTemplate(Context context, Request request,
			Response response, String[] featuresURI) throws ResourceException {
		return null;
	}
	@Override
	protected Template createTemplate(Form form) throws ResourceException {
		return null;
	}
}
