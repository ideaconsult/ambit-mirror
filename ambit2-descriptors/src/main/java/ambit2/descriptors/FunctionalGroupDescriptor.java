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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.qsar.result.IntegerResult;

import ambit2.base.data.Property;
import ambit2.core.processors.structure.HydrogenAdderProcessor;


/**
 * Functional groups (by SMARTS)
 * @author nina
 *
 */
public class FunctionalGroupDescriptor implements IMolecularDescriptor {
	protected static Logger logger = Logger.getLogger(FunctionalGroupDescriptor.class.getName());
    public final String[] paramNames = {"funcgroups","verbose"};
	protected List<FunctionalGroup> groups;
	protected String[] names;
	protected HydrogenAdderProcessor hp = new HydrogenAdderProcessor();
	protected boolean verbose = false;
	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
		for (FunctionalGroup group:groups)	group.setVerboseMatch(verbose);
	}

	public FunctionalGroupDescriptor() {
		try {
			FuncGroupsDescriptorFactory factory = new FuncGroupsDescriptorFactory();
			setGroups(factory.process(null));
		} catch (Exception x) {
			setGroups(new ArrayList<FunctionalGroup>());
		}
	}
	protected void  setGroups(List<FunctionalGroup> groups) {
		this.groups = groups;
		names= new String[groups.size()];
		for (int i=0; i < groups.size();i++)
			names[i]=groups.get(i).getName();
		for (FunctionalGroup group:groups)	group.setVerboseMatch(isVerbose());		
	}
	public String[] getDescriptorNames() {
		String[] realNames = new String[groups.size()];
		for (int i=0; i < groups.size();i++) { 
			realNames[i] = groups.get(i).getName();
		}
		return realNames;
	}
	public DescriptorValue calculate(IAtomContainer atomcontainer) {
		List<String> realNames = new ArrayList<String>();
		IntegerArrayResult results = new IntegerArrayResult();
		try {
			IAtomContainer target = hp.process((IAtomContainer)atomcontainer.clone());
			
			List explanation = new ArrayList();
			
			for (int i=0; i < groups.size();i++) { 
					VerboseDescriptorResult<Object,IntegerResult> result = groups.get(i).process(target);
					//if (result.getResult().intValue()>0) {
						results.add(result.getResult().intValue());
						explanation.add(result.getExplanation());
						realNames.add(groups.get(i).getName());
					//}
			}

			String[] n = new String[realNames.size()];
			VerboseDescriptorResult<Object, IntegerArrayResult> result = new VerboseDescriptorResult<Object, IntegerArrayResult>(
					results,
					explanation
					);

	        return new DescriptorValue(getSpecification(), getParameterNames(), 
	                getParameters(), 
	                result,
	                realNames.toArray(n));
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
	        return new DescriptorValue(getSpecification(), getParameterNames(), 
	                getParameters(), 
	                null,
	                null,x);
		}
	}

    public String[] getParameterNames() {
        return paramNames;
    }

    public Object getParameterType(String name) {
    	for (int i=0; i < paramNames.length;i++)
    		if (paramNames[i].equals(name))
	    		switch (i) {
	    		case 0: return new ArrayList<FunctionalGroup>();
	    		case 1: return false;
	    		default: return null;
	    		}
    	return null;
    }

	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
        	String.format(Property.AMBIT_DESCRIPTORS_ONTOLOGY,"OECDCategories"),
		    this.getClass().getName(),
		    "$Id: FunctionalGroupDescriptor.java,v 0.2 2009/03/19 08:10:00 Nina Jeliazkova Exp $",
            "http://ambit.sourceforge.net/OECDCategories.xml");
    };
    /**
     * 3 parameters : Smarts,name,hint; first two are mandatory.
     */
	public void setParameters(Object[] params) throws CDKException {
        if (params.length < 1) 
            throw new CDKException("FunctionalGroupDescriptor expects at least one parameter");
        
        if (!(params[0] instanceof List)) 
            throw new CDKException("The first parameter must be of type List<FunctionalGroup> instead of "+ params[0].getClass().getName());
        
        if (params.length > 1)         
        if (!(params[1] instanceof Boolean)) 
            throw new CDKException("The second parameter must be of type Boolean instead of "+params[1].getClass().getName());

        try {
        	setGroups((List<FunctionalGroup>)params[0]);
        } catch (Exception x) {
        	setGroups(new ArrayList<FunctionalGroup>());
        	throw new CDKException(x.getMessage());
        }
        try {
            if ((params.length > 1))
            	setVerbose(Boolean.valueOf(params[1].toString()));
            else
            	setVerbose(false);
        		
        } catch (Exception x) {
        	setVerbose(false);
        	throw new CDKException(x.getMessage());
        }
	}
    public Object[] getParameters() {
        Object[] params = new Object[2];
        params[0] = groups;
        params[1] = new Boolean(isVerbose());
        return params;
    }	
    @Override
    public String toString() {
    	return "Functional groups: ("+groups.size()+")";
    }

	public IDescriptorResult getDescriptorResultType() {

		return new VerboseDescriptorResult(null,null);
	}
}


