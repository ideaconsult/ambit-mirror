package ambit.ui.data;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import ambit.data.AmbitList;
import ambit.data.AmbitObject;
import ambit.data.IAmbitEditor;
import ambit.database.exception.DbAmbitException;
import ambit.ui.UITools;
import ambit.ui.actions.AbstractActionWithTooltip;

public class AmbitListOneItemEditor extends AbstractAmbitListEditor implements IAmbitEditor {
	protected int currentIndex = 0;
	protected JLabel currentIndexLabel;
	public AmbitListOneItemEditor(AmbitList list, boolean editable,String title) {
		super(list,editable,title);
		addWidgets();
		 
	}
	protected void addWidgets() {
		add(editorPanel,BorderLayout.CENTER);

		selectItem(0,isEditable());
		Dimension d = getItemEditorDimension();
		d.width += toolbar.getPreferredSize().width;
		d.height += toolbar.getPreferredSize().height;
		setPreferredSize(d);		
	}
	@Override
	public JToolBar createJToolbar() {
		JToolBar t = super.createJToolbar();
		 currentIndexLabel = new JLabel();
		AbstractActionWithTooltip action = new AbstractActionWithTooltip("",
				UITools.createImageIcon("ambit/ui/images/resultset_first.png"),"First") {
			public void actionPerformed(ActionEvent e) {
				AmbitObject o = selectItem(0, isEditable());
			}
		};
		JButton b = new JButton(action);
		b.setFocusable(false);
		t.add(b);
		
		action = new AbstractActionWithTooltip("",
				UITools.createImageIcon("ambit/ui/images/resultset_previous.png"),"Previous item") {
			public void actionPerformed(ActionEvent e) {
				AmbitObject o =  selectItem(currentIndex-1, isEditable());
			}
		};
		b = new JButton(action);
		b.setFocusable(false);
		t.add(b);		
		
		t.add(currentIndexLabel);
		action = new AbstractActionWithTooltip("",
				UITools.createImageIcon("ambit/ui/images/resultset_next.png"),"Next item") {
			public void actionPerformed(ActionEvent e) {
				AmbitObject o =  selectItem(currentIndex+1, isEditable());
			}
		};
		b = new JButton(action);
		b.setFocusable(false);
		t.add(b);		
		
		action = new AbstractActionWithTooltip("",
				UITools.createImageIcon("ambit/ui/images/resultset_last.png"),"Last item") {
			public void actionPerformed(ActionEvent e) {
				AmbitObject o = selectItem(list.size()-1, isEditable());
				
			}
		};
		b = new JButton(action);
		b.setFocusable(false);
		t.add(b);				
		t.setPreferredSize(new Dimension(200,24));
		t.setMinimumSize(new Dimension(200,24));
		return t;
	}
	
	public int getCurrentIndex() {
		return currentIndex;
	}
	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
		if (currentIndexLabel!=null)
			if (list != null)
			currentIndexLabel.setText(Integer.toString(currentIndex+1) + "/" + list.size());
			else currentIndexLabel.setText("");
	}
	
	@Override
	public AmbitObject selectItem(int index, boolean editable) {
		
		if ((currentIndex >=0) && (currentIndex < list.size())) {
			AmbitObject o = list.getItem(currentIndex);
			if (o != null) o.setSelected(false);
		}
		
		if (index < 0) index = 0;
		if (index >= list.size()) index = list.size()-1;
		AmbitObject o = super.selectItem(index, editable);
		if (o!= null) {
			setCurrentIndex(index);
			o.setSelected(true);
		}
		else setCurrentIndex(-1);
		return o;
	}
	public void deleteSelected() throws DbAmbitException {
		if (list == null) return;
		AmbitObject o = list.getItem(currentIndex);
		
		if (o != null) {
				if (JOptionPane.showConfirmDialog(this, 
						"Delete "+list.getItem(currentIndex).toString()+ "?"
						)==JOptionPane.OK_OPTION) {
					
					list.remove(currentIndex);	
				}
				
		}
		if (list.size() > 0)
			selectItem(currentIndex-1, false);
		else editorPanel.setEditor(null);		
	}
	@Override
	public void setAmbitList(AmbitList list) {
		super.setAmbitList(list);
		if ((list != null) && (list.size()>0) && (list.getItem(0)!=null)) {
			selectItem(0,list.isEditable());
			if (!list.isEditable()) setNoDataText("No data available");
		}
		
	}
}
