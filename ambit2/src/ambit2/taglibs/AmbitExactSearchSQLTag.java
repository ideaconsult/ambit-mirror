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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.MFAnalyser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.config.AmbitCONSTANTS;
import ambit2.smiles.SmilesParserWrapper;
import ambit2.database.core.DbSQL;

public class AmbitExactSearchSQLTag extends AmbitSearchTag  {
	protected String  cas = null;
	protected String  formula = null;
	protected String  name = null;
	protected String  alias = null;
	protected String  smiles = null;

	@Override
	protected void prepareSQL(Hashtable<String, String> properties, List parameters) throws JspException {
        String sql = DbSQL.getExactSearchSQL(properties, 0, 100,0,parameters);
        if (sql != null) {
        	JspContext pageContext = getJspContext(); 
        	pageContext.setAttribute(var, sql);
        	pageContext.setAttribute(params, parameters);    
        	
        } else 
        	throw new JspException("Empty SQL");	
	}
	protected Hashtable<String, String> prepareQuery() {
		Hashtable<String, String> properties = new Hashtable<String, String>();
		if (cas != null) properties.put(CDKConstants.CASRN, cas);
		if (name != null) properties.put(CDKConstants.NAMES, name);
		if (formula != null) properties.put(AmbitCONSTANTS.FORMULA, formula);
		
		String normalizedSmiles = null;
		if (mol != null) 
          	try {
          		normalizedSmiles = createSmiles(getMolecule(mol));
        	} catch (Exception x) {
        		normalizedSmiles = null;
        	}
        if ((normalizedSmiles == null) && (smiles != null))
        	try {
        		SmilesParserWrapper spt = SmilesParserWrapper.getInstance();
        		normalizedSmiles = createSmiles(spt.parseSmiles(smiles));
        	} catch (Exception x) {
        		normalizedSmiles = null;
        	}
        	
       	if (normalizedSmiles != null) properties.put(AmbitCONSTANTS.SMILES, normalizedSmiles);		
        	
		return properties;
	}
		
	protected String createSmiles(IMolecule m) throws Exception  {
		if (m != null) {
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(m);	
			CDKHueckelAromaticityDetector.detectAromaticity(m);
			MFAnalyser mfa = new MFAnalyser(m);
			mfa.removeHydrogensPreserveMultiplyBonded();
		}
		
    	if ((m != null) && (m.getAtomCount() > 0)) {
            SmilesGenerator g = new SmilesGenerator();
            return g.createSMILES(m);
    	}   
    	return null;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getCas() {
		return cas;
	}
	public void setCas(String cas) {
		this.cas = cas;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSmiles() {
		return smiles;
	}
	public void setSmiles(String smiles) {
		this.smiles = smiles;
	}



}


