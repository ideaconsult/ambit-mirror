package ambit2.rest.dataset.filtered;

import ambit2.db.update.dataset.QueryCount;
import net.idea.modbcum.i.facet.IFacet;

public class SubstanceTypeSearchResource<FACET extends IFacet<String>,Q extends QueryCount<FACET>>  extends StatisticsResource<FACET,Q>  {

	public SubstanceTypeSearchResource() {
		super();
		mode = StatsMode.substancetypes;
	}

	public static final String resource = "/substancetype";
	
	@Override
	protected ambit2.rest.dataset.filtered.StatisticsResource.StatsMode getSearchMode() {
		return StatsMode.substancetypes;
	}	
}
