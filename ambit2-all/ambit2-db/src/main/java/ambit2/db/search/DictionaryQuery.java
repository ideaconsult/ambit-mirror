/* DictionaryQuery.java
 * Author: nina
 * Date: Feb 6, 2009
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

package ambit2.db.search;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Dictionary;

public abstract class DictionaryQuery<T extends Dictionary> extends AbstractQuery<String, String, StringCondition,T> 
								implements IQueryRetrieval<T>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7315142224794511557L;
	public static final String SQL = 
		"select tObject.name as category,tSubject.name as field,relationship  from dictionary d "+
		"join template as tSubject on d.idsubject=tSubject.idtemplate "+
		"join template as tObject on d.idobject=tObject.idtemplate "+
		"where `%s`.name %s ? order by tObject.idtemplate";
	
	public DictionaryQuery() {
		setCondition(StringCondition.getInstance(StringCondition.C_REGEXP));
	}
	public DictionaryQuery(String value) {
		this();
		setValue(value);
	}		
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class, getValue()));
		return params;
	}
	public String getSQL() throws AmbitException {
		if (getValue() == null)
			return String.format(SQL, getTemplateName(), "is");
		else
			return String.format(SQL, getTemplateName(), getCondition());

	}	
	protected abstract String getTemplateName();
	@Override
	public String getFieldname() {
		return getTemplateName();
	}
	public T getObject(ResultSet rs) throws AmbitException {
		try {
			return (T)new Dictionary(rs.getString(2),rs.getString(1),rs.getString(3));
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
	public double calculateMetric(Dictionary object) {
		return 1;
	}
	public boolean isPrescreen() {
		return false;
	}
	@Override
	public String toString() {
		return (value==null)?"":getValue();
	}
}
