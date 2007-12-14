/**
 * Created on 2005-1-18
 *
 */
package ambit.ui.domain;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import ambit.data.AmbitObjectChanged;
import ambit.data.IAmbitObjectListener;
import ambit.domain.QSARDataset;


/**
 * a GUI panel for {@link ambit.applications.discovery.AmbitDiscoveryApp} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class ADPanel extends ADCorePanel implements ActionListener,IAmbitObjectListener  {
	protected QSARDataset dataset;
	JFormattedTextField thresholdEdit ;
	JLabel labelMode ;
	public JButton calcButton;
	/**
	 * 
	 */
	public ADPanel(String title) {
		super(title);
	}
	public ADPanel(String title,  Color  bClr, Color fClr) {
		super(title,bClr,fClr);
	}
	protected void initLayout() {
		layout = new BorderLayout();
		setLayout((BorderLayout) layout);
		setBackground(backClr);
		setBorder(BorderFactory.createMatteBorder(0,1,0,5,backClr));
		//setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED ));
   }
	
	protected void addWidgets() {
        setPreferredSize(new Dimension(86,128));		
		JLabel labelTitle = new JLabel("<html><b>Domain<p>estimation</b></html>");
		labelTitle.setOpaque(true);
		labelTitle.setForeground(foreClr);
		labelTitle.setBackground(backClr);
        labelTitle.setMaximumSize(new Dimension(Short.MAX_VALUE,24));
        labelTitle.setMinimumSize(new Dimension(128,24));        

/*        
		labelMode = new JLabel("<html><b><font color=red>by Ranges</font></b></html>");		
		labelMode.setOpaque(true);
		labelMode.setBackground(backClr);
		labelMode.setForeground(foreClr);
        		
        JLabel labelThreshold = new JLabel("<html><b>Threshold</b></html>");
        labelThreshold.setOpaque(true);
        labelThreshold.setBackground(backClr);
        labelThreshold.setForeground(foreClr);
        
        labelThreshold.setAlignmentX(LEFT_ALIGNMENT);
		thresholdEdit = new JFormattedTextField("");
		thresholdEdit.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		thresholdEdit.setHorizontalAlignment(JTextField.LEFT);		
		thresholdEdit.addActionListener(this);
        labelThreshold.setLabelFor(thresholdEdit);
        		
        add(labelMode);

        add(labelThreshold);
        //add(thresholdEdit);
        //add(scaleBox);        

     //   add(pcaBox);
      */
        add(labelTitle,BorderLayout.NORTH);
        
        calcButton = new JButton(
       		new AbstractAction("Run") { 
       			public void actionPerformed(ActionEvent e) {
				if (dataset != null) {
					dataset.estimateCoverage();
				} else {
					JOptionPane.showMessageDialog( null,
							"Model not available!\nUse <New Model> or <Open Model>\nfrom <File> menu",
						    "Warning!",						
						    JOptionPane.WARNING_MESSAGE);					
				}
			}
		});        
        //glayout.setConstraints(calcButton,c);
        calcButton.setVerticalTextPosition(AbstractButton.BOTTOM);
        
        calcButton.setMaximumSize(new Dimension(Short.MAX_VALUE,24));
        add(calcButton,BorderLayout.SOUTH);		
                
	}
	public void actionPerformed(ActionEvent e) {
	}

	/* (non-Javadoc)
	 * @see ambit.data.IAmbitObjectListener#ambitObjectChanged(ambit.data.AmbitObjectChanged)
	 */
	public void ambitObjectChanged(AmbitObjectChanged event) {

		if (event.getObject() instanceof QSARDataset) {
			setDataset((QSARDataset) event.getObject());		
			repaint();
		}	
	}
	
	/**
	 * @param dataset The dataset to set.
	 */
	protected void setDataset(QSARDataset dataset) {
		this.dataset = dataset;
	}
}
