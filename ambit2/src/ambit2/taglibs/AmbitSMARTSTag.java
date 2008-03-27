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

package ambit2.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;

import joelib.smarts.JOESmartsPattern;
import ambit2.data.molecule.SmartsQuery;

public class AmbitSMARTSTag extends AmbitMolTag {
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



	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}


	public String getSmarts() {
		return smarts;
	}

	public void setSmarts(String smarts) {
		this.smarts = smarts;
	}	
}


