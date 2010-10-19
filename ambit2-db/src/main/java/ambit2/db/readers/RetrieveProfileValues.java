package ambit2.db.readers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.QueryParam;
import ambit2.db.search.SetCondition;
import ambit2.db.search.SetCondition.conditions;

public class RetrieveProfileValues extends AbstractQuery<Profile<Property>,IStructureRecord,SetCondition,IStructureRecord> 
					implements IQueryRetrieval<IStructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6445434992210833166L;
	public static final String NaN = "NaN";
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
	public enum SearchMode {
		name {
			@Override
			public String getParam(Property arg0) {
				return arg0.getName();
			}
		},
		alias {
			@Override
			public String getSQL() {
				return "comments";
			}
			@Override
			public String getParam(Property arg0) {
				return arg0.getLabel();
			}
		},
		idproperty {
			@Override
			public String getParam(Property arg0) {
				try {
					if (arg0.getId()>0)
						return Integer.toString(arg0.getId());
					else return null;
				} catch (Exception x) {
					return null;
				}
			}
		};
		public String getSQL() {
			return toString();
		}
		public abstract String getParam(Property property);
	}
	protected SearchMode searchMode = SearchMode.name;
	
	public RetrieveProfileValues() {
		this(SearchMode.idproperty,null,false);
		
	}
	public RetrieveProfileValues(SearchMode querymode,Profile profile,boolean chemicalsOnly) {
		super();
		setSearchMode(querymode);
		setFieldname(profile);
		setCondition(new SetCondition(conditions.in));
		setChemicalsOnly(chemicalsOnly);
	}		
	public SearchMode getSearchMode() {
		return searchMode;
	}

	public void setSearchMode(SearchMode searchMode) {
		this.searchMode = searchMode;
	}
	protected boolean chemicalsOnly = false; 
	
	public boolean isChemicalsOnly() {
		return chemicalsOnly;
	}

	public void setChemicalsOnly(boolean chemicalsOnly) {
		this.chemicalsOnly = chemicalsOnly;
	}

	public boolean isSearchByAlias() {
		return searchMode==SearchMode.alias;
	}

	public void setSearchByAlias(boolean value) {
		searchMode = value?SearchMode.alias:SearchMode.name;
	}

	public boolean isSearchByID() {
		return searchMode==SearchMode.idproperty;
	}

	public void setSearchByID(boolean value) {
		searchMode = value?SearchMode.idproperty:SearchMode.name;
	}	
	protected final String sql_structure = 
		"select name,idreference,idproperty,idstructure,ifnull(text,value) as value_string,value_num,title,url,idchemical,id,units\n"+ 
		"from property_values\n"+ 
		"left join property_string using(idvalue_string)\n"+
		"join properties using(idproperty) join catalog_references using(idreference)\n"+ 
		"where status != 'ERROR'\n" +
		"%s\n"+
		"and idstructure=?\n";
	
	protected final String sql_structure_novalue = 
		"select name,idreference,idproperty,idstructure,null,null,title,url,idchemical,null,units from structure \n"+
		"join  properties join catalog_references using(idreference)\n"+
		"where status != 'ERROR' %s and idstructure=?";
	
	
	protected final String sql_chemical = 
		"select name,idreference,idproperty,idstructure,ifnull(text,value) as value_string,value_num,title,url,idchemical,id,units\n"+ 
		"from property_values\n"+ 
		"left join property_string using(idvalue_string)\n"+
		"join properties using(idproperty) join catalog_references using(idreference)\n"+ 
		"where status != 'ERROR'\n"+
		"%s\n"+
		"and idchemical=?\n";

	
	protected final String sql_chemical_novalue = 
		"select name,idreference,idproperty,idstructure,null,null,title,url,idchemical,null,units from structure \n"+
		"join  properties join catalog_references using(idreference)\n"+
		"where status != 'ERROR' %s and idchemical=? order by idstructure limit 1";
			
	protected final String sql_propery = "and idproperty in (%s)";
	
	/**
	 * 
	 * @return
	 */
	protected String sql() {
		return isChemicalsOnly()?
				(addNew?sql_chemical_novalue:sql_chemical):
				(addNew?sql_structure_novalue:sql_structure);
	}
	
	protected String getPropertyWhere() {
		StringBuilder b = new StringBuilder();
		if ((getFieldname()!=null) &&(getFieldname().size()>0)) {
			Iterator<Property> i = getFieldname().getProperties(true);
			
			String delimiter = "";
			while (i.hasNext()) {
				String p = searchMode.getParam(i.next());
				if (p != null) {
					b.append(delimiter);
					b.append("?");
					delimiter = ",";
				}
				
			}
		}
		return b.toString();
	}
	public String getSQL() throws AmbitException {
		
		
		return String.format(
				sql(),
				(getFieldname()==null || "".equals(getFieldname()) )
				?"":String.format(sql_propery,getPropertyWhere())
				);
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();

			if ((getFieldname()!=null) &&(getFieldname().size()>0)) {
				Iterator<Property> i = getFieldname().getProperties(true);
				while (i.hasNext()) {
					Property p = i.next();
					params.add(new QueryParam<Integer>(Integer.class, p.getId()));
				}
			}


		params.add(new QueryParam<Integer>(Integer.class, isChemicalsOnly()?getValue().getIdchemical():getValue().getIdstructure()));

		return params;		
	}

	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
		try {
			IStructureRecord record = getRecord();
			if (record==null) record = new StructureRecord();
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
