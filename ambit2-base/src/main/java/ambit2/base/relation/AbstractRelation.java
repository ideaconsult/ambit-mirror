package ambit2.base.relation;

import java.io.Serializable;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRelation;

/**
 * Abstract class to represent relationship between compounds
 * @author nina
 *
 * @param <RELATION>
 */
public class AbstractRelation<RELATION_TYPE,RELATION_METRIC extends Serializable> implements IStructureRelation<RELATION_TYPE,RELATION_METRIC> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6592354650015575251L;
	private IStructureRecord[] structures;
	private RELATION_METRIC relation;
	private RELATION_TYPE relationType;
	
	public AbstractRelation() {
		this(null,null);
	}
	
	public AbstractRelation(IStructureRecord structure1,IStructureRecord structure2, RELATION_METRIC relation) {
		this(new IStructureRecord[] {structure1,structure2},relation);
	}
	
	public AbstractRelation(IStructureRecord[] structures, RELATION_METRIC relation) {
		setStructures(structures);
		setRelation(relation);
	}
	@Override
	public RELATION_METRIC getRelation() {
		return relation;
	}

	@Override
	public void setRelation(RELATION_METRIC relation) {
		this.relation = relation;
	}

	public RELATION_TYPE getRelationType() {
		return relationType;
	}
	public void setRelationType(RELATION_TYPE relationtype) {
		this.relationType = relationtype;
	}
	
	@Override
	public IStructureRecord[] getStructures() {
		return structures;
	}

	@Override
	public void setStructures(IStructureRecord[] structures) {
		this.structures = structures;
	}

}
