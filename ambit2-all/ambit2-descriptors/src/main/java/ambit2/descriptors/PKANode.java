/* PKANode.java
 * Author: Nina Jeliazkova
 * Date: Oct 3, 2008 
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

package ambit2.descriptors;

import java.util.Hashtable;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.smarts.query.SMARTSException;
import ambit2.smarts.query.SmartsPatternAmbit;

public class PKANode {
	protected static Hashtable<String,SmartsPatternAmbit> smartsPattern = new Hashtable<String,SmartsPatternAmbit>();
    protected int id;
    protected int parent;
    protected double pka;
    protected double pka_range;
    protected boolean present;
    protected boolean terminal;
    protected PKANode nodeYes;
    protected PKANode nodeNo;
    protected String smarts;
    

    public synchronized void setNode(boolean smartsPresent, PKANode node) {
        if (smartsPresent)
            this.nodeYes = node;
        else 
            this.nodeNo = node;
    }

    public synchronized PKANode getNodeNo(boolean smartsPresent) {
        if (smartsPresent)
            return nodeYes;
        else 
            return nodeNo;
    }

    public PKANode() {
    }
    
    /**
     * 
     * @return true if the node is terminal
     */
    public synchronized boolean isTerminal() {
        return terminal;
    }
    public synchronized void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }    
    public synchronized int getId() {
        return id;
    }
    public synchronized void setId(int id) {
        this.id = id;
    }
    public synchronized int getParent() {
        return parent;
    }
    public synchronized void setParent(int parent) {
        this.parent = parent;
    }
    public synchronized double getPka() {
        return pka;
    }
    public synchronized void setPka(double pka) {
        this.pka = pka;
    }
    public synchronized double getPka_range() {
        return pka_range;
    }
    public synchronized void setPka_range(double pka_range) {
        this.pka_range = pka_range;
    }
    public synchronized boolean isPresent() {
        return present;
    }
    public synchronized void setPresent(boolean present) {
        this.present = present;
    }
    public synchronized String getSmarts() {
        return smarts;
    }
    public synchronized void setSmarts(String smarts) {
    	if ("".equals(smarts.trim()))
    		this.smarts = null;
    	else
    		this.smarts = smarts;

    }
    @Override
    public String toString() {
      //#node,#parent,children,FP,SMARTS,Y/N,pKa_cal,pKa_range
        StringBuffer b = new StringBuffer();
        b.append(getId());
        b.append(',');
        b.append(getParent());
        b.append(',');
        b.append(isTerminal());
        b.append(',');
        b.append(',');
        b.append(getSmarts());
        b.append(',');
        b.append(isPresent());
        b.append(',');
        b.append(getPka());
        b.append(',');
        b.append(getPka_range());
        
        return b.toString();
    }
    
    public boolean find(IAtomContainer ac) throws SMARTSException {
    	if (getSmarts() !=null) { 
	    	SmartsPatternAmbit pattern = smartsPattern.get(getSmarts());
	    	if (pattern == null) {
	    		pattern = new SmartsPatternAmbit(SilentChemObjectBuilder.getInstance());
	    		pattern.useMOEvPrimitive(true);
	    		pattern.setUseCDKIsomorphism(false);
	    		pattern.setSmarts(getSmarts());
	    		smartsPattern.put(getSmarts(), pattern);
	    	}
	        return pattern.hasSMARTSPattern(ac)>0;
    	} else 
    		return true;
    }
    
}