package ambit2.base.relation.composition;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.AbstractRelation;
import ambit2.base.relation.STRUCTURE_RELATION;

public class CompositionRelation extends AbstractRelation<STRUCTURE_RELATION, Proportion> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1214326347782066597L;
	public CompositionRelation(IStructureRecord structure1,IStructureRecord structure2, Proportion relation) {
		this(new IStructureRecord[] {structure1,structure2},relation);
	}
	public CompositionRelation(IStructureRecord[] structures, Proportion relation) {
		super(structures,relation);
		setRelationType(STRUCTURE_RELATION.HAS_CONSTITUENT);
	}
}
