/**
 * Created on 2005-1-18
 *
 */
package ambit2.ui.domain;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

import ambit2.ui.editors.ModelEditor;
import ambit2.data.AmbitListChanged;
import ambit2.data.AmbitObjectChanged;
import ambit2.data.IAmbitListListener;
import ambit2.data.descriptors.DescriptorsList;
import ambit2.data.model.Model;
import ambit2.domain.AllData;
import ambit2.domain.QSARDataset;
import ambit2.exceptions.AmbitException;
import ambit2.ui.data.AmbitObjectDialog;


/**
 * a GUI panel for {@link ambit2.applications.discovery.AmbitDiscoveryApp} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class ADDataSetPanel extends ADCorePanel implements IAmbitListListener,PropertyChangeListener {
	protected QSARDataset dataset;
	protected boolean editable = false;
	/**
	 * @return Returns the editable.
	 */
	public boolean isEditable() {
		return editable;
	}
	/**
	 * @param editable The editable to set.
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
		modelName.setEditable(editable);
		modelFileName.setEditable(editable);
		modelEndpoint.setEditable(editable);
		modelNDescr.setEditable(editable);	
		modelNPoints.setEditable(editable);	
		modelRMSE.setEditable(editable);		
	}
	protected JFormattedTextField modelName;
	protected JFormattedTextField modelFileName;
	protected JFormattedTextField modelEndpoint;
	protected JFormattedTextField modelNDescr;	
	protected JFormattedTextField modelNPoints;	
	protected JFormattedTextField modelRMSE;

	/**
	 * 
	 * @param title
	 */
	public ADDataSetPanel(String title) {
		super(title);
		dataset = null;
	}

	/**
	 * 
	 * @param title
	 * @param bClr
	 * @param fClr
	 */
	public ADDataSetPanel(String title, Color bClr, Color fClr) {
		super(title, bClr, fClr);
		dataset = null;
	}

	/* (non-Javadoc)
	 * @see ambit2.ui.domain.ADCorePanel#addWidgets()
	 */
	protected void addWidgets() {
		GridBagLayout glayout = (GridBagLayout) layout;		
		JLabel labelTitle = new JLabel("<html><b>"+caption+"</b><html>");
		labelTitle.setOpaque(true);
		labelTitle.setForeground(backClr);
		labelTitle.setBackground(foreClr);

        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(1,1,1,1);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1; 
        c.weighty = 1; 
        c.anchor = GridBagConstraints.NORTH;
        glayout.setConstraints(labelTitle,c);        
        add(labelTitle);

        
    	JLabel name = createEtchedLabel("<html><b><u>Name</u></b></html>", foreClr, false);
    	name.setToolTipText("click here to view the model");    	
    	name.addMouseListener(new MouseAdapter() {
	   		public void mouseClicked(MouseEvent e) {
	   			showModel();
	   		}
	    });		    	
    	
        modelName = createTextField("NA",foreClr,backClr,false);
    	JLabel fileName = createEtchedLabel("<html><b>Filename</b></html>", foreClr, false);        
    	modelFileName = createTextField("NA",foreClr,backClr,false);
    	JLabel endpoint = createEtchedLabel("<html><b>Endpoint</b></html>", foreClr, false);    	
    	modelEndpoint = createTextField("NA",foreClr,backClr,false);
    	JLabel ndescr = createEtchedLabel("<html><b><u>No. descriptors</u></b></html>", foreClr, false);
    	ndescr.setToolTipText("click here to view descriptors");    	
    	ndescr.addMouseListener(new MouseAdapter() {
	   		public void mouseClicked(MouseEvent e) {
	   			showDescriptors();
	   		}
	    });		    	
    	modelNDescr = createNumericField(new Integer(0),NumberFormat.getInstance(), foreClr,backClr,false);	
    	JLabel npoints = createEtchedLabel("<html><b><u>No. points</u></b></html>", foreClr, false);
    	npoints.setToolTipText("click here to view compounds");
    	npoints.addMouseListener(new MouseAdapter() {
	   		public void mouseClicked(MouseEvent e) {
	   			showCompounds();
	   		}
	    });		
    	
    	modelNPoints = createNumericField(new Integer(0),NumberFormat.getInstance(), foreClr,backClr,false);	
    	JLabel rmse = createEtchedLabel("<html><b>RMSE</b></html>", foreClr, false);    	
    	modelRMSE = createNumericField(new Double(0),NumberFormat.getInstance(), foreClr,backClr,false);       

   	
        c.weightx = 0.25;
        c.weighty = 0.5;
        c.gridwidth = GridBagConstraints.RELATIVE;        
        glayout.setConstraints(name,c);      
        add(name);
        c.gridwidth = GridBagConstraints.REMAINDER;
        glayout.setConstraints(endpoint,c);
        add(endpoint);

        c.gridwidth = GridBagConstraints.RELATIVE;        
        glayout.setConstraints(modelName,c);      
        add(modelName);        
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        glayout.setConstraints(modelEndpoint,c);        
        add(modelEndpoint);

        c.gridwidth = GridBagConstraints.RELATIVE;
        glayout.setConstraints(fileName,c);      
        add(fileName);
        c.gridwidth = GridBagConstraints.REMAINDER;        
        glayout.setConstraints(modelFileName,c);
        add(modelFileName);   
        

        c.gridwidth = GridBagConstraints.RELATIVE;        
        glayout.setConstraints(ndescr,c);
        add(ndescr);
        
        modelNDescr.setText("NA");
        c.gridwidth = GridBagConstraints.REMAINDER;
        glayout.setConstraints(modelNDescr,c);        
        add(modelNDescr);

        /*
        JTable dt = new JTable(new ADDescriptorsTableModel(xnames)); 
        
        JScrollPane sp = new JScrollPane(dt);
        sp.setPreferredSize(new Dimension(128, 64));        
        sp.setBackground(backClr);
        sp.setForeground(foreClr);        
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(sp,c);        
        add(sp);

		*/
        c.gridwidth = GridBagConstraints.RELATIVE;
        glayout.setConstraints(npoints,c);
        add(npoints);
        c.gridwidth = GridBagConstraints.REMAINDER;
        
        glayout.setConstraints(rmse,c);
        add(rmse);        

        c.gridwidth = GridBagConstraints.RELATIVE;
        glayout.setConstraints(modelNPoints,c);        
        add(modelNPoints);

        c.gridwidth = GridBagConstraints.REMAINDER;
        glayout.setConstraints(modelRMSE,c);        
        add(modelRMSE);
        
    	modelNDescr.addPropertyChangeListener("value",this);
    	modelNPoints.addPropertyChangeListener("value",this);
    	modelName.addPropertyChangeListener("value",this);            	
	}

	public void setDataset(QSARDataset ds) {
		dataset = ds;
		if (ds == null) {
			String s = "NA";
			modelName.setText(s) ;
			modelFileName.setText(s);
			modelEndpoint.setText(s);
			modelNDescr.setText(s);	
			modelNPoints.setText(s);	
			modelRMSE.setText(s);
			setEditable(false);			
		} else {
			modelName.setText(ds.getName()) ;
			modelFileName.setText(ds.getModel().getFileWithData());
			modelEndpoint.setText(ds.getYname());
			modelNDescr.setText(Integer.toString(ds.getNdescriptors()));	
			modelNPoints.setText(Integer.toString(ds.getNpoints()));	
			//modelRMSE.setText(ds.get);
			setEditable(!ds.isReadonly());
		}
		
	}
	/* (non-Javadoc)
	 * @see ambit2.data.IAmbitObjectListener#ambitObjectChanged(ambit2.data.AmbitObjectChanged)
	 */
	public void ambitObjectChanged(AmbitObjectChanged event) {
		if (event == null) {
			setDataset(null);		
			repaint();			
		} else { 
			if (event.getObject() instanceof QSARDataset) {
				setDataset((QSARDataset) event.getObject());		
				repaint();
			}
		}	

	}
	/* (non-Javadoc)
	 * @see ambit2.data.IAmbitListListener#ambitListChanged(ambit2.data.AmbitListChanged)
	 */
	public void ambitListChanged(AmbitListChanged event) {
		if ( (event.getObject() != null) &&
			 (event.getObject() instanceof QSARDataset)) { 
				setDataset((QSARDataset) event.getObject());
				repaint();
			}


	}
	public void propertyChange(PropertyChangeEvent e) {
	    Object source = e.getSource();
	    try {
	    if (source == modelNDescr) {
	        int nd = ((Number)modelNDescr.getValue()).intValue();
	        //dataset.setNdescriptors(nd,true);
	    } else if (source == modelNPoints) {
	        int np = ((Number)modelNPoints.getValue()).intValue();
	    	//dataset.setNpoints(np,true);
	    } else if (source == modelRMSE) {
	        double rmse = ((Number)modelRMSE.getValue()).doubleValue();
	    } else if (source == modelName) {
	        String name = ((String)modelName.getText());
	        dataset.setName(name);
	    }	    
	    } catch (NullPointerException ex) {
	    	//if dataset is not yet assigned
	    }
	}
	public void showModel() {
		if ((dataset != null) && (dataset.getData() != null)) {
		    try {
		    (new ModelEditor(dataset.getModel())).view(this,true, "");
		    } catch (AmbitException x) {
		        
		    }
		    /*
			AmbitObjectDialog dlg = AmbitObjectDialog.createAndShow(
					true,
					"Model",
					this, dataset.getModel(),new Dimension(500,500));
					*/
		}	
	}	
	public void showCompounds() {
		if ((dataset != null) && (dataset.getData() != null)) {
			AmbitObjectDialog dlg = AmbitObjectDialog.createAndShow(
					true,
					"Compounds",
					this, dataset.getData(),new Dimension(500,500));
		}	
	}
	public void showDescriptors() {
		if ((dataset != null) && (dataset.getModel() != null)) {
			Model m = dataset.getModel();
			AllData data = dataset.getData(); 
			DescriptorsList dlist = m.getDescriptors();
			if (dlist != null) {
				AmbitObjectDialog dlg = AmbitObjectDialog.createAndShow
				(true,"Descriptors",this, dlist);
				DescriptorsList x = data.getXDescriptors();
				if ((x == null) || data.isEmpty()) data.initialize(dlist);
				else if ( (dlist != null) && (!dlist.equals(x))) {
					//data.initialize(dlist);
					
				}
			}
		}	
	}
	
}
