/* ValueWriter.java
 * Author: nina
 * Date: Jan 9, 2009
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

package ambit2.db.processors;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import ambit2.core.data.IStructureRecord;

/**
 * Writes values into property tables
 * @author nina
 *
 * @param <Target>
 * @param <Result>
 */
public abstract class ValueWriter<Target, Result> extends AbstractPropertyWriter<Target, Result> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8373222804070419878L;
	protected static final String insert_descriptorvalue = "INSERT IGNORE INTO property_values (id,idproperty,idstructure,idvalue,status,user_name,idtype) ";
	protected static final String insert_string = "INSERT IGNORE INTO property_string (value) VALUES (?)";	
	protected static final String insert_number = "INSERT IGNORE INTO property_number (value) VALUES (?)";
	protected static final String select_string = "select null,?,?,idvalue,?,SUBSTRING_INDEX(user(),'@',1),idtype from property_string where value=?";
	protected static final String select_number = "select null,?,?,idvalue,?,SUBSTRING_INDEX(user(),'@',1),idtype from property_number where abs(value-?)<1E-4";	
    protected PreparedStatement ps_descriptorvalue_string;
    protected PreparedStatement ps_descriptorvalue_number;    
    protected PreparedStatement ps_insertstring;
    protected PreparedStatement ps_insertnumber;    
    protected IStructureRecord structure;
    
    public synchronized IStructureRecord getStructure() {
        return structure;
    }
    public synchronized void setStructure(IStructureRecord structure) {
        this.structure = structure;
    }
    protected boolean insertValue(String value, int idproperty) throws SQLException {
    	if (structure == null) throw new SQLException("Undefined structure");    	
    	if (ps_insertstring == null)
    		ps_insertstring = connection.prepareStatement(insert_string);
    	
    	ps_insertstring.clearParameters();
    	ps_insertstring.setString(1,value);
    	ps_insertstring.execute();    	
    	
    	if (ps_descriptorvalue_string == null)
            ps_descriptorvalue_string = connection.prepareStatement(insert_descriptorvalue+select_string);
    	
    	ps_descriptorvalue_string.clearParameters();
    	ps_descriptorvalue_string.setInt(1,idproperty);
    	ps_descriptorvalue_string.setInt(2,structure.getIdstructure());
    	ps_descriptorvalue_string.setString(3, "OK");
    	ps_descriptorvalue_string.setString(4, value);
    	return ps_descriptorvalue_string.execute();        	
    }
    protected boolean insertValue(double value, int idproperty) throws SQLException {
    	if (structure == null) throw new SQLException("Undefined structure");
    	if (ps_insertnumber == null)
            ps_insertnumber = connection.prepareStatement(insert_number);    
    	
    	ps_insertnumber.clearParameters();
    	ps_insertnumber.setDouble(1,value);
    	ps_insertnumber.execute();

    	if (ps_descriptorvalue_number == null)
    		ps_descriptorvalue_number = connection.prepareStatement(insert_descriptorvalue+select_number);
    	
    	ps_descriptorvalue_number.clearParameters();
    	ps_descriptorvalue_number.setInt(1,idproperty);
    	ps_descriptorvalue_number.setInt(2,structure.getIdstructure());
        ps_descriptorvalue_number.setString(3, "OK");
        ps_descriptorvalue_number.setDouble(4, value);
        return ps_descriptorvalue_number.execute();        	
    }        
    
    protected void descriptorEntry(Target target, int idproperty, String propertyName, int propertyIndex) throws SQLException {
    	boolean ok;
    	Object value = getValue(target,propertyName,propertyIndex);
    	if (value instanceof Number)
    		ok = insertValue(((Number)value).doubleValue(),idproperty);
    	else
    		ok = insertValue(value.toString(),idproperty);
	
    };
    protected abstract Object getValue(Target target, String propertyName, int index);
    
    public void close() throws SQLException {
    	super.close();
        if (ps_descriptorvalue_number != null)
        	ps_descriptorvalue_number.close();
        ps_descriptorvalue_number = null;
        
        if (ps_descriptorvalue_string != null)
        	ps_descriptorvalue_string.close();
        ps_descriptorvalue_string = null;       
        
        if (ps_insertstring != null)
        	ps_insertstring.close();
        ps_insertstring = null;  
        
        if (ps_insertnumber != null)
        	ps_insertnumber.close();
        ps_insertnumber = null;          
        super.close();
    }        
}
