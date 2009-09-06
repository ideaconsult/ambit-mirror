/* DescriptorsCalculator.java
 * Author: nina
 * Date: Apr 18, 2009
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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.HydrogenAdderProcessor;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.AbstractDBProcessor;
import ambit2.db.SessionID;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.descriptors.processors.DescriptorValue2Property;
import ambit2.descriptors.processors.DescriptorsFactory;
import ambit2.descriptors.processors.PropertyCalculationProcessor;

public class DescriptorsCalculator extends AbstractDBProcessor<IStructureRecord,IStructureRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2912324506031402660L;
    protected DescriptorsFactory d = new DescriptorsFactory();
    protected Profile<Property> descriptors = null;
	protected DbDescriptorValuesWriter writer = new DbDescriptorValuesWriter();
	protected DescriptorValue2Property selfwriter = new DescriptorValue2Property();
	protected MoleculeReader reader = new MoleculeReader();
	protected HydrogenAdderProcessor ha = new HydrogenAdderProcessor();
    protected PropertyCalculationProcessor calc = new PropertyCalculationProcessor();
    protected boolean assignProperties = false;
    
    public boolean isAssignProperties() {
		return assignProperties;
	}
	public void setAssignProperties(boolean assignProperties) {
		this.assignProperties = assignProperties;
	}
	public Profile<Property> getDescriptors() {
		return descriptors;
	}
	public void setDescriptors(Profile<Property> descriptors) {
		this.descriptors = descriptors;
	}

    
    public IStructureRecord process(IStructureRecord target)
    		throws AmbitException {
    	IAtomContainer a = reader.process(target);
    	//necessary for some calculations
    	for (Property p : target.getProperties()) {
    		a.setProperty(p.getName(), target.getProperty(p));
    	}
    	if (a==null) throw new AmbitException("Empty molecule"); 
    	if (a.getAtomCount()==0) throw new AmbitException("Empty molecule");
    	
    	writer.setStructure(target);
    	selfwriter.setStructure(target);
    	try {
    		ha.process(a);
      	} catch (Exception x) {
			x.printStackTrace();
    	}    		
        	if (descriptors==null)	descriptors = d.process(null);
    		Iterator<Property> i = descriptors.getProperties(true);
    		while (i.hasNext()) {
    			try {
    				Property p = i.next();
    				if (p.isEnabled()) {
    					calc.setProperty(i.next());
    					DescriptorValue value = calc.process(a);
    					if (isAssignProperties() || target.getIdstructure()<=0)
    						selfwriter.process(value);
    					if (target.getIdstructure()>0)
    						writer.write(value);

    				}
    			} catch (Exception x) {
    				x.printStackTrace();
    	
    			}
    			
    		}    	    		
  

    	
    	return target;

    }
	public void open() throws DbAmbitException {
		writer.open();
	}
	@Override
	public void close() throws SQLException {
		super.close();
		writer.close();
	}
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		writer.setConnection(connection);
	}
	@Override
	public void setSession(SessionID sessionID) {

		super.setSession(sessionID);
		writer.setSession(sessionID);
	}
}

