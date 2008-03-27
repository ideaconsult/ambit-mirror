/** 
 * Filename: ADActionPanel.java 
 * @Author: Nina Jeliazkova
 * @Date: 2005-1-19
 *
 * Copyright (C) 2005  AMBIT project http://luna.acad.bg/nina/projects
 *
 * Contact: nina@acad.bg
 * 
 */
package ambit2.ui.domain;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import ambit2.domain.DataModule;


/**
 * A panel for {@link ambit2.domain} classes 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class ADActionPanel extends ADCorePanel {
	protected DataModule dm;
	JButton calcButton;
	ADStatsPanel spanel;
    JRadioButton oneButton , allButton;
	
	/**
	 * 
	 * @param title
	 */
	public ADActionPanel(String title) {
		super(title);
		dm = null;
	}

	/**
	 * 
	 * @param title
	 * @param bClr
	 * @param fClr
	 */
	public ADActionPanel(String title, Color bClr, Color fClr) {
		super( title, bClr, fClr);
		dm = null;
	}
	public ADActionPanel(DataModule dm, 
			String title, Color bClr, Color fClr) {
		super( title, bClr, fClr);
		this.dm = dm;
	}	
	protected void initLayout() {
		layout = new BoxLayout(this,BoxLayout.PAGE_AXIS) ;
		setLayout((BoxLayout) layout);
		setBackground(backClr);
		setForeground(foreClr);
		setBorder(BorderFactory.createMatteBorder(0,1,0,5,backClr));
		//setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED ));
   }
	
	protected void addWidgets() {
        setMinimumSize(new Dimension(32,32));
        setPreferredSize(new Dimension(86,128));        
		JLabel labelTitle = new JLabel("<html><b>" + caption + "</b></html>");
		labelTitle.setOpaque(true);
		labelTitle.setForeground(foreClr);
		labelTitle.setBackground(backClr);

        add(labelTitle);
	
        oneButton = new JRadioButton("Selected");
        oneButton.setBackground(backClr);
        allButton = new JRadioButton("All");
        allButton.setBackground(backClr);
        ButtonGroup group = new ButtonGroup();
        group.add(oneButton);
        group.add(allButton);
        oneButton.setSelected(true);
        add(oneButton);
        add(allButton);

        calcButton = new JButton(
           		new AbstractAction("Run") { 
           			public void actionPerformed(ActionEvent e) {
    				if (dm != null) {
    					try {
    					dm.assessCoverage(oneButton.isSelected());
    					} catch (Exception x) {
    						x.printStackTrace();
    					}
    				}
    			}
    		});        
        calcButton.setBackground(backClr);
        calcButton.setMaximumSize(new Dimension(Short.MAX_VALUE,24));        
        add(calcButton);
        
	}	

	
}
