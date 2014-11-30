package ambit2.rest.dataset.filtered;

import net.idea.modbcum.i.facet.IFacet;
import ambit2.db.update.dataset.QueryCount;

public class StudySearchResource<FACET extends IFacet<String>,Q extends QueryCount<FACET>> extends StatisticsResource<FACET, Q> {
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
}
