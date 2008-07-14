package ambit.ui.query;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import ambit.database.aquire.AquireOptions;
import ambit.database.data.AmbitDatabaseToolsData;
import ambit.database.data.ISharedDbData;
import ambit.database.readers.SearchFactory;
import ambit.io.MyIOUtilities;
import ambit.ui.SpringUtilities;

public class SimilarityOptions extends JTabbedPane {
    int[] methods = {SearchFactory.MODE_FINGERPRINT,SearchFactory.MODE_ATOMENVIRONMENT};    
    protected JRadioButton method[];
    protected JFormattedTextField threshold;
    protected JFormattedTextField pagesize;
    protected JFormattedTextField page;
    protected JFormattedTextField tmpFile;
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
        public SimilarityOptions(final AmbitDatabaseToolsData dbaData) {
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
        destinationBox.setMaximumSize(d);
        genericPanel.add(destinationBox);
        
        JPanel filePanel = new JPanel(new BorderLayout());
        tmpFile = new JFormattedTextField();
        tmpFile.setValue(dbaData.getTMPFile());
        tmpFile.setMaximumSize(d);
        tmpFile.setPreferredSize(d);
        final JButton browse = new JButton("Browse");
        browse.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		File file = MyIOUtilities.selectFile(browse, "Select file", 
        				dbaData.getDefaultDir(),
        				new String[] {".txt",".csv",".xls",".sdf"},
        				new String[]{
        			"Tab delimited txt files (*.txt)",
        			"Comma delimited txt files (*.csv)",
        			"XLS files (*.xls)",
        			"SDF files (*.sdf)"},
        				false);
        		if (file != null)
        			tmpFile.setText(file.getAbsolutePath());
        	}
        });
        filePanel.setPreferredSize(d);
        filePanel.setMaximumSize(d);        
        filePanel.add(tmpFile,BorderLayout.CENTER);
        filePanel.add(browse,BorderLayout.EAST);
        
        genericPanel.add(new JLabel(ISharedDbData.RESULTS_DESTINATION[ISharedDbData.RESULTS_QUERYFILE]));
        genericPanel.add(filePanel);        
        
        Dimension d1 = new Dimension(Integer.MAX_VALUE,24*4);
        genericPanel.setMaximumSize(d1);
        genericPanel.setPreferredSize(d1);        
        SpringUtilities.makeCompactGrid(genericPanel, 4,2,5,5,2,2);
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
        aquireOptions.setMaximumSize(d1);
        aquireOptions.setPreferredSize(d1);         
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
    public String getTMPFile() {
        return tmpFile.getText();
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