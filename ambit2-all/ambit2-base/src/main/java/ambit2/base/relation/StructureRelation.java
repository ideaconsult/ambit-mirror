package ambit2.base.relation;

import ambit2.base.interfaces.IStructureRecord;

public class StructureRelation extends AbstractRelation<STRUCTURE_RELATION,Double,IStructureRecord,IStructureRecord>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6452365513215869937L;

	public StructureRelation() {
		this(null,null,null);
	}
	public StructureRelation(IStructureRecord structure1,IStructureRecord structure2, Double relation) {
		super(structure1,structure2,relation);
	}
}
