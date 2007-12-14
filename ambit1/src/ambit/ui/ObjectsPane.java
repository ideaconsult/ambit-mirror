package ambit.ui;

import java.awt.Dimension;

import javax.swing.JTabbedPane;

public class ObjectsPane extends JTabbedPane {

	public ObjectsPane(Dimension d) {
		super();
		setPreferredSize(d);
	}

	public ObjectsPane(Dimension d,int arg0) {
		super(arg0);
		setPreferredSize(d);
	}

	public ObjectsPane(Dimension d,int arg0, int arg1) {
		super(arg0, arg1);
		setPreferredSize(d);
	}

}
