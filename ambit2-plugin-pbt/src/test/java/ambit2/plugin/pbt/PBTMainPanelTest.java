package ambit2.plugin.pbt;

import java.awt.Dimension;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.junit.Test;


public class PBTMainPanelTest {
	@Test
	public void test() throws Exception {
  	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());		
		PBTMainPanel pbtPanel = new PBTMainPanel();
		pbtPanel.setPreferredSize(new Dimension(800,600));
		JOptionPane.showMessageDialog(null,pbtPanel,pbtPanel.toString(),JOptionPane.OK_OPTION);

	}
}
