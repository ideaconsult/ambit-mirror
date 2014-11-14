/* RetrieveStructure.java
 * Author: Nina Jeliazkova
 * Date: Aug 10, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
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

package ambit2.db.search.structure.pairwise;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.EQCondition;
import ambit2.smarts.CMLUtilities;

public class RetrieveStructurePair extends AbstractQuery<IStructureRecord,IStructureRecord,EQCondition,IStructureRecord[]> 
																		implements IQueryRetrieval<IStructureRecord[]> {
    protected enum rs_index {
    	idstructure,
    	idchemical,
    	ustructure,
    	format,
    	type_structure,
    	atomproperties,
    	preference
    }
	/**
	 * 
	 */
	private static final long serialVersionUID = 3952654869210259121L;
	protected String sql1 = 
    	"select idstructure,idchemical,uncompress(structure) as ustructure,\n"+
    	"format,type_structure,atomproperties,preference from structure\n"+
    	"where idstructure=?";	
	
	protected String sql2 = 
		"select s1.idstructure,s1.idchemical,uncompress(s1.structure) as ustructure1,\n"+
		"s1.format,s1.type_structure,s1.atomproperties,s1.preference,\n"+
		"s2.idstructure,s2.idchemical,uncompress(s2.structure) as ustructure2,\n"+
		"s2.format,s2.type_structure,s2.atomproperties,s2.preference\n"+
		"from structure s1, structure s2\n"+
		"where s1.idstructure=? and s2.idstructure=?";
	   
	
	protected IStructureRecord[] records = new IStructureRecord[] {null,null};
	
	@Override
	public double calculateMetric(IStructureRecord[] object) {
		return 1;
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (retrieve(getFieldname())) {
			
			 if (retrieve(getValue())) { //both idstructures
				 params.add(new QueryParam<Integer>(Integer.class, getFieldname().getIdstructure()));
				 params.add(new QueryParam<Integer>(Integer.class, getValue().getIdstructure()));
			 } else {  //first idstructure
				 params.add(new QueryParam<Integer>(Integer.class, getFieldname().getIdstructure()));
			 }
		} else if (retrieve(getValue())) {
			//second idstructure
			params.add(new QueryParam<Integer>(Integer.class, getValue().getIdstructure()));
		} else throw new AmbitException("No structures");
		return params;
	}

	@Override
	public String getSQL() throws AmbitException {
		if (retrieve(getFieldname())) {
			
			 if (retrieve(getValue())) { //both idstructures
				 return sql2;
			 } else {  //first idstructure
				 return sql1;
			 }
		} else if (retrieve(getValue())) {
			//second idstructure
			return sql1;
		} else throw new AmbitException(String.format("No structures %s %s",getFieldname(),getValue()));
	}

	@Override
	public IStructureRecord[] getObject(ResultSet rs) throws AmbitException {
		try {
			records[0] = getFieldname();
			records[1] = getValue();
			if (retrieve(getFieldname())) {
				
				 if (retrieve(getValue())) { //both idstructures
					 getObject(rs,records[0],1);
					 getObject(rs,records[1],rs_index.values().length+1);
				 } else {  //first idstructure
					 getObject(rs,records[0],1);
				 }
			} else if (retrieve(getValue())) {
				getObject(rs,records[1],1);
			} else throw new AmbitException("No structures");
			return records;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}
 
	protected boolean retrieve(IStructureRecord record) {
		return (record != null) &&
		       (record.getIdstructure()>0) &&
		       ((record.getContent()==null) || "".equals(record.getContent()));
		
	}
	protected void getObject(ResultSet rs, IStructureRecord r, int offset) throws SQLException {
				r.clear();
	            r.setIdchemical(rs.getInt(rs_index.idchemical.ordinal()+offset));
	            r.setIdstructure(rs.getInt(rs_index.idstructure.ordinal()+offset));
	            r.setContent(rs.getString(rs_index.ustructure.ordinal()+offset));
	            r.setFormat(rs.getString(rs_index.format.ordinal()+offset));
         
	            try {
	            	String t = rs.getString(rs_index.type_structure.ordinal()+offset);
	            	for (STRUC_TYPE type : STRUC_TYPE.values()) if (type.toString().equals(t)) {
	            		r.setType(type);
	            		break;
	            	}
	            } catch (Exception x) {
	            	r.setType(STRUC_TYPE.NA);
	            }
	            if ((rs.getString(rs_index.atomproperties.ordinal()+offset)==null) || "".equals(rs.getString(rs_index.atomproperties.ordinal()+offset).trim()))
	            	r.removeProperty(Property.getInstance(CMLUtilities.SMARTSProp, CMLUtilities.SMARTSProp));
	            else
	            r.setProperty(Property.getInstance(CMLUtilities.SMARTSProp, CMLUtilities.SMARTSProp), rs.getString(rs_index.atomproperties.ordinal()+offset));

	}

}
