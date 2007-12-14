package ambit.ui.data.molecule;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ambit.ui.CorePanel;

public class ActionPanel extends CorePanel {
	protected JButton[] button;
	protected JComponent resultComponent;
	protected String buttonPosition = BorderLayout.SOUTH;
	public ActionPanel(Action action, String title ) {
		super(title);
		initButtons(action);
	}
	public ActionPanel(Action action, String title ,String buttonPosition) {
		super(title);
		this.buttonPosition = buttonPosition;
		initButtons(action);
	}

	public ActionPanel(Action action,String title, Color bClr, Color fClr) {
		super(title, bClr, fClr);
		initButtons(action);
	}
	public ActionPanel(Action action, String title, Color bClr, Color fClr,String buttonPosition) {
		super(title, bClr, fClr);
		this.buttonPosition = buttonPosition;
		initButtons(action);
	}	
	public ActionPanel(ActionMap actions, String title ) {
		super(title);
		initButtons(actions);
	}
	
	protected void initButtons(Action action) {
		button = new JButton[1];
		button[0] = new JButton(action);
		add(button[0],buttonPosition);

	}
	protected void initButtons(ActionMap actions) {
		JPanel p = new JPanel(new GridLayout(1,actions.size()));
		button = new JButton[actions.size()];
		Object[] keys = actions.allKeys();
        
        for (int i=0; i < keys.length;i++) {
        	button[i] = new JButton(actions.get(keys[i]));
        	p.add(button[i]);
        }
		add(p,buttonPosition);
		setToolTipText("Use the top button to get a studyList of compounds relevant to the operation. \nUse the bottom button to perform action on the current compound (displayed at the left panel)");

	}
	

	protected void addWidgets() {
		setLayout(new BorderLayout());
		resultComponent = getResultComponent();
		add(resultComponent,BorderLayout.CENTER);
		setMinimumSize(new Dimension(48,96));
		setBorder(BorderFactory.createTitledBorder(caption));		

	}
	protected JComponent getResultComponent() {
	    return new JLabel("");
	}

}
