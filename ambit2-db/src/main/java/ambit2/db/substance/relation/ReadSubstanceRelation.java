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

import ambit2.base.data.Property;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.QueryParam;
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
	public final static String sql = 
		"select idsubstance,idchemical,-1,1,proportion_typical_value as metric,relation as text,proportion_real_lower,proportion_real_lower_value," +
		"proportion_real_upper,proportion_real_upper_value,proportion_real_unit,proportion_typical,proportion_typical_unit,rs_prefix,hex(rs_uuid)\n"+
		"from substance_relation where idsubstance=? and relation=?";
	
	public ReadSubstanceRelation(SubstanceRecord structure) {
		this(STRUCTURE_RELATION.HAS_CONSTITUENT,structure);
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
		return 	sql;
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getValue()!=null && getValue().getIdsubstance()>0)
			params.add(new QueryParam<Integer>(Integer.class,getValue().getIdsubstance()));
		else throw new AmbitException("Empty ID");
		if (getFieldname()!=null)
			params.add(new QueryParam<String>(String.class,getFieldname().name()));
		else throw new AmbitException("Relation not specified");		
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
}