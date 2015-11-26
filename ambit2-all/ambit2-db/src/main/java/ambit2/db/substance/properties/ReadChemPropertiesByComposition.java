package ambit2.db.substance.properties;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.SetCondition;
import net.idea.modbcum.q.query.AbstractQuery;
import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ReliabilityParams._r_flags;
import ambit2.base.data.substance.SubstanceProperty;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.composition.CompositionRelation;

/**
 * compound identifiers for each of the substance composition components
 * Retrieves
 * 
 * @author nina
 * 
 */
public class ReadChemPropertiesByComposition
		extends
		AbstractQuery<SubstanceRecord, IStructureRecord, SetCondition, IStructureRecord>
		implements IQueryRetrieval<IStructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8810690686530660839L;
	private static final String _sql = "select idchemical,idstructure,comments,if(status='TRUNCATED',text,value),value_num,title,url,name,idproperty,type\n"
			+ "from property_values left join property_string using(idvalue_string) \n"
			+ "join properties using(idproperty) join catalog_references using(idreference)\n"
			+ "where idchemical in (%s) and comments regexp (\"^http://www.opentox.org/echaEndpoints.owl\")";

	protected IStructureRecord record;

	public IStructureRecord getRecord() {
		return record;
	}

	public void setRecord(IStructureRecord record) {
		this.record = record;
	}

	protected String getSQLTemplate() {
		return _sql;
	}

	protected boolean isAllowed(CompositionRelation r) {
		switch (r.getRelationType()) {
		case HAS_ADDITIVE:
			return false;
		case HAS_IMPURITY:
			return false;
		}
		return true;
	}

	@Override
	public String getSQL() throws AmbitException {
		StringBuilder params = null;

		if (getFieldname() != null
				&& getFieldname().getRelatedStructures() != null)
			for (CompositionRelation r : getFieldname().getRelatedStructures()) {
				if (!isAllowed(r))
					continue;
				if (r.getSecondStructure() != null
						&& r.getSecondStructure().getIdchemical() > 0) {
					if (params == null) {
						params = new StringBuilder();
						params.append("?");
					} else
						params.append(",?");
				}
			}
		if (params == null)
			throw new AmbitException("No idchemical");
		return String.format(getSQLTemplate(), params.toString());
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = null;

		if (getFieldname() != null
				&& getFieldname().getRelatedStructures() != null)
			for (CompositionRelation r : getFieldname().getRelatedStructures()) {
				if (!isAllowed(r))
					continue;
				if (r.getSecondStructure() != null
						&& r.getSecondStructure().getIdchemical() > 0) {
					if (params == null)
						params = new ArrayList<QueryParam>();
					params.add(new QueryParam<Integer>(Integer.class, r
							.getSecondStructure().getIdchemical()));
				}
			}
		if (params == null)
			throw new AmbitException("No idchemical");
		return params;
	}

	@Override
	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
		try {
			IStructureRecord record = getRecord();
			if (record == null)
				record = new StructureRecord();
			if (record.getIdstructure() > 0)
				; // skip
			else
				record.setIdstructure(rs.getInt(2));
			record.setIdchemical(rs.getInt(1));
			LiteratureEntry le = LiteratureEntry.getInstance(rs.getString(6),
					rs.getString(7));
			try {
				le.setType(_type.valueOf(rs.getString(10)));
			} catch (Exception x) {
			}
			Property p = new Property(rs.getString(8), le);

			p.setId(rs.getInt(9));
			p.setEnabled(true);
			p.setLabel(rs.getString(3));
			// p.setLabel("http://www.opentox.org/echaEndpoints.owl#UNKNOWN_TOXICITY");
			// p.setLabel("http://www.opentox.org/echaEndpoints.owl#TO_ACUTE_ORAL");
			// text
			Object value = rs.getObject(4);
			if (value == null) {
				// number
				value = rs.getObject(5);
				if (value == null) {
					record.setRecordProperty(p, Double.NaN);
					p.setClazz(Number.class);
				} else
					try {
						record.setRecordProperty(p, value);
						p.setClazz(Number.class);
					} catch (Exception x) { // non-numbers, because of the
						// concat ...
						record.setRecordProperty(p, rs.getString(5));
						p.setClazz(String.class);
					}
			} else {
				if (NaN.equals(value.toString())) {
					record.setRecordProperty(p, Double.NaN);
					p.setClazz(Number.class);
				} else {
					record.setRecordProperty(p, rs.getString(4));
					p.setClazz(String.class);
				}
			}
			return record;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public double calculateMetric(IStructureRecord object) {
		return 1;
	}

	/**
	 * Hack for merging dataset properties with substances; This is not
	 * necessarily correct, as the substance may have many components!
	 * 
	 * @param target
	 * @param detail
	 * @return
	 * @throws Exception
	 */
	public SubstanceRecord processDetail(SubstanceRecord target,
			IStructureRecord detail) throws Exception {
		for (Property p : detail.getRecordProperties()) {
			if (p.getName().indexOf("#explanation") > 0)
				continue;
			else if (p.getName().indexOf("Error") >= 0)
				continue;
			else if (p.getName().indexOf("Alert for") == 0)
				continue;
			else if (p.getName().indexOf("For a better assessment") >= 0)
				continue;

			Protocol._categories category = Protocol._categories.SUPPORTING_INFO_SECTION;
			if (p.getLabel().endsWith("Carcinogenicity"))
				category = Protocol._categories.TO_CARCINOGENICITY_SECTION;
			else if (p.getLabel().endsWith("Mutagenicity"))
				category = Protocol._categories.TO_GENETIC_IN_VITRO_SECTION;
			else if (p.getLabel().endsWith("Dissociation_constant_pKa"))
				category = Protocol._categories.PC_DISSOCIATION_SECTION;
			else if (p.getLabel().endsWith(
					"Octanol-water_partition_coefficient_Kow"))
				category = Protocol._categories.PC_PARTITION_SECTION;
			else if (p.getLabel().endsWith("Acute_toxicity_to_fish_lethality"))
				category = Protocol._categories.EC_FISHTOX_SECTION;
			else if (p.getLabel().endsWith("Eye_irritation_corrosion"))
				category = Protocol._categories.TO_EYE_IRRITATION_SECTION;
			else if (p.getLabel().endsWith("SkinIrritationCorrosion"))
				category = Protocol._categories.TO_SKIN_IRRITATION_SECTION;
			else if (p.getLabel().endsWith("SkinSensitisation"))
				category = Protocol._categories.TO_SENSITIZATION_SECTION;
			else if (p.getLabel().endsWith("PersistenceBiodegradation"))
				category = Protocol._categories.TO_BIODEG_WATER_SCREEN_SECTION;
			else if (p.getLabel().endsWith("Genotoxicity"))
				category = Protocol._categories.TO_GENETIC_IN_VITRO_SECTION;
			else if (p.getLabel().endsWith("ReproductiveToxicity"))
				category = Protocol._categories.TO_REPRODUCTION_SECTION;
			else if (p.getLabel().endsWith("DevelopmentalToxicity"))
				category = Protocol._categories.TO_DEVELOPMENTAL_SECTION;
			else if (p.getLabel().endsWith("Sensitisation"))
				category = Protocol._categories.TO_SENSITIZATION_SECTION;
			else if (p.getLabel().endsWith("Respiratory_sensitisation"))
				category = Protocol._categories.TO_REPEATED_INHAL_SECTION;

			SubstanceProperty sp = new SubstanceProperty("TOX", category.name()
					.replace("_SECTION", ""), p.getName(), p.getUnits(),
					p.getReference());
			sp.setEnabled(true);
			if (p.getReference().getType().equals(_type.Dataset))
				sp.setStudyResultType(_r_flags.experimentalresult);
			else
				sp.setStudyResultType(_r_flags.estimatedbycalculation);

			if (detail.getRecordProperty(p) != null) {
				target.setRecordProperty(sp, detail.getRecordProperty(p));
				/*
				 * Object value = target.getProperty(sp); if (value == null)
				 * target.setProperty(sp, detail.getProperty(p)); else { if
				 * (value instanceof MultiValue) { IValue v = new
				 * Value(detail.getProperty(p)); ((MultiValue) value).add(v); }
				 * else { MultiValue v = new MultiValue(); IValue vo = new
				 * Value(value); v.add(vo); vo = new
				 * Value(detail.getProperty(p)); v.add(vo);
				 * target.setProperty(sp, v); }
				 * 
				 * }
				 */
			}

		}
		return target;
	}
}
