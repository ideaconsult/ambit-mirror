/* AtomDistancesResult.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-9 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
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

package ambit.processors.results;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.TreeSet;

import ambit.database.query.DistanceQuery;
import ambit.exceptions.AmbitException;
import ambit.processors.IAmbitResult;

/**
 * A set of {@link ambit.processors.results.AtomDistanceResult}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-9
 */
public class AtomDistancesResult implements IAmbitResult {
	protected String title;
    protected TreeSet set;
    public static final String property = "DISTANCE2ATOMS";
    DistanceQuery query = null;    
    /**
     * 
     */
    public AtomDistancesResult(DistanceQuery query) {
        super();
        set = new TreeSet();
        this.query = query;        
    }
    public AtomDistancesResult() {
        super();
        set = new TreeSet();
    }    
    /* (non-Javadoc)
     * @see ambit.processors.IAmbitResult#clear()
     */
    public void clear() {
        set.clear();

    }
    public void add(AtomDistanceResult atomDistance) {
        set.add(atomDistance);
    }
    public void addFiltered(AtomDistanceResult atomDistance) {
    	if (atomDistance.query(query))
    		set.add(atomDistance);
    }    
    public void remove(AtomDistanceResult atomDistance) {
        set.remove(atomDistance);
    }    
    public Iterator getIterator() {
        return set.iterator();
    }
    /* (non-Javadoc)
     * @see ambit.processors.IAmbitResult#update(java.lang.Object)
     */
    public void update(Object object) throws AmbitException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see ambit.processors.IAmbitResult#write(java.io.Writer)
     */
    public void write(Writer writer) throws AmbitException {
        try {
            writer.write(toString());
        } catch (IOException x) {
            throw new AmbitException(x);
        }

    }
    public int size() {
        return set.size();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return set.toString();
    }
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
		
	}


}
