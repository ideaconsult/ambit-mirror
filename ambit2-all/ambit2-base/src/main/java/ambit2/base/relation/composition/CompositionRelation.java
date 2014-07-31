package ambit2.base.relation.composition;

import java.util.Map;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.facet.IFacet;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.AbstractRelation;
import ambit2.base.relation.STRUCTURE_RELATION;

/**
 * Substance composition
 * @author nina
 *
 */
public class CompositionRelation extends AbstractRelation<STRUCTURE_RELATION, Proportion,SubstanceRecord,IStructureRecord> implements IStructureRecord {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1214326347782066597L;
	
	
	
	public enum jsonFeature {
		substance,
		component,
		compositionUUID,
		name,
		relation,
		proportion
		;
		
		public String jsonname() {
			return name();
		}
	}
	
	protected String compositionUUID;
	public String getCompositionUUID() {
		return compositionUUID;
	}
	public void setCompositionUUID(String compositionUUID) {
		this.compositionUUID = compositionUUID;
	}
	public CompositionRelation(SubstanceRecord structure1,IStructureRecord structure2, Proportion proportion) {
		this(structure1,structure2,STRUCTURE_RELATION.HAS_CONSTITUENT,proportion);
	}
	public CompositionRelation(SubstanceRecord structure1,IStructureRecord structure2, STRUCTURE_RELATION type, Proportion proportion) {
		super(structure1,structure2,proportion);
		setRelationType(type);
	}
	@Override
	public String getFormula() {
		return getSecondStructure().getFormula();
	}
	@Override
	public void setFormula(String formula) {
		getSecondStructure().setFormula(formula);
	}
	@Override
	public String getSmiles() {
		return getSecondStructure().getSmiles();
	}
	@Override
	public void setSmiles(String smiles) {
		getSecondStructure().setSmiles(smiles);
	}
	@Override
	public String getInchi() {
		return getSecondStructure().getInchi();
	}
	@Override
	public void setInchi(String inchi) {
		getSecondStructure().setInchi(inchi);		
	}
	@Override
	public String getInchiKey() {
		return getSecondStructure().getInchiKey();
	}
	@Override
	public void setInchiKey(String key) {
		getSecondStructure().setInchiKey(key);		
	}
	@Override
	public int getIdchemical() {
		return getSecondStructure().getIdchemical();
	}
	@Override
	public void setIdchemical(int idchemical) {
		getSecondStructure().setIdchemical(idchemical);
	}
	@Override
	public String getFormat() {
		return getSecondStructure().getFormat();
	}
	@Override
	public void setFormat(String format) {
		getSecondStructure().setFormat(format);
	}
	@Override
	public int getIdstructure() {
		return getSecondStructure().getIdstructure();
	}
	@Override
	public void setIdstructure(int idstructure) {
		getSecondStructure().setIdstructure(idstructure);
	}
	@Override
	public String getContent() {
		return getSecondStructure().getContent();
	}
	@Override
	public void setContent(String content) {
		getSecondStructure().setContent(content);
	}
	@Override
	public boolean isSelected() {
		return getSecondStructure().isSelected();
	}
	@Override
	public void setSelected(boolean value) {
		getSecondStructure().setSelected(value);
	}
	@Override
	public int getNumberOfProperties() {
		return getSecondStructure().getNumberOfProperties();
	}
	@Override
	public Iterable<Property> getProperties() {
		return getSecondStructure().getProperties();
	}
	@Override
	public void setProperty(Property key, Object value) {
		getSecondStructure().setProperty(key, value);
	}
	@Override
	public Object getProperty(Property key) {
		return getSecondStructure().getProperty(key);
	}
	@Override
	public Object removeProperty(Property key) {
		return getSecondStructure().removeProperty(key);
	}
	@Override
	public void clearProperties() {
		getSecondStructure().clearProperties();
	}
	@Override
	public void addProperties(Map newProperties) {
		getSecondStructure().addProperties(newProperties);
	}
	@Override
	public String getWritableContent() {
		return getSecondStructure().getWritableContent();
	}
	@Override
	public ILiteratureEntry getReference() {
		return getSecondStructure().getReference();
	}
	@Override
	public void setReference(ILiteratureEntry reference) {
		getSecondStructure().setReference(reference);
	}
	@Override
	public STRUC_TYPE getType() {
		return getSecondStructure().getType();
	}
	@Override
	public void setType(STRUC_TYPE type) {
		getSecondStructure().setType(type);
	}
	@Override
	public int getDataEntryID() {
		return getSecondStructure().getDataEntryID();
	}
	@Override
	public void setDataEntryID(int id) {
		getSecondStructure().setDataEntryID(id);
	}
	@Override
	public int getDatasetID() {
		return getSecondStructure().getDatasetID();
	}
	@Override
	public void setDatasetID(int id) {
		getSecondStructure().setDatasetID(id);
	}
	@Override
	public boolean usePreferedStructure() {
		return getSecondStructure().usePreferedStructure();
	}
	@Override
	public void setUsePreferedStructure(boolean value) {
		getSecondStructure().setUsePreferedStructure(value);
	}
	@Override
	public Iterable<IFacet> getFacets() {
		return getSecondStructure().getFacets();
	}
	@Override
	public void addFacet(IFacet facet) {
		getSecondStructure().addFacet(facet);
	}
	@Override
	public void removeFacet(IFacet facet) {
		getSecondStructure().removeFacet(facet);
		
	}
	@Override
	public void clearFacets() {
		getSecondStructure().clearFacets();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}


}
