package ambit2.base.relation;

import ambit2.base.interfaces.IStructureRecord;


public class SimilarityRelation extends AbstractRelation<Double> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3387121122768795230L;
	public SimilarityRelation(IStructureRecord structure1,IStructureRecord structure2) {
		super(structure1,structure2,null);
	}
	public SimilarityRelation(IStructureRecord structure1,IStructureRecord structure2, Double relation) {
		super(structure1,structure2,relation);
	}
	public SimilarityRelation(IStructureRecord[] structures, Double relation) {
		super(structures,relation);
	}
}
