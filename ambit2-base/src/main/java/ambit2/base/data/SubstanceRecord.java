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
	protected int idsubstance;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6844893699492741683L;
	
	public SubstanceRecord() {
		super();
	}
	public SubstanceRecord(int idsubstance) {
		super();
		setIdsubstance(idsubstance);
	}
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
	
	public int getIdsubstance() {
		return idsubstance;
	}
	public void setIdsubstance(int idsubstance) {
		this.idsubstance = idsubstance;
	}
	public void setPublicName(String name) {
		if (name==null)
			removeProperty(Property.getPublicNameInstance());
		else setProperty(Property.getPublicNameInstance(),name);
	}
	public String getPublicName() {
		Object name = getProperty(Property.getPublicNameInstance());
		return name==null?null:name.toString();
	}
	public void setName(String name) {
		if (name==null)
			removeProperty(Property.getNameInstance());
		else setProperty(Property.getNameInstance(),name);
	}
	public String getName() {
		Object name = getProperty(Property.getNameInstance());
		return name==null?null:name.toString();
	}
	public void setI5UUID(String uuid) {
		if (uuid==null)
			removeProperty(Property.getI5UUIDInstance());
		else setProperty(Property.getI5UUIDInstance(),uuid);
	}
	public String getI5UUID() {
		Object name = getProperty(Property.getI5UUIDInstance());
		return name==null?null:name.toString();
	}

}
