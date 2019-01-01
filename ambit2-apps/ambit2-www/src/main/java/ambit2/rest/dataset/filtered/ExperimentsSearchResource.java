package ambit2.rest.dataset.filtered;

import net.idea.modbcum.i.facet.IFacet;
import ambit2.db.update.dataset.QueryCount;

/**
 * Used for endpoint autocomplete
 * @author nina
 *
 * @param <FACET>
 * @param <Q>
 */
public class ExperimentsSearchResource<FACET extends IFacet<String>,Q extends QueryCount<FACET>>  extends StatisticsResource<FACET,Q>  {

	public ExperimentsSearchResource() {
		super();
		mode = StatsMode.experiment_endpoints;
	}

	public static final String resource = "/experiment_endpoints";
	
	@Override
	protected ambit2.rest.dataset.filtered.StatisticsResource.StatsMode getSearchMode() {
		return StatsMode.experiment_endpoints;
	}
}
