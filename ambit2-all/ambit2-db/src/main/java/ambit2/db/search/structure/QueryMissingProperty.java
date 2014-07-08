package ambit2.db.search.structure;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.ISourceDataset;
import ambit2.base.data.Property;
import ambit2.db.search.BooleanCondition;

public class QueryMissingProperty extends AbstractStructureQuery<ISourceDataset,Property,BooleanCondition> {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8610455060315105098L;
	protected static String MISSING_DESCRIPTOR =
		"select ? as idquery,s.idchemical,s.idstructure,1 as selected,1 as metric,type_structure as text from structure s\n" +
		"join struc_dataset using(idstructure)\n"+
		"left outer join\n"+
		"property_values p on s.idstructure=p.idstructure\n"+
		"and id_srcdataset=?\n"+
		"and idproperty=?\n"+
		"where type_structure != 'NA' and id is null";	
	
/**
SELECT structure.idchemical,s.idstructure,type_structure,p1.status,p2.status FROM
structure
join struc_dataset s using(idstructure)
left outer join
property_values p1 on s.idstructure=p1.idstructure
and p1.idproperty =36
and id_srcdataset=1
left outer join
property_values p2 on s.idstructure=p2.idstructure
and p2.idproperty =37
and id_srcdataset=1
where type_structure != "NA" and p2.id is null and p1.id is null	
 */
	protected long pageSize;
	public QueryMissingProperty() {
		setCondition(BooleanCondition.getInstance(BooleanCondition.BOOLEAN_CONDITION.B_NO.toString()));
	}
	public String getSQL() throws AmbitException {
		if (getFieldname()==null || getFieldname().getID()<=0) throw new AmbitException("Invalid dataset");
		if (getValue()==null || getValue().getId()<=0) throw new AmbitException("Invalid property id");
		return MISSING_DESCRIPTOR;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		if (getFieldname()==null || getFieldname().getID()<=0) throw new AmbitException("Invalid dataset");
		if (getValue()==null || getValue().getId()<=0) throw new AmbitException("Invalid property id");
		
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		params.add(new QueryParam<Integer>(Integer.class, getFieldname().getID()));
		params.add(new QueryParam<Integer>(Integer.class, getValue().getId()));
		return params;
	}
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Dataset structures with missing values for descriptor");
		return b.toString();
	}
	@Override
	public long getPageSize() {
		return pageSize;
	}
	@Override
	public void setPageSize(long records) {
		this.pageSize = records;
	}
}
