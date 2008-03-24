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

package ambit2.query.smarts;

import java.io.Serializable;

import org.openscience.cdk.interfaces.IAtomContainer;

public abstract class AbstractSmartsPattern<T> implements Serializable, ISmartsPattern<T> {
    protected boolean negate = false;
    protected String smarts = "";
    protected String name;
    protected String hint;
	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNegate() {
		return negate;
	}

	public void setNegate(boolean negate) {
		this.negate = negate;

	}

    @Override
    public String toString() {
    	return getSmarts();
    }
    public int match(IAtomContainer mol) throws SMARTSException {
    	return hasSMARTSPattern(getObjectToVerify(mol));
    }

	public String getSmarts() {
		return smarts;
	}

	public void setSmarts(String smarts) throws SMARTSException {
		this.smarts = smarts;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ISmartsPattern) {
			ISmartsPattern p = (ISmartsPattern)obj;
			
			return getSmarts().equals(p.getSmarts()) && (isNegate() == p.isNegate());
			
		} else return false;
	}

}


