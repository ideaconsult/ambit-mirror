package ambit2.base.relation.composition;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.STRUCTURE_RELATION;

/**
 * Substance composition: additives
 * @author nina
 *
 */
public class Additive extends CompositionRelation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6746334118446516264L;

	public Additive(SubstanceRecord structure1,IStructureRecord structure2, Proportion relation) {
		super(structure1,structure2,STRUCTURE_RELATION.HAS_ADDITIVE,relation);
	}
}
