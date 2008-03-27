/*
Copyright (C) 2005-2006  

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

package ambit2.taglibs;

import java.util.Hashtable;
import java.util.List;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;

import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.tools.MFAnalyser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.smiles.SmilesParserWrapper;
import ambit2.database.core.DbSQL;

public class AmbitSimilaritySearchTag extends AmbitSearchTag {
	protected String threshold = "0";
	protected String smiles = null;

	@Override
	protected void prepareSQL(Hashtable<String, String> query,
			List parameters) throws JspException {
		try {
			IMolecule m = null;
			if (mol != null) m = getMolecule(mol);
			if (m==null) 
				if (smiles != null) {
					SmilesParserWrapper sp = SmilesParserWrapper.getInstance();
					m = sp.parseSmiles(smiles);
				} else	
					throw new JspException("Empty molecule");
			

			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(m);	
			CDKHueckelAromaticityDetector.detectAromaticity(m);			
			double t = 0.5;
			try {
				t = Double.parseDouble(getThreshold());
			} catch (Exception x) {
				t = 0.5;
			}
			if (m != null) {
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(m);	
				CDKHueckelAromaticityDetector.detectAromaticity(m);				
				MFAnalyser mfa = new MFAnalyser(m);
				mfa.removeHydrogensPreserveMultiplyBonded();
			}
	        String sql = DbSQL.getSimilaritySearchSQL(m, getPage(), getPagesize(),t , -1, parameters);
	        
	        if (sql != null) {
	        	JspContext pageContext = getJspContext(); 
	        	pageContext.setAttribute(var, sql);
	        	pageContext.setAttribute(params, parameters);    
	        	
	        } else 
	        	throw new JspException("Empty SQL");
		} catch (Exception x) {
			throw new JspException(x);
		}

	}
	public String getSmiles() {
		return smiles;
	}
	public void setSmiles(String smiles) {
		this.smiles = smiles;
	}
	public String getThreshold() {
		return threshold;
	}
	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}	

}


