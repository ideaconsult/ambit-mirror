/* FastTemplateHandler3D.java
 * Author: Nina Jeliazkova
 * Date: Jul 13, 2006 
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

package ambit.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.BitSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.RingSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.modeling.builder3d.TemplateHandler3D;

/**
 * A faster version of {@link TemplateHandler3D}, which does not read all template structures in memory, but user {@link ambit.io.RandomAccessSDFReader}.<br>
 * Expects "data/templates/ringTemplateStructures.sdf" instead of "org/openscience/cdk/modeling/builder3d/data/ringTemplateFingerprints.txt.gz" packaed in cdk.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Jul 13, 2006
 */
public class FastTemplateHandler3D extends TemplateHandler3D {
    Molecule molecule;
    RingSet sssr;
    Vector fingerprintData = null;
    Vector ringTemplates = null;    
    protected RandomAccessReader ra;
    protected static FastTemplateHandler3D templateHandler = null;
    /**
     * 
     */
    protected FastTemplateHandler3D() {
        fingerprintData = new Vector();
        ringTemplates = new Vector(75);
        ra = null;
    }
    
    public static FastTemplateHandler3D getInstance() {
    	if (templateHandler == null) templateHandler = new FastTemplateHandler3D();
    	return templateHandler;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {
        try {
        	templateHandler = null;
        if (ra != null) ra.close();
        } catch (IOException x) {
            x.printStackTrace();
        }
        super.finalize();
    }
    public void loadTemplates() throws CDKException {
        if (ra != null) {
            System.out.println("Templates already loaded");
            return;
        }
        try {
            ra = new RandomAccessSDFReader(new File("data/templates/ringTemplateStructures.sdf"),DefaultChemObjectBuilder.getInstance());
            ra.setChemObjectReader(new MDLReader());
        } catch (IOException x) {
            throw new CDKException(x.getMessage());
        }
        InputStream ins;
        BufferedReader fin = null;
        //System.out.println("TEMPLATE Finger");
        try {

            ins = this.getClass().getClassLoader().getResourceAsStream("org/openscience/cdk/modeling/builder3d/data/ringTemplateFingerprints.txt.gz");
            fin = new BufferedReader(new InputStreamReader(new GZIPInputStream(ins)));
        } catch (Exception exc3) {
            System.out.println("Could not read Fingerprints from FingerprintFile due to: " + exc3.getMessage());
        }
        String s = null;
        while (true) {
            try {
                s = fin.readLine();
            } catch (Exception exc4) {
                exc4.printStackTrace();
            }

            if (s == null) {
                break;
            }
            fingerprintData.add((BitSet) getBitSetFromFile(new StringTokenizer(s, "\t ;{, }")));
        }
        //System.out.println("Fingerprints are read in:"+fingerprintData.size());
    }
    public void mapTemplates(IAtomContainer ringSystems, double NumberOfRingAtoms) throws Exception {
        //System.out.println("Map Template...START---Number of Ring Atoms:"+NumberOfRingAtoms);
        IAtomContainer template;
        QueryAtomContainer queryRingSystem = QueryAtomContainerCreator.createAnyAtomContainer(ringSystems, false);
        QueryAtomContainer query;
        BitSet ringSystemFingerprint = new Fingerprinter().getFingerprint(queryRingSystem);
        RMap map;
        org.openscience.cdk.interfaces.IAtom atom1;
        org.openscience.cdk.interfaces.IAtom atom2;
        boolean flagMaxSubstructure = false;
        for (int i = 0; i < fingerprintData.size(); i++) {
            template = getTemplateAt(i);
            if (template.getAtomCount() != ringSystems.getAtomCount()) {
                continue;
            }
            if (Fingerprinter.isSubset(ringSystemFingerprint, (BitSet) fingerprintData.get(i))) {
                query = QueryAtomContainerCreator.createAnyAtomContainer(template, true);
                if (UniversalIsomorphismTester.isSubgraph(ringSystems, query)) {
                    List list = UniversalIsomorphismTester.getSubgraphAtomsMap(ringSystems, query);
                    if ((NumberOfRingAtoms) / list.size() == 1) {
                        flagMaxSubstructure = true;
                    }

                    for (int j = 0; j < list.size(); j++) {
                        map = (RMap) list.get(j);
                        atom1 = ringSystems.getAtomAt(map.getId1());
                        atom2 = template.getAtomAt(map.getId2());
                        if (atom1.getFlag(CDKConstants.ISINRING)) {
                            atom1.setX3d(atom2.getX3d());
                            atom1.setY3d(atom2.getY3d());
                            atom1.setZ3d(atom2.getZ3d());
                        }
                    }//for j

                    if (flagMaxSubstructure) {
                        break;
                    }

                }//if subgraph
            }//if fingerprint
        }//for i
        if (!flagMaxSubstructure) {
            System.out.println("WARNING:Maybe RingTemplateError");
        }
    }
    
    private BitSet getBitSetFromFile(StringTokenizer st) {
        BitSet bitSet = new BitSet(1024);
        for (int i = 0; i < st.countTokens(); i++) {

            try {
                bitSet.set(Integer.parseInt(st.nextToken()));
            } catch (NumberFormatException nfe) {
                // do nothing
            }
        }
        return bitSet;
    }    
    /* (non-Javadoc)
     * @see org.openscience.cdk.modeling.builder3d.TemplateHandler3D#getTemplateAt(int)
     */
    public IAtomContainer getTemplateAt(int position) {
        try {
            return (IAtomContainer)ra.readRecord(position);
        } catch (Exception x) {
            x.printStackTrace();
            return null;
        }
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.modeling.builder3d.TemplateHandler3D#getTemplateCount()
     */
    public int getTemplateCount() {
        return ra.getNumberOfRecords();
    }
}
