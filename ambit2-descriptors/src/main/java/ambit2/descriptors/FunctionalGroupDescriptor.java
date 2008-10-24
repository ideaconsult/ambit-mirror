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

package ambit2.descriptors;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.qsar.result.IntegerResultType;

import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.query.smarts.ISmartsPattern;
import ambit2.core.query.smarts.SMARTSException;
import ambit2.core.query.smarts.SmartsPatternFactory;


/**
 * Functional groups (by SMARTS)
 * @author nina
 *
 */
public class FunctionalGroupDescriptor implements IMolecularDescriptor {
	protected ISmartsPattern<IAtomContainer> query;
	protected boolean verbose = false;
	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public FunctionalGroupDescriptor() throws SMARTSException {
		query = SmartsPatternFactory.createSmartsPattern(
				SmartsPatternFactory.SmartsParser.smarts_nk, "", false);
	}

	public DescriptorValue calculate(IAtomContainer arg0) throws CDKException {
		try {
			
			int hits = 0;
			IAtomContainer match = null;
			IDescriptorResult result;
			if (isVerbose()) 
				try {
					match = query.getMatchingStructure(arg0);
					hits = (match.getAtomCount()>0) ?1:0;
					result = new VerboseDescriptorResult<IAtomContainer,IntegerResult>(new IntegerResult(hits),match);
				} catch (SMARTSException x) {
					match = null;
					hits = 0;
					result = new VerboseDescriptorResult<String,IntegerResult>(new IntegerResult(hits),x.getMessage());
				}
			else {
				hits = query.match(arg0);
				result = new VerboseDescriptorResult<String,IntegerResult>(new IntegerResult(hits),null);
			}	
			
	        return new DescriptorValue(getSpecification(), getParameterNames(), 
	                getParameters(), 
	                result,
	                new String[] {AmbitCONSTANTS.SMARTSQuery});
		} catch (Exception x) {
			throw new CDKException(x.getMessage());
		}
	}

    public String[] getParameterNames() {
        String[] params = new String[4];
        params[0] = "smarts";
        params[1] = "name";
        params[2] = "comment";
        params[3] = "verbose";
        return params;
    }

    public Object getParameterType(String name) {
        return "";
    }

	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://ambit.acad.bg/downloads/AmbitDb/html/funcgroups.xml",
		    this.getClass().getName(),
		    "$Id: FunctionalGroupDescriptor.java,v 0.1 2007/12/13 14:59:00 Nina Jeliazkova Exp $",
            "ambit.acad.bg");
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
            if ((params.length > 3))
            	setVerbose(Boolean.valueOf(params[3].toString()));
            else
            	setVerbose(false);
        		
        } catch (Exception x) {
        	setVerbose(false);
        	throw new CDKException(x.getMessage());
        }
	}
    public Object[] getParameters() {
        Object[] params = new Object[4];
        params[0] = new String(query.getSmarts());
        params[1] = new String(query.getName());
        if (query.getHint() == null)
        	params[2] = null;
        else
        	params[2] = new String(query.getHint());
        params[3] = new Boolean(isVerbose());
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


