/*
 * Created on 2005-7-12
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
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import ambit.data.AbstractAmbitListListener;
import ambit.data.AbstractAmbitObjectListener;
import ambit.data.AmbitListChanged;
import ambit.data.AmbitObjectChanged;
import ambit.data.IAmbitObjectListener;
import ambit.domain.QSARDataset;
import ambit.ui.CorePanel;

/**
 * TODO add description
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-7-12
 */
public class ADChartAttributePanel extends CorePanel {
	//gui
    protected IAmbitObjectListener modelListener = null;
    protected IAmbitObjectListener testListener = null;    
    
	protected ADChartAttributeTableModel model = null;
	protected JTable table = null;
	protected JScrollPane pane = null;
	
	
	
	/**
	 * @param title
	 */
	public ADChartAttributePanel(String title,QSARDataset ds, IADPlotsType pType) {
		super(title);
		initListeners();
		createCombo(pType);
		createTable(ds, pType);
	}

	/**
	 * @param title
	 * @param bClr
	 * @param fClr
	 */
	public ADChartAttributePanel(String title, Color bClr, Color fClr,QSARDataset ds, ADScatterPlotsType pType) {
		super(title, bClr, fClr);
		initListeners();
		createCombo(pType);
		createTable(ds, pType);
	}
	protected void initLayout() {
		layout = new BorderLayout();
		setLayout((BorderLayout)layout);
		setBackground(backClr);
		setBorder(BorderFactory.createMatteBorder(5,5,5,5,backClr));
	    
	}
	protected void initListeners() {
	    modelListener = new AbstractAmbitObjectListener() {
	        /* (non-Javadoc)
             * @see ambit.data.AbstractAmbitObjectListener#ambitObjectChanged(ambit.data.AmbitObjectChanged)
             */
            public void ambitObjectChanged(AmbitObjectChanged event) {
        		if (event == null) updateData(null);
        		else {
        				if (event.getObject() instanceof QSARDataset)
        				    updateData((QSARDataset) event.getObject());
        		}
            }
	    };
	    testListener = new AbstractAmbitListListener() {
	        /** test datasets modify event
             * @see ambit.data.AbstractAmbitListListener#ambitListChanged(ambit.data.AmbitListChanged)
             */
            public void ambitListChanged(AmbitListChanged event) {
                // TODO Auto-generated method stub

            }
            /** selected test data set modified event
             * @see ambit.data.AbstractAmbitListListener#ambitObjectChanged(ambit.data.AmbitObjectChanged)
             */
            public void ambitObjectChanged(AmbitObjectChanged event) {
                // TODO Auto-generated method stub
        	    /*
        		if (event == null) setDataset(null);
        		else {
        			QSARDataset ds = (QSARDataset) event.getObject();
        			QSARdatasets dss = (QSARdatasets) event.getList();
        			if ((dss != null) && (dss instanceof QSARdatasets)) {
        				setDatasets(dss,ds);
        			}
        		}
        		*/
            }
	    };
	    
	}
	/* (non-Javadoc)
	 * @see ambit.ui.CorePanel#addWidgets()
	 */
	protected void addWidgets() {
	}
	
	private void createCombo(IADPlotsType pType) {
	   	JPanel p = new JPanel();
     	p.setPreferredSize(new Dimension(Integer.MAX_VALUE, 48));
     	p.setBackground(backClr);
     	p.setForeground(foreClr);
     	p.setOpaque(true);     
     	p.setLayout(new BoxLayout(p,BoxLayout.PAGE_AXIS));
		p.setBorder(BorderFactory.createTitledBorder("Select the Plot Type"));

		JComboBox box = new JComboBox(pType.predefinedvalues());
		box.addActionListener(new AbstractAction() {
			/* (non-Javadoc)
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent e) {
            	if (model == null) return;
		        JComboBox cb = (JComboBox)e.getSource();
		        String plotType = (String)cb.getSelectedItem();
		        model.setPlotType(plotType);
            }
		});
		p.add(box);
	    add(p,BorderLayout.NORTH);
	}
	private JScrollPane createTable(QSARDataset ds, IADPlotsType pType) {
		
		Object[] longValues = null;
		boolean vLines = false;
		if (ds != null) { 
			model = new ADChartAttributeTableModel(ds,pType);
			longValues = ((ADChartAttributeTableModel) model).longValues;
			vLines = true;
		}
		
		table = new JTable(model);
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(vLines);
     	table.setBackground(backClr);
     	table.setForeground(foreClr);
		table.setOpaque(true);
     	     	
		//initColumnSizes(table, model, longValues);		
		table.setPreferredScrollableViewportSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
     	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
     	pane = new JScrollPane(table);
     	pane.setAutoscrolls(true);
     	pane.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
     	pane.setBackground(backClr);
     	pane.setForeground(foreClr);
     	pane.setOpaque(true);     

		table.getTableHeader().setReorderingAllowed(false); 
	    table.getTableHeader().setResizingAllowed(true); 
     	
		table.getParent().setBackground(backClr);
		
	    pane.setPreferredSize(new Dimension(100,200));
	    add(pane,BorderLayout.CENTER);
	    
		return pane;
	}
	
	public void updateData(QSARDataset ds, ADScatterPlotsType plotsType) {
		model.updateData(ds,plotsType);
	}
	public void updateData(QSARDataset ds) {
		model.updateData(ds);	    
	}	
    public IAmbitObjectListener getModelListener() {
        return modelListener;
    }
    public IAmbitObjectListener getTestListener() {
        return testListener;
    }
}
