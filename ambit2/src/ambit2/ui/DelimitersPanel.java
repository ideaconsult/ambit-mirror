/*
Copyright (C) 2005-2006  

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

package ambit2.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.border.TitledBorder;

import org.openscience.cdk.io.formats.IChemFormat;

import ambit2.io.AmbitFileFilter;
import ambit2.io.DelimitedFileFormat;

public class DelimitersPanel extends JPanel implements ItemListener, PropertyChangeListener {
	protected static String[][] delimiters = {{"Comma",","},{"Semicolon",";"},{"Tab","\t"},{"Space"," "},{"Other"," "}};
	protected JRadioButton[] boxes;
	protected JFormattedTextField field;
	protected String delimiter = ",";
	protected TitledBorder border;
	public DelimitersPanel() {
		super(new GridLayout(delimiters.length+1,1));
		border = BorderFactory.createTitledBorder("");
		setBorder(border);
		ButtonGroup g = new ButtonGroup();
		boxes = new JRadioButton[delimiters.length];
		for (int i=0;i<delimiters.length;i++) {
			boxes[i] = new JRadioButton(delimiters[i][0],i==0);
			boxes[i].setActionCommand(delimiters[i][1]);
			boxes[i].addItemListener(this);
			g.add(boxes[i]);
			add(boxes[i]);
		}
		field = new JFormattedTextField(new AbstractFormatter() {
			@Override
			public Object stringToValue(String text) throws ParseException {
				if ((text==null) || (text.length()==0)) return "";
				else return text.substring(0,1);
			}
			@Override
			public String valueToString(Object value) throws ParseException {
				if (value == null) return " ";
				else return value.toString();
			}
			
		});
		field.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				delimiter = field.getText();
			}
		});
		field.setMaximumSize(new Dimension(24,12));
		add(field);
		setControlsVisible(false);
		/*
		field.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
				if (input instanceof JFormattedTextField) {
		             JFormattedTextField ftf = (JFormattedTextField)input;
		             AbstractFormatter formatter = ftf.getFormatter();
		             if (formatter != null) {
		                 String text = ftf.getText();
		                 try {
		                      formatter.stringToValue(text);
		                      return true;
		                  } catch (ParseException pe) {
		                      return false;
		                  }
		              }
		          }
		          return true;
			
			}
		      public boolean shouldYieldFocus(JComponent input) {
		          return verify(input);
		      }				
		});
		*/
			


	}
	public void setControlsVisible(boolean visible) {
		for (int i=0; i < boxes.length;i++)
			boxes[i].setVisible(visible);
		field.setVisible(visible);
		
		if (visible) border.setTitle("Delimiters"); else border.setTitle(""); 
	}
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		delimiter = ( (JRadioButton)source).getActionCommand();
	}	
	public char getDelimiter() {
		if (delimiter.length() ==0) return ',';
		else return delimiter.charAt(0);
	}
	public IChemFormat getFormat() {
		if (field.isVisible()) {
			return new DelimitedFileFormat(getDelimiter(),'"');
		} else return null;
	}
	public void propertyChange(PropertyChangeEvent e) {
	    boolean update = false;
	    String prop = e.getPropertyName();
	    File file = null;
	    //If the directory changed, don't show an image.
	    if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
	        file = null;
	        update = true;

	    //If a file became selected, find out which one.
	    } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
	        file = (File) e.getNewValue();
	        update = true;
	    }

	    //Update the preview accordingly.
	    if (update) {
	        if (isShowing()) {
	        	if (file != null) {
	        		String ext = AmbitFileFilter.getSuffix(file).toLowerCase();
	        		if (".csv".equals(ext)) {
	        			boxes[0].setSelected(true);
	        			setControlsVisible(true);
	        		} else if (".txt".equals(ext)) {
	        			boxes[2].setSelected(true);
	        			setControlsVisible(true);
	        		} else  
	        			setControlsVisible(false);
	        	} else setControlsVisible(false);
	            repaint();
	        }
	    } 
	}
	
	
}