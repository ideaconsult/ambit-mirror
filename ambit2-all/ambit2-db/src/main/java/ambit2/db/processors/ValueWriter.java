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

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.SourceDataset;
import ambit2.db.update.dataset.DatasetAddTuple;

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
	protected static final String insert_int = "INSERT IGNORE INTO property_int (value) VALUES (?)";
	
	protected static final String select_string = "select null,?,?,idvalue,?,SUBSTRING_INDEX(user(),'@',1),idtype from property_string where value=?";
	protected static final String select_number = "select null,?,?,idvalue,?,SUBSTRING_INDEX(user(),'@',1),idtype from property_number where abs(value-?)<1E-4";
	protected static final String select_int = "select null,?,?,idvalue,?,SUBSTRING_INDEX(user(),'@',1),idtype from property_int where value=?";
	
	protected static final String onduplicate_number = " on duplicate key update property_values.idvalue=property_number.idvalue";
	protected static final String onduplicate_string = " on duplicate key update property_values.idvalue=property_string.idvalue";
	protected static final String onduplicate_int = " on duplicate key update property_values.idvalue=property_int.idvalue";
	
	protected static final String insert_tuple_string = "insert into property_tuples select ?,id from property_values join property_string using(idvalue,idtype) where idproperty=? and idstructure=? and value=? and idtype=?";
	protected static final String insert_tuple_number = "insert into property_tuples select ?,id from property_values join property_number using(idvalue,idtype) where idproperty=? and idstructure=?  and (abs(value-(?))<1E-4) and idtype=?";
	protected static final String insert_tuple_int = 	"insert into property_tuples select ?,id from property_values join property_int using(idvalue,idtype) where idproperty=? and idstructure=? and value=? and idtype=?";
	
	//protected static final String insert_tuple  = "insert into tuples select null,id_srcdataset from src_dataset where name=?";
    protected PreparedStatement ps_descriptorvalue_string;
    protected PreparedStatement ps_descriptorvalue_number;    
    protected PreparedStatement ps_descriptorvalue_int;
    
    protected PreparedStatement ps_insertstring;
    protected PreparedStatement ps_insertnumber;
    protected PreparedStatement ps_insertint;
    
    protected PreparedStatement ps_inserttuplestring = null;
    protected PreparedStatement ps_inserttuplenumber = null;
    protected PreparedStatement ps_inserttupleint = null;
    
    protected PreparedStatement ps_inserttuple = null;
    
    protected IStructureRecord structure;
    protected DatasetAddTuple tuple = new DatasetAddTuple();
    
    public synchronized IStructureRecord getStructure() {
        return structure;
    }
    public synchronized void setStructure(IStructureRecord structure) {
        this.structure = structure;
    }
    @Override
    protected int getTuple(SourceDataset dataset) {
    	try {
    		if (dataset == null) return -1;
	    	tuple.setGroup(dataset);
	    	tuple.setObject(-1);
	    	exec.process(tuple);
	    	return tuple.getObject();
    	} catch (AmbitException x) {
    		x.printStackTrace();
    		return -1;
    	} finally {
    		
    	}
     }

    protected boolean insertValue(String value, Property property, int idtuple, mode error) throws SQLException {
    	if ((value != null) && (value.length()>255))
    		value = value.substring(0,255);
    	if (structure == null) throw new SQLException("Undefined structure");    	
    	if (ps_insertstring == null)
    		ps_insertstring = connection.prepareStatement(insert_string);
    	
    	ps_insertstring.clearParameters();
    	ps_insertstring.setString(1,value);
    	ps_insertstring.execute();    	
    	
    	if (ps_descriptorvalue_string == null)
            ps_descriptorvalue_string = connection.prepareStatement(insert_descriptorvalue+select_string+onduplicate_string);
    	
    	ps_descriptorvalue_string.clearParameters();
    	ps_descriptorvalue_string.setInt(1,property.getId());
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
	        	ps_inserttuplestring.setInt(2,property.getId());
	        	ps_inserttuplestring.setInt(3,structure.getIdstructure());
	        	ps_inserttuplestring.setString(4,value);
	        	ps_inserttuplestring.setInt(5,0);
	        	if (ps_inserttuplestring.executeUpdate()<=0)
	        		logger.warn("Tuple not inserted "+property.getId()+ " "+value);

    		} 
    	} else return false;
    	return true;
    }
    protected boolean insertValue(int value, Property property, int idtuple, mode error) throws SQLException {
    	if (structure == null) throw new SQLException("Undefined structure");    	
    	if (ps_insertint == null)
    		ps_insertint = connection.prepareStatement(insert_int);
    	
    	ps_insertint.clearParameters();
    	ps_insertint.setInt(1,value);
    	ps_insertint.execute();    	
    	
    	if (ps_descriptorvalue_int == null)
            ps_descriptorvalue_int = connection.prepareStatement(insert_descriptorvalue+select_int+onduplicate_int);
    	
    	ps_descriptorvalue_int.clearParameters();
    	ps_descriptorvalue_int.setInt(1,property.getId());
    	ps_descriptorvalue_int.setInt(2,structure.getIdstructure());
    	ps_descriptorvalue_int.setString(3, error.toString());
   		ps_descriptorvalue_int.setInt(4, value);
   		
    	if (ps_descriptorvalue_int.executeUpdate()>0) { 
    		if (idtuple >0 ) {
	        	if (ps_inserttupleint == null) ps_inserttupleint = connection.prepareStatement(insert_tuple_int);
	        	ps_inserttupleint.clearParameters();
	        	ps_inserttupleint.setInt(1,idtuple);
	        	ps_inserttupleint.setInt(2,property.getId());
	        	ps_inserttupleint.setInt(3,structure.getIdstructure());
	        	ps_inserttupleint.setInt(4,value);
	        	ps_inserttupleint.setInt(5,2);
	        	if (ps_inserttupleint.executeUpdate()<=0)
	        		logger.warn("Tuple not inserted "+property.getId()+ " "+value);

    		} 
    	} else return false;
    	return true;
    }
    
    protected boolean insertValue(double value, Property property,int idtuple,mode error) throws SQLException {
    	if (structure == null) throw new SQLException("Undefined structure");
    	if (ps_insertnumber == null)
            ps_insertnumber = connection.prepareStatement(insert_number);    
    	
    	ps_insertnumber.clearParameters();
    	ps_insertnumber.setDouble(1,value);
    	ps_insertnumber.execute();

    	if (ps_descriptorvalue_number == null)
    		ps_descriptorvalue_number = connection.prepareStatement(insert_descriptorvalue+select_number+onduplicate_number);
    	
    	ps_descriptorvalue_number.clearParameters();
    	ps_descriptorvalue_number.setInt(1,property.getId());
    	ps_descriptorvalue_number.setInt(2,structure.getIdstructure());
        ps_descriptorvalue_number.setString(3, error.toString());
        ps_descriptorvalue_number.setDouble(4, value);
        if (ps_descriptorvalue_number.executeUpdate()>0) {
        	if (idtuple >0 ) {
    	        	if (ps_inserttuplenumber == null) 
    	        		ps_inserttuplenumber = connection.prepareStatement(insert_tuple_number);
    	        	ps_inserttuplenumber.clearParameters();
    	        	ps_inserttuplenumber.setInt(1,idtuple);
    	        	ps_inserttuplenumber.setInt(2,property.getId());
    	        	ps_inserttuplenumber.setInt(3,structure.getIdstructure());
    	        	ps_inserttuplenumber.setDouble(4, value);
    	        	ps_inserttuplenumber.setInt(5,1);
    	        	int ok = ps_inserttuplenumber.executeUpdate();
    	        	if (ok<=0) {
    	        		logger.warn("Tuple not inserted "+property.getId()+ " "+value + " " +ps_inserttuplenumber);

    	        	}

        	} else {
        		logger.warn("Tuple < 0 "+property.getId()+ " "+value + " " +ps_inserttuplenumber);
        	}
       	} else {
       		logger.warn("idtuple="+idtuple+" idproperty="+property.getId()+" value "+value);
       		return false;
       	}
       	return true;

    }        
    
    protected void descriptorEntry(Target target, Property property, int propertyIndex, int idtuple) throws SQLException {
    	Object value = getValue(target,property,propertyIndex);
    	if (value instanceof Number) {
    		if (value instanceof Integer) {
    			logger.warn(property.getName() + value);
    			insertValue(((Integer)value).intValue(),property,idtuple,mode.UNKNOWN);
    		} else
    		if ((value instanceof Double) && ((Double)value).isNaN()) {
    			logger.warn(property.getName() + value);
    			insertValue(value.toString(),property,idtuple,mode.ERROR);
    		} else
    			insertValue(((Number)value).doubleValue(),property,idtuple,mode.UNKNOWN);
    			
    	} else
    		if (value != null)
    			insertValue(value.toString(),property,idtuple,mode.UNKNOWN);
    		else
    			insertValue(null,property,idtuple,mode.ERROR);
	
    };
    protected abstract Object getValue(Target target, Property propertyName, int index);
    
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


