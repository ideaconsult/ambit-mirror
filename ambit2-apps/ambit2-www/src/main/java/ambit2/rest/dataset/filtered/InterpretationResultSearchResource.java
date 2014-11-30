package ambit2.rest.dataset.filtered;

import net.idea.modbcum.i.facet.IFacet;
import ambit2.db.update.dataset.QueryCount;

/**
 * Used for interpretation result autocomplete
 * @author nina
 *
 * @param <FACET>
 * @param <Q>
 */
public class InterpretationResultSearchResource<FACET extends IFacet<String>,Q extends QueryCount<FACET>>  extends StatisticsResource<FACET,Q>  {

	public InterpretationResultSearchResource() {
		super();
		mode = StatsMode.interpretation_result;
	}

	public static final String resource = "/interpretation_result";
	
	@Override
	protected ambit2.rest.dataset.filtered.StatisticsResource.StatsMode getSearchMode() {
		return StatsMode.interpretation_result;
	}
}
