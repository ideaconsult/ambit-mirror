/* PropertyCount.java
 * Author: nina
 * Date: May 1, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.db.search.property;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyTemplateStats;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.StringCondition;

public class PropertyCount extends	AbstractQuery<String, String, StringCondition, PropertyTemplateStats> 
									implements IQueryRetrieval<PropertyTemplateStats> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5610950555709854734L;

	public enum PropertyCriteria  {template,property};
	public static final String SQL = 
		"select properties.name,properties.units,properties.comments,title,url,template.name,count(distinct(idstructure)) from\n"+
		"property_values\n"+
		"join properties using(idproperty)\n"+
		"join catalog_references using(idreference)\n"+ 
		"join template_def using(idproperty)\n"+
		"join template using(idtemplate)\n"+
		"%s"+
		"group by idproperty";
	public static final String where = "where `%s`.name %s ?\n"; 
	public PropertyCount() {
		setCondition(StringCondition.getInstance("="));
	}

	public List<QueryParam> getParameters() throws AmbitException {
		if ((getFieldname()==null) || (getValue()==null)) return null;
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class, getValue()));
		return params;		
		
	}

	public String getSQL() throws AmbitException {
		if ((getFieldname()==null) || (getValue()==null)) 
			return String.format(SQL,"");
		else {
			return String.format(SQL,String.format(where,getFieldname(),getCondition().getSQL()));
		}
	}
	public PropertyTemplateStats getObject(ResultSet rs) throws AmbitException {
		try {
			Property p =Property.getInstance(rs.getString(1),rs.getString(4),rs.getString(5));
			p.setUnits(rs.getString(2));
			p.setLabel(rs.getString(3));
			
			return new PropertyTemplateStats(p,rs.getString(6),rs.getInt(7));
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	public double calculateMetric(PropertyTemplateStats object) {
		return 1;
	}
	public boolean isPrescreen() {
		return false;
	}
}
