package ambit2.base.data;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.substance.ExternalIdentifier;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRelation;
import ambit2.base.json.JSONUtils;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.base.relation.composition.Proportion;

/**
 * Same as the parent class, but with related structures
 * @author nina
 *
 */
public class SubstanceRecord extends StructureRecord {
	protected List<ExternalIdentifier> externalids;
	protected String ownerUUID;
	protected String ownerName;
	protected int idsubstance;
	protected String substancetype;
	protected String companyName;
	protected String publicName;
	protected String companyUUID;
	public enum jsonSubstance {
		URI,
		externalIdentifiers,
		i5uuid,
		documentType,
		format,
		name,
		publicname,
		content,
		substanceType,
		referenceSubstance,
		ownerUUID,
		ownerName,
		composition;
		public String jsonname() {
			return name();
		}
	}
	protected List<ProtocolApplication> measurements;
	public List<ProtocolApplication> getMeasurements() {
		return measurements;
	}
	public void setMeasurements(List<ProtocolApplication> measurements) {
		this.measurements = measurements;
	}
	public void addMeasurement(ProtocolApplication measurement) {
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
	public SubstanceRecord(String uuid) {
		super();
		setCompanyUUID(uuid);
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
	public IStructureRelation addStructureRelation(String compositionUUID,IStructureRecord record, STRUCTURE_RELATION relation, Proportion value) {
		if (relatedStructures==null) relatedStructures = new ArrayList<CompositionRelation>();
		CompositionRelation r = new CompositionRelation(this,record,value);
		r.setCompositionUUID(compositionUUID);
		r.setRelationType(relation);
		relatedStructures.add(r);
		return r;
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
		if (externalids!=null) externalids.clear();
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
	
	public List<ExternalIdentifier> getExternalids() {
		return externalids;
	}
	public void setExternalids(List<ExternalIdentifier> externalids) {
		this.externalids = externalids;
	}	
	public String toJSON(String baseReference) {
		String uri = getURI(baseReference,this);
		StringBuilder builder = new StringBuilder();
		builder.append("\n\t{\n");
		builder.append(String.format("\t\t\"%s\":%s,\n",jsonSubstance.URI.name(),JSONUtils.jsonQuote(JSONUtils.jsonEscape(uri))));
		builder.append(String.format("\t\t\"%s\":%s,\n",jsonSubstance.ownerUUID.name(),JSONUtils.jsonQuote(JSONUtils.jsonEscape(getOwnerUUID()))));
		builder.append(String.format("\t\t\"%s\":%s,\n",jsonSubstance.ownerName.name(),JSONUtils.jsonQuote(JSONUtils.jsonEscape(getOwnerName()))));
		builder.append(String.format("\t\t\"%s\":%s,\n",jsonSubstance.i5uuid.name(),JSONUtils.jsonQuote(JSONUtils.jsonEscape(getCompanyUUID()))));
		builder.append(String.format("\t\t\"%s\":%s,\n",jsonSubstance.name.name(),JSONUtils.jsonQuote(JSONUtils.jsonEscape(getCompanyName()))));
		builder.append(String.format("\t\t\"%s\":%s,\n",jsonSubstance.publicname.name(),JSONUtils.jsonQuote(JSONUtils.jsonEscape(getPublicName()))));
		builder.append(String.format("\t\t\"%s\":%s,\n",jsonSubstance.format.name(),JSONUtils.jsonQuote(JSONUtils.jsonEscape(getFormat()))));
		builder.append(String.format("\t\t\"%s\":%s,\n",jsonSubstance.substanceType.name(),JSONUtils.jsonQuote(JSONUtils.jsonEscape(getSubstancetype()))));
		builder.append(String.format("\t\t\"%s\": {\n\t\t\t\"%s\":%s,\n\t\t\t\"%s\":%s\n\t\t},\n",
				jsonSubstance.referenceSubstance.name(),
				jsonSubstance.i5uuid.name(),JSONUtils.jsonQuote(JSONUtils.jsonEscape(getReferenceSubstanceUUID())),
				"uri",
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(
						baseReference+"/query/compound/search/all?search="+getReferenceSubstanceUUID()
						))
				));
		builder.append(String.format("\t\t\"%s\":[\n",jsonSubstance.composition.name()));		
		if (getRelatedStructures()!=null) {
			for (CompositionRelation relation : getRelatedStructures()) {
				builder.append(relation.toJSON(uri, 
						String.format("{\"compound\": { \"URI\": \"%s/compound/%d\"}}",
								baseReference,relation.getSecondStructure().getIdchemical())));
			}
		}
		builder.append("\t],\n");
		builder.append(String.format("\t\t\"%s\":[\n",jsonSubstance.externalIdentifiers.name()));
		if (getExternalids()!=null) {
			String d = "";
			for (ExternalIdentifier id : getExternalids()) {
				builder.append(d);
				builder.append(id.toString());
				d = ",";
			}	
		}	
		builder.append("\t]\n");
		builder.append("\t}");
		return builder.toString();
	}	
	
	public static String getURI(String ref, SubstanceRecord item) {
		if (item.getCompanyUUID()!=null)
			return String.format("%s/substance/%s", ref,item.getCompanyUUID());
		else 
			return String.format("%s/substance/%d", ref,item.getIdsubstance());
	}
}
