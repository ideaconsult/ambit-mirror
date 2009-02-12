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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import ambit2.core.data.IStructureRecord;
import ambit2.db.SourceDataset;

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
	protected static final String insert_descriptorvalue = "INSERT INTO property_values (id,idproperty,idstructure,idvalue,status,user_name,idtype) ";
	protected static final String insert_string = "INSERT IGNORE INTO property_string (value) VALUES (?)";	
	protected static final String insert_number = "INSERT IGNORE INTO property_number (value) VALUES (?)";
	protected static final String select_string = "select null,?,?,idvalue,?,SUBSTRING_INDEX(user(),'@',1),idtype from property_string where value=?";
	protected static final String select_number = "select null,?,?,idvalue,?,SUBSTRING_INDEX(user(),'@',1),idtype from property_number where abs(value-?)<1E-4";
	protected static final String onduplicate_number = " on duplicate key update property_values.idvalue=property_number.idvalue";
	protected static final String onduplicate_string = " on duplicate key update property_values.idvalue=property_string.idvalue";
	
	protected static final String insert_tuple_string = "insert into property_tuples select ?,id from values_string where idproperty=? and idstructure=? and value=? and idtype=?";
	protected static final String insert_tuple_number = "insert into property_tuples select ?,id from values_number where idproperty=? and idstructure=? and (abs(value-?)<1E-4) and idtype=?";
	protected static final String insert_tuple  = "insert into tuples select null,id_srcdataset from src_dataset where name=?";
    protected PreparedStatement ps_descriptorvalue_string;
    protected PreparedStatement ps_descriptorvalue_number;    
    protected PreparedStatement ps_insertstring;
    protected PreparedStatement ps_insertnumber;
    protected PreparedStatement ps_inserttuplestring = null;
    protected PreparedStatement ps_inserttuplenumber = null;    
    protected PreparedStatement ps_inserttuple = null;
    
    protected IStructureRecord structure;
    
    public synchronized IStructureRecord getStructure() {
        return structure;
    }
    public synchronized void setStructure(IStructureRecord structure) {
        this.structure = structure;
    }
    @Override
    protected int getTuple(SourceDataset dataset) {
    	int tuple = -1;
    	PreparedStatement ps = null;
    	try {
	    	ps = connection.prepareStatement(insert_tuple,Statement.RETURN_GENERATED_KEYS);
	    	ps.setString(1,dataset.getName());
	    	if (ps.executeUpdate() > 0) { //row inserted
	    		ResultSet rs = ps.getGeneratedKeys();
	    		while (rs.next()) tuple = rs.getInt(1);
	    		rs.close();
	    	}
    	} catch (Exception x) {
    		logger.error(x);
    		tuple = -1;
    	} finally {
    		try {ps.close();} catch (Exception x) {logger.error(x);}
    	}
    	return tuple;
    }
    protected boolean insertValue(String value, int idproperty, int idtuple, mode error) throws SQLException {
    	if (structure == null) throw new SQLException("Undefined structure");    	
    	if (ps_insertstring == null)
    		ps_insertstring = connection.prepareStatement(insert_string);
    	
    	ps_insertstring.clearParameters();
    	ps_insertstring.setString(1,value);
    	ps_insertstring.execute();    	
    	
    	if (ps_descriptorvalue_string == null)
            ps_descriptorvalue_string = connection.prepareStatement(insert_descriptorvalue+select_string+onduplicate_string);
    	
    	ps_descriptorvalue_string.clearParameters();
    	ps_descriptorvalue_string.setInt(1,idproperty);
    	ps_descriptorvalue_string.setInt(2,structure.getIdstructure());
    	ps_descriptorvalue_string.setString(3, error.toString());
    	if (value == null)
    		ps_descriptorvalue_string.setNull(4,Types.VARCHAR);
    	else
    		ps_descriptorvalue_string.setString(4, value);
    	if (ps_descriptorvalue_string.executeUpdate()>0) { 
    		if (idtuple >0 ) {
	        	if (ps_inserttuplestring == null) ps_inserttuplestring = connection.prepareStatement(insert_tuple_string);
	        	ps_inserttuplestring.clearParameters();
	        	ps_inserttuplestring.setInt(1,idtuple);
	        	ps_inserttuplestring.setInt(2,idproperty);
	        	ps_inserttuplestring.setInt(3,structure.getIdstructure());
	        	ps_inserttuplestring.setString(4,value);
	        	ps_inserttuplestring.setInt(5,0);
	        	if (ps_inserttuplestring.executeUpdate()<=0)
	        		logger.warn("Tuple not inserted "+idproperty+ " "+value);

    		} 
    	} else return false;
    	return true;
    }
    protected boolean insertValue(double value, int idproperty,int idtuple,mode error) throws SQLException {
    	if (structure == null) throw new SQLException("Undefined structure");
    	if (ps_insertnumber == null)
            ps_insertnumber = connection.prepareStatement(insert_number);    
    	
    	ps_insertnumber.clearParameters();
    	ps_insertnumber.setDouble(1,value);
    	ps_insertnumber.execute();

    	if (ps_descriptorvalue_number == null)
    		ps_descriptorvalue_number = connection.prepareStatement(insert_descriptorvalue+select_number+onduplicate_number);
    	
    	ps_descriptorvalue_number.clearParameters();
    	ps_descriptorvalue_number.setInt(1,idproperty);
    	ps_descriptorvalue_number.setInt(2,structure.getIdstructure());
        ps_descriptorvalue_number.setString(3, error.toString());
        ps_descriptorvalue_number.setDouble(4, value);
        if (ps_descriptorvalue_number.executeUpdate()>0) {
        	if (idtuple >0 ) {
    	        	if (ps_inserttuplenumber == null) 
    	        		ps_inserttuplenumber = connection.prepareStatement(insert_tuple_number);
    	        	ps_inserttuplenumber.clearParameters();
    	        	ps_inserttuplenumber.setInt(1,idtuple);
    	        	ps_inserttuplenumber.setInt(2,idproperty);
    	        	ps_inserttuplenumber.setInt(3,structure.getIdstructure());
    	        	ps_inserttuplenumber.setDouble(4, value);
    	        	ps_inserttuplenumber.setInt(5,1);
    	        	int ok = ps_inserttuplenumber.executeUpdate();
    	        	if (ok<=0) {
    	        		logger.warn("Tuple not inserted "+idproperty+ " "+value + " " +ps_inserttuplenumber);

    	        	}

        	} else {
        		logger.warn("Tuple < 0 "+idproperty+ " "+value + " " +ps_inserttuplenumber);
        	}
       	} else {
       		logger.warn("idtuple="+idtuple+" idproperty="+idproperty+" value "+value);
       		return false;
       	}
       	return true;

    }        
    
    protected void descriptorEntry(Target target, int idproperty, String propertyName, int propertyIndex, int idtuple) throws SQLException {
    	Object value = getValue(target,propertyName,propertyIndex);
    	if (value instanceof Number) {
    		if ((value instanceof Double) && ((Double)value).isNaN()) {
    			logger.warn(propertyName + value);
    			insertValue(value.toString(),idproperty,idtuple,mode.ERROR);
    		} else
    			insertValue(((Number)value).doubleValue(),idproperty,idtuple,mode.OK);
    			
    	} else
    		if (value != null)
    			insertValue(value.toString(),idproperty,idtuple,mode.OK);
    		else
    			insertValue(null,idproperty,idtuple,mode.ERROR);
	
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


