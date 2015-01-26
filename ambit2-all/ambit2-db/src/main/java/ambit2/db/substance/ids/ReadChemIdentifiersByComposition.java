package ambit2.db.substance.ids;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.SetCondition;
import net.idea.modbcum.q.query.AbstractQuery;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.composition.CompositionRelation;

/**
 * compound identifiers for each of the substance composition components
 * Retrieves
 * 
 * @author nina
 * 
 */
public class ReadChemIdentifiersByComposition extends
	AbstractQuery<SubstanceRecord, IStructureRecord, SetCondition, IStructureRecord> implements
	IQueryRetrieval<IStructureRecord> {
    /**
	 * 
	 */
    private static final long serialVersionUID = -8810690686530660839L;
    private static final String _sql = "select idchemical,idstructure,comments,if(status='TRUNCATED',text,value),value_num,title,url\n"
	    + "from property_values left join property_string using(idvalue_string) \n"
	    + "join properties using(idproperty) join catalog_references using(idreference)\n"
	    + "where idchemical in (%s) and comments in (\n"
	    + "\"http://www.opentox.org/api/1.1#ChemicalName\", \"http://www.opentox.org/api/1.1#IUCLID5_UUID\","
	    + "\"http://www.opentox.org/api/1.1#TradeName\",\"http://www.opentox.org/api/1.1#CASRN\",\"http://www.opentox.org/api/1.1#EINECS\")";

    protected IStructureRecord record;

    public IStructureRecord getRecord() {
	return record;
    }

    public void setRecord(IStructureRecord record) {
	this.record = record;
    }

    @Override
    public String getSQL() throws AmbitException {
	StringBuilder params = null;

	if (getFieldname() != null && getFieldname().getRelatedStructures() != null)
	    for (CompositionRelation r : getFieldname().getRelatedStructures())
		if (r.getSecondStructure() != null && r.getSecondStructure().getIdchemical() > 0) {
		    if (params == null) {
			params = new StringBuilder();
			params.append("?");
		    } else
			params.append(",?");
		}
	if (params == null)
	    throw new AmbitException("No idchemical");
	return String.format(_sql, params.toString());
    }

    @Override
    public List<QueryParam> getParameters() throws AmbitException {
	List<QueryParam> params = null;

	if (getFieldname() != null && getFieldname().getRelatedStructures() != null)
	    for (CompositionRelation r : getFieldname().getRelatedStructures())
		if (r.getSecondStructure() != null && r.getSecondStructure().getIdchemical() > 0) {
		    if (params == null)
			params = new ArrayList<QueryParam>();
		    params.add(new QueryParam<Integer>(Integer.class, r.getSecondStructure().getIdchemical()));
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
    public boolean isPrescreen() {
	return false;
    }

    @Override
    public double calculateMetric(IStructureRecord object) {
	return 1;
    }

}
