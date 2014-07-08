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

package ambit2.db.readers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.smarts.CMLUtilities;

public class RetrieveStructure extends AbstractStructureRetrieval<IStructureRecord> {
    /**
     * 
     */
    private static final long serialVersionUID = 7257863166977468657L;
    protected boolean preferedStructure = false;
    public boolean isPreferedStructure() {
		return preferedStructure;
	}
	public void setPreferedStructure(boolean preferedStructure) {
		this.preferedStructure = preferedStructure;
	}
	/*
	protected String sql_prefered = 
    	"select structure.idstructure,idchemical,uncompress(structure) as ustructure,\n"+
    	"format,type_structure,atomproperties,preference from structure\n"+
    	"inner join (select min(preference) as p ,idchemical from structure where structure.idchemical=?) ids using(idchemical)\n"+
    	"where structure.preference=ids.p ";
	*/
	protected String sql_prefered = 
    	"select structure.idstructure,idchemical,uncompress(structure) as ustructure,\n"+
    	"format,type_structure,atomproperties,preference from structure\n"+
    	"where structure.%s =?\n"+
    	"order by idchemical,preference,idstructure ";	
	public RetrieveStructure() {
		this(false);//initial behaviour
	}
	public RetrieveStructure(boolean preferredStructure) {
		super();
		setPreferedStructure(preferredStructure);
		setFieldname(false);
	}
    /*
     * (non-Javadoc)
     * @see ambit2.db.search.AbstractQuery#setValue(java.lang.Object)
     */
    @Override
    public void setValue(IStructureRecord value) {
    	super.setValue(value);
    	setFieldname((value!=null) && (value.getIdstructure()<=0));
    	if ((value!=null) && (value.getType()!=null) && STRUC_TYPE.NA.equals(value.getType()) && preferedStructure) 
    			setFieldname(true);
    }
    @Override
	public String getSQL() throws AmbitException {
    	if (getFieldname())
    		return String.format(isPreferedStructure()?sql_prefered:sql,"idchemical");
    	else 
    		return String.format(sql,"idstructure");
	}
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getFieldname()?getValue().getIdchemical():getValue().getIdstructure()));
		return params;		
	}    
    @Override
    public Boolean getFieldname() {
    	return value.getIdstructure()<=0?true:fieldname;
    }
    public IStructureRecord getObject(ResultSet rs) throws AmbitException {
        try {
            IStructureRecord r = getValue();
            r.clear();
            r.setIdchemical(rs.getInt(_sqlids.idchemical.name()));
            r.setIdstructure(rs.getInt(_sqlids.idstructure.name()));
            r.setContent(rs.getString(_sqlids.ustructure.name()));
            r.setFormat(rs.getString(_sqlids.format.name()));
            
            try {
            	r.setSelected(rs.getBoolean("selected"));
            } catch (Exception x) {
            	r.setSelected(true);
            }            
            try {
            	String t = rs.getString(_sqlids.type_structure.name());
            	for (STRUC_TYPE type : STRUC_TYPE.values()) if (type.toString().equals(t)) {
            		r.setType(type);
            		break;
            	}
            } catch (Exception x) {
            	r.setType(STRUC_TYPE.NA);
            }
            try {
	            Object ts = rs.getString(_sqlids.atomproperties.name());
	            if ((ts==null) || "".equals(ts.toString().trim()))
	            	r.removeProperty(Property.getInstance(CMLUtilities.SMARTSProp, CMLUtilities.SMARTSProp));
	            else
	            r.setProperty(Property.getInstance(CMLUtilities.SMARTSProp, CMLUtilities.SMARTSProp), ts);
            } catch (Exception x) { r.removeProperty(Property.getInstance(CMLUtilities.SMARTSProp, CMLUtilities.SMARTSProp));}

            return r;
        } catch (SQLException x){
            throw new AmbitException(x);
        }
    }

    
}
