package ambit2.pubchem.test;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import ambit2.pubchem.DepictRequest;

public class DepictRequestTest {

	public static void main(String[] args) {
		if (args.length<1) {
			System.out.println("Query not defined");
			Runtime.getRuntime().exit(1);
		}
		DepictRequest depict = new DepictRequest();
		try {

			Image image = depict.process(args[0]);
			JOptionPane.showMessageDialog(null,new JLabel(new ImageIcon(image)));
		} catch (Exception x) {
			x.printStackTrace(System.err);
			Runtime.getRuntime().exit(1);
		}
	}
}
