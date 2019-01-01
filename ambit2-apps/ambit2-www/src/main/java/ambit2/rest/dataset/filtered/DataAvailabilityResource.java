package ambit2.rest.dataset.filtered;

import ambit2.db.update.dataset.QueryCount;
import net.idea.modbcum.i.facet.IFacet;

public class DataAvailabilityResource<FACET extends IFacet<String>,Q extends QueryCount<FACET>>  extends StatisticsResource<FACET,Q>  {

	public DataAvailabilityResource() {
		super();
		mode = StatsMode.data_availability;
	}

	public static final String resource = "/data_availability";
	
	@Override
	protected ambit2.rest.dataset.filtered.StatisticsResource.StatsMode getSearchMode() {
		return StatsMode.data_availability;
	}
}