package ambit2.waffles.test;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import ambit2.base.external.CommandShell;
import ambit2.waffles.ShellWafflesLearn;
import ambit2.waffles.learn.options.WafflesLearnAlgorithm;

public class CommandShellTest {
	protected ShellWafflesLearn shell;
	@Before
	public void setUp() throws Exception {
		shell = new ShellWafflesLearn();
		File homeDir = new File(System.getProperty("java.io.tmpdir") +"/.ambit2/" + System.getProperty("user.name") + "/waffles/*");
		homeDir.delete();
		

	}


	@Test
	public void testAddExecutableFreeBSD() throws Exception {
		String exec = shell.getExecutable(CommandShell.os_FreeBSD, null);
		File file = new File(exec);
		Assert.assertTrue(file.exists());
	}	


	@Test
	public void testAddExecutableWin()  throws Exception {
		String exec = shell.getExecutable(CommandShell.os_WINDOWS, null);
		File file = new File(exec);
		Assert.assertTrue(file.exists());
	}

	@Test
	public void testAddExecutableLinux() throws Exception {
		String exec = shell.getExecutable(CommandShell.os_LINUX, null);
		File file = new File(exec);
		Assert.assertTrue(file.exists());
	}

	
	@Test
	public void testTrain() throws Exception {
		ShellWafflesLearn learn = new ShellWafflesLearn();
		/**
		 * Train a model
		 */
		File outputFile = File.createTempFile("waffles_", ".model");
		outputFile.deleteOnExit();
		/*
		learn.setOutputFile(null);
		learn.setOutProperty(WafflesLearnOption.model.name());
		*/
		URL dataset = getClass().getClassLoader().getResource("ambit2/waffles/learn/test/iris.arff");
		File file = new File(dataset.getFile());
		Assert.assertTrue(file.exists());
		File model = learn.train(file, outputFile, WafflesLearnAlgorithm.randomforest, "10");
		Assert.assertNotNull(model);
		
		/**
		 * Now predict
		 */
		outputFile = File.createTempFile("waffles_", ".arff");
		outputFile.deleteOnExit();
		File results = learn.predict(file, model, outputFile,"-ignore 0");
		Assert.assertNotNull(results);
	}


}

