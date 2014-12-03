package ambit2.rest.dataset.filtered;

import net.idea.modbcum.r.QueryReporter;

import org.restlet.Request;

import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.substance.QueryCountProtocolApplications;
import ambit2.db.substance.study.facet.SubstanceByCategoryFacet;
import ambit2.rest.bundle.BundleStudyJSONReporter;

public class StudySearchResource extends StatisticsResource<SubstanceByCategoryFacet, QueryCountProtocolApplications> {
	public static final String resource = "/study";
	public StudySearchResource() {
		super();
		mode = StatsMode.protocol_applications;
		setHtmlbyTemplate(true);
	}
	@Override
	protected ambit2.rest.dataset.filtered.StatisticsResource.StatsMode getSearchMode() {
		return StatsMode.protocol_applications;
	}
	@Override
	public String getTemplateName() {
		return super.getTemplateName();
	}
	
	@Override
	protected QueryReporter createJSONReporter(Request request, String jsonp) {
		SubstanceEndpointsBundle bundle = null;
		if (queryObject instanceof QueryCountProtocolApplications) {
			bundle = ((QueryCountProtocolApplications)queryObject).getBundle();
		}
		return new BundleStudyJSONReporter(request,jsonp,bundle);
	}
}
