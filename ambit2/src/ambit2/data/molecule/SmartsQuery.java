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

package ambit2.data.molecule;

import joelib.molecule.JOEMol;
import joelib.smarts.JOESmartsPattern;

import org.openscience.cdk.interfaces.IMolecule;

import ambit2.query.smarts.Convertor;


public class SmartsQuery implements Comparable<SmartsQuery>{
	protected String name;
	protected String hint;

	protected JOESmartsPattern joeSmartsPatern = null;
	public SmartsQuery() {
		super();
		setHint("");
		setName("");
	}
	public SmartsQuery(String smarts) throws Exception {
		this(smarts,smarts,"");
	}	
	public SmartsQuery(String smarts,String name,String hint) throws Exception {
		super();
		setSmarts(smarts);
		setName(name);
		setHint(hint);
	}		
	public int match(IMolecule mol) throws Exception {
       	JOEMol joemol = Convertor.convert(mol);
        joeSmartsPatern.match(joemol);
        return joeSmartsPatern.numMatches();
 	}
    public String getSmarts() {
    	if (joeSmartsPatern!= null)
    		return joeSmartsPatern.getSMARTS();
    	else return null;
    }
    public void setSmarts(String smarts) throws Exception {
    	if (joeSmartsPatern == null) joeSmartsPatern = new JOESmartsPattern();
    	joeSmartsPatern.init(smarts.trim());
    }
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name.trim();
	}
	public String getHint() {
		return hint;
	}
	public void setHint(String hint) {
		this.hint = hint;
	}
	@Override
	public String toString() {
		return name;
	}
    public int hashCode() {
    	int hash = 7;
    	int var_code = (null == name ? 0 : name.hashCode());
    	hash = 31 * hash + var_code; 
    	var_code = (null == getSmarts() ? 0 : getSmarts().hashCode());
    	return hash;
    }
	public int compareTo(SmartsQuery o) {
		return getSmarts().compareTo(o.getSmarts());
	}

}


