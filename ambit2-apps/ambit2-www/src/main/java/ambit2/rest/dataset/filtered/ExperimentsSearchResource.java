package ambit2.rest.dataset.filtered;

import java.io.File;

import org.restlet.Request;

import ambit2.db.substance.study.facet.FacetAnnotator;
import ambit2.db.substance.study.facet.ResultsCountFacet;
import ambit2.db.update.dataset.QueryCount;
import ambit2.rest.AmbitFreeMarkerApplication;
import ambit2.rest.facet.FacetJSONReporter;
import net.idea.modbcum.r.QueryReporter;

/**
 * Used for endpoint autocomplete
 * @author nina
 *
 * @param <FACET>
 * @param <Q>
 */
public class ExperimentsSearchResource<E>  extends StatisticsResource<ResultsCountFacet<E>,QueryCount<ResultsCountFacet<E>>>  {

	public ExperimentsSearchResource() {
		super();
		mode = StatsMode.experiment_endpoints;
	}

	public static final String resource = "/experiment_endpoints";

	@Override
	protected ambit2.rest.dataset.filtered.StatisticsResource.StatsMode getSearchMode() {
		return StatsMode.experiment_endpoints;
	}
	
	@Override
	protected QueryReporter createJSONReporter(Request request, String jsonp) {
		FacetAnnotator annotator = new FacetAnnotator(new File(((AmbitFreeMarkerApplication) getApplication()).getProperties().getMapFolder()));
		return new FacetJSONReporter(request, jsonp,annotator);
	}
}
