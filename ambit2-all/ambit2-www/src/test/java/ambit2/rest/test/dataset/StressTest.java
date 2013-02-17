package ambit2.rest.test.dataset;

import java.net.URL;
import java.util.logging.Level;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.opentox.dsl.task.RemoteTask;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;

import ambit2.rest.ChemicalMediaType;
import ambit2.rest.test.ProtectedResourceTest;

public class StressTest extends ProtectedResourceTest {

	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/dataset", port);
	}
	
	@Test
	public void test() throws Exception {
		int nt = 10;
		MyThread[] threads = new MyThread[nt];
		for (int i= 0; i < nt; i++) try {
			threads[i] = new MyThread(String.format("thread-%d",i));
			threads[i].start();
		} catch (Exception x) {
			logger.log(Level.SEVERE,"loop",x);
		}
		for (int i= 0; i < nt; i++) try {
			 try {
			       threads[i].join();
			       logger.log(Level.INFO,threads[i].toString());
			    } catch (InterruptedException x) {
			    	logger.log(Level.SEVERE,"interrupted",x);    	
			    }
		} catch (Exception x) {
			logger.log(Level.SEVERE,"join loop",x);
		}
		logger.log(Level.INFO,"completed");
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT id_srcdataset FROM src_dataset");
		Assert.assertEquals(nt+3,table.getRowCount());
		c.close();
		long time = 0;
		for (int i= 0; i < nt; i++) {
				time += threads[i].time;
		       logger.log(Level.INFO,threads[i].toString());
		}
		logger.log(Level.INFO,"Average time per thread "+Long.toString(time/nt)+ " ms.");
		
	}
	public Reference createEntryFromFile() throws Exception {
		
        IDatabaseConnection c = getConnection();	
		ITable table = 	c.createQueryTable("EXPECTED","SELECT * FROM structure");
		Assert.assertEquals(5,table.getRowCount());
		c.close();
		
		URL url = getClass().getClassLoader().getResource("input.sdf");
		FileRepresentation rep = new FileRepresentation(
				url.getFile(),
				ChemicalMediaType.CHEMICAL_MDLSDF, 0);
		RemoteTask task = new RemoteTask(new Reference(getTestURI()),ChemicalMediaType.CHEMICAL_MDLSDF, rep,Method.POST);

		while (!task.poll()) {
			Thread.yield();
			Thread.sleep(3000);
			logger.log(Level.INFO,"poll",task);
		}
		if (task.isERROR()) throw task.getError();
		Assert.assertEquals(Status.SUCCESS_OK, task.getStatus());
		return task.getResult();
		
	}	
	
	class MyThread extends Thread {
		public long time;
		public Reference dataset;

		public MyThread(String name) {
			super(name);
		}
		@Override
		public void run() {
			time = System.currentTimeMillis();
			try {
				dataset = createEntryFromFile();
				logger.info(dataset.toString());
			} catch (Exception x) {
				logger.log(Level.SEVERE,this.getName(),x);
			} finally {
				time = System.currentTimeMillis() - time;
			}
		}
		public String toString() {
			return String.format("%d ms\t%s",time,dataset);
		};

		
	}
}


