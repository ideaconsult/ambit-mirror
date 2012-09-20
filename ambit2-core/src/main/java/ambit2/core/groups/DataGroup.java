/* DataGroup.java
 * Author: Nina Jeliazkova
 * Date: Aug 9, 2008 
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

package ambit2.core.groups;

import java.util.ArrayList;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IPseudoAtom;

/**
 * Corresponds to DAT group found in MOL files
 * <pre>
           * fff...fff:
           * 30 character field name (in MACCS-II no blanks, commas, or hyphens)
           * gg:
           * Field type (in MACCS-II F = formatted, N = numeric, T = text)
           * hhh...hhh
           * 20-character field units or format
           * ii:
           * Nonblank if data line is a query rather than Sgroup data, MQ = MACCS-II query, IQ = ISIS query, PQ = program name code query
           * jjj...:
           * Data query operator (blank for MACCS-II) * 
 * </pre>
 * <pre>
 * SDD
x,y:
Coordinates (2F10.4)
eee:
(Reserved for future use)
f:
Data display, A = attached, D = detached
g:
Absolute, relative placement, A = absolute, R = relative
h:
Display units, blank = no units displayed, U = display units
i:
(Reserved for future use)
jjj:
Number of characters to display (1...999 or ALL)
kkk:
Number of lines to display (unused, always 1)
ll:
(Reserved for future use)
m:
Tag character for tagged detached display (if non-blank)
n:
Data display DASP position (1...9). (MACCS-II only)
oo:
(Reserved for future use) 
 * </pre>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 9, 2008
 */
public class DataGroup extends AbstractGroup {
    public static String MRV_MULTICENTER ="MRV_MULTICENTER";
    public enum DGROUP_DISPLAY {
        X,
        Y,
        RESERVED0,
        DATADISPLAY,
        PLACEMENT,
        DISPLAYUNITS,
        RESERVED1,
        NUMCHARS,
        NUMLINES,
        RESERVED2,
        TAG,
        DASP,
        RESERVED3
    }    
    public enum SGROUP_FIELD {
        FIELDTYPE,
        FIELDFORMAT,
        QUERYTYPE,
        QUERYOPERATOR
    }
    protected IAtom multicenter = null;
    /**
     * 
     */
    private static final long serialVersionUID = 7303743162178189029L;
    
    
    public DataGroup(String label,int number) {
        super(label,number);
        bonds = new ArrayList<IBond>();
        setGroupVisible(this, false);
        setPoint2d(new Point2d(0,0));
    }

    public synchronized String getFieldFormat() {
        return getProperty(SGROUP_FIELD.FIELDFORMAT).toString();
    }

    public synchronized void setFieldFormat(String fieldFormat) {
        setProperty(SGROUP_FIELD.FIELDFORMAT,fieldFormat);
    }

    public synchronized String getFieldType() {
        return getProperty(SGROUP_FIELD.FIELDTYPE).toString();
    }

    public synchronized void setFieldType(String fieldType) {
        setProperty(SGROUP_FIELD.FIELDTYPE,fieldType);
    }

    public synchronized String getName() {
        return getLabel();
    }

    public synchronized void setName(String name) {
        setLabel(name.trim());
    }

    public synchronized String getQueryOperator() {
        return getProperty(SGROUP_FIELD.QUERYOPERATOR).toString();
    }

    public synchronized void setQueryOperator(String queryOperator) {
        setProperty(SGROUP_FIELD.QUERYOPERATOR,queryOperator);
    }

    public synchronized String getQueryType() {
        return getProperty(SGROUP_FIELD.QUERYTYPE).toString();
    }

    public synchronized void setQueryType(String queryType) {
        setProperty(SGROUP_FIELD.QUERYTYPE,queryType);
    }

    public synchronized IAtom getMulticenter() {
        return multicenter;
    }

    public synchronized void setMulticenter(IAtom atom) {
        this.multicenter = atom;
        if (atom instanceof IPseudoAtom)
            ((IPseudoAtom)atom).setLabel("X");        
    }
    
}
