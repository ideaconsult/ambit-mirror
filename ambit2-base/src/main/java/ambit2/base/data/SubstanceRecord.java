package ambit2.base.data;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.base.relation.composition.Proportion;

/**
 * Same as the parent class, but with related structures
 * @author nina
 *
 */
public class SubstanceRecord extends StructureRecord {
	protected String ownerUUID;
	protected String ownerName;
	protected int idsubstance;
	protected String substancetype;
	protected String companyName;
	protected String publicName;
	protected String companyUUID;
	protected List<ProtocolApplication> measurements;
	public List<ProtocolApplication> getMeasurements() {
		return measurements;
	}
	public void setMeasurements(List<ProtocolApplication> measurements) {
		this.measurements = measurements;
	}
	public void addtMeasurement(ProtocolApplication measurement) {
		if (this.measurements ==null) this.measurements = new ArrayList<ProtocolApplication>();
		measurements.add(measurement);
	}
	public String getSubstancetype() {
		return substancetype;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getPublicName() {
		return publicName;
	}
	public void setPublicName(String publicname) {
		this.publicName = publicname;
	}
	public String getCompanyUUID() {
		return companyUUID;
	}
	public void setCompanyUUID(String companyUUID) {
		this.companyUUID = companyUUID;
	}
	public void setSubstancetype(String substancetype) {
		this.substancetype = substancetype;
	}
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
	protected List<CompositionRelation> relatedStructures;
	
	public List<CompositionRelation> getRelatedStructures() {
		return relatedStructures;
	}
	public void setRelatedStructures(List<CompositionRelation> relatedStructures) {
		this.relatedStructures = relatedStructures;
	}
	public void addStructureRelation(CompositionRelation relation) {
		if (relatedStructures==null) relatedStructures = new ArrayList<CompositionRelation>();
		relatedStructures.add(relation);
	}
	public void addStructureRelation(String compositionUUID,IStructureRecord record, STRUCTURE_RELATION relation, Proportion value) {
		if (relatedStructures==null) relatedStructures = new ArrayList<CompositionRelation>();
		CompositionRelation r = new CompositionRelation(this,record,value);
		r.setCompositionUUID(compositionUUID);
		r.setRelationType(relation);
		relatedStructures.add(r);
	}	
	
	public int getIdsubstance() {
		return idsubstance;
	}
	public void setIdsubstance(int idsubstance) {
		this.idsubstance = idsubstance;
	}
	
	public void setReferenceSubstanceUUID(String uuid) {
		if (uuid==null)
			removeProperty(Property.getI5UUIDInstance());
		else setProperty(Property.getI5UUIDInstance(),uuid);
	}
	public String getReferenceSubstanceUUID() {
		Object name = getProperty(Property.getI5UUIDInstance());
		return name==null?null:name.toString();
	}
	@Override
	public void clear() {
		super.clear();
		idsubstance = -1;
		setOwnerUUID(null);
		
		if (relatedStructures!=null) relatedStructures.clear();
		if (measurements!=null) measurements.clear();
	}
	@Override
	public String toString() {
		return String.format("idsubstance=%d %s",getIdsubstance(),getCompanyUUID());
	}
	public String getOwnerUUID() {
		return ownerUUID;
	}
	public void setOwnerUUID(String ownerUUID) {
		this.ownerUUID = ownerUUID;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	
}
