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

package ambit.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;

import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;

public class AmbitIsomorphismTag extends AmbitMolTag {
	protected String  var ="hit";	
	protected String query;
	protected String queryType="mol";
	protected IMolecule queryMolecule=null;
	protected boolean subgraph=false;
	
	public boolean isSubgraph() {
		return subgraph;
	}
	public void setSubgraph(boolean subgraph) {
		this.subgraph = subgraph;
	}
	@Override
	public void doTag() throws JspException, IOException {
       	try {		
			if (queryMolecule == null) {
				queryMolecule = getMolecule(query,queryType);
				HueckelAromaticityDetector.detectAromaticity(queryMolecule);
			}	
       	} catch (Exception x) {
    		throw new JspException(x);
       	}				
		if (queryMolecule == null)
			throw new JspException("Query undefined!");
		if (queryMolecule.getAtomCount() == 0)
			throw new JspException("Empty query molecule!");		
		
		int hit = 0;
       	try {
       		IMolecule m = getMolecule(getMol());
    		if (m == null)
    			throw new JspException("Structure undefined!");       		
    		if (m.getAtomCount() == 0)
    			throw new JspException("Empty molecule!");		
       		
       		HueckelAromaticityDetector.detectAromaticity(m);
       		if (subgraph)
       			if (UniversalIsomorphismTester.isSubgraph(m, queryMolecule)) hit = 1; else hit = 0;
       		else
       			if (UniversalIsomorphismTester.isIsomorph(m, queryMolecule)) hit = 1; else hit = 0;
	            
        	JspContext pageContext = getJspContext(); 
        	pageContext.setAttribute(var, Integer.toString(hit));
       	} catch (Exception x) {
        		throw new JspException(x);
       	}	
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		if (this.query != query) {
			this.query = query;
			queryMolecule = null;
		}
	}
	public String getQueryType() {
		return queryType;
	}
	public void setQueryType(String queryType) {
		if (this.queryType != queryType) {
			this.queryType = queryType;
			queryMolecule = null;
		}
	}
	public String getVar() {
		return var;
	}
	public void setVar(String var) {
		this.var = var;
	}	
}
