package ambit2.base.relation.composition;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.STRUCTURE_RELATION;

/**
 * Substance composition: impurities
 * @author nina
 *
 */
public class Impurity extends CompositionRelation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6746334118446516264L;

	public Impurity(SubstanceRecord structure1,IStructureRecord structure2, Proportion relation) {
		super(structure1,structure2,STRUCTURE_RELATION.HAS_IMPURITY,relation);
	}
}