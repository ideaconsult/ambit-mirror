package ambit2.base.data;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRelation;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.base.relation.composition.Proportion;

/**
 * Same as the parent class, but with related structures
 * @author nina
 *
 */
public class SubstanceRecord extends StructureRecord {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6844893699492741683L;
	protected List<IStructureRelation> relatedStructures;
	
	public List<IStructureRelation> getRelatedStructures() {
		return relatedStructures;
	}
	public void setRelatedStructures(List<IStructureRelation> relatedStructures) {
		this.relatedStructures = relatedStructures;
	}
	public void addStructureRelation(IStructureRelation relation) {
		if (relatedStructures==null) relatedStructures = new ArrayList<IStructureRelation>();
		relatedStructures.add(relation);
	}
	public void addStructureRelation(IStructureRecord record, STRUCTURE_RELATION relation, Proportion value) {
		if (relatedStructures==null) relatedStructures = new ArrayList<IStructureRelation>();
		CompositionRelation r = new CompositionRelation(this,record,value);
		r.setRelationType(relation);
		relatedStructures.add(r);
	}	
}
