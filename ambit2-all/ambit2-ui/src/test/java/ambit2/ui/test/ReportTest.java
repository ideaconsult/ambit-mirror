package ambit2.ui.test;


import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

public class ReportTest {

	@Before
	public void setUp() throws Exception {
	}
	
	public ReportTest() {

	}
	public void show() throws Exception {
		InputStream io;
/*
		 io = getClass().getClassLoader().getResourceAsStream("test.jrxml");
		System.out.println(io);
		JasperDesignViewer v = new JasperDesignViewer(io,true);
		v.show();		
		io.close();
		*/
		/*
		io = getClass().getClassLoader().getResourceAsStream("FirstReport.jrprint");
		JasperViewer v = new JasperViewer(io,false);
		v.show();
		io.close();
		*/		
	}
	@Test
	public void test() throws Exception {
		/*
		String file = "src/test/resources/test.jasper";
		JasperCompileManager.compileReportToFile("src/test/resources/test.jrxml",file);
		Assert.assertTrue(new File(file).exists());
		String result = JasperFillManager.fillReportToFile(file, new HashMap(), new JREmptyDataSource());		
		Assert.assertTrue(new File(result).exists());
		String pdf = "src/test/resources/test.pdf";
		JasperRunManager.runReportToPdfFile(file,pdf,
				 new HashMap(), new JREmptyDataSource());
*/

	}
	public static void main(String[] args) throws Exception {
		ReportTest t = new ReportTest();
		t.show();
	}
}
