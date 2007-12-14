/* CatalogReference.java
 * Author: Nina Jeliazkova
 * Date: Mar 4, 2007 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit.data.qmrf;

import ambit.data.AmbitObject;
import ambit.data.IAmbitEditor;

public class CatalogReference extends Catalog {
    protected Catalog catalog;
    public CatalogReference(String elementID) {
        super(elementID);
        this.catalog = null;
    }
    public synchronized Catalog getCatalog() {
        return catalog;
    }
    public synchronized void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }
    @Override
    public int addItem(AmbitObject entry) {
        catalog.addItem(entry);
        return super.addItem(entry);
    }

    public int addReference(String idref) {
        return addReference(getCatalog().id(idref));
    }
    public int addReference(CatalogEntry e) {
        if (e !=null)
            return super.addItem(e);
        else return -1;
    }    
    
	@Override
	public IAmbitEditor editor(boolean editable) {
		CatalogReferenceEditor p = new CatalogReferenceEditor(this,editable);
		p.setEditable(editable);
		return p;
	}
	public Catalog getExternal_catalog() {
		if (catalog != null)
		return catalog.external_catalog;
		else return null;
	}
	public void setExternal_catalog(Catalog external_catalog) {
		if (catalog != null)
		catalog.setExternal_catalog(external_catalog);
	}
}
