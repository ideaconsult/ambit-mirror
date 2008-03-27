package ambit2.ui.editors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JToolBar.Separator;

import ambit2.data.AmbitList;
import ambit2.data.AmbitObject;
import ambit2.database.exception.DbAmbitException;
import ambit2.exceptions.AmbitException;
import ambit2.ui.EditorPanel;
import ambit2.ui.UITools;
import ambit2.ui.actions.AbstractActionWithTooltip;

public class AbstractAmbitListEditor extends JPanel implements IAmbitEditor {
	protected AmbitList list;
	protected EditorPanel editorPanel;
	protected JToolBar toolbar;
	protected AbstractActionWithTooltip newAction;
	protected AbstractActionWithTooltip deleteAction;
	protected JToggleButton editBox;
	
	public AbstractAmbitListEditor(AmbitList list, boolean editable) {
		this(list,editable,"");
	}
	public AbstractAmbitListEditor(AmbitList list, boolean editable, String title) {
		super(new BorderLayout());
		setAmbitList(list);

		editorPanel = createEditorPanel();
		editorPanel.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
		editorPanel.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
		
		toolbar = createJToolbar();
		toolbar.setFloatable(true);
		toolbar.setBorder(BorderFactory.createEtchedBorder());
		add(toolbar, getToolbarPosition());
	/*
        editorPanel.setMinimumSize(getItemEditorDimension());
        editorPanel.setPreferredSize(getItemEditorDimension());
		*/
		//setBorder(BorderFactory.createTitledBorder(title));
	}
	
	protected String getToolbarPosition() {
		return BorderLayout.NORTH;
	}
	public void editItem(boolean enabled) {
		editorPanel.setEditable(enabled);
	}	
	public AmbitObject addNewItem() {
		AmbitObject o = list.createNewItem();
		if (o == null)
			JOptionPane.showMessageDialog(this,"Can't create new item");
		else {
			selectItem(list.addItem(o),true);
		}
		
		return o;
	}
	public void deleteSelected() throws DbAmbitException {
		for (int i=list.size()-1; i >=0; i--) {
			if (list.getItem(i).isSelected())
				if (JOptionPane.showConfirmDialog(this, 
						"Delete "+list.getItem(i).toString()+ "?"
						)==JOptionPane.OK_OPTION) {
					
					list.remove(i);	
				}
				
		}
		if (list.size() > 0)
			selectItem(0, false);
		else editorPanel.setEditor(null);		
	}
	protected boolean isListEditable() {
		if (list == null) return false;
		else return list.isEditable();
	}
	public AmbitObject selectItem(int index, boolean editable) {
		
		AmbitObject o = list.getItem(index);
		if (editorPanel!= null)
			if (o != null) {
				editorPanel.setEditor(o.editor(isEditable()));
				editorPanel.setEditable(editable);
				
			} else editorPanel.setEditor(null);		
		return o;
	}
		protected EditorPanel createEditorPanel() {
		    EditorPanel e = new EditorPanel();
		    e.setBackground(Color.white);
		    return e;
		
		}
		
		public JComponent getJComponent() {
			return this;
		}
		@Override
		public boolean isFocusable() {
			return false;
		}
		
		public void setEditable(boolean editable) {
			if (list !=null)
			list.setEditable(editable);
			if (editBox != null)
			editBox.setEnabled(editable);
			newAction.setEnabled(editable);
			deleteAction.setEnabled(editable);
			
		}
		public boolean isEditable() {
			if (list ==null) return false;
			return list.isEditable();
		}
		
		public boolean view(Component parent, boolean editable, String title) throws AmbitException {
		
			return JOptionPane.showConfirmDialog(parent,this,title,JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE) 
					== JOptionPane.OK_OPTION;
		}
		public void setAmbitList(AmbitList list) {
			this.list = list;
			if (editorPanel != null)
				editorPanel.setEditor(null);
		}
		
		public void setNoDataText(String noDataText) {
			if (editorPanel != null)
				editorPanel.setNoDataText(noDataText);
		}
		
		protected int getToolbarOrientation() {
			return JToolBar.HORIZONTAL;
		}
		public JToolBar createJToolbar() {
			JToolBar toolbar = new JToolBar(getToolbarOrientation());
			toolbar.setFocusable(true);
			toolbar.setFloatable(true);
			
			
			newAction = new AbstractActionWithTooltip("",UITools.createImageIcon("ambit2/ui/images/table_row_insert.png"),"Add new item") {
				public void actionPerformed(ActionEvent e) {
					addNewItem();

				}
			};
			JButton b = new JButton(newAction);
			b.setFocusable(false);
			toolbar.add(b);
			newAction.setEnabled(isEditable());
			deleteAction = new AbstractActionWithTooltip("",UITools.createImageIcon("ambit2/ui/images/table_row_delete.png"),"Delete selected items") {
				public void actionPerformed(ActionEvent e) {
					try {
						deleteSelected();
					} catch (Exception x) {
						JOptionPane.showMessageDialog(getParent(),x.getMessage());
					}

				}
				
			};
			b = new JButton(deleteAction);
			b.setFocusable(false);
			toolbar.add(b);		
			toolbar.add(b);
			//
			/*
			editBox = new JToggleButton(UITools.createImageIcon("ambit2/ui/images/draw_16.png"),false);
			editBox.setFocusable(false);
			editBox.setEnabled(list.isEditable());
			editBox.setToolTipText("Enable/disable editing of the current item");
			editBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					   Object source = e.getItemSelectable();
					    if (e.getStateChange() == ItemEvent.DESELECTED)
					    	editItem(false);
					    else if (e.getStateChange() == ItemEvent.SELECTED)
					    	editItem(true);
					
				}
			});
			editBox.setMinimumSize(new Dimension(24,24));
			editBox.setPreferredSize(new Dimension(24,24));	
			toolbar.add(new Separator());
			toolbar.add(editBox);
			*/
			toolbar.add(new Separator());
			toolbar.setPreferredSize(new Dimension(150,32));
			return toolbar;
			
		}
		protected Dimension getItemEditorDimension() {
			return new Dimension(350,100); //editorPanel.getPreferredSize();
		}

}
