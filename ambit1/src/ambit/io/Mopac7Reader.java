/* Mopac7Reader.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-8 
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

package ambit.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.io.DefaultChemObjectReader;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.tools.LoggingTool;

import ambit.data.descriptors.DescriptorDefinition;

/**
 * Reads MOPAC output, extracts several electronic parameters and assigns them as a molecule properties.<br>
 * parameters "NO. OF FILLED LEVELS",	"TOTAL ENERGY","FINAL HEAT OF FORMATION","IONIZATION POTENTIAL","ELECTRONIC ENERGY","CORE-CORE REPULSION","MOLECULAR WEIGHT"
 * Doesn't update structure coordinates ! (TODO fix) <br>
 * Used by {@link ambit.processors.descriptors.MopacShell} 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-8
 */
public class Mopac7Reader extends DefaultChemObjectReader {
    BufferedReader input = null;
    private LoggingTool logger = null;
    public static String[] parameters = {"NO. OF FILLED LEVELS",
        	"TOTAL ENERGY","FINAL HEAT OF FORMATION","IONIZATION POTENTIAL",
        	"ELECTRONIC ENERGY","CORE-CORE REPULSION","MOLECULAR WEIGHT","eHOMO","eLUMO"};
    protected static String eigenvalues = "EIGENVALUES";
    protected static String filledLevels = "NO. OF FILLED LEVELS";
    protected DescriptorDefinition eHomo, eLumo;
    
    /**
     * Contructs a new Mopac7reader that can read Molecule from a given Reader.
     *
     * @param  in  The Reader to read from
     */
    public Mopac7Reader(Reader in) {
        logger = new LoggingTool(this);
        input = new BufferedReader(in);

    }

    public Mopac7Reader(InputStream input) {
        this(new InputStreamReader(input));
    }
    /*
     *FINAL HEAT OF FORMATION =        -32.90826 KCAL =   -137.68818 KJ


          TOTAL ENERGY          =      -1618.31024 EV
          ELECTRONIC ENERGY       =      -6569.42640 EV  POINT GROUP:     C1  
          CORE-CORE REPULSION     =       4951.11615 EV

          IONIZATION POTENTIAL    =         10.76839
          NO. OF FILLED LEVELS    =         23
          MOLECULAR WEIGHT        =    122.123
 
     */
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.ChemObjectReader#read(IChemObject)
     */
    public IChemObject read(IChemObject arg0) throws CDKException {
        StringBuffer eigenvalues = new StringBuffer();
        if (arg0 instanceof AtomContainer) {
             AtomContainer a = (AtomContainer) arg0;
	         try {
	            String line = input.readLine();
	            while (line != null) {
	                if (line.indexOf(Mopac7Reader.eigenvalues) >= 0) {
	                    line = input.readLine();
	                    line = input.readLine();
	                    while (!line.trim().equals("")) {
	                        eigenvalues.append(line);
	                        line = input.readLine();
	                    }
	                    a.setProperty(Mopac7Reader.eigenvalues,eigenvalues.toString());
	                } else
	                    for (int i=0; i < parameters.length;i++)
	    		            if (line.indexOf(parameters[i]) >= 0) {
	    		            	String v = line.substring(line.indexOf("=")+1).trim();
	    		            	v = v.replaceAll("EV","");
	    		            	v = v.replaceAll("KCAL","");
			                    a.setProperty(parameters[i],v);
			                    break;
	    		            }    
	                line = input.readLine();
	            }
	            calcHomoLumo(a);
	            return a;
            } catch (Exception x) {
                throw new CDKException(x.getMessage());
            }
        } else return null;
    }
    protected void calcHomoLumo(IAtomContainer mol) {
        Object eig = mol.getProperty(eigenvalues);
        if (eig == null) return;
        //mol.getProperties().remove(eigenvalues);
        Object nfl = mol.getProperty(filledLevels);
        //mol.getProperties().remove(filledLevels);
        if (nfl == null) return;
        int n = 0;
        try {
            n = Integer.parseInt(nfl.toString());
        } catch (Exception x) {
            return;
        }
        String[] e = eig.toString().split("\\s");
        int m = 0;
        for (int i=0; i < e.length;i++) {
            if (e[i].trim().equals("")) continue;
            else 
                try {
                    double d = Double.parseDouble(e[i]);
                    m++;
                    if (m==n) {mol.setProperty("eHOMO",e[i]); }
                    else if (m==(n+1)) {  mol.setProperty("eLUMO",e[i]); }
                } catch (Exception x) {
                    return;
                }                
        }
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.ChemObjectReader#setReader(java.io.Reader)
     */
    public void setReader(Reader arg0) throws CDKException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.io.ChemObjectReader#setReader(java.io.InputStream)
     */
    public void setReader(InputStream arg0) throws CDKException {
        // TODO Auto-generated method stub

    }

 
    /* (non-Javadoc)
     * @see org.openscience.cdk.io.ChemObjectIO#close()
     */
    public void close() throws IOException {
        input.close();

    }
            
    public synchronized DescriptorDefinition getEHomo() {
        return eHomo;
    }
    public synchronized void setEHomo(DescriptorDefinition homo) {
        eHomo = homo;
    }
    public synchronized DescriptorDefinition getELumo() {
        return eLumo;
    }
    public synchronized void setELumo(DescriptorDefinition lumo) {
        eLumo = lumo;
    }
    public String toString() {
    	return "MOPAC7 format";
    }
	public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IChemFile.class.equals(interfaces[i])) return true;
			if (ISetOfMolecules.class.equals(interfaces[i])) return true;
			if (IMolecule.class.equals(interfaces[i])) return true;
		}
		return false;
	}
	/* (non-Javadoc)
     * @see org.openscience.cdk.io.IChemObjectIO#getFormat()
     */
    public IResourceFormat getFormat() {
        // TODO Auto-generated method stub
        return null;
    }
}
