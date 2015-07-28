package ambit2.db.substance.ids;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceName;
import ambit2.base.data.substance.SubstancePublicName;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.db.substance.properties.ReadChemPropertiesByComposition;

/**
 * compound identifiers for each of the substance composition components
 * Retrieves
 * 
 * @author nina
 * 
 */
public class ReadChemIdentifiersByComposition extends
		ReadChemPropertiesByComposition {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8810690686530660839L;
	private static final String _sql = "select idchemical,idstructure,comments,if(status='TRUNCATED',text,value),value_num,title,url,name\n"
			+ "from property_values left join property_string using(idvalue_string) \n"
			+ "join properties using(idproperty) join catalog_references using(idreference)\n"
			+ "where idchemical in (%s) and comments in (\n"
			+ "\"http://www.opentox.org/api/1.1#ChemicalName\", \"http://www.opentox.org/api/1.1#IUCLID5_UUID\","
			+ "\"http://www.opentox.org/api/1.1#TradeName\",\"http://www.opentox.org/api/1.1#CASRN\",\"http://www.opentox.org/api/1.1#EINECS\")";

	@Override
	protected String getSQLTemplate() {
		return _sql;
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
			// LiteratureEntry le =
			// LiteratureEntry.getInstance(rs.getString(6),rs.getString(7));
			Property p = new Property(rs.getString(3));
			p.setEnabled(true);
			p.setLabel(rs.getString(3));
			Object value = rs.getObject(4);

			if (value == null) {
				value = rs.getObject(5);

				if (value == null) {
					record.setProperty(p, Double.NaN);
					p.setClazz(Number.class);
				} else
					try {
						record.setProperty(p, rs.getFloat(5));
						p.setClazz(Number.class);
					} catch (Exception x) { // non-numbers, because of the
						// concat ...
						record.setProperty(p, rs.getString(5));
						p.setClazz(String.class);
					}
			} else {
				if (NaN.equals(value.toString())) {
					record.setProperty(p, Double.NaN);
					p.setClazz(Number.class);
				} else {
					record.setProperty(p, rs.getString(4));
					p.setClazz(String.class);
				}
			}
			return record;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
	@Override
	public SubstanceRecord processDetail(SubstanceRecord target,
			IStructureRecord detail) throws Exception {
		for (CompositionRelation r : target.getRelatedStructures())
			if (detail.getIdchemical() == r.getSecondStructure()
					.getIdchemical()) {
				for (Property p : detail.getProperties()) {
					r.getSecondStructure()
							.setProperty(p, detail.getProperty(p));
				}
				if (target.getIdsubstance() == -1) {

					r.getRelation().setReal_lower("=");
					r.getRelation().setReal_lowervalue(100.0);
					r.getRelation().setReal_uppervalue(100.0);
					r.getRelation().setReal_upper("=");
					r.getRelation().setReal_unit("%");
					r.getRelation().setTypical("=");
					r.getRelation().setTypical_value(100.0);
					r.getRelation().setTypical_unit("%");

					for (Property p : detail.getProperties()) {
						if (Property.opentox_Name.equals(p.getLabel())) {
							target.setProperty(new SubstancePublicName(),
									detail.getProperty(p));
						} else if (Property.opentox_TradeName.equals(p
								.getLabel())) {
							target.setProperty(new SubstanceName(),
									detail.getProperty(p));
						}
					}
				}
				break;
			}
		return target;
	}
}
