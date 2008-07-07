/*
Copyright (C) 2005-2008  

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

package ambit.processors.structure;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit.data.descriptors.FunctionalGroupDescriptor;
import ambit.data.molecule.FunctionalGroup;
import ambit.data.molecule.SmartsFragmentsList;
import ambit.data.molecule.SmartsResult;
import ambit.exceptions.AmbitException;
import ambit.processors.DefaultAmbitProcessor;

/**
 * Sets IAtomContainer.setProperty(FunctionalGroupDescriptor.class.getName(),List<SmartsResult> );
 * @author nina
 *
 */
public class SMARTSSearchProcessor extends DefaultAmbitProcessor {
		protected List<FunctionalGroupDescriptor> fragments;
		protected SmartsFragmentsList list;
		public SMARTSSearchProcessor(SmartsFragmentsList list) throws AmbitException {
			super();
			fragments = new ArrayList<FunctionalGroupDescriptor>();

            for (int i=0; i < list.size(); i++)
                if  (list.getItem(i).isSelected()) {
                	FunctionalGroup group = (FunctionalGroup) list.getItem(i); 
                	FunctionalGroupDescriptor d = new FunctionalGroupDescriptor();
        			try {
        				d.setParameters(new String[] { 
        						group.getSmarts(),
        						group.getName(),
        						group.getHint()});			
        				fragments.add(d);
        			} catch (CDKException x) {
        				logger.error(x);
        			}
                }
            if (fragments.size()==0)
            	throw new AmbitException("No fragment defined!");
		
			
		}
		@Override
		/**
		 * Sets IAtomContainer.setProperty(FunctionalGroupDescriptor.class.getName(),List<SmartsResult> );
		 */
		public Object process(Object object) throws AmbitException {
			if (object instanceof IAtomContainer) {
				IAtomContainer a = (IAtomContainer) object;
				a.removeProperty(FunctionalGroupDescriptor.class.getName());
				List<SmartsResult> results = null;
				for (int i=0; i < fragments.size();i++)
					try {
						SmartsResult match = (SmartsResult)fragments.get(i).calculate(a);
						if (match.getMatch()>0) {
							if (results == null) results = new ArrayList<SmartsResult>();
							results.add(match);
						}	
					} catch (CDKException x) {
						logger.error(x);
					}
				if (results != null) {
					a.setProperty(FunctionalGroupDescriptor.class.getName(),results);
					return object;					
				} else return null;
	
			}
			else return object;		
		}
		public List<FunctionalGroupDescriptor> getFragments() {
			return fragments;
		}
		public void setFragments(List<FunctionalGroupDescriptor> fragments) {
			this.fragments = fragments;
		}
		@Override
		public String toString() {
			StringBuffer b = new StringBuffer();
			for (int i=0; i < fragments.size();i++) {
				b.append(fragments.get(i).getParameters()[1]);
				b.append(' ');
			}	
			return b.toString();
			
		}
}
