/* IStructureRecord.java
 * Author: Nina Jeliazkova
 * Date: May 7, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
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

package ambit2.base.interfaces;

import java.util.Map;

public interface IStructureRecord {
	public enum MOL_TYPE {SDF,CML,CSV};
    public abstract String getFormat();

    public abstract void setFormat(String format);

    public abstract int getIdchemical();

    public abstract void setIdchemical(int idchemical);

    public abstract int getIdstructure();

    public abstract void setIdstructure(int idstructure);

    public abstract String getContent();

    public abstract void setContent(String content);

    public abstract Map getProperties();

    public abstract void setProperties(Map properties);
    
    public void setProperty(Object key,Object value);    
    public Object getProperty(Object key);
    
    public abstract void clear();

    public abstract String getWritableContent();
}