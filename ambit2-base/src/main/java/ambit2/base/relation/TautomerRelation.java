package ambit2.base.relation;

import ambit2.base.interfaces.IStructureRecord;

public class TautomerRelation extends StructureRelation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3387121122768795230L;
	public TautomerRelation(IStructureRecord structure1,IStructureRecord structure2) {
		super(structure1,structure2,null);
		setRelationType(STRUCTURE_RELATION.HAS_TAUTOMER);
	}
	public TautomerRelation(IStructureRecord structure1,IStructureRecord structure2, Double relation) {
		super(structure1,structure2,relation);
		setRelationType(STRUCTURE_RELATION.HAS_TAUTOMER);
	}
}
