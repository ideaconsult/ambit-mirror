package ambit.test.database.readers;

import java.awt.Dimension;
import java.io.FileOutputStream;

import javax.swing.JOptionPane;

import junit.framework.TestCase;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.MDLWriter;

import ambit.database.DbConnection;
import ambit.database.core.DbDescriptors;
import ambit.database.processors.ReadAliasProcessor;
import ambit.database.processors.ReadCASProcessor;
import ambit.database.processors.ReadNameProcessor;
import ambit.database.processors.ReadSMILESProcessor;
import ambit.database.processors.ReadSubstanceProcessor;
import ambit.database.query.DescriptorQueryList;
import ambit.database.search.DbSearchDescriptors;
import ambit.io.batch.DefaultBatchProcessing;
import ambit.io.batch.EmptyBatchConfig;
import ambit.io.batch.IBatchStatistics;
import ambit.processors.DefaultAmbitProcessor;
import ambit.processors.IAmbitProcessor;
import ambit.processors.ProcessorsChain;
import ambit.test.ITestDB;
import ambit.ui.query.DescriptorQueryPanel;

/**
 * JUnit test for {@link ambit.database.search.DbSearchDescriptors}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 2, 2006
 */
public class DbSearchDescriptorsTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(DbSearchDescriptorsTest.class);
	}

	public void testDescriptorsSearch() {
		try {
			DbConnection conn = new DbConnection(ITestDB.host,ITestDB.port,ITestDB.database,ITestDB.user,"");
			conn.open(true);
			
			//get descriptors list from database
			DbDescriptors dbd = new DbDescriptors(conn);
			dbd.initialize();
			dbd.initializeInsert();
			DescriptorQueryList descriptors = new DescriptorQueryList();
			dbd.loadQuery(descriptors);
			dbd.close();
			
			dbd = null;
			
			descriptors.getDescriptorQuery(0).setEnabled(true);
			descriptors.getDescriptorQuery(1).setEnabled(true);
			
			//Launch visualisation of the list

			
			DescriptorQueryPanel panel = new DescriptorQueryPanel(descriptors,null,false);
			panel.setPreferredSize(new Dimension(600,300));
			if (JOptionPane.showConfirmDialog(null,panel,"Descriptors",JOptionPane.YES_NO_OPTION)   == JOptionPane.NO_OPTION)
			    return;
			   
			DbSearchDescriptors reader = new DbSearchDescriptors(
					conn,
					descriptors,
					null,
					0,100
					);
			IChemObjectWriter writer = new MDLWriter(new FileOutputStream("data/misc/DbSearchDescriptorsTest.sdf"));
			IAmbitProcessor processor = new DefaultAmbitProcessor();
			DefaultBatchProcessing batch = new DefaultBatchProcessing(
					reader,
					writer,
					processor,
					new EmptyBatchConfig());
			batch.start();
			long rr = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_READ);
			long rp = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_PROCESSED);
			long rw = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_WRITTEN);

			long re = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_ERROR);
			assertEquals(100,rr);
			assertEquals(100,rp);
			assertTrue(rw == rr);
			assertTrue(rw >0);
			
			conn.close();
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
		
	}
	public void test() {
	    try {
	        DbConnection conn = new DbConnection(ITestDB.host,ITestDB.port,ITestDB.database,ITestDB.user,"");
			conn.open(true);
	        DbDescriptors dbd = new DbDescriptors(conn);
			dbd.initialize();
			dbd.initializeInsert();
			DescriptorQueryList descriptors = new DescriptorQueryList();
			dbd.loadQuery(descriptors);
			
//			enable only the first descriptor
			descriptors.getDescriptorQuery(0).setEnabled(true);
			descriptors.getDescriptorQuery(0).setCondition("between");
			descriptors.getDescriptorQuery(0).setMinValue(2);
			descriptors.getDescriptorQuery(0).setMaxValue(3);			
			//Launch visualisation of the list
			
			DescriptorQueryPanel panel = new DescriptorQueryPanel(descriptors,null,false);
			panel.setPreferredSize(new Dimension(600,300));
			if (JOptionPane.showConfirmDialog(null,panel,"Descriptors",JOptionPane.YES_NO_OPTION)   == JOptionPane.NO_OPTION)
			    return;

			DbSearchDescriptors reader = new DbSearchDescriptors(
					conn,
					descriptors,
					null,
					0,10
					);

			ProcessorsChain processors = new ProcessorsChain();
			processors.add(new ReadSubstanceProcessor(conn.getConn()));
			processors.add(new ReadCASProcessor(conn.getConn()));
			processors.add(new ReadSMILESProcessor(conn.getConn()));
			processors.add(new ReadNameProcessor(conn.getConn()));
			processors.add(new ReadAliasProcessor(conn.getConn()));
			
			while (reader.hasNext()) {
			    Object object = processors.process(reader.next());
			    System.out.println(((IChemObject) object).getProperties());
			}
			reader.close();
			
			conn.close();
	    } catch (Exception x) {
			x.printStackTrace();
			fail();
		}
	}
}
