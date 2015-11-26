/*
Copyright Ideaconsult Ltd. (C) 2005-2013 

Contact: www.ideaconsult.net

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

*/

package ambit2.db.processors;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.util.Iterator;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IPDBAtom;
import org.openscience.cdk.qsar.DescriptorValue;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.IStructureDiagramHighlights;
import ambit2.core.helper.CDKHueckelAromaticityDetector;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.processors.structure.HydrogenAdderProcessor;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.descriptors.processors.DescriptorValue2Property;
import ambit2.descriptors.processors.DescriptorsFactory;
import ambit2.descriptors.processors.PropertyCalculationProcessor;

public class DescriptorsCalculator extends AbstractDescriptorCalculator<IAtomContainer> implements IStructureDiagramHighlights {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2912324506031402660L;
    protected DescriptorsFactory d = new DescriptorsFactory();

	protected DbDescriptorValuesWriter writer = new DbDescriptorValuesWriter();
	protected DescriptorValue2Property selfwriter = new DescriptorValue2Property();
	protected MoleculeReader reader = new MoleculeReader();
	protected HydrogenAdderProcessor ha = new HydrogenAdderProcessor();
	protected AtomConfigurator cfg = new AtomConfigurator();
    protected PropertyCalculationProcessor calc = new PropertyCalculationProcessor();
    

	public Dimension getImageSize() {
		return calc==null?null:calc.getImageSize();
	}
	public void setImageSize(Dimension imageSize) {
		calc.setImageSize(imageSize);
	}

	@Override
	public IAtomContainer preprocess(IStructureRecord target) throws AmbitException {
    	IAtomContainer a = reader.process(target);
    	boolean preprocess = ((a != null) && (a.getAtomCount()>0)) ;
    	for (IAtom atom : a.atoms()) 
    	    if (atom instanceof IPDBAtom) {
    		preprocess = false;
    		break;
    	    }
    	
    	//necessary for some calculations
    	for (Property p : target.getRecordProperties()) try {
    		a.setProperty(p.getName(), target.getRecordProperty(p));
    	} catch (Exception x) {}

    	
    	writer.setStructure(target);
    	selfwriter.setStructure(target);
    	
    	if (preprocess)  {
        	try {
        		cfg.process(a);
          	} catch (Exception x) {
          		//logger.warn(x);
        	}    	
        	try {
        		ha.process(a);
          	} catch (Exception x) {
          		//logger.warn(x);
        	}           	
          	try {
          		CDKHueckelAromaticityDetector.detectAromaticity(a);
          	} catch (Exception x) {
          		//logger.warn(x);
          	}    		
    	} else {
    		
    	}
    	return a;
	}
	
    public IStructureRecord process(IStructureRecord target)
    		throws AmbitException {
    	IAtomContainer a = preprocess(target);

        if (descriptors==null)	descriptors = d.process(null);
    	Iterator<Property> i = descriptors.getProperties(true);
    	while (i.hasNext()) {
    		try {
    			Property p = i.next();
    			if (p.isEnabled()) {
    				DescriptorValue value = null;
    				calc.setProperty(i.next());
    				value = calc.process(a);
    				if (isAssignProperties() || target.getIdstructure()<=0)
    					selfwriter.process(value);
    				if (target.getIdstructure()>0)
    					writer.write(value);
    				}
   			} catch (Exception x) {
   				logger.log(Level.WARNING,x.getMessage(),x);
    		}
    	}    	    		
      	
    	return target;

    }
	public void open() throws DbAmbitException {
		writer.open();
	}
	@Override
	public void close() throws Exception {
		super.close();
		writer.close();
	}
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		writer.setConnection(connection);
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("\n-- Descriptor calculator --\n");
		try {
	       if (descriptors==null)	descriptors = d.process(null);
	    	Iterator<Property> i = descriptors.getProperties(true);
	    	while (i.hasNext()) {
	    		try {
	    			Property p = i.next();
	    			if (p.isEnabled()) 
	    				b.append(p.getClazz().getName());
	   			} catch (Exception x) {
	   				logger.log(Level.WARNING,x.getMessage(),x);
	    		}
	    	} 
		} catch (Exception x) {b.append(x.getMessage());};
		return b.toString();
	      	
	}
	public BufferedImage getImage(IAtomContainer mol) throws AmbitException  {
		return getImage(mol, null,150,150,false);
	}
	public BufferedImage getImage(IAtomContainer mol,
			String ruleID, int width, int height, boolean atomnumbers)
			throws AmbitException {

        if (descriptors==null)	descriptors = d.process(null);
    	Iterator<Property> i = descriptors.getProperties(true);
    	while (i.hasNext()) {
    		try {
    			Property p = i.next();
    			if (p.isEnabled()) {
    				calc.setProperty(i.next());
    				return calc.getImage(mol, ruleID, width, height, atomnumbers);
    			}	
    		} catch (AmbitException x) {
    			throw x;
   			} catch (Exception x) {
   				throw new AmbitException(x);
    		}
    	}    	    		
      	return null;
	}
	@Override
	public BufferedImage getLegend(int width, int height) throws AmbitException {
        if (descriptors==null)	descriptors = d.process(null);
    	Iterator<Property> i = descriptors.getProperties(true);
    	while (i.hasNext()) {
    		try {
    			Property p = i.next();
    			if (p.isEnabled()) {
    				calc.setProperty(i.next());
    				return calc.getLegend(width, height);
    			}	
    		} catch (AmbitException x) {
    			throw x;
   			} catch (Exception x) {
   				throw new AmbitException(x);
    		}
    	}    	    		
      	return null;
	}
}

