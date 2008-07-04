package nplugins.demo;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import nplugins.shell.PluginMainPanel;

public class DemoMainPanel extends PluginMainPanel<DemoPlugin> {

	/**
     * 
     */
    private static final long serialVersionUID = 5849585238824876528L;

    public DemoMainPanel(DemoPlugin model) {
		super(model);
	}

	@Override
	protected void addWidgets() {
		add(new JScrollPane(new JTextArea(getPlugin().toString())));

	}

}
