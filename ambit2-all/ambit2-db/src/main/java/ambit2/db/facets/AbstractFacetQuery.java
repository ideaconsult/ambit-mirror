package ambit2.db.facets;

import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.facet.IFacet;
import ambit2.db.search.AbstractQuery;

/**
 * 
 * @author nina
 *
 * @param <F>
 * @param <T>
 * @param <C>
 * @param <FACETVALUE>
 */
public abstract class AbstractFacetQuery<F,T,C extends IQueryCondition,FACET extends IFacet> extends AbstractQuery<F,T,C,FACET> 
																implements IQueryRetrieval<FACET>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6912724132776390125L;
	protected abstract FACET createFacet(String facetURL);
	
	public AbstractFacetQuery(String facetURL) {
	}

}
