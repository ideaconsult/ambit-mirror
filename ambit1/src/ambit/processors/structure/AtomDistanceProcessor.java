/* AtomDistanceProcessor.java
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

package ambit.processors.structure;

import javax.vecmath.Point3d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit.data.IAmbitEditor;
import ambit.data.molecule.Compound;
import ambit.database.query.DistanceQuery;
import ambit.exceptions.AmbitException;
import ambit.processors.DefaultAmbitProcessor;
import ambit.processors.DefaultProcessorEditor;
import ambit.processors.IAmbitResult;
import ambit.processors.results.AtomDistanceResult;
import ambit.processors.results.AtomDistancesResult;

/**
 * Generates pairwise atom distances, fills in an {@link ambit.processors.results.AtomDistancesResult} object with distances and
 * assigns the object as molecule property {@link ambit.processors.results.AtomDistancesResult#property} <br>
 * Used by {@link ambit.database.writers.AtomDistanceWriter} 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-9
 */
public class AtomDistanceProcessor extends DefaultAmbitProcessor {
    protected boolean skipH = true;
    protected IAmbitResult result = null;
    public static final String ERR_3DNOTDEFINED = "3D coordinates not defined!";
    DistanceQuery query = null;
    /**
     * 
     */
    public AtomDistanceProcessor(DistanceQuery query) {
        super();
        this.query = query;
    }
    public AtomDistanceProcessor() {
        super();
    }
    /* (non-Javadoc)
     * @see ambit.processors.IAmbitProcessor#process(java.lang.Object)
     */
    public Object process(Object object) throws AmbitException {
        if (object instanceof IAtomContainer) 
            return calculateDistances((IAtomContainer) object);
        else if (object instanceof Compound)
            return calculateDistances(((Compound) object).getMolecule());
        else return null;
    }

    /* (non-Javadoc)
     * @see ambit.processors.IAmbitProcessor#createResult()
     */
    public IAmbitResult createResult() {
        return new AtomDistancesResult(query);
    }

    /* (non-Javadoc)
     * @see ambit.processors.IAmbitProcessor#getResult()
     */
    public IAmbitResult getResult() {
        if (result == null) result = createResult();
        return result;
    }

    /* (non-Javadoc)
     * @see ambit.processors.IAmbitProcessor#setResult(ambit.processors.IAmbitResult)
     */
    public void setResult(IAmbitResult result) {
        if (result instanceof AtomDistancesResult)
            this.result = result;

    }
    public Object calculateDistances(IAtomContainer ac) throws AmbitException {
        double distance = 0;
        ac.removeProperty(AtomDistancesResult.property);
        AtomDistancesResult r = (AtomDistancesResult) getResult();
        r.clear();
        for (int i=0; i < ac.getAtomCount();i++) {
            IAtom atom1 = ac.getAtomAt(i);
            if (skipH && (atom1.getSymbol().equals("H"))) continue;
            Point3d p1 = atom1.getPoint3d();
            if (p1 == null) throw new AmbitException(ERR_3DNOTDEFINED);
            
            for (int j=i+1; j < ac.getAtomCount();j++) {
                IAtom atom2 = ac.getAtomAt(j);
                if (skipH && (atom2.getSymbol().equals("H"))) continue;
                Point3d p2 = atom2.getPoint3d();
                if (p2 == null) throw new AmbitException(ERR_3DNOTDEFINED);
                r.addFiltered(new AtomDistanceResult(
                        atom1.getSymbol(),
                        atom2.getSymbol(),
                        p1.distance(p2)
                        ));
                
            }
        }
        ac.setProperty(AtomDistancesResult.property,r);
        return ac;
    }
    public synchronized boolean isSkipH() {
        return skipH;
    }
    public synchronized void setSkipH(boolean skipH) {
        this.skipH = skipH;
    }
    /* (non-Javadoc)
     * @see ambit.processors.IAmbitProcessor#close()
     */
    public void close() {

    }
    public IAmbitEditor getEditor() {

    	return new DefaultProcessorEditor(this);
    }
    public String toString() {
    	return "Calculates distances between atoms";
    }
}
