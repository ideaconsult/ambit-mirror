/* EinecsTest.java
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

package ambit2.core.test.io;


import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;

import ambit2.core.io.MDLV2000Reader;

public class EinecsTest extends TestCase {
    protected String maindir;
    @Before
    public void setUp() throws Exception {
        maindir = "F:/ChemDatabases/EINECS/ChemIDplus/";
    }

    @After
    public void tearDown() throws Exception {
    }
    //
    public void readFiles(String dir) throws Exception {
        File[] files = new File(dir).listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                String name = pathname.getName().toLowerCase();
                return name.endsWith(".sdf") || name.endsWith(".mol");
            }
        });
        if (files == null) throw new Exception("Files not found");
        for (File file: files) {
            
            MDLV2000Reader reader = new MDLV2000Reader(new FileInputStream(file));
            IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
            try {
                reader.read(mol);
            } catch (Exception x) {
                System.out.println(file);
            }
            reader.close();
        }
    }
    
    public void test() throws Exception {
        String[] dirs = {

                "einecs_structures_V13Apr07_cas_Checked_by_ECB_NOPUBCHEM_ChemIDplus",
                "einecs_structures_V13Apr07_cas_DSSTOX_NOPUBCHEM_ChemIDplus",
                "einecs_structures_V13Apr07_cas_High_NOPUBCHEM_ChemIDplus",
                "einecs_structures_V13Apr07_cas_LMC_NOPUBCHEM",
                "einecs_structures_V13Apr07_cas_Low_NOPUBCHEM_ChemIDplus",
                "einecs_structures_V13Apr07_cas_Medium_NOPUBCHEM",
                "einecs_structures_V13Apr07_cas_N_A_formula_NOPUBCHEM_ChemIDplus"
                
        };
        for (String dir: dirs)
            readFiles(maindir+dir+"/"+dir);
    }

}
