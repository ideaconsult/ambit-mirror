package ambit.ui.query;

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

import ambit.applications.dbadmin.AmbitDatabase;
import ambit.database.data.AmbitDatabaseToolsData;
import ambit.database.query.DescriptorQueryList;
import ambit.database.query.ExperimentConditionsQuery;
import ambit.database.query.ExperimentQuery;
import ambit.ui.UITools;

import com.l2fprod.common.demo.ButtonBarMain;
import com.l2fprod.common.swing.JButtonBar;

public class DbQueryOptionsPanel extends JPanel implements Observer {
	protected static JFrame self = null;
	 private Component currentComponent;
	protected AmbitDatabaseToolsData data;
	protected ExperimentsQueryPanel experimentsPanel;
	protected DescriptorQueryPanel descriptorsPanel;
	protected SimilarityOptions optionsPanel;
	
	public DbQueryOptionsPanel(AmbitDatabaseToolsData data) {
		super();
		this.data = data;
		addWidgets(data);
	}
	public void addWidgets(final AmbitDatabaseToolsData data) {
		data.addObserver(this);
		JButtonBar toolbar = new JButtonBar(JButtonBar.VERTICAL);
		setLayout(new BorderLayout());
		add(toolbar,BorderLayout.WEST);
		
		descriptorsPanel = new DescriptorQueryPanel(
				(DescriptorQueryList) data.getDescriptors(),data.getDescriptorActions(),false);
		
		
		experimentsPanel = new ExperimentsQueryPanel(
				data.getExperiments(),data.getStudyConditions(),data.getExperimentsActions());        

		StructureQueryPanel structurePanel  = new StructureQueryPanel(data.getQueries(),data.getStructureActions());
		
		
		DbSmartsQueryPanel fragmentsEditor =  new DbSmartsQueryPanel(data.getFragments(),data.getSMARTSActions());
		
		ButtonGroup group = new ButtonGroup();
		
		addButton("Structure",UITools.createImageIcon("ambit/ui/images/benzene_32x32.jpg") 
				//new ImageIcon(ButtonBarMain.class.getResource("icons/propertysheet32x32.png")),
				,structurePanel, toolbar, group);
	    addButton("Descriptors",new ImageIcon(ButtonBarMain.class.getResource("icons/folder32x32.png")) 
	    		,descriptorsPanel, toolbar, group);
	    addButton("Experiments", UITools.createImageIcon("ambit/ui/images/experiment.png"),experimentsPanel, toolbar, group);
	    addButton("SMARTS",UITools.createImageIcon("ambit/ui/images/smarts.png"),
	    		fragmentsEditor, toolbar, group);	    

	    optionsPanel = new SimilarityOptions(data);
	    /*
	    JPanel p = new JPanel();
	    p.setLayout(new BorderLayout());
	    p.add(optionsPanel,BorderLayout.CENTER);
	    JButton b = new JButton(new AbstractAction("Apply") {
	    	public void actionPerformed(ActionEvent e) {
	    		
	    	}
	    });
	    b.setMaximumSize(new Dimension(48,32));
    	b.setMinimumSize(new Dimension(32,24));
    	b.setPreferredSize(new Dimension(48,32));
	    p.add(b,BorderLayout.SOUTH);
	    */
	    addButton("Options",UITools.createImageIcon("ambit/ui/images/cog.png"),
	    		optionsPanel, toolbar, group);	   	    
		
	}
	protected void updateOptions() {
		data.setTMPFile(optionsPanel.getTMPFile());
        data.setSimilarityMethod(optionsPanel.getMethod());
        data.setSimilarityThreshold(optionsPanel.getThreshold());
        data.setPageSize(optionsPanel.getMaxResults());
        data.setPage(optionsPanel.getPage());
        data.setAquire_endpoint(optionsPanel.getEndpoint());
        data.setAquire_endpoint_description(optionsPanel.getEndpointDescription());
        data.setAquire_species(optionsPanel.getSpecies());
        data.setResultDestination(optionsPanel.getDestination());
        data.setSource(optionsPanel.getSource());
        data.setAquire_simpletemplate(optionsPanel.isUseSimpletemplate());		
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
		      if (currentComponent == optionsPanel)
		    	  updateOptions();
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
