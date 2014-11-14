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
import java.sql.Types;
import java.util.logging.Level;

import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.update.value.UpdateStructurePropertyIDNumber;
import ambit2.db.update.value.UpdateStructurePropertyIDString;

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
	public static final int n_idproperty = 1;
	public static final int n_idchemical = 2;
	public static final int n_idstructure = 3;
	public static final int n_error = 4;
	public static final int n_value_num = 5;
	public static final int n_text = 5;
	public static final int n_value = 6;
	
	private static final long serialVersionUID = 8373222804070419878L;
	
	
	

	
	public static final String onduplicate_number = " on duplicate key update value_num=values(value_num), status=values(status), idvalue_string=null,text=null,idtype='NUMERIC',user_name=values(user_name) ";
	
	//protected static final String insert_tuple = "insert into property_tuples select ?,id from property_values where idproperty=? and idstructure=?";
	
	//protected static final String insert_tuple  = "insert into tuples select null,id_srcdataset from src_dataset where name=?";
    protected PreparedStatement ps_descriptorvalue_string;
    protected PreparedStatement ps_descriptorvalue_number;    
    
    protected PreparedStatement ps_insertstring;
    
    //protected PreparedStatement ps_inserttuplestring = null;
    //protected PreparedStatement ps_inserttuplenumber = null;
    
    protected IStructureRecord structure;
    //protected DatasetAddTuple tuple = new DatasetAddTuple();
    
    public synchronized IStructureRecord getStructure() {
        return structure;
    }
    public synchronized void setStructure(IStructureRecord structure) {
        this.structure = structure;
    }
    @Override
    protected int getTuple(SourceDataset dataset) {
    	/*
    	try {
    		if (dataset == null) return -1;
	    	tuple.setGroup(dataset);
	    	tuple.setObject(-1);
	    	exec.process(tuple);
	    	return tuple.getObject();
    	} catch (AmbitException x) {
    		logger.log(Level.FINE,x.getMessage(),x);
    		return -1;
    	} finally {
    		
    	}
    	*/
    	return -1;
     }

    protected boolean insertValue(String value, Property property, int idtuple, mode error) throws SQLException {
    	
    	String longText = null;
    	if ((value != null) && (value.length()>255)) {
    		if (logger.isLoggable(Level.FINER))
    			logger.finer("Value truncated to 255 symbols "+value);
    		longText = value;    		
    		value = value.substring(0,255);
    		error = mode.TRUNCATED;

    	}
    	if ((value !=null) && value.indexOf("<html>")>=0) {
    			value = "";
    			longText = "";
    		
    	}
    	if (structure == null) throw new SQLException("Undefined structure");    	
    	if (ps_insertstring == null)
    		ps_insertstring = connection.prepareStatement(UpdateStructurePropertyIDString.insert_string);
    	
    	ps_insertstring.clearParameters();
    	ps_insertstring.setString(1,value);
    	ps_insertstring.execute();    	
    	
    	if (ps_descriptorvalue_string == null)
            ps_descriptorvalue_string = connection.prepareStatement(UpdateStructurePropertyIDNumber.insert_descriptorvalue+
            		UpdateStructurePropertyIDString.select_string+
            		UpdateStructurePropertyIDString.onduplicate_string);
    	
    	ps_descriptorvalue_string.clearParameters();
    	ps_descriptorvalue_string.setInt(n_idproperty,property.getId());
    	ps_descriptorvalue_string.setInt(n_idchemical,structure.getIdchemical());
    	ps_descriptorvalue_string.setInt(n_idstructure,structure.getIdstructure());
    	ps_descriptorvalue_string.setString(n_error, error.toString());
    	if (longText == null) ps_descriptorvalue_string.setNull(n_text,Types.VARCHAR);
    	else ps_descriptorvalue_string.setString(n_text, longText);
    	if (value == null) ps_descriptorvalue_string.setNull(n_value,Types.VARCHAR);
    	else ps_descriptorvalue_string.setString(n_value, value);
    	
    	
    	try {
	    	if (ps_descriptorvalue_string.executeUpdate()>0) { 
	    	} else return false;
    	} catch (Exception x) {
    		logger.log(Level.WARNING,property.toString(),x);
    	}
    	return true;
    }
  
    
    protected boolean insertValue(double value, Property property,int idtuple,mode error) throws SQLException {
    	if (structure == null) throw new SQLException("Undefined structure");

    	if (ps_descriptorvalue_number == null)
    		ps_descriptorvalue_number = connection.prepareStatement(
    				UpdateStructurePropertyIDNumber.insert_descriptorvalue+UpdateStructurePropertyIDNumber.select_number+onduplicate_number);
    	
    	//"values (null,idproperty,idstructure,null,?,SUBSTRING_INDEX(user(),'@',1),null,?)";
    	    	
    	ps_descriptorvalue_number.clearParameters();
    	ps_descriptorvalue_number.setInt(n_idproperty,property.getId());
    	ps_descriptorvalue_number.setInt(n_idchemical,structure.getIdchemical());
    	ps_descriptorvalue_number.setInt(n_idstructure,structure.getIdstructure());
        ps_descriptorvalue_number.setString(n_error, error.toString());
        ps_descriptorvalue_number.setDouble(n_value_num, value);
        
        //ps_descriptorvalue_number.setDouble(5, value);
        //ps_descriptorvalue_number.setString(6, error.toString());     
        
        if (ps_descriptorvalue_number.executeUpdate()>0) {
 
       	} else {
       		logger.fine("idtuple="+idtuple+" idproperty="+property.getId()+" value "+value);
       		return false;
       	}
       	return true;

    }        
    
    protected boolean insertValueNaN(Property property,int idtuple,mode error) throws SQLException {
    	if (structure == null) throw new SQLException("Undefined structure");

    	if (ps_descriptorvalue_number == null)
    		ps_descriptorvalue_number = connection.prepareStatement(UpdateStructurePropertyIDNumber.insert_descriptorvalue+UpdateStructurePropertyIDNumber.select_number+onduplicate_number);
    	
    	//"values (null,idproperty,idstructure,null,?,SUBSTRING_INDEX(user(),'@',1),null,?)";
    	    	
    	ps_descriptorvalue_number.clearParameters();
    	ps_descriptorvalue_number.setInt(n_idproperty,property.getId());
    	ps_descriptorvalue_number.setInt(n_idchemical,structure.getIdchemical());
    	ps_descriptorvalue_number.setInt(n_idstructure,structure.getIdstructure());
        ps_descriptorvalue_number.setString(n_error, error.toString());
        ps_descriptorvalue_number.setNull(n_value_num, Types.DOUBLE);
        
        //ps_descriptorvalue_number.setNull(5, Types.DOUBLE);
        //ps_descriptorvalue_number.setString(6, error.toString());     
        
        if (ps_descriptorvalue_number.executeUpdate()>0) {
       	} else {
       		//logger.warn("idtuple="+idtuple+" idproperty="+property.getId()+" value "+value);
       		return false;
       	}
       	return true;

    }         
    
    protected void descriptorEntry(Target target, Property property, int propertyIndex, int idtuple) throws SQLException {
    	Object value = getValue(target,property,propertyIndex);
    	if (value instanceof Number) {
    		if (Double.isNaN( ((Number)value).doubleValue())) {
    			logger.finer(property.getName() + value);
    			insertValueNaN(property,idtuple,mode.ERROR);
    		} else
    			insertValue(((Number)value).doubleValue(),property,idtuple,mode.UNKNOWN);
    	} else
    		if (value != null)
    			if (value instanceof Exception) {
    				insertValue(((Exception)value).getMessage()==null?value.toString():((Exception)value).getMessage(),property,idtuple,mode.ERROR);
    			} else
    				insertValue(value.toString(),property,idtuple,mode.UNKNOWN);
    				
    		else
    			insertValue(null,property,idtuple,mode.ERROR);
	
    };
    protected abstract Object getValue(Target target, Property propertyName, int index);
    
    public void close() throws Exception {
        if (ps_descriptorvalue_number != null)ps_descriptorvalue_number.close();
        ps_descriptorvalue_number = null;
        if (ps_descriptorvalue_string != null)	ps_descriptorvalue_string.close();
        ps_descriptorvalue_string = null;       
        if (ps_insertstring != null)	ps_insertstring.close();
        ps_insertstring = null;  
       
        super.close();
    }
}


