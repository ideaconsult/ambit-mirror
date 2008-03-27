/*
Copyright (C) 2005-2007  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.data.descriptors;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.qsar.result.IntegerResultType;

import ambit2.config.AmbitCONSTANTS;
import ambit2.query.smarts.ISmartsPattern;
import ambit2.query.smarts.SMARTSException;
import ambit2.query.smarts.SmartsPatternFactory;


/**
 * Functional groups (by SMARTS)
 * @author nina
 *
 */
public class FunctionalGroupDescriptor implements IMolecularDescriptor {
	protected ISmartsPattern<IAtomContainer> query;
	public FunctionalGroupDescriptor() throws SMARTSException {
		query = SmartsPatternFactory.createSmartsPattern(
				SmartsPatternFactory.SmartsParser.smarts_cdk, "", false);
	}

	public DescriptorValue calculate(IAtomContainer arg0) throws CDKException {
		try {
	        return new DescriptorValue(getSpecification(), getParameterNames(), 
	                getParameters(), new IntegerResult(query.match(arg0)),
	                new String[] {AmbitCONSTANTS.SMARTSQuery});
		} catch (Exception x) {
			throw new CDKException(x.getMessage());
		}
	}

    public String[] getParameterNames() {
        String[] params = new String[3];
        params[0] = "smarts";
        params[1] = "name";
        params[2] = "comment";
        return params;
    }

    public Object getParameterType(String name) {
        return "";
    }

	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://ambit2.acad.bg/downloads/AmbitDb/html/funcgroups.xml",
		    this.getClass().getName(),
		    "$Id: FunctionalGroupDescriptor.java,v 0.1 2007/12/13 14:59:00 Nina Jeliazkova Exp $",
            "ambit2.acad.bg");
    };
    /**
     * 3 parameters : Smarts,name,hint; first two are mandatory.
     */
	public void setParameters(Object[] params) throws CDKException {
        if (params.length < 2) 
            throw new CDKException("FunctionalGroupDescriptor expects at least two parameter");
        
        if (!(params[0] instanceof String)) 
            throw new CDKException("The first parameter must be of type String");
        if (!(params[1] instanceof String)) 
            throw new CDKException("The second parameter must be of type String");

        try {
        	query.setSmarts(params[0].toString());
        	query.setName(params[1].toString());
            if ((params.length > 2))
            	query.setHint(params[2].toString());
            
        		
        } catch (Exception x) {
        	throw new CDKException(x.getMessage());
        }
	}
    public Object[] getParameters() {
        Object[] params = new Object[3];
        params[0] = new String(query.getSmarts());
        params[1] = new String(query.getName());
        params[2] = new String(query.getHint());
        return params;
    }	
    @Override
    public String toString() {
    	if (query == null) return getClass().getName();
    	else return "Functional group: "+query.toString();
    }
    public int hashCode() {
    	return query.hashCode();
    }

	public IDescriptorResult getDescriptorResultType() {

		return new IntegerResultType();
	}
}


