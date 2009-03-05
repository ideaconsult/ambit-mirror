/*
Copyright (C) 2005-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit.data.qmrf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

import ambit.data.IAmbitEditor;
import ambit.exceptions.AmbitException;
import ambit.ui.AmbitColors;
import ambit.ui.UITools;

public class QMRFWelcomePanel extends JPanel implements IAmbitEditor {
	protected QMRFObject qmrf;
	protected static final String[] fields = {"version","name","author","date","contact","email","url"};
	protected static final String[] fieldNames = {"Version","Name","Author","Date","Contact","Email","www"};
	protected QMRFAttributesPanel centerPanel;
	
	public QMRFWelcomePanel(QMRFObject qmrf) {
		this(qmrf,true);
	}
	public QMRFWelcomePanel(QMRFObject qmrf, boolean editable) {
		super();
		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
        add(createLabel("<html><b>(Q)SAR Model Reporting Format (QMRF), Version "+qmrf.getVersion()+"</b></html>"));
		this.qmrf = qmrf;
		        
		centerPanel = new QMRFAttributesPanel(qmrf.getAttributes(),fields,fieldNames);
		centerPanel.setEditable(editable);
		/*
		SpringLayout layout = new SpringLayout();
		centerPanel.setLayout(layout);
		centerPanel.setBackground(Color.white);

		
		field = new JFormattedTextField[fields.length];
		Dimension mind = new Dimension(100,18);
		Dimension maxd = new Dimension(Integer.MAX_VALUE,18);
        for (int i=0; i < fields.length;i++) {
        	JLabel label = new JLabel(fieldNames[i]);
        	label.setForeground(AmbitColors.DarkClr);
        	field[i] = new JFormattedTextField(qmrf.getProperty(fields[i]));
        	field[i].setEditable(editable);
        	//field[i].setMinimumSize(mind);
        	field[i].setMaximumSize(maxd);
        	field[i].setForeground(AmbitColors.DarkClr);
        	label.setLabelFor(field[i]);
        	
        	centerPanel.add(label);
        	centerPanel.add(field[i]);
        }
        SpringUtilities.makeCompactGrid(centerPanel,
                field.length, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
                */
        add(new JScrollPane(centerPanel));
        setBackground(Color.white);
        
        add(createLabel("<html><b>Background</b></html>"));
        
        //String html = "<html><font color=\"#2059c9\" face=\"" + getFont().getFontName() + "\">";
        //String html="<html><font face=\"" + getFont().getFontName() + "\">";
        String html="<html><font face=\"Arial\">";
        add(createText(html+QMRFObject.getBackground()));
        //add(createText(html+"Please, try to fill in the fields of the QMRF for the model of interest. If the field is not pertinent with the model you are describing, or if you cannot provide the requested information, please answer \"no information available\". The set of information that you provide will be used to facilitate regulatory considerations of (Q)SARs. For this purpose, the structure of the QMRF is devised to reflect as much as possible the OECD principles for the validation, for regulatory purposes, of (Q)SAR models. You are invited to consult the OECD \"Guidance Document on the Validation of (Quantitative) Structure-Activity Relationship Models\" that can aid you in filling in a number of fields of the QMRF</html>"));
        
        
        add(createLabel("<html><b>Submission Procedure</b></html>"));
        add(createText(html+
        	"If you wish to submit the QMRF for inclusion in the JRC QSAR Model Database, please save your QMRF as xml file and upload it by the on-line submission procedure"
        	));
        /*
        add(createLabel("<html><b>Disclaimer</b></html>"));
        add(createText("The JRC accepts no liability for the use of any information obtained from the QRF and related documentation. This is not the final version of the QRF which is still under development. It is likely that newer versions will be released soon. Please, check the JRC website for the latest version available.."));
        */
        
	}
	protected static JComponent createText(String text) {
		   JTextPane t = new JTextPane();
	       //t.setEditorKit(new HTMLEditorKit());
	       t.setEditable(false);
	       t.setContentType("text/html");
	       t.setSelectionColor(Color.white);
	       t.setSelectedTextColor(Color.black);
	       t.setForeground(Color.black);
	       t.setBackground(Color.white);
	       t.setText(text);
	       t.addHyperlinkListener(new HyperlinkListener() {
	    	   public void hyperlinkUpdate(HyperlinkEvent e) {
	    		      if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
	    		        StringTokenizer st = new StringTokenizer(e.getDescription(), " ");
	    		        if (st.hasMoreTokens()) {
	    		          String s = st.nextToken();
	    		          UITools.openURL(s);
	    		        }
	    		      }
	    		    }

	       });
	       return new JScrollPane(t);
	}
	protected static JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        l.setOpaque(true);
        l.setBackground(AmbitColors.DarkClr);
        l.setForeground(Color.white);
        l.setMinimumSize(new Dimension(Integer.MAX_VALUE,18));
        l.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));		
        return l;
	}
	public JComponent getJComponent() {
		return this;
	}

	public void setEditable(boolean editable) {
		centerPanel.setEditable(editable);
	}

	public boolean view(Component parent, boolean editable, String title)
			throws AmbitException {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean isEditable() {
		return centerPanel.isEditable();
	}

}


