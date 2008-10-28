package ambit2.ui.test;


import javax.swing.JOptionPane;

import org.junit.Before;
import org.junit.Test;

import ambit2.core.io.FileInputState;
import ambit2.ui.editors.FileSelector;

public class FileSelectorTest {

	@Before
	public void setUp() throws Exception {

	}
	
	@Test
	public void test() throws Exception {
		FileInputState f = new FileInputState();
		FileSelector fs = new FileSelector();
		fs.setObject(f);
		JOptionPane.showMessageDialog(null,fs);		
	}

}
