/* ServletsTest.java
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

package ambit2.test.servlets;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import junit.framework.TestCase;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.MFAnalyser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.database.AmbitID;
import ambit2.database.ConnectionPool;
import ambit2.ui.data.CompoundImageTools;
import ambit2.database.data.DbCompoundImageTools;
import ambit2.database.readers.SearchFactory;
import ambit2.database.search.DbSearchReader;
import ambit2.io.HTMLTableWriter;
import ambit2.io.batch.IBatch;
import ambit2.io.batch.IBatchStatistics;
import ambit2.test.ITestDB;

public class ServletsTest extends TestCase {

    
    public void setUp() throws Exception {
    }

    
    public void tearDown() throws Exception {
    }
    public void testImage() {
        CompoundImageTools tools = new CompoundImageTools();
        tools.setImageSize(new Dimension(200,200));
        tools.setBackground(Color.white);
        BufferedImage buffer = tools.getImage("c1ccccc1");
        try {
            FileOutputStream os = new FileOutputStream("test.png");
            ImageIO.write(buffer, "png", os);
            os.flush();
            os.close();
            assertTrue(new File("test.png").exists());
        } catch (Exception x) {
            
        }
    }
    public void testImageDB() {
        DbCompoundImageTools tools = new DbCompoundImageTools();
        tools.setImageSize(new Dimension(200,200));
        tools.setBackground(Color.white);
        try {
            
            ConnectionPool pool = new ConnectionPool(
                    ITestDB.host,ITestDB.port,ITestDB.database,ITestDB.user,"",1,1);
            Connection conn = pool.getConnection();        
            BufferedImage buffer = tools.getImage(conn,new AmbitID(1,1));

            pool.returnConnection(conn);
            conn.close();
            FileOutputStream os = new FileOutputStream("test.png");
            ImageIO.write(buffer, "png", os);
            os.flush();
            os.close();
            assertTrue(new File("test.png").exists());
        } catch (Exception x) {
            
        }
    }    
    public void testFingerprints() {
        search(SearchFactory.MODE_FINGERPRINT);
    }
    public void testAE() {
        search(SearchFactory.MODE_ATOMENVIRONMENT);
    }
    public void testSubstructure() {
        search(SearchFactory.MODE_SUBSTRUCTURE);
    }        
    public void search(int mode) {
        try {
            long now = System.currentTimeMillis();
            ConnectionPool pool = new ConnectionPool(
                    ITestDB.host,ITestDB.port,ITestDB.database,ITestDB.user,"",1,1);
            Connection conn = pool.getConnection();
            IAtomContainer mol = MoleculeFactory.makeAlkane(3);

			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
			CDKHueckelAromaticityDetector.detectAromaticity(mol);            
            
            MFAnalyser mfa = new MFAnalyser(mol);
            mol = mfa.removeHydrogensPreserveMultiplyBonded();
            System.err.println((System.currentTimeMillis()-now)/1000.0 + " sec.");
            
            
            //DefaultChemObjectWriter writer = new DelimitedFileWriter(System.out);
     
            HTMLTableWriter writer = new HTMLTableWriter(new FileOutputStream("test.html"));
            ArrayList<String> header = new ArrayList<String>();
            header.add(CDKConstants.CASRN);
            header.add("ChemName_IUPAC");
            header.add("INChI");
            header.add(CDKConstants.NAMES);
            
            writer.setHeader(header);
            
            IBatch batch = SearchFactory.createSearchBatch(mode, 
                    conn,
                    mol,
                    null,
                    0,
                    0,
                    100,
                    writer);
            header.add(((DbSearchReader)batch.getInput()).getSimilarityLabel());
            System.out.println(batch.toString());
            batch.start();
            assertTrue(batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_WRITTEN)>0);
            
            pool.returnConnection(conn);
            conn.close();
            System.err.println((System.currentTimeMillis()-now)/1000.0 + " sec.");
            
            System.out.println(batch.getBatchStatistics().toString());
        } catch (Exception x) {
            x.printStackTrace();
            fail();
        }

    }
    
    /**
     * http://www.stardeveloper.com/articles/display.html?article=2001081301&page=1
     */


}
