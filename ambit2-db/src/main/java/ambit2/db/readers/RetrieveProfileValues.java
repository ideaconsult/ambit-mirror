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
		"select name,idreference,idproperty,idstructure,ifnull(text,value) as value_string,value_num,title,url,-1,id,units from property_values \n"+
		"left join property_string using(idvalue_string) \n"+
		"join properties using(idproperty) join catalog_references using(idreference) \n"+
		"where idstructure=? %s %s";
	
	protected final String sql_structure_novalue = 
		"select name,idreference,idproperty,idstructure,null,null,title,url,idchemical,null,units from structure \n"+
		"join  properties join catalog_references using(idreference)\n"+
		"where idstructure=? %s %s";
	
	protected final String sql_chemical = 
		"select name,idreference,idproperty,idstructure,ifnull(text,value) as value_string,value_num,title,url,idchemical,id,units from property_values \n"+
		"join structure using(idstructure) left join property_string using(idvalue_string) \n"+
		"join properties using(idproperty) join catalog_references using(idreference) \n"+
		"where idchemical=? %s %s group by comments,idvalue_string";
	
	protected final String sql_chemical_novalue = 
		"select name,idreference,idproperty,idstructure,null,null,title,url,idchemical,null,units from structure \n"+
		"join  properties join catalog_references using(idreference)\n"+
		"where idchemical=? %s %s order by idstructure limit 1";
				

	protected final String where = "and %s ";

	protected String sql() {
		return isChemicalsOnly()?
				(addNew?sql_chemical_novalue:sql_chemical):
				(addNew?sql_structure_novalue:sql_structure);
	}
	public String getSQL() throws AmbitException {
		StringBuilder b = new StringBuilder();

		if ((getFieldname()!=null) &&(getFieldname().size()>0)) {
			Iterator<Property> i = getFieldname().getProperties(true);
			
			int count = 0;
			b.append(getCondition());
			b.append(" (");
			String delimiter = "";
			while (i.hasNext()) {
				String p = searchMode.getParam(i.next());
				if (p != null) {
					b.append(delimiter);
					b.append(p);
					count++;
					delimiter = ",";
				}
				
			}
			b.append(")");
			if (count == 0) {
				b = new StringBuilder();
				b.append(" is null");
			}
		}
		
		return String.format(
				sql(),
				(getFieldname()==null || "".equals(getFieldname()) )
				?"":String.format(where,searchMode.getSQL()),
				b.toString()
				);
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, isChemicalsOnly()?getValue().getIdchemical():getValue().getIdstructure()));

		return params;		
	}

	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
		try {
			IStructureRecord record = getRecord();
			if (record==null) record = new StructureRecord();
			record.setIdstructure(rs.getInt(4));
			record.setIdchemical(rs.getInt(9));
			LiteratureEntry le = LiteratureEntry.getInstance(rs.getString(7),rs.getString(8),rs.getInt(2));
			Property p = Property.getInstance(rs.getString(1),le); 
			Object value = rs.getObject(5);
			if (value == null) record.setProperty(p,rs.getFloat(6));
			else record.setProperty(p,rs.getString(5));	
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
