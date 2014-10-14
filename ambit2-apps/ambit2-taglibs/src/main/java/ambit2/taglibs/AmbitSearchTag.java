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

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.openscience.cdk.interfaces.IMolecule;

import ambit2.core.data.MoleculeTools;


public abstract class AmbitSearchTag extends SimpleTagSupport {
	protected String  mol = null;
	protected String  var ="sql";
	protected String  params ="params";
	protected int page=0;
	protected int pagesize=100;
	public AmbitSearchTag() {
		super();
	}
	
	protected Hashtable<String, String> prepareQuery() {
		return new Hashtable<String,String>();
	}
	protected List prepareParameters() {
		return new ArrayList();
	}

	protected abstract void prepareSQL(Hashtable<String, String>  query, List parameters) throws JspException ;
	
	protected IMolecule getMolecule(String mol) throws Exception {

		String s = URLDecoder.decode(mol,"UTF-8");
        return  MoleculeTools.readMolfile(s);	
	}
	@Override
	public void doTag() throws JspException, IOException {
		try
	    {
			Hashtable<String,String> properties = prepareQuery();
			
			List parameters = prepareParameters();
			
			prepareSQL(properties, parameters);
			

	    }
	    catch (Exception e)
	    {
	    	throw new JspException(e);
	
	    }
	
	    return; 
	
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
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

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}


}
