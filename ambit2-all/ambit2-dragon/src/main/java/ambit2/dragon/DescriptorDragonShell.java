/* DescriptorDragonShell.java
 * Author: Nina Jeliazkova
 * Date: 2011-06-15 
 * Revision: 1.0 
 * 
 * Copyright (C) 2005-2011  Ideaconsult Ltd.
 * 
 * Contact: jeliazkova.nina@gmail.com
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
package ambit2.dragon;

import java.util.Iterator;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;

import ambit2.base.data.Property;
import ambit2.base.external.ShellException;
import ambit2.base.log.AmbitLogger;
import ambit2.core.data.StringDescriptorResultType;


/**
 * Used by {@link DescriptorDragonShell} <br> 
 * Invokes dragon6shell (http://talete.it)
 * DRAGON_HOME environment variable should point to Talete Dragon6 directory. <br>
 * @author Nina Jeliazkova jeliazkova.nina@gmail.com
 * <b>Created</b> 2011-07-01
 */
public class DescriptorDragonShell implements IMolecularDescriptor  {
    protected static AmbitLogger logger = new  AmbitLogger(DescriptorDragonShell.class);
    protected DragonShell shell;
   

    /**
     * 
     */

    public DescriptorDragonShell() throws ShellException {
        super();
        shell = new DragonShell();
        
    }

    public String toString() {
    	return "Dragon6 descriptors";
    }
    
    public String[] getParameterNames() {
        return null;
    }
    /**
     * TODO blocks, subblocks, descriptors
     */
    public Object[] getParameters() {
        return null;
    }
    public Object getParameterType(String arg0) {
        return "";
    }
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
        	String.format(Property.AMBIT_DESCRIPTORS_ONTOLOGY,"Dragon"),
            this.getClass().getName(),
            "$Id: DescriptorDragonShell.java,v 0.1 2011/06/13 19:15:00 jeliazkova.nina@gmail.com$",
            "http://talete.it");
    };
    public void setParameters(Object[] arg0) throws CDKException {
    }
    
    @Override
    public DescriptorValue calculate(IAtomContainer arg0) {
    	DoubleArrayResult r = null;
    	String[] descriptorNames = null;
    	try {
    		if ((arg0==null) || (arg0.getAtomCount()==0)) throw new CDKException("Empty molecule!");
    		logger.info(toString());
	        IAtomContainer newmol = shell.runShell(arg0);
	        
	        r = new DoubleArrayResult(newmol.getProperties().size());
	        Iterator keys = newmol.getProperties().keySet().iterator();
	        descriptorNames = new String[newmol.getProperties().size()];
	        int i=0;
	        while (keys.hasNext()) {
	        	Object key = keys.next();
	        	Object value = newmol.getProperty(key);
	            r.add(value instanceof Double?(Double) value:Double.NaN);
	            descriptorNames[i] = key.toString();
	            i++;
	        }
	        return new DescriptorValue(getSpecification(),
	                getParameterNames(),getParameters(),r,descriptorNames);
    	} catch (Exception x) {
    		/*
    		Throwable cause = x;
    		while (cause != null) {
    			if (cause.getCause()==null) throw new CDKException(cause.getMessage());
    			cause = cause.getCause();
    		}
    		*/  
	        return new DescriptorValue(getSpecification(),
	                getParameterNames(),getParameters(),r,descriptorNames,x);    		
    	}
        
    }
    public String[] getDescriptorNames() {
    	/*
    	return new String[] {
    			SOMEShell.SOME_RESULT,
        		someindex.aliphaticHydroxylation.name(),
        		someindex.aromaticHydroxylation.name(),
        		someindex.NDealkylation.name(),
        		someindex.NOxidation.name(),
        		someindex.ODealkylation.name(),
        		someindex.SOxidation.name()
    	};
    	*/
    	return null;
    }
    public IDescriptorResult getDescriptorResultType() {
    	return new StringDescriptorResultType();
    }
    protected void debug(String message) {
    	logger.info(message);
    }
   
}
