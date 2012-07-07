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
public class AbstractRelation<RELATION extends Serializable> implements IStructureRelation<RELATION> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6592354650015575251L;
	private IStructureRecord[] structures;
	private RELATION relation;
	
	public AbstractRelation() {
		this(null,null);
	}
	
	public AbstractRelation(IStructureRecord structure1,IStructureRecord structure2, RELATION relation) {
		this(new IStructureRecord[] {structure1,structure2},relation);
	}
	
	public AbstractRelation(IStructureRecord[] structures, RELATION relation) {
		setStructures(structures);
		setRelation(relation);
	}
	@Override
	public RELATION getRelation() {
		return relation;
	}

	@Override
	public void setRelation(RELATION relation) {
		this.relation = relation;
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
