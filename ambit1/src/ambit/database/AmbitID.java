/* AmbitID.java
 * Author: Nina Jeliazkova
 * Date: Apr 1, 2007 
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

package ambit.database;

import ambit.misc.AmbitCONSTANTS;


public class AmbitID  implements Comparable<AmbitID>{
    private int idstructure = -1;
    private int idsubstance = -1;
    public AmbitID() {
        this(-1,-1);
    }
    public AmbitID(int idsubstance,int idstructure) {
        setIdSubstance(idsubstance);
        setIdStructure(idstructure);
    }
    public AmbitID(Object idsubstance,Object idstructure) {
        super();
        setIdSubstance(idsubstance);
        setIdStructure(idstructure);
    }
    
    protected synchronized int getId(Object id) {
        try {
            if (id instanceof Number)
                return ((Number)id).intValue();
            else 
                return Integer.parseInt(id.toString());
        } catch (Exception x) {
            return -1;
        }

    }    
    public int compareTo(AmbitID o) {
        
        int r =  getIdSubstance() - o.getIdSubstance();
        if (r==0) return getIdStructure() - o.getIdStructure();
        else return r;
        
    }
    
    @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append(AmbitCONSTANTS.AMBIT_IDSUBSTANCE);
        b.append('=');
        b.append(Integer.toString(idsubstance));
        b.append(',');
        b.append(AmbitCONSTANTS.AMBIT_IDSTRUCTURE);
        b.append('=');
        b.append(Integer.toString(idstructure));        
        return b.toString();
    }

    public synchronized int getIdStructure() {
        return idstructure;
    }
    public synchronized void setIdStructure(int idstructure) {
        this.idstructure = idstructure;
    }
    public synchronized void setIdStructure(Object idstructure) {
        setIdStructure(getId(idstructure));
    }    
    public synchronized int getIdSubstance() {
        return idsubstance;
    }
    public synchronized void setIdSubstance(int idsubstance) {
        this.idsubstance = idsubstance;
    }
    public synchronized void setIdSubstance(Object idsubstance) {
        setIdSubstance(getId(idsubstance));
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AmbitID) { 
            AmbitID o = (AmbitID) obj;
            return (getIdSubstance() == o.getIdSubstance()) &&
                    (getIdStructure() == o.getIdStructure());
        } else return false;
    }
}
