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

package ambit.taglibs;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import joelib.smarts.JOESmartsPattern;

import org.openscience.cdk.interfaces.IMolecule;

import ambit.data.molecule.Compound;
import ambit.data.molecule.MoleculeTools;
import ambit.data.molecule.SmartsQuery;
import ambit.data.molecule.SmilesParserWithTimeout;

public class AmbitSMARTSTag extends SimpleTagSupport {
	public static final String type_mol = "mol";
	public static final String type_cml = "cml";
	public static final String type_smiles = "smiles";
	protected String  mol = null;
	protected String molType = "mol";
	protected String  var ="match";
	protected String smarts = "";
	
	

	
	@Override
	public void doTag() throws JspException, IOException {
        if ((smarts != null) && (!"".equals(smarts))) {
        	JOESmartsPattern joeSmartsPatern = new JOESmartsPattern();
        	if (!joeSmartsPatern.init(smarts)) throw new JspException("Invalid SMARTS pattern "+smarts);
        	
        	try {
        		SmartsQuery query = new SmartsQuery(getSmarts());
	        	int match = query.match(getMolecule(getMol()));
	            
	        	JspContext pageContext = getJspContext(); 
	        	pageContext.setAttribute(var, Integer.toString(match));
        	} catch (Exception x) {
        		throw new JspException(x);
        	}	
   
        	
        } else 
        	throw new JspException("Empty SMARTS");
	}	

	protected IMolecule getMolecule(String mol) throws Exception {
		if (type_cml.equals(molType)) {
			return Compound.readMolecule(mol);
		} else if (type_mol.equals(molType)) { 
			String s = URLDecoder.decode(mol,"UTF-8");
		    return  MoleculeTools.readMolfile(s);
		} else if (type_smiles.equals(molType)) {
			SmilesParserWithTimeout p = new SmilesParserWithTimeout();
			return p.parseSmiles(mol, 30000);
    
		} else throw new Exception("Invalid type "+molType);
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public String getMol() {
		return mol;
	}

	public void setMol(String mol) {
		this.mol = mol;
	}

	public String getMolType() {
		return molType;
	}

	public void setMolType(String molType) {
		this.molType = molType;
	}

	public String getSmarts() {
		return smarts;
	}

	public void setSmarts(String smarts) {
		this.smarts = smarts;
	}	
}


