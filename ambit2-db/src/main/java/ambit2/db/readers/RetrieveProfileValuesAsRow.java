package ambit2.db.readers;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.SetCondition;
import ambit2.db.search.SetCondition.conditions;

public class RetrieveProfileValuesAsRow extends AbstractQuery<Profile<Property>,int[],SetCondition,IStructureRecord> 
					implements IQueryRetrieval<IStructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6445434992210833166L;
	public static final String NaN = "NaN";
	protected Boolean chemicalsOnly = true;
	
	public Boolean getChemicalsOnly() {
		return chemicalsOnly;
	}

	public void setChemicalsOnly(Boolean chemicalsOnly) {
		this.chemicalsOnly = chemicalsOnly;
	}
	protected IStructureRecord record;
	public IStructureRecord getRecord() {
		return record;
	}

	public void setRecord(IStructureRecord record) {
		this.record = record;
	}
	protected boolean addNew = false;
	public boolean isAddNew() {
		return addNew;
	}

	public void setAddNew(boolean addNew) {
		this.addNew = addNew;
	}

	
	public RetrieveProfileValuesAsRow() {
		this(null,false);
		
	}
	public RetrieveProfileValuesAsRow(Profile profile,boolean chemicalsOnly) {
		super();
		setChemicalsOnly(chemicalsOnly);
		setFieldname(profile);
		setCondition(new SetCondition(conditions.in));

	}		
	protected final String sql_property = 
		"MAX(CASE WHEN pv.idproperty = ? THEN value ELSE NULL END) AS v%d,\n"+
		"MAX(CASE WHEN pv.idproperty = ? THEN value_num ELSE NULL END) AS n%d\n";

	protected final String sql_structure = 
		"SELECT pv.idchemical,pv.idstructure \n" +
		"%s\n"+
	    "FROM\n"+
	    "property_values AS pv\n"+
	    "LEFT JOIN property_string s ON s.idvalue_string=pv.idvalue_string\n"+
	  //  "JOIN structure USING(idstructure)\n" +
	    "WHERE idstructure %s %s\n"+
	    "AND idproperty in (%s)\n"+
	    "GROUP BY pv.idstructure\n";
		
	
	protected final String sql_chemical = 
		"SELECT idchemical,idstructure \n" +
		"%s\n"+
	    "FROM\n"+
	    "property_values AS pv\n"+
	    "LEFT JOIN property_string s ON s.idvalue_string=pv.idvalue_string\n"+
	    //"LEFT JOIN structure USING(idstructure)\n"+
	    "WHERE idchemical %s %s\n"+
	    "AND idproperty in (%s)\n"+
	    "GROUP BY idchemical\n";
	
	protected String getStructureWhere() throws AmbitException {
		if ((getValue()==null) || (getValue().length==0)) throw new AmbitException("Structure id missing");
		//if (getValue().length==1) return "?";
		StringBuilder b = new StringBuilder();

		String delimiter = "(";
		for (int i=0;i < getValue().length;i++)  {
			if (getValue()[i]<=0) continue;
			b.append(String.format("%s?",delimiter));
			delimiter = ",";
		}
		b.append(")");
		return b.toString();
	}
	
	protected String getPropertySQL() {
		StringBuilder b = new StringBuilder();

		if ((getFieldname()!=null) &&(getFieldname().size()>0)) {
			Iterator<Property> i = getFieldname().getProperties(true);
			
			int count = 0;
		
			String delimiter = ",";
			while (i.hasNext()) {
				int p = i.next().getId();
				if (p > 0) {
					b.append(delimiter);
					b.append(String.format(sql_property,p,p));
					count++;
				}
				
			}
		}
		return b.toString();
	}
	
	protected String getPropertyWhere() {
		StringBuilder b = new StringBuilder();

		if ((getFieldname()!=null) &&(getFieldname().size()>0)) {
			Iterator<Property> i = getFieldname().getProperties(true);
			
			int count = 0;
		
			String delimiter = "";
			while (i.hasNext()) {
				int p = i.next().getId();
				if (p > 0) {
					b.append(delimiter);
					b.append("?");
					count++;
					delimiter = ",";
				}
				
			}
		}
		return b.toString();
	}	
	public String getSQL() throws AmbitException {
		return String.format(String.format(chemicalsOnly?sql_chemical:sql_structure,
				getPropertySQL(),
				getCondition().getSQL(),
				getStructureWhere(),
				getPropertyWhere()));
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();

			if ((getFieldname()!=null) &&(getFieldname().size()>0)) {
				Iterator<Property> i = getFieldname().getProperties(true);
				while (i.hasNext()) {
					int p = i.next().getId();
					if (p >0) {
						params.add(new QueryParam<Integer>(Integer.class, p));
						params.add(new QueryParam<Integer>(Integer.class, p));
					}
					
				}
			}

		if (getValue()!=null)
		for (int i=0;i < getValue().length;i++)  
			if (getValue()[i]<=0) continue;
			else params.add(new QueryParam<Integer>(Integer.class, getValue()[i]));
		
		
		if ((getFieldname()!=null) &&(getFieldname().size()>0)) {
			Iterator<Property> i = getFieldname().getProperties(true);
			while (i.hasNext()) {
				int p = i.next().getId();
				if (p >0) {
					params.add(new QueryParam<Integer>(Integer.class, p));
				}
			}
		}
		return params;		
	}

	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
		try {
			IStructureRecord record = getRecord();
			if (record==null) record = new StructureRecord();
			record.setIdchemical(rs.getInt(1));
			record.setIdstructure(rs.getInt(2));
			
			Iterator<Property> i = getFieldname().getProperties(true);
			while (i.hasNext()) {
				Property p = i.next();
				if (p.getId() >0) try {
					String name = String.format("v%d",p.getId());
					Object value = rs.getString(name);
					if (value == null) {
						name = String.format("n%d",p.getId());
						if (rs.getObject(name)!= null)
							value = rs.getFloat(name);
					}
					record.setProperty(p,value);
				} catch (Exception x) {
					logger.log(Level.WARNING,x.getMessage(),x);
				}
				
			}

			/*
			if (chemicalsOnly && record.getIdstructure()>0) 
				; //skip
			else record.setIdstructure(rs.getInt(4));
			record.setIdchemical(rs.getInt(9));
			LiteratureEntry le = LiteratureEntry.getInstance(rs.getString(7),rs.getString(8),rs.getInt(2));
			Property p = Property.getInstance(rs.getString(1),le); 
			Object value = rs.getObject(5);
			
			if (value == null) {
				value = rs.getObject(6);
				record.setProperty(p,value==null?Double.NaN:rs.getFloat(6));
				p.setClazz(Number.class);
			}
			else {
				if (NaN.equals(value.toString())) {
					record.setProperty(p,Double.NaN);
					p.setClazz(Number.class);
				} else {
					record.setProperty(p,rs.getString(5));
					p.setClazz(String.class);
				}
			}
			*/
			return record;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
		
	}

	public double calculateMetric(IStructureRecord object) {
		return 1;
	}
	public boolean isPrescreen() {
		return false;
	}
	
	
}
