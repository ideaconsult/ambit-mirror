package ambit2.db.search.property;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Dictionary;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.StringCondition;
import ambit2.db.search.property.AbstractPropertyRetrieval.PROPERTY_TABLE;

public class QueryOntology  extends AbstractQuery<Boolean, Dictionary, StringCondition, Property> implements
												IQueryRetrieval<Property> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6711409305156505699L;
	protected String sqlParent = 
	
		"select 0,t2.name,t1.name,'','','',0,-2 ord,-1 idreference,'Parent' comments,true islocal\n"+
		"from ((`template` `t1`\n"+
		"join `dictionary` `d` on((`t1`.`idtemplate` = `d`.`idsubject`)))\n"+
		"join `template` `t2` on((`d`.`idobject` = `t2`.`idtemplate`)))\n"+
		"where t1.name %s ?\n"+
		"union\n";		
	protected String sqlChild = 	
		"select 1,t2.name,t1.name,'','','',0,-1 ord,-1 idreference,'Child' comments,true islocal\n"+
		"from ((`template` `t1`\n"+
		"join `dictionary` `d` on((`t1`.`idtemplate` = `d`.`idsubject`)))\n"+
		"join `template` `t2` on((`d`.`idobject` = `t2`.`idtemplate`)))\n"+
		"where t2.name %s ?\n"+
		"union\n"+
		"select 2,template.name,properties.name,units,title,url,idproperty,`order` ord,idreference,comments,islocal from catalog_references\n"+
		"join properties using(idreference)\n"+
		"join template_def using(idproperty)\n"+
		"join template using(idtemplate)\n"+
		"where template.name %s ?\n"+
		"order by ord";
	protected boolean includeParent = true;
	public boolean isIncludeParent() {
		return includeParent;
	}

	public void setIncludeParent(boolean includeParent) {
		this.includeParent = includeParent;
	}

	public QueryOntology(Dictionary dictionary) {
		setFieldname(true);
		setValue(dictionary);
	}
	
	public QueryOntology() {
		setFieldname(true);
	}
	public double calculateMetric(Property object) {
		return 1;
	}

	public boolean isPrescreen() {
		return false;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		String value = getFieldname()?getValue().getTemplate():getValue().getParentTemplate();
		if (includeParent)
			params.add(new QueryParam<String>(String.class, value));
		params.add(new QueryParam<String>(String.class, value));
		params.add(new QueryParam<String>(String.class, value));		
		return params;
	}

	public String getSQL() throws AmbitException {
		String value = getFieldname()?getValue().getTemplate():getValue().getParentTemplate();
		String c = (value==null?"is":"=");
		return String.format(includeParent?sqlParent+sqlChild:sqlChild,c,c,c);
	}

	public Property getObject(ResultSet rs) throws AmbitException {
		try {
			switch (rs.getInt(1)) {
			case 0: {
				return new Dictionary(rs.getString(3),rs.getString(2));
			}
			case 1: {
				return new Dictionary(rs.getString(3),rs.getString(2));
			}
			case 2: {
				Property p = Property.getInstance(rs.getString(3), 
						LiteratureEntry.getInstance(rs.getString(5),rs.getString(6)));
				p.setId(rs.getInt(7));
				p.setUnits(rs.getString(4));
				p.setLabel(rs.getString(PROPERTY_TABLE.comments.toString()));
				p.getReference().setId(rs.getInt(PROPERTY_TABLE.idreference.toString()));
				
				try {
					p.setNominal(rs.getBoolean(PROPERTY_TABLE.islocal.toString()));
				} catch (Exception x) { p.setNominal(false);}
				return p;				

			}
			default: return null;
			}
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}

	@Override
	public String toString() {
		return getValue()==null?"Directory":
			String.format("%s",getFieldname()?getValue().getTemplate():getValue().getParentTemplate());
	}
	
}
