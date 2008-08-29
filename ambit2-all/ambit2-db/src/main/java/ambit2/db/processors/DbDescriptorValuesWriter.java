/* DbDescriptorValuesWriter.java
 * Author: Nina Jeliazkova
 * Date: May 5, 2008 
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

package ambit2.db.processors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.BooleanResult;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.qsar.result.IntegerResult;

import ambit2.core.data.IStructureRecord;
import ambit2.core.data.IntArrayResult;



public class DbDescriptorValuesWriter extends DbDescriptorWriter {
    protected static final String insert_descriptorvalue = "INSERT IGNORE INTO dvalues (iddescriptor,idstructure,value,error,status,user_name) VALUES (?,?,?,?,?,SUBSTRING_INDEX(user(),'@',1))";
    protected PreparedStatement ps_descriptorvalue;
    protected IStructureRecord structure;
    public synchronized IStructureRecord getStructure() {
        return structure;
    }
    public synchronized void setStructure(IStructureRecord structure) {
        this.structure = structure;
    }
    @Override
    protected void prepareStatement(Connection connection) throws SQLException {
        super.prepareStatement(connection);
        ps_descriptorvalue = connection.prepareStatement(insert_descriptorvalue,Statement.RETURN_GENERATED_KEYS);
    }
    /**
     * INSERT IGNORE INTO dvalues (iddescriptor,idstructure,value,error,status,user_name) VALUES (?,?,?,?,?,SUBSTRING_INDEX(user(),'@',1))
     */
    @Override
    protected void descriptorEntry(DescriptorValue descriptor,int iddescriptor, int index) throws SQLException {
        ps_descriptorvalue.clearParameters();
        ps_descriptorvalue.setInt(1,iddescriptor);
        ps_descriptorvalue.setInt(2,structure.getIdstructure());
        IDescriptorResult result = descriptor.getValue();
        double value = Double.NaN;
        if (result instanceof DoubleResult) value = ((DoubleResult) result).doubleValue();
        else if (result instanceof IntegerResult) value = ((IntegerResult) result).intValue();
        else if (result instanceof BooleanResult) {
            if (((BooleanResult) result).booleanValue()) value = 1;
            else value = 0;
        }
        else if (result instanceof IntegerArrayResult) value = ((IntegerArrayResult) result).get(index);
        else if (result instanceof IntArrayResult) value = ((IntArrayResult) result).get(index);
        else if (result instanceof DoubleArrayResult) value = ((DoubleArrayResult) result).get(index);
        ps_descriptorvalue.setDouble(3, value);
        ps_descriptorvalue.setNull(4, Types.FLOAT);
        ps_descriptorvalue.setString(5, "OK");
        ps_descriptorvalue.execute();
    }

}
