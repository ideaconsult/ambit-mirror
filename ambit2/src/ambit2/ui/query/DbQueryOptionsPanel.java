package ambit2.ui.query;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import ambit2.applications.dbadmin.AmbitDatabase;
import ambit2.data.IDataContainers;
import ambit2.database.data.AmbitDatabaseToolsData;
import ambit2.database.query.DescriptorQueryList;
import ambit2.database.query.ExperimentConditionsQuery;
import ambit2.database.query.ExperimentQuery;
import ambit2.ui.UITools;

import com.l2fprod.common.demo.ButtonBarMain;
import com.l2fprod.common.swing.JButtonBar;

public class DbQueryOptionsPanel extends JPanel implements Observer {
	protected static JFrame self = null;
	 private Component currentComponent;
	protected IDataContainers data;
	protected ExperimentsQueryPanel experimentsPanel;
	protected DescriptorQueryPanel descriptorsPanel;
	
	public DbQueryOptionsPanel(AmbitDatabaseToolsData data) {
		super();
		this.data = data;
		addWidgets(data);
	}
	public void addWidgets(AmbitDatabaseToolsData data) {
		data.addObserver(this);
		JButtonBar toolbar = new JButtonBar(JButtonBar.VERTICAL);
		setLayout(new BorderLayout());
		add(toolbar,BorderLayout.WEST);
		
		descriptorsPanel = new DescriptorQueryPanel(
				(DescriptorQueryList) data.getDescriptors(),data.getDescriptorActions(),false);
		
		
		experimentsPanel = new ExperimentsQueryPanel(
				data.getExperiments(),data.getStudyConditions(),data.getExperimentsActions());        

		StructureQueryPanel structurePanel  = new StructureQueryPanel(data.getQueries(),data.getStructureActions());
		
		ButtonGroup group = new ButtonGroup();
		
		addButton("Structure",UITools.createImageIcon("ambit2/ui/images/benzene_32x32.jpg") 
				//new ImageIcon(ButtonBarMain.class.getResource("icons/propertysheet32x32.png")),
				,structurePanel, toolbar, group);
	    addButton("Descriptors",new ImageIcon(ButtonBarMain.class.getResource("icons/folder32x32.png")) 
	    		,descriptorsPanel, toolbar, group);
	    addButton("Experiments", UITools.createImageIcon("ambit2/ui/images/experiment.png"),experimentsPanel, toolbar, group);
	    
		
	}
    private void addButton(String title,Icon icon,
    	      final Component component, JButtonBar bar, ButtonGroup group) {
    	      Action action = new AbstractAction(title, icon) {
    	        public void actionPerformed(ActionEvent e) {
    	          show(component);
    	        }
    	      };

    	      JToggleButton button = new JToggleButton(action);
    	      bar.add(button);

    	      group.add(button);

    	      if (group.getSelection() == null) {
    	        button.setSelected(true);
    	        show(component);
    	      }
    	    }
    	  
		private void show(Component component) {
		    if (currentComponent != null) {
		      remove(currentComponent);
		    }
		    add("Center", currentComponent = component);
		    revalidate();
		    repaint();
		  }
	public static JFrame  getDbQueryOptions(AmbitDatabaseToolsData data)  {
		if (self == null) {
			self = new JFrame("Structure, descriptors and experimental data search");
			self.getContentPane().setLayout(new BorderLayout());
			self.getContentPane().add(new DbQueryOptionsPanel(data),BorderLayout.CENTER);
			self.pack();
			AmbitDatabase.centerScreen(self);
			self.setVisible(true);
			
		}
		return self;
	}
	public void update(Observable arg0, Object arg1) {
		if ((arg0 instanceof AmbitDatabaseToolsData) && (arg1 != null) && (arg1 instanceof ExperimentQuery)) {
		    ExperimentConditionsQuery c = ((AmbitDatabaseToolsData) arg0).getStudyConditions();
			experimentsPanel.setExperiments((ExperimentQuery) arg1,c);
		}	
		if ((arg0 instanceof AmbitDatabaseToolsData) && (arg1 != null) && (arg1 instanceof DescriptorQueryList))
			descriptorsPanel.setDescriptors((DescriptorQueryList) arg1);		
	}
}
