/*
 * Created on 2005-12-18
 *
 */
package ambit2.ui.actions.search;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import ambit2.database.aquire.AquireOptions;
import ambit2.database.data.AmbitDatabaseToolsData;
import ambit2.database.data.ISharedDbData;
import ambit2.database.readers.SearchFactory;
import ambit2.ui.SpringUtilities;
import ambit2.ui.UITools;
import ambit2.ui.actions.AmbitAction;

/**
 * Sets the similarity method to use. See example for {@link ambit2.database.data.AmbitDatabaseToolsData}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2005-12-18
 */
public class QueryOptionsAction extends AmbitAction implements Observer{

    /**
     * @param userData
     * @param mainFrame
     */
    public QueryOptionsAction(Object userData, JFrame mainFrame) {
        this(userData, mainFrame,"Options");
    }

    /**
     * @param userData
     * @param mainFrame
     * @param arg0
     */
    public QueryOptionsAction(Object userData, JFrame mainFrame, String arg0) {
        this(userData, mainFrame, arg0,UITools.createImageIcon("ambit2/ui/images/search.png"));
    }

    /**
     * @param userData
     * @param mainFrame
     * @param arg0
     * @param arg1
     */
    public QueryOptionsAction(Object userData, JFrame mainFrame,
            String arg0, Icon arg1) {
        super(userData, mainFrame, arg0, arg1);
        putValue(SHORT_DESCRIPTION, "Various options influencing search results");
        if (userData instanceof Observable)
        	update(((Observable) userData),null);        
    }
    /* (non-Javadoc)
     * @see ambit2.ui.actions.AmbitAction#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
    	
		if (userData instanceof AmbitDatabaseToolsData) {
			AmbitDatabaseToolsData dbaData = ((AmbitDatabaseToolsData) userData);
            SimilarityOptions options = new SimilarityOptions(dbaData);
            if (JOptionPane.showConfirmDialog(mainFrame,options,"Search options",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
                dbaData.setSimilarityMethod(options.getMethod());
                dbaData.setSimilarityThreshold(options.getThreshold());
                dbaData.setPageSize(options.getMaxResults());
                dbaData.setPage(options.getPage());
                dbaData.setAquire_endpoint(options.getEndpoint());
                dbaData.setAquire_endpoint_description(options.getEndpointDescription());
                dbaData.setAquire_species(options.getSpecies());
                dbaData.setResultDestination(options.getDestination());
                dbaData.setSource(options.getSource());
                dbaData.setAquire_simpletemplate(options.isUseSimpletemplate());
                //putValue(NAME,"Similarity method: " + options.getMethod());
            }
            options = null;
			/*
			String s = (String)JOptionPane.showInputDialog(
			                    mainFrame,"Options",
			                    "Select similarity method",
			                    JOptionPane.PLAIN_MESSAGE,
			                    null,
			                    possibilities,
			                    dbaData.getSimilarityMethod());
	
	//		If a string was returned, say so.
			if ((s != null) && (s.length() > 0)) {
					dbaData.setSimilarityMethod(s);
					putValue(NAME,"Similarity method: " + s);
			}
            */		
		}			
		

    }
    public void update(Observable arg0, Object arg1) {
    	if (arg0 instanceof AmbitDatabaseToolsData ) {
   			//putValue(NAME,"Similarity method: " + ((AmbitDatabaseToolsData) arg0).getSimilarityMethod() );
   			//putValue(SHORT_DESCRIPTION,"Access similarity by " + ((AmbitDatabaseToolsData) arg0).getSimilarityMethod());
    	}
    	
    }   
}

class SimilarityOptions extends JTabbedPane {
    int[] methods = {SearchFactory.MODE_FINGERPRINT,SearchFactory.MODE_ATOMENVIRONMENT};    
    protected JRadioButton method[];
    protected JFormattedTextField threshold;
    protected JFormattedTextField pagesize;
    protected JFormattedTextField page;
    protected AquireOptions aquireOptions;
    protected JComboBox destinationBox;
    protected JComboBox sourceBox;
    protected int selectedMethod;
    protected NumberFormat nf ;
    /*
    SimilarityOptions options = new SimilarityOptions(dbaData.getSimilarityMethod(),
            dbaData.getSimilarityThreshold(),
            dbaData.getPageSize(),
            dbaData.getPage(),
            dbaData.getAquire_endpoint_description(),
            dbaData.getAquire_species(),
            dbaData.isAquire_simpletemplate(),
            dbaData.getResultDestination(),
            dbaData.getSource());
            
        	similarityMethod, 
        	double similarityThreshold, 
        	int maxresults, 
        	int startpage, 
    			String endpointDescription, 
    			String species,
    			boolean useSimpletemplate, 
    			int destination, 
    			int source
    			
endpointDescription,species,useSimpletemplate    			            
    */
        public SimilarityOptions(AmbitDatabaseToolsData dbaData) {
        super();
        
        nf = new DecimalFormat("0.0##");//DecimalFormat.getInstance();
        nf.setMaximumFractionDigits(3);
        
        NumberFormat intf = DecimalFormat.getIntegerInstance();
        System.out.println(nf);
        JPanel genericPanel = new JPanel();
        Dimension d = new Dimension(Integer.MAX_VALUE,24);
        SpringLayout sl = new SpringLayout();
        genericPanel.setLayout(sl);
        page = new JFormattedTextField(intf);
        page.setValue(dbaData.getPage()+1);
        page.setMaximumSize(d);
        page.setPreferredSize(d);
        genericPanel.add(new JLabel("Display results starting from page"));
        genericPanel.add(page);
        
        pagesize = new JFormattedTextField(intf);
        pagesize.setValue(dbaData.getPageSize());
        pagesize.setMaximumSize(d);
        pagesize.setPreferredSize(d);
        genericPanel.add(new JLabel("Maximum number of hits (page size)"));
        genericPanel.add(pagesize);
        
        genericPanel.add(new JLabel("Select results destination"));
        destinationBox = new JComboBox(ISharedDbData.RESULTS_DESTINATION);
        destinationBox.setSelectedItem(ISharedDbData.RESULTS_DESTINATION[dbaData.getResultDestination()]);
        destinationBox.setPreferredSize(d);
        genericPanel.add(destinationBox);
        SpringUtilities.makeCompactGrid(genericPanel, 3,2,5,5,2,2);
        addTab("General",genericPanel);
        
        
        JPanel similarityPanel = new JPanel();
        sl = new SpringLayout();
        
        
        
        JPanel mpanel = new JPanel();
        Dimension md = new Dimension(Integer.MAX_VALUE,18*methods.length);
        mpanel.setLayout(new GridLayout(methods.length,1));
        ButtonGroup bg = new ButtonGroup();
        method = new JRadioButton[methods.length];
        for (int i=0; i < methods.length;i++) {
        	method[i] = new JRadioButton(new AbstractAction(SearchFactory.title[methods[i]]) {
        		public void actionPerformed(ActionEvent arg0) {
        			selectedMethod = SearchFactory.getSimilarityByTitle(getValue(NAME).toString());
        			
        		}
        	});
        	bg.add(method[i]);
        	mpanel.add(method[i]);
        	if (methods[i] == dbaData.getSimilarityMethod()) { 
        		method[i].setSelected(true);
        		selectedMethod = methods[i];
        	}	
        		
        }
        
        mpanel.setPreferredSize(md);
        mpanel.setMaximumSize(md);
        threshold = new JFormattedTextField(nf);
        threshold.setValue(dbaData.getSimilarityThreshold());
        
        threshold.setPreferredSize(d);
        threshold.setMaximumSize(d);        
        similarityPanel.setLayout(sl);
        similarityPanel.add(new JLabel("Similarity method"));
        similarityPanel.add(mpanel);
        similarityPanel.add(new JLabel("Similarity threshold"));
        similarityPanel.add(threshold);
        SpringUtilities.makeCompactGrid(similarityPanel, 2,2,5,5,2,2);
        addTab("Similarity",similarityPanel);
        
        aquireOptions = new AquireOptions(null,dbaData);
        addTab("AQUIRE",aquireOptions);
        
        JPanel sourcePanel = new JPanel();
        sl = new SpringLayout();
        sourcePanel.setLayout(sl);
        sourceBox = new JComboBox(ISharedDbData.SOURCE);

        sourceBox.setMaximumSize(d);
        sourceBox.setPreferredSize(d);
        sourceBox.setSelectedItem(ISharedDbData.SOURCE[dbaData.getSource()]);
        sourcePanel.add(new JLabel("Structures to process"));
        sourcePanel.add(sourceBox);
        
        SpringUtilities.makeCompactGrid(sourcePanel, 1,2,5,5,2,2);
        addTab("Molecule processing",sourcePanel);
    }
    public String getEndpoint() {
        return aquireOptions.getEndpoint();
    }
    public String getEndpointDescription() {
        return aquireOptions.getEndpointDescription();
    }    
    public String getSpecies() {
        return aquireOptions.getSpecies();
    }
    public int getMethod() {
        return selectedMethod;
    }
    public double getThreshold() {
        try {
            
            Number n = (Number)threshold.getValue();
            return n.doubleValue();

        } catch (Exception x) {
        	x.printStackTrace();
            return 0.5;
        }
    }
    public int getMaxResults() {
        try {
        	return ((Number)pagesize.getValue()).intValue();
        } catch (Exception x) {
            return 1000;
        }
    }
    public int getPage() {
        try {
            int p = ((Number)page.getValue()).intValue()-1;
            if (p <0) return 0; else return p;
        } catch (Exception x) {
            return 0;
        }
    }    
    public int getDestination() {
    	int d = destinationBox.getSelectedIndex();
    	if (d < 0) return ISharedDbData.MEMORY_LIST;
    	else return d;
    }
    public int getSource() {
    	int d = sourceBox.getSelectedIndex();
    	if (d < 0) return ISharedDbData.MEMORY_CURRENT;
    	else return d;
    }    
    public boolean isUseSimpletemplate() {
    	return aquireOptions.isUseSimpletemplate();
    }
}