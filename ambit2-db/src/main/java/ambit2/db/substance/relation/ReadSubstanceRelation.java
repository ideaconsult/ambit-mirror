/* ReadSubstanceRelation
 * Author: nina
 * Date: Aug 06, 2013
 * 
 * Copyright (C) 2005-2013  Ideaconsult Ltd.
 * 
 * Contact: www.ideaconsult.net
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
package ambit2.db.substance.relation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.I5Utils;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.structure.AbstractStructureQuery;

/**
 * Reads all structures, related to the query substance via substance_relation table
 * @author nina
 *
 */
public class ReadSubstanceRelation extends AbstractStructureQuery<STRUCTURE_RELATION,SubstanceRecord,NumberCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8329798753353233477L;
	private final static String sql_template =
		/*
		"select idsubstance,idchemical,-1,1,proportion_typical_value as metric,relation as text,proportion_real_lower,proportion_real_lower_value," +
		"proportion_real_upper,proportion_real_upper_value,proportion_real_unit,proportion_typical,proportion_typical_unit,s.rs_prefix,hex(s.rs_uuid)\n"+
		"from substance_relation join substance s using(idsubstance) ";
	*/
		"select -1 as idquery,idchemical,-1 as idstructure,1 as selected,1 as metric,inchi as text from chemicals where idchemical in\n"+
		"(select idchemical from substance_relation join substance using(idsubstance) %s)";	
		
	
	private final static String where_substance_id = "idsubstance=?";
	private final static String where_substance_uuid= "prefix=? and uuid=unhex(?)";
	private final static String where_owner_uuid= "owner_prefix=? and owner_uuid=unhex(?)";
	private final static String where_relation = "relation=?";
	public ReadSubstanceRelation(SubstanceRecord structure) {
		this(null,structure);
	}
	public ReadSubstanceRelation() {
		this((SubstanceRecord)null);
	}

	public ReadSubstanceRelation(STRUCTURE_RELATION relation, SubstanceRecord id) {
		super();
		setFieldname(relation);
		setValue(id);
		setCondition(NumberCondition.getInstance("="));
	}	
	public String getSQL() throws AmbitException {
		StringBuilder sql = new StringBuilder();
		String c = "\nwhere\n";
		if (getValue()!=null) {
			if (getValue().getIdsubstance()>0) {
				sql.append(c); sql.append(where_substance_id); c = " and ";
			} else if (getValue().getSubstanceUUID()!=null) {
				sql.append(c); sql.append(where_substance_uuid); c = " and ";
			} else if (getValue().getOwnerUUID()!=null) {
				sql.append(c); sql.append(where_owner_uuid); c = " and ";
			}
		} 
		if (getFieldname()!=null) {
			sql.append(c); sql.append(where_relation); 
		}
		return String.format(sql_template,sql);
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getValue()!=null) {
			if (getValue().getIdsubstance()>0)
				params.add(new QueryParam<Integer>(Integer.class,getValue().getIdsubstance()));
			else if (getValue().getSubstanceUUID()!=null) {
				String[] uuid = I5Utils.splitI5UUID(getValue().getSubstanceUUID());
				params.add(new QueryParam<String>(String.class, uuid[0]));
				params.add(new QueryParam<String>(String.class, uuid[1].replace("-", "").toLowerCase()));
			} else if (getValue().getOwnerUUID()!=null) {
				String[] uuid = I5Utils.splitI5UUID(getValue().getOwnerUUID());
				params.add(new QueryParam<String>(String.class, uuid[0]));
				params.add(new QueryParam<String>(String.class, uuid[1].replace("-", "").toLowerCase()));				
			}
		} 
		
		if (getFieldname()!=null)
			params.add(new QueryParam<String>(String.class,getFieldname().name()));
		
		return params;
	}
	@Override
	public String toString() {
		if (getValue()==null) return getFieldname()==null?"Related structures":getFieldname().name();
		else return String.format("%s of /substance/%d",getFieldname()==null?"":getFieldname().name(),getValue().getIdsubstance());
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}
	@Override
	public double calculateMetric(IStructureRecord object) {
		return 1;
	}
	@Override
	protected void retrieveMetric(IStructureRecord record, ResultSet rs) throws SQLException {
		record.setProperty(Property.getInstance("metric",toString(),"http://ambit.sourceforge.net"), retrieveValue(rs));
	}	
	
	@Override
	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
		try {
			IStructureRecord record = new StructureRecord();
			
			record.setIdchemical(rs.getInt(2));
			record.setIdstructure(rs.getInt(3));
			//retrieveStrucType(record, rs);
			record.setInchi(rs.getString(6));
			//metric
			retrieveMetric(record, rs);
			return record;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
}