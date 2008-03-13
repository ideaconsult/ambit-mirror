/* RandomAccessSDFReader.java
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

package ambit2.io;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLFormat;
import org.openscience.cdk.io.listener.IReaderListener;

/**
 * Random access of SDF file. Doesn't load molecules in memory, uses prebuilt index and seeks to find the correct record offset.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Jul 13, 2006
 */
public class RandomAccessSDFReader extends RandomAccessReader {

    /**
     * @param file
     * @param builder
     * @throws IOException
     */
    public RandomAccessSDFReader(File file, IChemObjectBuilder builder)
            throws IOException {
        this(file, builder,null);
    }
    public RandomAccessSDFReader(File file, IChemObjectBuilder builder, IReaderListener listener)
    throws IOException {
        super(file, builder,listener);
    }
    @Override
    public IChemObjectReader createChemObjectReader() {
        //return new FilteredMDLReader();
    	return new MDLV2000Reader();
    }
    protected boolean isRecordEnd(String line) {
        return line.equals("$$$$");
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectIO#getFormat()
     */
    public IResourceFormat getFormat() {
        return MDLFormat.getInstance();
    }
    public Object readContent(String buffer, int length) throws CDKException {
        if (chemObjectReader == null) return new String(b,0,length);
        else {
        	chemObjectReader.setReader(new StringReader(buffer));
        	/*
            
            return chemObjectReader.read(builder.newMolecule());
            */
            
            //read(IMolecule) doesn't read properties ...
            IChemObject co = chemObjectReader.read(builder.newChemFile());
            if (co instanceof IChemFile) {
                int c = ((IChemFile) co).getChemSequenceCount();
                for (int i=0; i <c;i++) {
                    Iterator cm = ((IChemFile) co).getChemSequence(i).chemModels();
                    while (cm.hasNext()) {
                    	Iterator sm = ((IChemModel)cm.next()).getMoleculeSet().molecules();
                        while (sm.hasNext()) {
                        	
                        	co = (IMolecule) sm.next();
                        	break;
                        }	
                        	/*
                        for (int k=0; k < sm.getAtomContainerCount();k++) {
                        	co  = sm.getAtomContainer(k);
                        	break;
                        }
                        
                    	sm.removeAllAtomContainers();
                    	sm = null;
                    	*/
                    	break;
                    }
                    cm = null;
                    break;
                }
                //cs = null;
            }
            return co;
            
        }
    }

}
