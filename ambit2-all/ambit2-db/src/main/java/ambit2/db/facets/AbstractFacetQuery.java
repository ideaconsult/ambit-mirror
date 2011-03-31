package ambit2.db.facets;

import ambit2.base.facet.IFacet;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.IQueryCondition;

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
	public AbstractFacetQuery(String facetURL) {
	}

}
