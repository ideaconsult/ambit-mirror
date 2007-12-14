 /**
 * Created on 2005-1-18
 *
 */
package ambit.ui.domain;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ambit.data.AmbitListChanged;
import ambit.data.IAmbitListListener;
import ambit.data.AmbitObjectChanged;
import ambit.domain.QSARDataset;
import ambit.domain.QSARdatasets;


/**
 * a GUI panel for {@link ambit.applications.discovery.AmbitDiscoveryApp} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class ADDataSetsPanel extends ADCorePanel implements IAmbitListListener {
	//protected ADDataSetPanel dsPanel;
	protected JLabel labelTitle;
	protected JTable table;
	protected QSARdatasets datasets = null;
	protected ADDataSetTableModel dstm;
	/**
	 * @param title
	 */
	public ADDataSetsPanel(QSARdatasets ds,String title) {
		super(title);
		datasets = ds;
	}

	/**
	 * @param title
	 * @param bClr
	 * @param fClr
	 */
	public ADDataSetsPanel(String title, Color bClr, Color fClr) {
		super(title, bClr, fClr);
	}
	protected void initLayout() {
		layout = new BorderLayout() ;
		setLayout((BorderLayout) layout);
		setBackground(backClr);
		setBorder(BorderFactory.createMatteBorder(5,5,5,5,backClr));
   }

	/* (non-Javadoc)
	 * @see ambit.ui.domain.ADCorePanel#addWidgets()
	 */
	protected void addWidgets() {
		BorderLayout glayout = (BorderLayout) layout;
		labelTitle = new JLabel("<html><b>"+caption+"</b><html>");
		labelTitle.setOpaque(true);
		labelTitle.setForeground(backClr);
		labelTitle.setBackground(foreClr);
        
        add(labelTitle,BorderLayout.NORTH);

        dstm = new ADDataSetTableModel( datasets);
     	table = new JTable(dstm);
//     	Ask to be notified of selection changes.
     	ListSelectionModel rowSM = table.getSelectionModel();
     	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
     	table.setToolTipText("A studyList of test data sets");
     	table.setOpaque(true);
     	table.setBackground(backClr);
     	table.setForeground(foreClr);     	
     	rowSM.addListSelectionListener(new ListSelectionListener() {
     	    public void valueChanged(ListSelectionEvent e) {
     	        //Ignore extra messages.
     	        if (e.getValueIsAdjusting()) return;

     	        ListSelectionModel lsm =
     	            (ListSelectionModel)e.getSource();
     	        if (!lsm.isSelectionEmpty()) { 
     	           datasets.setSelectedIndex( lsm.getMinSelectionIndex()); 
     	        } else {
     	        	datasets.setSelectedIndex(-1);
     	        }
     	    }
     	});
     	JScrollPane scrollPane = new JScrollPane(table);
     	scrollPane.setOpaque(true);
     	scrollPane.setToolTipText("A studyList of test data sets. Use <open test data set> to add a data set to the studyList");     	
     	scrollPane.setPreferredSize(new Dimension(120, 64));
     	scrollPane.setBackground(backClr);
     	scrollPane.setForeground(foreClr);     	
     	table.setPreferredScrollableViewportSize(new Dimension(120, 64));
     	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
     	add(scrollPane,BorderLayout.CENTER);     	
        
     	JPanel p = new JPanel();
    	p.setMinimumSize(new Dimension(32,64));     	
    	p.setPreferredSize(new Dimension(80,72));
     	p.setBackground(backClr);
     	p.setForeground(foreClr);     	
     	p.setLayout(new BoxLayout(p,BoxLayout.PAGE_AXIS));
        JButton bAdd = new JButton( 	new AbstractAction("edit") { 
          	public void actionPerformed(ActionEvent e) {
          		QSARDataset ds = (QSARDataset) datasets.getSelectedItem();          		
          		ADDataSetDialog.editDataset(ds,"Edit selected test data set");          		
    		}
        });        	
        
        bAdd.setMaximumSize(new Dimension(Short.MAX_VALUE,24));        
        JButton bRemove =  new JButton(
        	new AbstractAction("close") { 
          	public void actionPerformed(ActionEvent e) {
          		QSARDataset ds = (QSARDataset) datasets.getSelectedItem();          		
    			if ((ds != null) && 
   				   (JOptionPane.showConfirmDialog( null,
   							"Are you sure to close dataset <" + ds.getName() + "> ?",
   						    "Please confirm",						
							JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION))  					
    					datasets.remove(ds);
    		}
    		});        	
        
        bRemove.setMaximumSize(new Dimension(Short.MAX_VALUE,24));
        JButton bRemoveAll = new JButton(
              new AbstractAction("close all") { 
              	public void actionPerformed(ActionEvent e) {
        			if ((datasets != null) && 
       				   (JOptionPane.showConfirmDialog( null,
       							"Are you sure to close all test data sets?",
       						    "Please confirm",						
								JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION))  					
        					datasets.clear();
        		}
        		});        	
        bRemoveAll.setMaximumSize(new Dimension(Short.MAX_VALUE,24));        
        p.add(bAdd);
        p.add(bRemove);
        p.add(bRemoveAll);                
     	add(p,BorderLayout.EAST);        
	}

	protected QSARDataset getDataSet(int i) {
		if (datasets == null) return null;
		else return datasets.getDataSet(i);
	}
	/**
	 * @return Returns the datasets.
	 */
	protected QSARdatasets getDatasets() {
		return datasets;
	}
	/**
	 * @param datasets The datasets to set.
	 */
	protected void setDatasets(QSARdatasets datasets) {
		this.datasets = datasets;
		dstm.setDatasets(datasets);
		int nd = 0;
		if (datasets != null) nd = datasets.size(); 
		labelTitle.setText("<html><b>"+caption+"</b> [" + nd + "]<html>");
	}
	/* (non-Javadoc)
	 * @see ambit.data.IAmbitListListener#ambitListChanged(ambit.data.AmbitListChanged)
	 */
	public void ambitListChanged(AmbitListChanged event) {
		if (event == null) {
			setDatasets(null);
			table.revalidate() ;
			repaint();			
		} else 
			if (event.getList() instanceof QSARdatasets) { 
				setDatasets((QSARdatasets) event.getList());
				table.revalidate() ;
				repaint();
			}

	}
	/* (non-Javadoc)
	 * @see ambit.data.IAmbitObjectListener#ambitObjectChanged(ambit.data.AmbitObjectChanged)
	 */
	public void ambitObjectChanged(AmbitObjectChanged event) {
		// TODO ambitObjectChanged
		//System.err.println("TODO ambitObjectChanged");
	}
}
