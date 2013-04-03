/* DescriptorMopacShell.java
 * Author: Nina Jeliazkova
 * Date: 2008-12-13 
 * Revision: 1.0 
 * 
 * Copyright (C) 2005-2008  Ideaconsult Ltd.
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
package ambit2.mopac;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;

import ambit2.base.data.Property;
import ambit2.base.external.ShellException;


/**
 * Used by {@link DescriptorMopacShell} <br> 
 * Invokes mopac.exe from Mopac 7.1  http://openmopac.net/Downloads/Downloads.html.<br>
 * Doesn't work for molecules with number of heavy atoms > 60, number of all atoms > 120.<br>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-8
 */
public class DescriptorMopacShell implements IMolecularDescriptor {
	static Logger logger = Logger.getLogger(DescriptorMopacShell.class.getName());

    protected String[] params = new String[] {"mopac_commands","mopac_executable","force_field"};

	public static final String EHOMO = "EHOMO";
	public static final String ELUMO = "ELUMO";
    protected String force_field = AbstractMopacShell.defaultparams[2]; 
    protected AbstractMopacShell mopac_shell;
    /**
     * 
     */

    public DescriptorMopacShell() throws ShellException {
        super();
        mopac_shell = new MopacShell();
        if (logger!=null) logger.setLevel(Level.FINER);
    }
 
    public String toString() {
    	return "Calculates electronic descriptors by " + mopac_shell + " (http://openmopac.net)";
    }
    
    public String[] getParameterNames() {
        return params;
    }
    public Object[] getParameters() {
        Object params[] = new Object[4];
        params[0] = mopac_shell.getMopac_commands();
        try {
        	params[1] = mopac_shell.getExecutable();
        } catch (Exception x) {
        	params[1] = AbstractMopacShell.defaultparams[1];
        }
        params[2] = force_field;
        return (params);
    }
    public Object getParameterType(String arg0) {
        return "";
    }
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
        	String.format(Property.AMBIT_DESCRIPTORS_ONTOLOGY,"MOPAC"),
            this.getClass().getName(),
            "$Id: DescriptorMopacShell.java,v 0.2 2007/09/21 13:02:00 nina@acad.bg$",
            "http://openmopac.net/Downloads/MOPAC_7.1executable.zip");
    };
    public void setParameters(Object[] arg0) throws CDKException {
        if (arg0.length > 3) {
            throw new CDKException("DescriptorMopacShell at most 3 parameters");
        }
        if (arg0.length > 0)
        	mopac_shell.setMopac_commands(arg0[0].toString());
        if (arg0.length > 2)
        	force_field = arg0[2].toString();
        try {
	        if (arg0.length > 1)
	        	mopac_shell.addExecutable(arg0[1].toString(), null) ;
	        File file = new File(mopac_shell.getExecutable());
	        if (!file.exists()) 
	        	mopac_shell.addExecutable(AbstractMopacShell.defaultparams[1],null);
        } catch (Exception x) {
        	logger.log(Level.SEVERE,x.getMessage(),x);
        }
    }

    public DescriptorValue calculate(IAtomContainer mol) {
    	DoubleArrayResult r = null;
    	try {
    		if ((mol==null) || (mol.getAtomCount()==0)) throw new CDKException("Empty molecule!");
    		logger.finer(toString());
	        IAtomContainer newmol = mopac_shell.runShell(mol);
	        if ((newmol==null) || (newmol.getAtomCount()==0)) {
	        	throw new ShellException(mopac_shell,String.format(mopac_shell.getMsgemptymolecule(), mopac_shell.toString()));
	        }
	        r = new DoubleArrayResult(Mopac7Reader.parameters.length);
	        Object value = null;
	        for (int i=0; i< Mopac7Reader.parameters.length;i++) 
	        try {
	        	value = newmol.getProperty(Mopac7Reader.parameters[i]);
	        	if (value==null) {
	        		logger.warning(Mopac7Reader.parameters[i]+" = "+value);
	        		r.add(Double.NaN);
	        	} else {
	                logger.finer(Mopac7Reader.parameters[i]+" = "+value);
		            r.add(Double.parseDouble(value.toString()));
	        	}
	        } catch (Exception x) {
	        	logger.warning(Mopac7Reader.parameters[i]+" = "+value);
	            r.add(Double.NaN);
	        }
	       
	        return new DescriptorValue(getSpecification(),
	                getParameterNames(),getParameters(),r,Mopac7Reader.parameters);
    	} catch (Exception x) {
	        return new DescriptorValue(getSpecification(),
	                getParameterNames(),getParameters(),r,Mopac7Reader.parameters,x);    		
    	}
        
    }
    public String[] getDescriptorNames() {
    	return Mopac7Reader.parameters;
    }
    public IDescriptorResult getDescriptorResultType() {
    	return new DoubleArrayResult();
    }
    protected void debug(String message) {
    	logger.fine(message);
    }
	public boolean isErrorIfDisconnected() {
		return mopac_shell.isErrorIfDisconnected();
	}
	public void setErrorIfDisconnected(boolean errorIfDisconnected) {
		mopac_shell.setErrorIfDisconnected(errorIfDisconnected);
	}

}
