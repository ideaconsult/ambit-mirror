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
public class AbstractRelation<RELATION_TYPE,RELATION_METRIC extends Serializable,FIRSTSTRUC extends IStructureRecord,SECONDSTRUC extends IStructureRecord> 
			implements IStructureRelation<RELATION_TYPE,RELATION_METRIC,FIRSTSTRUC,SECONDSTRUC> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6592354650015575251L;
	private FIRSTSTRUC structure1;
	private SECONDSTRUC structure2;
	private RELATION_METRIC relation;
	private RELATION_TYPE relationType;
	protected String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AbstractRelation(FIRSTSTRUC structure1,SECONDSTRUC structure2, RELATION_METRIC relation) {
		super();
		setFirstStructure(structure1);
		setSecondStructure(structure2);
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
	public FIRSTSTRUC getFirstStructure() {
		return structure1;
	}

	@Override
	public SECONDSTRUC getSecondStructure() {
		return structure2;
	}

	@Override
	public void setFirstStructure(FIRSTSTRUC struc) {
		structure1 = struc;
	}

	@Override
	public void setSecondStructure(SECONDSTRUC struc) {
		structure2 = struc;
	}
	public void clear() {
		if (structure1!=null) structure1.clear();
		if (structure2!=null) structure2.clear();
		
	}

}
