/* AtomDistanceProcessor.java
 * Author: nina
 * Date: Apr 21, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
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

package ambit2.core.data.distances;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.interfaces.IAtomContainer;


public class AtomDistanceProcessor extends	DefaultAmbitProcessor<IAtomContainer,List<AtomDistance>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2602737581794155267L;

	public List<AtomDistance> process(IAtomContainer target)
			throws AmbitException {
		/*
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
	        */
		return null;
	    }
}
