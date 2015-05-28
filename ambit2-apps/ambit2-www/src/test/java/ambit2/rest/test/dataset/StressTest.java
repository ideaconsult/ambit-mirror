package ambit2.rest.test.dataset;

import java.net.URL;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;
import net.idea.opentox.cli.structure.CompoundClient;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Before;
import org.junit.Test;
import org.opentox.dsl.task.RemoteTask;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;

import ambit2.rest.ChemicalMediaType;
import ambit2.rest.test.ProtectedResourceTest;

public class StressTest extends ProtectedResourceTest {
    static long timeout = 500000;

    @Before
    public void setUp() throws Exception {
	super.setUp();
	Logger tempLogger = logger;
	Level level = Level.INFO;
	while (tempLogger != null) {
	    tempLogger.setLevel(level);
	    for (Handler handler : tempLogger.getHandlers())
		handler.setLevel(level);
	    tempLogger = tempLogger.getParent();
	}
    }

    @Override
    public String getTestURI() {
	return String.format("http://localhost:%d/dataset", port);
    }

    @Test
    public void test() throws Exception {
	int nt = 1000;
	MyThread[] threads = new MyThread[nt];
	for (int i = 0; i < nt; i++)
	    try {
		threads[i] = new MyThread(String.format("thread-%d", i), 1000);
		threads[i].start();
	    } catch (Exception x) {
		logger.log(Level.SEVERE, "loop", x);
	    }
	logger.log(Level.INFO, "Waiting threads to join");
	for (int i = 0; i < nt; i++)
	    try {
		try {
		    threads[i].join();
		    logger.log(Level.INFO, threads[i].toString());
		} catch (InterruptedException x) {
		    logger.log(Level.SEVERE, "interrupted", x);
		}
	    } catch (Exception x) {
		logger.log(Level.SEVERE, "join loop", x);
	    }
	logger.log(Level.INFO, "completed");
	long time = 0;
	String s = "";
	for (int i = 0; i < nt; i++) {
	    time += threads[i].time;
	    s += "\n" + threads[i].time;
	    Assert.assertNull(threads[i].getName(), threads[i].error);
	    Assert.assertNotNull(threads[i].getName(), threads[i].dataset);
	    Assert.assertNotNull(threads[i].getName(), threads[i].calculated);
	}
	logger.log(Level.INFO, s);
	logger.log(Level.INFO, "Average time per thread " + Long.toString(time / nt) + " ms.");

	IDatabaseConnection c = getConnection();
	ITable table = c.createQueryTable("EXPECTED", "SELECT id_srcdataset FROM src_dataset");
	Assert.assertEquals(nt + 3, table.getRowCount());
	c.close();

    }

    class MyThread extends Thread {
	public long sleep;
	public long time;
	public Reference dataset;
	public Reference calculated;
	public Exception error = null;
	protected Logger threadLogger;
	protected Method method;
	protected CompoundClient cli;
	
	public MyThread(String name, long sleep, Method method) {
	    super(name);
	    threadLogger = Logger.getLogger(name);
	    this.sleep = sleep;
	    this.method = method;
	}

	public MyThread(String name, long sleep) {
	    this(name, sleep, Method.POST);
	}

	@Override
	public void run() {
	    time = System.currentTimeMillis();
	    if (Method.GET.equals(method)) {
		try {
		    
		} catch (Exception x) {
		    error = x;
		    threadLogger.log(Level.SEVERE, this.getName(), x);
		} finally {
		    time = System.currentTimeMillis() - time;
		}
	    } else
		try {
		    dataset = createEntryFromFile();
		    if (dataset != null)
			threadLogger.info(dataset.toString());

		    Form form = new Form();
		    form.add("dataset_uri", dataset.toString());
		    RemoteTask task = new RemoteTask(new Reference(String.format("http://localhost:%d/model/%d", port,
			    Math.random() > 0.5 ? 1 : 2)), MediaType.TEXT_URI_LIST, form.getWebRepresentation(),
			    Method.POST);
		    threadLogger.info("Model " + task.getUrl());
		    while (!task.poll()) {
			Thread.yield();
			Thread.sleep(sleep);
			if ((System.currentTimeMillis() - time) > timeout) {
			    threadLogger.log(Level.INFO, "50s timeout expired", task);
			    calculated = null;
			    return;
			} else
			    threadLogger.log(Level.INFO, "poll model task", task);
		    }
		    if (task.isERROR())
			throw task.getError();
		    Assert.assertEquals(Status.SUCCESS_OK, task.getStatus());
		    calculated = task.getResult();
		} catch (Exception x) {
		    error = x;
		    threadLogger.log(Level.SEVERE, this.getName(), x);
		} finally {
		    time = System.currentTimeMillis() - time;
		}
	}

	public Reference createEntryFromFile() throws Exception {

	    IDatabaseConnection c = getConnection();
	    ITable table = c.createQueryTable("EXPECTED", "SELECT * FROM structure");
	    Assert.assertEquals(5, table.getRowCount());
	    c.close();

	    URL url = getClass().getClassLoader().getResource("test.sdf");
	    FileRepresentation rep = new FileRepresentation(url.getFile(), ChemicalMediaType.CHEMICAL_MDLSDF, 0);
	    RemoteTask task = new RemoteTask(new Reference(getTestURI()), ChemicalMediaType.CHEMICAL_MDLSDF, rep,
		    Method.POST);

	    long t = System.currentTimeMillis();
	    while (!task.poll()) {
		Thread.yield();
		Thread.sleep(sleep);

		if ((System.currentTimeMillis() - t) > timeout) {
		    threadLogger.log(Level.INFO, "50s timeout expired (dataset poll)", task);
		    return dataset;
		} else
		    threadLogger.log(Level.INFO, "poll dataset task ", task);
	    }
	    if (task.isERROR())
		throw task.getError();
	    Assert.assertEquals(Status.SUCCESS_OK, task.getStatus());
	    return task.getResult();

	}

	public String toString() {
	    return String.format("%s\t%d ms\t%s\t%s", getName(), time, dataset, calculated);
	};

    }
}
