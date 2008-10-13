package ambit2.plugin.pbt;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PBTTableModelTest {
	@Test
	public void test() throws Exception  {
		PBTTableModel model = new PBTTableModel();
		model.setDefinition("ambit2/plugin/pbt/xml/p_page.xml");
		assertEquals(6,model.getColumnCount());
		assertEquals(19,model.getRowCount());
		
		for (int i=0;i < model.getRowCount();i++) {
			System.out.print(i);
			System.out.print('\t');
			for (int j=0;j < model.getColumnCount();j++) {
				System.out.print('\'');
				System.out.print(model.getValueAt(i,j));
				System.out.print('\'');
				System.out.print('\t');
			}
		System.out.println();
		}
		
		assertEquals("SECTION BIODEGRADATION",model.getValueAt(3,1));
		assertEquals("REPORT REFERENCE",model.getValueAt(6,6));
		assertEquals("READY TESTS (Seawater)",model.getValueAt(6,1));
		
		
	}
}
