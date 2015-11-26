package ambit2.db.readers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.SetCondition;

public class RetrieveGroupedValuesByAlias extends AbstractQuery<Profile,IStructureRecord,SetCondition,IStructureRecord> 
															implements IQueryRetrieval<IStructureRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8264868908094861525L;
	protected IStructureRecord record;
	public IStructureRecord getRecord() {
		return record;
	}

	public void setRecord(IStructureRecord record) {
		this.record = record;
	}
	protected final String sql_grouped = 
	"select idchemical,idstructure,comments,group_concat(distinct(if(status='TRUNCATED',text,value)) SEPARATOR '\n'),group_concat(distinct(value_num))," +
	"group_concat(distinct(title)),group_concat(distinct(url))\n"+
	"from property_values left join property_string using(idvalue_string) \n"+
	"join properties using(idproperty)\n"+
	"join catalog_references using(idreference) where idchemical = ? and comments in\n"+ 
	"(%s) group by comments order by comments\n";
	
	public RetrieveGroupedValuesByAlias(Profile aliases) {
		super();
		setFieldname(aliases);
	}
	public double calculateMetric(IStructureRecord object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		if ((getFieldname()==null) || (getFieldname().size()==0)) throw new AmbitException("no params");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class,getValue().getIdchemical()));
		return params;	
	}

	public String getSQL() throws AmbitException {
		StringBuilder b = new StringBuilder();
		String d = "";
		int count = 0;
		Iterator<Property> p = getFieldname().iterator();
		while (p.hasNext()) {
			b.append(String.format("%s\"%s\"",d,p.next().getLabel()));
			d = ",";
			count++;
		}
		if (count==0) throw new AmbitException("Empty params");
		return String.format(sql_grouped,b.toString());
	}

/*
 * 	"select idchemical,idstructure,comments,group_concat(distinct(value)),group_concat(distinct(value_num))," +
	"group_concat(distinct(title)),group_concat(distinct(url)),-1,id,units from\n"+
 * (non-Javadoc)
 * @see ambit2.db.readers.IRetrieval#getObject(java.sql.ResultSet)
 */
	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
		try {
			IStructureRecord record = getRecord();
			if (record==null) record = new StructureRecord();
			if (record.getIdstructure()>0) 
				; //skip
			else record.setIdstructure(rs.getInt(2));
			record.setIdchemical(rs.getInt(1));
			//LiteratureEntry le = LiteratureEntry.getInstance(rs.getString(6),rs.getString(7));
			Property p = new Property(rs.getString(3)); 
			p.setEnabled(true);
			p.setLabel(rs.getString(3));
			Object value = rs.getObject(4);
			
			if (value == null) {
				value = rs.getObject(5);
				
				if (value==null) {
					record.setRecordProperty(p, Double.NaN); p.setClazz(Number.class);
				} else try {
					record.setRecordProperty(p,rs.getFloat(5));
					p.setClazz(Number.class);
				} catch (Exception x) { //non-numbers, because of the concat ... 
					record.setRecordProperty(p,rs.getString(5));
					p.setClazz(String.class);
				}
			}
			else {
				if (NaN.equals(value.toString())) {
					record.setRecordProperty(p,Double.NaN);
					p.setClazz(Number.class);
				} else {
					record.setRecordProperty(p,rs.getString(4));
					p.setClazz(String.class);
				}
			}
			return record;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
		
	}


}
