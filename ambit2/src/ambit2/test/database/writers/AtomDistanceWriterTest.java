/* AtomDistanceWriterTest.java
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

package ambit2.test.database.writers;

import java.sql.Connection;

import junit.framework.TestCase;
import ambit2.database.ConnectionPool;
import ambit2.data.AmbitUser;
import ambit2.database.readers.DbStructureDistanceReader;
import ambit2.database.writers.AtomDistanceWriter;
import ambit2.io.batch.DefaultBatchProcessing;
import ambit2.io.batch.EmptyBatchConfig;
import ambit2.io.batch.IBatchStatistics;
import ambit2.processors.structure.AtomDistanceProcessor;
import ambit2.test.ITestDB;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-9
 */
public class AtomDistanceWriterTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AtomDistanceWriterTest.class);
    }
    public void test() {
        try {
			ConnectionPool pool = new ConnectionPool(
			        ITestDB.host,ITestDB.port,ITestDB.database,"root","",1,1);
			Connection conn = pool.getConnection();
			
		    AtomDistanceProcessor processor = new AtomDistanceProcessor();
		    DbStructureDistanceReader reader;

		    AtomDistanceWriter writer = new AtomDistanceWriter(conn,new AmbitUser()); 
		    DefaultBatchProcessing batch;
		    while (true) {
		        reader = new DbStructureDistanceReader(conn,1);
		        batch = new DefaultBatchProcessing(
					reader,
					writer,
					processor,
					new EmptyBatchConfig());
		    	//BatchProcessingDialog d = new BatchProcessingDialog(batch,(Frame)null,true);
				//d.show();
		        batch.start();
		        long records = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_READ);
		        //if (records == 0) 
		            break;
		    }	
		    
        } catch (Exception x) {
            x.printStackTrace();
            fail();
            
        }
    }
}
