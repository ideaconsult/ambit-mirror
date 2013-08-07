package ambit2.base.relation.composition;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.AbstractRelation;
import ambit2.base.relation.STRUCTURE_RELATION;

/**
 * Substance composition
 * @author nina
 *
 */
public class CompositionRelation extends AbstractRelation<STRUCTURE_RELATION, Proportion,SubstanceRecord,IStructureRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1214326347782066597L;
	public CompositionRelation(SubstanceRecord structure1,IStructureRecord structure2, Proportion proportion) {
		this(structure1,structure2,STRUCTURE_RELATION.HAS_CONSTITUENT,proportion);
	}
	public CompositionRelation(SubstanceRecord structure1,IStructureRecord structure2, STRUCTURE_RELATION type, Proportion proportion) {
		super(structure1,structure2,proportion);
		setRelationType(type);
	}

}
