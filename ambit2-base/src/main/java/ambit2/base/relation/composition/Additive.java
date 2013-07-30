package ambit2.base.relation.composition;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.STRUCTURE_RELATION;

public class Additive extends CompositionRelation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6746334118446516264L;

	public Additive(IStructureRecord structure1,IStructureRecord structure2, Proportion relation) {
		super(new IStructureRecord[] {structure1,structure2},relation);
		setRelationType(STRUCTURE_RELATION.HAS_ADDITIVE);
	}
}
