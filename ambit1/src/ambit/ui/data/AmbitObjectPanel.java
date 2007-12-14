/**
 * Created on 2005-3-23
 *
 */
package ambit.ui.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.openscience.cdk.Molecule;

import ambit.data.AmbitList;
import ambit.data.AmbitListChanged;
import ambit.data.AmbitObject;
import ambit.data.AmbitObjectChanged;
import ambit.data.IAmbitEditor;
import ambit.data.IAmbitListListener;
import ambit.data.IAmbitObjectListener;
import ambit.data.molecule.AmbitPoint;
import ambit.data.molecule.Compound;
import ambit.exceptions.AmbitException;
import ambit.ui.AmbitColors;
import ambit.ui.AmbitTextField;
import ambit.ui.CorePanel;
import ambit.ui.domain.AmbitPointPanel;


/**
 * A panel to display and edit arbitrary {@link ambit.data.AmbitObject} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class AmbitObjectPanel extends CorePanel implements IAmbitEditor, 
										MouseListener, 
										KeyListener, PropertyChangeListener,
										ListSelectionListener,
										IAmbitObjectListener,
										IAmbitListListener {
	protected boolean editable = true;
	protected AmbitObject ao = null;
	protected TreeMap labels, edits;
	protected Stack parents = null;
	
	protected AmbitObjectPanel subpanel = null;
	
	protected AbstractTableModel model = null;
	protected JTable table = null;
	protected JScrollPane pane = null;
	protected JPanel listPanel = null;
	protected JLabel dbLabel = null;	
	protected int selectedRow = -1;
	protected Dimension cDim = new Dimension(100,200);
	protected int orientation = JSplitPane.HORIZONTAL_SPLIT;
	protected Dimension minD = new Dimension(32,32);
	
    public synchronized int getOrientation() {
        return orientation;
    }
    public synchronized void setOrientation(int orientation) {
        this.orientation = orientation;
    }
	/**
	 * 
	 * @param title
	 * @param obj
	 */
	public AmbitObjectPanel(String title, AmbitObject obj) {
		super(title,Color.WHITE,AmbitColors.DarkClr);
		labels = new TreeMap();
		edits = new TreeMap();		
		this.ao = obj;
		parents = new Stack();
		addWidgets();	
		setFocusable(true);
		addKeyListener(this);
		setMinimumSize(new Dimension(200,200));
		setPreferredSize(new Dimension(300,300));
	}

	/**
	 * @param title
	 * @param bClr
	 * @param fClr
	 */
	public AmbitObjectPanel(String title, Color bClr, Color fClr, AmbitObject obj) {
		super(title, bClr, fClr);
		labels = new TreeMap();
		edits = new TreeMap();		
		this.ao = obj;
		parents = new Stack();
		addWidgets();
		setFocusable(true);		
		addKeyListener(this);
		setMinimumSize(new Dimension(200,200));
	}
	public AmbitObjectPanel(String title, Color bClr, Color fClr, AmbitObject obj, int orientation) {
		super(title, bClr, fClr);
		labels = new TreeMap();
		edits = new TreeMap();		
		this.ao = obj;
		parents = new Stack();
		this.orientation = orientation;
		addWidgets();
		setFocusable(true);		
		addKeyListener(this);
		setMinimumSize(new Dimension(200,200));
	}

	/* (non-Javadoc)
	 * @see ambit.ui.CorePanel#addWidgets()
	 */
	protected void addWidgets() {
		if (ao == null) return;
		GridBagConstraints cc = new GridBagConstraints();
		cc.insets = new Insets(1,1,1,1);

    	cc.weightx = 0.5;			
    	cc.gridwidth = GridBagConstraints.REMAINDER;
		cc.anchor = GridBagConstraints.NORTH;

		String s = "";
		if (!parents.empty()) {
			Object obj = parents.peek();
			s = obj.toString();
			JLabel backLabel = createTitledLabel("Add","<html><u>Back to " +
					s.substring(0,Math.min((int)minD.getHeight(),s.length())) +
					"</u><html>","Press <Esc> or click here to go back to "+ s,Color.black,false);
			backLabel.addMouseListener(new MouseAdapter() {
			   		public void mouseClicked(MouseEvent e) {
			   			back();
			   		}
			   });		

			backLabel.setPreferredSize(new Dimension(Integer.MAX_VALUE,(int)minD.getHeight()));   
        	((GridBagLayout) layout).setConstraints(backLabel,cc);			
			add(backLabel);
		}
		Class c = ao.getClass();
		
		
		if (ao instanceof AmbitList) {
			cc.weightx = 1;
			JLabel label = createHeader(c.getName());
			cc.fill = GridBagConstraints.HORIZONTAL;	        		
			((GridBagLayout) layout).setConstraints(label,cc);			
			add(label);		

			
			listPanel = createTable((AmbitList) ao);
	    	listPanel.setBorder(BorderFactory.createEtchedBorder(foreClr,Color.darkGray));
     		//listPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	    	listPanel.setPreferredSize(cDim);
	    	listPanel.setMinimumSize(new Dimension(100,100));
	    	
		
			subpanel = createSubpanel(((AmbitList) ao).getSelectedItem());
			subpanel.setBackground(backClr);
			subpanel.setForeground(foreClr);
			subpanel.setPreferredSize(cDim);
	    	subpanel.setBorder(BorderFactory.createEtchedBorder(foreClr,Color.darkGray));
			//add(subpanel);
	    	cc.weightx = 1;			
			cc.weighty = 1;			
	    	cc.gridwidth = GridBagConstraints.REMAINDER;			
			JSplitPane splitPane = new JSplitPane(orientation,   listPanel,subpanel );
			splitPane.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
			((GridBagLayout) layout).setConstraints(splitPane,cc);
			add(splitPane);
			
		} else if (ao instanceof Compound) {
		    listPanel = (JPanel )ao.editor(isEditable()).getJComponent();
			cc.weightx = 1;			
			cc.weighty = 1;			
	    	cc.gridwidth = GridBagConstraints.REMAINDER;
	    	cc.fill = GridBagConstraints.BOTH;
			listPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));			
	    	((GridBagLayout) layout).setConstraints(listPanel,cc);
			add(listPanel);
		} else if (ao instanceof AmbitPoint) {
			listPanel = new AmbitPointPanel("",backClr,foreClr);
			((AmbitPointPanel) listPanel).setAmbitPoint((AmbitPoint)ao);			
			cc.weightx = 1;			
			cc.weighty = 1;			
	    	cc.gridwidth = GridBagConstraints.REMAINDER;
	    	cc.fill = GridBagConstraints.BOTH;
			listPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));			
	    	((GridBagLayout) layout).setConstraints(listPanel,cc);
			add(listPanel);			
		} else {
			JLabel label = createHeader(c.getName());
			cc.fill = GridBagConstraints.HORIZONTAL;	        		
			((GridBagLayout) layout).setConstraints(label,cc);			
			add(label);		
			
		   boolean defaultEdit = false;	
		   Method[] theMethods = c.getMethods();
		   for (int i = 0; i < theMethods.length; i++) {
		      String methodString = theMethods[i].getName();
		      if (methodString.equals("getClass")) continue;
		      if (methodString.equals("getId")) continue;		      
		      if (methodString.equals("getType")) continue;		      
		      Class[] parameterTypes = theMethods[i].getParameterTypes();
		      
		      
		      int p = methodString.indexOf("get");
		      if ((p == 0) && (parameterTypes.length == 0)) {
		      	s = "";
		      	//0 - createEdit, 1 - combobox, 2 - show DefaultReadData in a table; 3- show Molecule
		      	int doWhat = 0;
		      	Object obj = null;
		      	try {
		      		obj = theMethods[i].invoke(ao,null);
		      		if (obj == null) continue;
		      		else if (obj instanceof Vector) { s = "Vector"; doWhat = 2; continue; }
		      		else if (obj instanceof Molecule) { s = "Molecule";doWhat = 3; continue;}		      		
		      		else s = obj.toString();
		      	} catch (IllegalAccessException x) {
		      		System.err.println(methodString);
		      		x.printStackTrace();
		      	} catch (InvocationTargetException x) {
		      		System.err.println(methodString);		      		
		      		x.printStackTrace();
		      	} catch (IllegalArgumentException x) {
		      		System.err.println(methodString);		      		
		      		x.printStackTrace();
		      	}
		      	boolean e = theMethods[i].getReturnType().getName().equals("java.lang.String")
						|| theMethods[i].getReturnType().getName().equals("int")
						|| theMethods[i].getReturnType().getName().equals("double");
		      	
		      	String tooltip, caption;
		      	
		      	if (e) { 
		      		tooltip =  methodString.substring(p+3) + " : " + s;
		      		caption = methodString.substring(p+3);		      		
		      	} else {
		      		tooltip = methodString.substring(p+3) + ": Click here to edit";
		      		caption = "<HTML><B>" + methodString.substring(p+3) + "</B></HTML>";
		      	}	
		      	JComponent edit = null;

		      	Method hasPatterns = null;
		      	String[] patterns = null;
		      	try {
		      		hasPatterns = c.getMethod("predefinedvalues",null);
		      		patterns = (String[]) hasPatterns.invoke(ao,null);
		      	} catch (IllegalAccessException x) {
	      			x.printStackTrace();
	      			hasPatterns = null;
	      			patterns = null;
		      	} catch (NoSuchMethodException x) {
	      			x.printStackTrace();
	      			hasPatterns = null;
	      			patterns = null;
		      	} catch (InvocationTargetException x) {
	      			x.printStackTrace();
	      			hasPatterns = null;
	      			patterns = null;
	      		}
	      		
				if (patterns != null) doWhat = 1;
				switch(doWhat) {
				case 1: {
	 				edit = createCombo(labels,edits,
	 					methodString.substring(p+3),
		    			caption,
		    			s,
		    			tooltip,
		    			patterns,
		    			e & ao.isEditable());
	 				break;
				}	
				default: {
		      		edit = createWidget(labels,edits,
			    			methodString.substring(p+3),
			    			caption,
			    			s,
			    			tooltip,
			    			e & ao.isEditable());
		      		break;					
				}
		    	}

 				if (doWhat < 2) {
 	 				edit.setMinimumSize(new Dimension(80,20));		      		
 					
					if (!ao.isEditable())
						edit.setForeground(Color.black);
			      	//------------
		      		Class[] param = new Class[] {theMethods[i].getReturnType()};
		      		Method setMethod = null;
		      		try {
		      			setMethod = c.getMethod(
		      				"set" + methodString.substring(p+3),
		      				param);
		      		} catch (NoSuchMethodException x) {
		      			
		      			setMethod = null;
		      			edit.setEnabled(false);
		      		}
		      		if (edit instanceof AmbitTextField) 
		      			((AmbitTextField)edit).setMethods(ao,theMethods[i],
		      				setMethod,
		      				param
		      				);
			      	//--------
			      	if (!e) {
			      		edit.setBackground(AmbitColors.BrightClr);
			      		edit.setForeground(AmbitColors.DarkClr);
			      		edit.addKeyListener(this);	
			      		edit.addMouseListener(this);	      		
			      	} else {
			      		edit.addPropertyChangeListener("value",this);		      		
			      	}
 				}
		      	/*
		         System.out.println("Name: " + methodString);
		         String returnString = theMethods[i].getReturnType().getName();
		         
		         System.out.println("   Return Type: " + returnString);
		         
	         
		         System.out.print("   Parameter Types:");
		         for (int k = 0; k < parameterTypes.length; k ++) {
		            String parameterString = parameterTypes[k].getName();
		            System.out.print(" " + parameterString);
		         }
		         System.out.println();
		        */
		       }
		   }

		   placeWidgets(labels, edits,cc);
		   
	   
		   cc.anchor = GridBagConstraints.NORTH;
		   cc.fill =  GridBagConstraints.BOTH;
		   String syncDB = "";
		   if (ao.hasID()) syncDB = ""; 
		   dbLabel = createTitledLabel("",syncDB,"",Color.darkGray,false);
	    	cc.weightx = 1;			
	    	cc.gridwidth = GridBagConstraints.REMAINDER;
			cc.anchor = GridBagConstraints.NORTH;
		   ((GridBagLayout) layout).setConstraints(dbLabel,cc);			
		   
		   add(dbLabel);
		   
		  }   
	
	}


	public AmbitObject getAO() {
		return ao;
	}
	public void setAO(AmbitObject ao) {
	    if (this.ao != null)
	        this.ao.removeAmbitObjectListener(this);
		if (ao != null)
			ao.addAmbitObjectListener(this);
		if (ao instanceof AmbitList) {
			((AmbitList) ao).addListListener(this); 
		}
		this.ao = ao;
		labels.clear(); edits.clear();
		removeAll();
		addWidgets();
		revalidate();
		repaint();
	}
	public void propertyChange(PropertyChangeEvent e) {
	    Object source = e.getSource();
	    try {
	    	((AmbitTextField) source).updateValue(e.getNewValue());
	    	
	    } catch (NullPointerException ex) {
	    	ex.printStackTrace();
	    }
	}
	
	protected void extinguish() {
			if (listPanel != null) listPanel.removeAll();
			listPanel = null;	
			subpanel = null;
			model = null;
			table = null;
			pane = null;

	}			

	protected void go(Object o) {
		if ((o!=null) && (o instanceof AmbitObject)) {
			parents.push(ao);
			extinguish();
			setAO((AmbitObject) o);
		}			
	}
	public void mouseClicked(MouseEvent e ) {
		Object o = ((AmbitTextField) e.getSource()).getAmbitValue();
		go(o);
	}
	public void mouseEntered(MouseEvent e ) {
	}
	public void mouseExited(MouseEvent e ) {
	}
	public void mousePressed(MouseEvent e ) {
	}
	public void mouseReleased(MouseEvent e ) {
	}
	public void keyPressed(KeyEvent e) {
		if (e.getSource() == this) {
			if (e.getKeyCode() == 27) back(); 
		} else {
			Object o = ((AmbitTextField) e.getSource()).getAmbitValue();
			go(o);
		}
	}
	public void keyReleased(KeyEvent e) {
		
	}
	public void keyTyped(KeyEvent e) {
		
	}	
	protected JPanel createTable(AmbitList alist) {
		listPanel = new JPanel(new BorderLayout());
		listPanel.setBackground(Color.green);
		listPanel.setOpaque(true);
		
		//Object[] longValues = null;
		boolean vLines = false;
		/*
		if (alist instanceof QSARDataset) { 
			model = new QSARDatasetTableModel((QSARDataset)alist);
			//longValues = ((AdDataTableModel) model).getLongValues();
			vLines = true;
		} else {
		*/
			model = new AmbitListTableModel(alist);
		//}	
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
     	
     	ListSelectionModel rowSM = table.getSelectionModel();
     	rowSM.addListSelectionListener(this);
		rowSM.setLeadSelectionIndex(alist.getSelectedIndex());
     	
		table.getParent().setBackground(backClr);     	
		listPanel.add(pane,BorderLayout.CENTER);
		
		JPanel p = new JPanel();
		BoxLayout bl = new BoxLayout(p,BoxLayout.LINE_AXIS);
		p.setLayout(bl);
		
		p.setOpaque(true);
		p.setBackground(backClr);
		p.setPreferredSize(new Dimension(Integer.MAX_VALUE,(int)minD.getHeight()));

		JLabel button = createTitledLabel("<<","<html><u><b>Prev</b></u><html>",
				"Go to the previous item",foreClr,false);	
		button.setPreferredSize(minD);
		button.addMouseListener(new MouseAdapter() {
	   		public void mouseClicked(MouseEvent e) {
	   			((AmbitList) ao).prev();
	   		}
	   });							
		p.add(button);		
		p.add(Box.createHorizontalStrut(2));
		button = createTitledLabel(">>","<html><u><b>Next</b></u><html>",
				"Go to the next item",foreClr,false);
		button.setPreferredSize(minD);		
		button.addMouseListener(new MouseAdapter() {
	   		public void mouseClicked(MouseEvent e) {
	   			((AmbitList) ao).next();
	   		}
	   });							
		p.add(button);
		p.add(Box.createHorizontalStrut(5));		
		p.add(Box.createHorizontalGlue());		
		button = createTitledLabel("Add","<html><u><b>Add<b></u><html>",
				"Add a new item to this studyList",foreClr,false);
		//button.setAlignmentY(JLabel.CENTER);
		//button.setHorizontalTextPosition(SwingConstants.CENTER);		
		//button.setVerticalTextPosition(SwingConstants.CENTER);
		
		button.setPreferredSize(minD);		
		button.addMouseListener(new MouseAdapter() {
		   		public void mouseClicked(MouseEvent e) {
		   			if (!ao.isEditable()) {
						JOptionPane.showMessageDialog( null,
								"Adding items is not allowed!",
							    "Warning: this studyList is readonly!",						
							    JOptionPane.INFORMATION_MESSAGE);		   				
		   				return;
		   			}
		   			AmbitObject o = ((AmbitList) ao).getSelectedItem();
		   			if (o != null)		
		   				try {
		   					o = (AmbitObject)o.clone();
		   				} catch (CloneNotSupportedException x) {
		   					o = null;
		   				}
			   		if (o == null) {
			   			o = ((AmbitList) ao).createNewItem();
			   		}
		   			if (o != null) {
		   				AmbitObjectDialog d = AmbitObjectDialog.createAndShow(
		   						true,
		   						"New item",
		   						subpanel,o);
		   				d.setTitle("Add item");
		   				if (d.getResult() == JOptionPane.OK_OPTION)  
		   					((AmbitList) ao).addItem(o);
		   				
		   			} else
					JOptionPane.showMessageDialog( null,
							"Add",
						    "to be done",						
						    JOptionPane.INFORMATION_MESSAGE);
		   		}
		   });		
		p.add(button);
		p.add(Box.createHorizontalStrut(2));		
		button = createTitledLabel("Delete","<html><u><b>Delete<b></u><html>",
				"Delete selected item from the studyList",foreClr,false);
		button.setPreferredSize(minD);		
		button.addMouseListener(new MouseAdapter() {
	   		public void mouseClicked(MouseEvent e) {
	   			if (!ao.isEditable()) {
					JOptionPane.showMessageDialog( null,
							"Delete item is not allowed!",
						    "Warning : This studyList is readonly!",						
						    JOptionPane.INFORMATION_MESSAGE);		   				
	   				return;
	   			}	   			
	   			int i = ((AmbitList) ao).getSelectedIndex();
	   			if (i > -1)
	   	        if (JOptionPane.showConfirmDialog(null,
	   	        		"Are you sure to delete this item ?\n" + ((AmbitList) ao).getSelectedItem().toString(),
						"Please confirm",JOptionPane.YES_NO_OPTION)
	   	        		==JOptionPane.YES_OPTION) {
	   	        	((AmbitList) ao).remove(((AmbitList) ao).getSelectedIndex());
	   	        }
	   		}
	   });							
		p.add(button);
		
		listPanel.add(p,BorderLayout.SOUTH);
		return listPanel;
					
	}	
	/*
	    private void initColumnSizes(JTable table, AbstractTableModel model,
    			Object[] longValues) {
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;

        for (int i = 0; i < table.getColumnCount(); i++) {
            column = table.getColumnModel().getColumn(i);
            comp = table.getDefaultRenderer(model.getColumnClass(i)).
                             getTableCellRendererComponent(
                                 table, longValues[i],
                                 false, false, 0, i);
            cellWidth = comp.getPreferredSize().width;
            //System.out.println("column\t"+Integer.toString(i+1)+"\twidth\t"+cellWidth);
            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }
    } 
    */
	 public void valueChanged(ListSelectionEvent e) {
 	        if (e.getValueIsAdjusting()) return;

 	        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
 	        if (lsm.isSelectionEmpty()) {
 	            
 	        } else {
 	            selectedRow = lsm.getMinSelectionIndex();
 	            if (ao instanceof AmbitList ) {
 	            	((AmbitList) ao).setSelectedIndex(selectedRow);
 	            }
 	            /*
 		 		if (ao instanceof AmbitList ) {
 		 			if (subpanel != null)
 		 				subpanel.setAo(((AmbitList) ao).getItem(selectedRow));
 		 		} else if (ao instanceof DefaultReadData) {
 		 			if (subpanel != null) {
 		 				subpanel.setAo(((DefaultReadData) ao).getCompound(selectedRow));
 		 			}	
 		 		}
 		 		*/
 	        }
	 }
	 protected void back() {
	 	try {
	 		Object obj = parents.peek();
	 		if (obj instanceof AmbitObject) {
	 			extinguish();
	 			setAO((AmbitObject) parents.pop());
	 		}
	 	} catch (EmptyStackException x) {
	 		//just stay here 
	 	}
	 }
	 
	 protected AmbitObjectPanel createSubpanel(AmbitObject o) {
	 	AmbitObjectPanel ap = new AmbitObjectPanel("Click on the list",backClr, foreClr,o);
		ap.setMinimumSize(new Dimension(200,200));
		return ap;
	 }
	/**
	 * @see ambit.data.IAmbitObjectListener#ambitObjectChanged(ambit.data.AmbitObjectChanged)
	 */
	public void ambitObjectChanged(AmbitObjectChanged event) {
	    //System.out.println(event);
		 if (getAO() != event.getObject()) 
				setAO(event.getObject());
		 else if (getAO() instanceof AmbitList) {
			if (subpanel != null) {
				subpanel.setAO(event.getObject());

				subpanel.revalidate();
			}	
		} else setAO(event.getObject());
	}
	/**
	 * @see ambit.data.IAmbitListListener#ambitListChanged(ambit.data.AmbitListChanged)
	 */
	public void ambitListChanged(AmbitListChanged event) {
	    //System.out.println(event);
		if (table != null) table.revalidate();
		AmbitObject selectedObject = event.getObject();
		AmbitList list = event.getList(); 
		if ((selectedObject != null) && (subpanel != null) && (list != null)) {
			
			subpanel.setAO(selectedObject);
			if ((list != null) && (table != null)) {
				ListSelectionModel rowSM = table.getSelectionModel();
				rowSM.setLeadSelectionIndex(list.getSelectedIndex());
			}
		}
	}
	private JLabel createHeader(String classname) {
		int pp = classname.lastIndexOf('.');
		JLabel label = createColorLabel(
				"<HTML><B>"+ classname.substring(pp+1)+"</B><HTML>",
				foreClr,false);
		label.setForeground(backClr);
		return label;
	}
	public boolean view(Component parent, boolean editable, String title) throws AmbitException {
	    return JOptionPane.showConfirmDialog(parent,this,"",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)
        == JOptionPane.OK_OPTION;
	}
	public JComponent getJComponent() {
	    return this;
	}
	/* (non-Javadoc)
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof AmbitObject)
            setAO((AmbitObject)value);
        return this;
    }
    public boolean isEditable() {
    	return editable;
    }
    public void setEditable(boolean editable) {
    	this.editable = editable;
    	
    }
}
