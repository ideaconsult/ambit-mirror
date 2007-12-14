/* AtomDistanceProcessorTest.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-9 
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

package ambit.test.io.batch;

import java.awt.Frame;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;

import junit.framework.TestCase;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IteratingMDLReader;

import ambit.database.ConnectionPool;
import ambit.exceptions.AmbitIOException;
import ambit.io.FileOutputState;
import ambit.io.batch.DefaultBatchProcessing;
import ambit.io.batch.EmptyBatchConfig;
import ambit.io.batch.IBatchStatistics;
import ambit.io.batch.IJobStatus;
import ambit.processors.structure.AtomDistanceProcessor;
import ambit.test.ITestDB;
import ambit.ui.batch.BatchProcessingDialog;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-9
 */
public class AtomDistanceProcessorTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AtomDistanceProcessorTest.class);
    }
    public void testAtomDistance() {
		try {
		    
			String outputFile = "data/misc/224824_distance.sdf";
			IteratingMDLReader reader = new IteratingMDLReader(
					new FileInputStream("data/misc/224824.sdf"),
					DefaultChemObjectBuilder.getInstance());
			IChemObjectWriter writer = FileOutputState.getWriter(
					new FileOutputStream(outputFile),outputFile);
			
			ConnectionPool pool = new ConnectionPool(
			        ITestDB.host,ITestDB.port,ITestDB.database,ITestDB.user,"",1,1);
			Connection conn = pool.getConnection();
			AtomDistanceProcessor processor = new AtomDistanceProcessor();

			DefaultBatchProcessing batch = new DefaultBatchProcessing(
					reader,
					writer,
					processor,
					new EmptyBatchConfig());
			assertNotNull(batch.getInput());
			assertNotNull(batch.getOutput());
			
			BatchProcessingDialog d = new BatchProcessingDialog(batch,(Frame)null,true);
			d.show();
			//batch.start();
			assertTrue(batch.getStatus().isStatus(IJobStatus.STATUS_DONE));
			assertEquals(1, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_READ));
			assertEquals(1, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_WRITTEN));
			assertEquals(1, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_PROCESSED));
			assertEquals(0, batch.getBatchStatistics().getRecords(
					IBatchStatistics.RECORDS_ERROR));
			
			pool.returnConnection(conn);
			pool = null;
		} catch (AmbitIOException x) {
			x.printStackTrace();
			fail();			
		} catch (FileNotFoundException x) {
			x.printStackTrace();
			fail();
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
	}

}
