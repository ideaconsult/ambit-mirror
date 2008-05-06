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
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import ambit2.data.AmbitListChanged;
import ambit2.data.AmbitObjectChanged;
import ambit2.data.IAmbitListListener;
import ambit2.domain.DataCoverage;
import ambit2.domain.DataCoverageDescriptors;
import ambit2.domain.DataCoverageStats;
import ambit2.domain.IDataCoverageStats;


/**
 * a GUI panel for {@link ambit2.applications.discovery.AmbitDiscoveryApp} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class ADStatsPanel extends ADCorePanel implements IAmbitListListener {
	protected IDataCoverageStats coverage;
	JLabel adMode;
	JLabel mMode;
	JLabel pcaMode;
	JLabel threshold;
	JLabel labelNoIn , labelNoOut, labelRmseIn, labelRmseOut;
	
	/**
	 * 
	 */
	public ADStatsPanel(String title) {
		super(title);
	}
	public ADStatsPanel(String title,  Color  bClr, Color fClr) {
		super(title,bClr,fClr);
	}
	
	protected void initLayout() {
		layout = new GridBagLayout() ;
		setLayout((GridBagLayout) layout);
		setBackground(backClr);
		//setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED ));
		setBorder(BorderFactory.createMatteBorder(6,5,5,5,backClr));
        setMaximumSize(new Dimension(Short.MAX_VALUE,Short.MAX_VALUE));		
   }
	

	/* (non-Javadoc)
	 * @see ambit2.ui.domain.ADCorePanel#addWidgets()
	 */
	protected void addWidgets() {
		String[] tooltips= {"Number of points in-domain, click here to view compounds",
				"Number of points out-of-domain, click here to view compounds",
				"RMSE of points in-domain",
				"RMSE of points out-of-domain"};
		GridBagLayout glayout = (GridBagLayout) layout; 
		JLabel labelTitle = new JLabel("<html><b>AD statistics for " + caption + "</b></html>");
        labelTitle.setMaximumSize(new Dimension(Short.MAX_VALUE,24));
        labelTitle.setMinimumSize(new Dimension(64,24));        
		labelTitle.setOpaque(true);
		labelTitle.setForeground(backClr);
		labelTitle.setBackground(foreClr);
		
		JLabel ladMode  = createEtchedLabel("<html><b>Status:</b></html>",foreClr,false);		
		adMode = createTitledLabel("","not assessed","Whether the domain was assessed or not. To assess the domain click <Run>",foreClr,false);

		JLabel lmMode  = createEtchedLabel("<html><b>Method:</b></html>",foreClr,false);
		mMode = createTitledLabel("","Ranges","The method used to assess the domain. Use <Domain/Options/Method> menu to select between the methods.",foreClr,false);
		JLabel lpcaMode  = createEtchedLabel("<html><b>Preprocessing:</b></html>",foreClr,false);		
		pcaMode = createTitledLabel("","PCA","Whether the data were preprocessed before applying the method above. Use <Domain/Options/Preprocessing> to change this setting.",foreClr,false);

		JLabel lthreshold  = createEtchedLabel("<html><b><u>Threshold</u></b></html>",foreClr,false);
		lthreshold.addMouseListener(new MouseAdapter() {
	   		public void mouseClicked(MouseEvent e) {
	   			showThreshold();
	   		}
	    });		
		
		threshold = createTitledLabel("","All","The percent of points in the training set,which determine the domain of the model. \nUse <Domain/Options/Threshold> to change this setting.",foreClr,false);
		
		
		int t = 255;
		int t1 = 155;
		JLabel lNoIn  = createEtchedLabel("<html><b>No (in)</b></html>",foreClr,false);
		lNoIn.addMouseListener(new MouseAdapter() {
	   		public void mouseClicked(MouseEvent e) {
	   			showCompoundsIn();
	   		}
	    });		
		
        labelNoIn = createColorLabel("NA",new Color(t1,t,t1),true);
        labelNoIn.setToolTipText(tooltips[0]);
        lNoIn.setToolTipText(tooltips[0]);        

        JLabel lNoOut  = createEtchedLabel("<html><b>No (out)</b></y></html>",foreClr,false);
        labelNoOut = createColorLabel("NA",new Color(t,t1,t1),true);
        labelNoOut.setToolTipText(tooltips[1]);
        lNoOut.setToolTipText(tooltips[1]);        
		lNoOut.addMouseListener(new MouseAdapter() {
	   		public void mouseClicked(MouseEvent e) {
	   			showCompoundsOut();
	   		}
	    });		
        
		JLabel lRmseIn  = createEtchedLabel("<html><b>RMSE (in)</b></html>",foreClr,false);        
		labelRmseIn = createColorLabel("NA",new Color(t1,t,t1),true);
        lRmseIn.setToolTipText(tooltips[2]);		
        labelRmseIn.setToolTipText(tooltips[2]);
        
        JLabel lRmseOut  = createEtchedLabel("<html><b>RMSE (out)</b></html>",foreClr,false);
        labelRmseOut = createColorLabel("NA",new Color(t,t1,t1),true);
        lRmseOut.setToolTipText(tooltips[3]);
        labelRmseOut.setToolTipText(tooltips[3]);
        
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1; 
        c.weighty = 1; 
        c.anchor = GridBagConstraints.NORTH;
        
        glayout.setConstraints(labelTitle,c);        
        add(labelTitle);

		c.gridwidth = GridBagConstraints.RELATIVE;        
        glayout.setConstraints(ladMode,c);        
        add(ladMode);
        
        
        c.gridwidth = GridBagConstraints.REMAINDER;        
        c.anchor = GridBagConstraints.NORTH;
        glayout.setConstraints(adMode,c);        
        add(adMode);

		c.gridwidth = GridBagConstraints.RELATIVE;        
        glayout.setConstraints(lmMode,c);        
        add(lmMode);
        
        c.gridwidth = GridBagConstraints.REMAINDER;        
        c.anchor = GridBagConstraints.NORTH;
        glayout.setConstraints(mMode,c);        
        add(mMode);

		c.gridwidth = GridBagConstraints.RELATIVE;        
        glayout.setConstraints(lpcaMode,c);        
        add(lpcaMode);
        
        c.gridwidth = GridBagConstraints.REMAINDER;        
        c.anchor = GridBagConstraints.NORTH;
        glayout.setConstraints(pcaMode,c);        
        add(pcaMode);


		c.gridwidth = GridBagConstraints.RELATIVE;        
        glayout.setConstraints(lthreshold,c);        
        add(lthreshold);
        
        c.gridwidth = GridBagConstraints.REMAINDER;        
        c.anchor = GridBagConstraints.NORTH;
        glayout.setConstraints(threshold,c);        
        add(threshold);

        
		c.gridwidth = GridBagConstraints.RELATIVE;        
        glayout.setConstraints(lNoIn,c);        
        add(lNoIn);
        
		c.gridwidth = GridBagConstraints.REMAINDER;        
        glayout.setConstraints(lRmseIn,c);        
        add(lRmseIn);        

        c.insets = new Insets(2,2,2,2);	
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = GridBagConstraints.RELATIVE;        
        glayout.setConstraints(labelNoIn,c);        
        add(labelNoIn);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1;
        c.weighty = 1; 
        glayout.setConstraints(labelRmseIn,c);        
        add(labelRmseIn);

        c.insets = new Insets(0,0,0,0);        
		c.gridwidth = GridBagConstraints.RELATIVE;        
        glayout.setConstraints(lNoOut,c);        
        add(lNoOut);        
		c.gridwidth = GridBagConstraints.REMAINDER;        
        glayout.setConstraints(lRmseOut,c);        
        add(lRmseOut);        
        
        c.insets = new Insets(2,2,2,2);        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.NORTH;
        glayout.setConstraints(labelNoOut,c);        
        add(labelNoOut);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.NORTH;
        glayout.setConstraints(labelRmseOut,c);        
        add(labelRmseOut);

                
	}
	 
	public void actionPerformed(ActionEvent e) {
	}
	/* (non-Javadoc)
	 * @see ambit2.data.IAmbitListListener#ambitListChanged(ambit2.data.AmbitListChanged)
	 */
	/* (non-Javadoc)
	 * @see ambit2.data.IAmbitListListener#ambitListChanged(ambit2.data.AmbitListChanged)
	 */
	public void ambitListChanged(AmbitListChanged event) {
		Object src = event.getObject();
		if ( (src != null) &&
			 (src instanceof DataCoverageStats)) { 
				DataCoverageStats c = (DataCoverageStats) src; 
				setCoverage(c);		
				repaint();
			}


	}
 	/* (non-Javadoc)
	 * @see ambit2.data.IAmbitObjectListener#ambitObjectChanged(ambit2.data.AmbitObjectChanged)
	 */
	public void ambitObjectChanged(AmbitObjectChanged event) {
		if (event == null) {
			setCoverage(null);		
			repaint();			
		} else {
			Object src = event.getObject();
			if (src instanceof IDataCoverageStats) {
				IDataCoverageStats c = (IDataCoverageStats) src; 
				setCoverage(c);		
				repaint();
			}
		}	
	}
	

	/**
	 * @return Returns the coverage.
	 */
	protected IDataCoverageStats getCoverage() {
		return coverage;
	}
	/**
	 * @param coverage The coverage to set.
	 */
	protected void setCoverage(IDataCoverageStats coverage) {
		this.coverage = coverage;
		if (coverage == null) {
			adMode.setText("not assessed");
			mMode.setText("NA");
			pcaMode.setText("NA");			
			labelNoIn.setText("NA") ;
			labelNoOut.setText("NA") ;
			labelRmseIn.setText("NA") ;
			labelRmseOut.setText("NA");
			
		} else {
				if (coverage.isAssessed())	adMode.setText("assessed");
				else adMode.setText("not assessed");
				DataCoverage method = coverage.getMethod(); 
				if (method == null)   {
					mMode.setText("NA");
					pcaMode.setText("NA");
					threshold.setText("NA") ;				
				} else if (method instanceof DataCoverageDescriptors){					
					mMode.setText(method.getName());
					if (((DataCoverageDescriptors)method).isPca()) pcaMode.setText("PCA");
					else pcaMode.setText("no PCA");
					threshold.setText(Double.toString(method.getPThreshold()*100)+"%") ;				
				} else {
					mMode.setText(method.getName());
					threshold.setText(Double.toString(method.getPThreshold()*100)+"%") ;				
				}
				labelNoIn.setText(Integer.toString(coverage.getNoIn())) ;
				labelNoOut.setText(Integer.toString(coverage.getNoOut())) ;
				
				NumberFormat formatter = new DecimalFormat("###.##");
				labelRmseIn.setText(formatter.format(coverage.getRmseIn())) ;
				labelRmseOut.setText(formatter.format(coverage.getRmseOut())) ;
		}
	}
	public void showCompoundsIn() {
		//TODO showCompoundsIn
		System.out.println("TODO: ADStatsPanel: showCompoundsIn");
	}
	public void showCompoundsOut() {
		//TODO showCompoundsOut
		System.out.println("TODO: ADStatsPanel: showCompoundsOut");
	}
	public void showThreshold() {
		//TODO showCompoundsOut
		System.out.println("TODO: ADStatsPanel: showThreshold");
		
	}	
	
	
}
