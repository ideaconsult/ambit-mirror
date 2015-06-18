package ambit2.base.data;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.facet.IFacet;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.substance.ExternalIdentifier;
import ambit2.base.facet.BundleRoleFacet;
import ambit2.base.facet.SubstanceStudyFacet;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRelation;
import ambit2.base.json.JSONUtils;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.base.relation.composition.Proportion;

/**
 * Same as the parent class, but with related structures
 * 
 * @author nina
 * 
 *         <pre>
 * {
 *     "substance": [
 *         {
 *             "URI": "http://host:8080/data/substance/IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734",
 *             "ownerUUID": "IUC4-44bf02d8-47c5-385d-b203-9a8f315911cb",
 *             "ownerName": "OECD / Paris / France",
 *             "i5uuid": "IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734",
 *             "name": "formaldehyde (IUC4 DSN 52)",
 *             "publicname": "",
 *             "format": "i5._5.",
 *             "substanceType": "Existing Chemical",
 *             "referenceSubstance": {
 *                 "i5uuid": "ECB5-053aa8c4-d29b-4aa5-b457-5cc3b47f7d8b",
 *                 "uri": "http://apps.ideaconsult.net:8080/data/query/compound/search/all?search=ECB5-053aa8c4-d29b-4aa5-b457-5cc3b47f7d8b"
 *             },
 *             "composition": [],
 *             "externalIdentifiers": [
 *                 {
 *                     "type": "IUCLID4",
 *                     "id": "DSN 52"
 *                 }
 *             ]
 *         },
 *         {
 *             "URI": "http://host:8080/enanomapper/substance/NWKI-71060af4-1613-35cf-95ee-2a039be0388a",
 *             "ownerUUID": "NWKI-9f4e86d0-c85d-3e83-8249-a856659087da",
 *             "ownerName": "NanoWiki",
 *             "i5uuid": "NWKI-71060af4-1613-35cf-95ee-2a039be0388a",
 *             "name": "Kim2012 NM1",
 *             "publicname": "CuO",
 *             "format": "",
 *             "substanceType": "MetalOxide",
 *             "referenceSubstance": {
 *                 "i5uuid": "NWKI-71060af4-1613-35cf-95ee-2a039be0388a",
 *                 "uri": "http://host:8080/enanomapper/query/compound/search/all?search=NWKI-71060af4-1613-35cf-95ee-2a039be0388a"
 *             },
 *             "composition": [],
 *             "externalIdentifiers": [
 *                 {
 *                     "type": "Composition",
 *                     "id": "CuO"
 *                 },
 *                 {
 *                     "type": "DATASET",
 *                     "id": "NanoWiki"
 *                 },
 *                 {
 *                     "type": "Has_Identifier",
 *                     "id": "139"
 *                 }
 *             ]
 *         }
 *     ]
 * }
 * </pre>
 */
public class SubstanceRecord extends StructureRecord {
	protected List<ExternalIdentifier> externalids;
	protected String ownerUUID;
	protected String ownerName;
	protected int idsubstance;
	protected String substancetype;
	protected String internalName;
	protected String publicName;
	protected String internalUUID;

	public enum jsonSubstance {
		URI, externalIdentifiers, i5uuid, documentType, format, name, publicname, content, substanceType, referenceSubstance, ownerUUID, ownerName, composition;
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
		if (this.measurements == null)
			this.measurements = new ArrayList<ProtocolApplication>();
		measurements.add(measurement);
	}

	/**
	 * IUCLID5: monoconsituent, multiconstituent, UVCB (Unknown or Variable
	 * composition, Complex reaction products or Biological materials)
	 * http://echa
	 * .europa.eu/documents/10162/13643/nutshell_guidance_substance_en.pdf
	 * 
	 * <pre>
	 * "substanceType": "Existing Chemical"
	 * </pre>
	 * 
	 * To be extended for nanomaterials.
	 * 
	 * @return
	 */
	public String getSubstancetype() {
		return substancetype;
	}

	/**
	 * Substance name, as assigned by the owner
	 * 
	 * <pre>
	 * 		"i5uuid": "IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734",
	 * 		"name": "formaldehyde (IUC4 DSN 52)",
	 * </pre>
	 * 
	 * @return
	 */
	public String getSubstanceName() {
		return internalName;
	}

	/**
	 * Substance name, as assigned by the owner
	 * 
	 * @param companyName
	 */
	public void setSubstanceName(String internalName) {
		this.internalName = internalName;
	}

	/**
	 * Public name of the substance
	 * 
	 * @return
	 */
	public String getPublicName() {
		return publicName;
	}

	public void setPublicName(String publicname) {
		this.publicName = publicname;
	}

	/**
	 * Substance UUID, as assigned by the owner
	 * 
	 * <pre>
	 * 	"i5uuid": "IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734",
	 * 	"name": "formaldehyde (IUC4 DSN 52)",
	 * </pre>
	 * 
	 * @return
	 */
	public String getSubstanceUUID() {
		return internalUUID;
	}

	/**
	 * Substance UUID as assigned by the substance owner
	 * 
	 * @param internalUUID
	 */
	public void setSubstanceUUID(String internalUUID) {
		this.internalUUID = internalUUID;
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
		setSubstanceUUID(uuid);
	}

	public SubstanceRecord(int idsubstance) {
		super();
		setIdsubstance(idsubstance);
	}

	protected List<CompositionRelation> relatedStructures;

	/**
	 * {@link CompositionRelation}
	 * 
	 * <pre>
	 * 	"composition": []
	 * </pre>
	 */
	public List<CompositionRelation> getRelatedStructures() {
		return relatedStructures;
	}

	public void setRelatedStructures(List<CompositionRelation> relatedStructures) {
		this.relatedStructures = relatedStructures;
	}

	public void addStructureRelation(CompositionRelation relation) {
		if (relatedStructures == null)
			relatedStructures = new ArrayList<CompositionRelation>();
		relatedStructures.add(relation);
	}

	public IStructureRelation addStructureRelation(String compositionUUID,
			IStructureRecord record, STRUCTURE_RELATION relation,
			Proportion value) {
		if (relatedStructures == null)
			relatedStructures = new ArrayList<CompositionRelation>();
		CompositionRelation r = new CompositionRelation(this, record, value);
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
		if (uuid == null)
			removeProperty(Property.getI5UUIDInstance());
		else
			setProperty(Property.getI5UUIDInstance(), uuid);
	}

	/**
	 * referenceSubstance (link to reference structure).Typical use
	 * {@link IStructureRecord}
	 * 
	 * @return
	 */
	public String getReferenceSubstanceUUID() {
		Object name = getProperty(Property.getI5UUIDInstance());
		return name == null ? null : name.toString();
	}

	@Override
	public void clear() {
		super.clear();
		idsubstance = -1;
		setOwnerUUID(null);

		if (relatedStructures != null)
			relatedStructures.clear();
		if (measurements != null)
			measurements.clear();
		if (externalids != null)
			externalids.clear();
	}

	@Override
	public String toString() {
		return String.format("idsubstance=%d %s", getIdsubstance(),
				getSubstanceUUID());
	}

	/**
	 * UUID of the company, which is the substance owner (usually the substance
	 * manufacturer)
	 * 
	 * <pre>
	 *             "ownerUUID": "IUC4-44bf02d8-47c5-385d-b203-9a8f315911cb",
	 *             "ownerName": "OECD / Paris / France",
	 * </pre>
	 * 
	 * @return
	 */
	public String getOwnerUUID() {
		return ownerUUID;
	}

	public void setOwnerUUID(String ownerUUID) {
		this.ownerUUID = ownerUUID;
	}

	/**
	 * Name of the company, which is the substance owner (usually the substance
	 * manufacturer)
	 * 
	 * <pre>
	 *             "ownerUUID": "IUC4-44bf02d8-47c5-385d-b203-9a8f315911cb",
	 *             "ownerName": "OECD / Paris / France",
	 * </pre>
	 * 
	 * @return
	 */
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
		String uri = getURI(baseReference, this);
		StringBuilder builder = new StringBuilder();
		builder.append("\n\t{\n");
		builder.append(String.format("\t\t\"%s\":%s,\n",
				jsonSubstance.URI.name(),
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(uri))));
		builder.append(String.format("\t\t\"%s\":%s,\n",
				jsonSubstance.ownerUUID.name(),
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(getOwnerUUID()))));
		builder.append(String.format("\t\t\"%s\":%s,\n",
				jsonSubstance.ownerName.name(),
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(getOwnerName()))));
		builder.append(String.format("\t\t\"%s\":%s,\n",
				jsonSubstance.i5uuid.name(),
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(getSubstanceUUID()))));
		builder.append(String.format("\t\t\"%s\":%s,\n",
				jsonSubstance.name.name(),
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(getSubstanceName()))));
		builder.append(String.format("\t\t\"%s\":%s,\n",
				jsonSubstance.publicname.name(),
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(getPublicName()))));
		builder.append(String.format("\t\t\"%s\":%s,\n",
				jsonSubstance.format.name(),
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(getFormat()))));
		builder.append(String.format("\t\t\"%s\":%s,\n",
				jsonSubstance.substanceType.name(),
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(getSubstancetype()))));
		builder.append(String.format(
				"\t\t\"%s\": {\n\t\t\t\"%s\":%s,\n\t\t\t\"%s\":%s\n\t\t},\n",
				jsonSubstance.referenceSubstance.name(), jsonSubstance.i5uuid
						.name(), JSONUtils.jsonQuote(JSONUtils
						.jsonEscape(getReferenceSubstanceUUID())), "uri",
				JSONUtils.jsonQuote(JSONUtils.jsonEscape(baseReference
						+ "/query/compound/search/all?search="
						+ getReferenceSubstanceUUID()))));
		builder.append(String.format("\t\t\"%s\":[\n",
				jsonSubstance.composition.name()));
		if (getRelatedStructures() != null) {
			for (CompositionRelation relation : getRelatedStructures()) {
				builder.append(relation.toJSON(uri, String.format(
						"{\"compound\": { \"URI\": \"%s/compound/%d\"}}",
						baseReference, relation.getSecondStructure()
								.getIdchemical())));
			}
		}
		builder.append("\t],\n");
		builder.append(String.format("\t\t\"%s\":[\n",
				jsonSubstance.externalIdentifiers.name()));
		if (getExternalids() != null) {
			String d = "";
			for (ExternalIdentifier id : getExternalids()) {
				builder.append(d);
				builder.append(id.toString());
				d = ",";
			}
		}
		builder.append("\t],\n");

		builder.append(String.format("\n\t%s:{\n",
				JSONUtils.jsonQuote("bundles")));
		Iterable<IFacet> facets = getFacets();
		String delimiter = "";
		if (facets != null)
			for (IFacet facet : facets)
				if (facet instanceof BundleRoleFacet) {
					builder.append(delimiter);
					builder.append("\n\t\t");
					builder.append(facet.toJSON(baseReference, null));
					delimiter = ",";
				}
		builder.append("\n\t\t}");

		builder.append(String.format("\n\t,%s:[\n",
				JSONUtils.jsonQuote("studysummary")));
		if (facets != null)
			for (IFacet facet : facets)
				if (facet instanceof SubstanceStudyFacet) {
					builder.append(delimiter);
					builder.append("\n\t\t");
					builder.append(facet.toJSON(uri, null));
					delimiter = ",";
				}

		builder.append("\t]");
		builder.append("\t}");
		return builder.toString();
	}

	public static String getURI(String ref, SubstanceRecord item) {
		if (item.getSubstanceUUID() != null)
			return String.format("%s/substance/%s", ref,
					item.getSubstanceUUID());
		else if (item.getIdsubstance() > 0)
			return String.format("%s/substance/%d", ref, item.getIdsubstance());
		if (item.getIdchemical() > 0)
			return String.format("%s/compound/%d", ref, item.getIdchemical());
		else
			return null;
	}
}
