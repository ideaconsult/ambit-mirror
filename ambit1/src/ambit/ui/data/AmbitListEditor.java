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

package ambit.ui.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import ambit.data.AmbitList;
import ambit.data.AmbitObject;
import ambit.data.IAmbitSearchable;
import ambit.data.qmrf.AmbitListTableModelNew;
import ambit.ui.AmbitColors;
import ambit.ui.ColorTableCellRenderer;
import ambit.ui.UITools;
import ambit.ui.actions.AbstractActionWithTooltip;

public class AmbitListEditor extends AmbitListOneItemEditor {
	protected JSplitPane splitPane;
	protected JTable table;
	protected AmbitListTableModelNew model;
	
	public AmbitListEditor() {
		this(null,false,"",new Dimension(300,200));
	}
	public AmbitListEditor(AmbitList list, boolean searchPanel) {
		this(list,searchPanel,"",new Dimension(300,200));
	}
	public AmbitListEditor(AmbitList list, boolean searchPanel,Dimension dimension) {
		this(list,searchPanel,"",dimension);
	}	
	public AmbitListEditor(AmbitList list, boolean searchPanel, String title) {
		this(list,JSplitPane.VERTICAL_SPLIT,searchPanel,title,new Dimension(300,200));
	}
	
	public AmbitListEditor(AmbitList list, boolean searchPanel, String title,Dimension dimension) {
		this(list,JSplitPane.VERTICAL_SPLIT,searchPanel,title,dimension);
	}
	public AmbitListEditor(AmbitList list,int newOrientation, boolean searchPanel) {
		this(list,newOrientation,searchPanel,"",new Dimension(300,200));
	}
	public AmbitListEditor(AmbitList list,int newOrientation, boolean searchPanel,Dimension dimension) {
		this(list,newOrientation,searchPanel,"",dimension);
	}
	public AmbitListEditor(AmbitList list,int newOrientation, boolean searchPanel, String title,Dimension dimension) {
		super(list,list.isEditable(),title);
		add(editorPanel,BorderLayout.CENTER);
		add(createListPanel(list,searchPanel,dimension),BorderLayout.SOUTH);
		setBorder(BorderFactory.createTitledBorder(title));
		/*
		splitPane = new JSplitPane(newOrientation);
		add(splitPane,BorderLayout.CENTER);
		
		JComponent c = createListPanel(list,searchPanel);
		c.setMinimumSize(new Dimension(100,100));
		splitPane.setRightComponent(c);
		
		splitPane.setLeftComponent(editorPanel);
        splitPane.setDividerLocation(100);
        */
        
        if (list.size() > 0)
        	selectItem(0,isListEditable());
        else editorPanel.setEditor(null);
        setBorder(BorderFactory.createTitledBorder(title));
        
        
	}
	@Override
	protected void addWidgets() {

	}
	@Override
	protected int getToolbarOrientation() {
		return JToolBar.HORIZONTAL;
	}
	@Override
	protected String getToolbarPosition() {
		return BorderLayout.NORTH;
	}

	protected AmbitListTableModelNew createTableModel(AmbitList list) {
		return new AmbitListTableModelNew(list,false);
	}
	protected JComponent createListPanel(AmbitList list, boolean searchPanel,Dimension dimension) {
		model = createTableModel(list);
		table = new JTable(model, createColumnsModel(model));
		table.getTableHeader().setReorderingAllowed(false);
		table.setToolTipText("Click to see item details");
		table.setPreferredScrollableViewportSize(dimension);
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
        table.setTableHeader(null);
		//dataTable.setShowGrid(false);
		table.setForeground(AmbitColors.DarkClr);
		table.setBackground(Color.white);
		
		//scrollPane.setPreferredSize(prefDimension);
		//scrollPane.setMinimumSize(minDimension);
		//scrollPane.getViewport().setBackground(Color.white);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setCellSelectionEnabled(true);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				selectItem(table.rowAtPoint(e.getPoint()),isListEditable());
			}
		});
		
		table.setBackground(Color.white);
		table.setOpaque(true);

		JScrollPane sp = new JScrollPane(table);
		sp.setOpaque(true);
		sp.setBackground(Color.white);
//		table.getParent().setOpaque(true);
		table.getParent().setBackground(Color.white);
		sp.setMinimumSize(new Dimension(60,60));
		sp.setPreferredSize(dimension); //new Dimension(120,120)

		
        JComponent c = null;
        if (searchPanel) c = createSearchPanel();
		if (c == null)
			return sp;
		else {
			c.setMinimumSize(new Dimension(60,24));
			c.setPreferredSize(new Dimension(120,24));
			JPanel p = new JPanel(new BorderLayout());
			p.add(c,BorderLayout.NORTH);
			p.add(sp,BorderLayout.CENTER);
            p.setBackground(Color.white);
			return p;
		}
	}
	
	public JComponent createSearchPanel() {
		if (list instanceof IAmbitSearchable) {
			JToolBar b = new JToolBar();
			b.setFloatable(true);
			final JFormattedTextField field = new JFormattedTextField();
			field.setToolTipText("Enter text to search within the list.");
			b.add(field);
			final AbstractAction search = new AbstractActionWithTooltip("Find",
					UITools.createImageIcon("ambit/ui/images/search.png"),"Search within this catalog") {
				public void actionPerformed(ActionEvent e) {
					try {
					int found = ((IAmbitSearchable) list).search(field.getText(),false);
					if (found >=0)
						selectItem(found, false);
					else 
						selectItem(-1, false);
					} catch (Exception x) {
						x.printStackTrace();
					}
	
				}
			};
			b.add(search);
			/*
			field.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					search.actionPerformed(null);
				}
			});
			*/
			field.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					 int keyCode = e.getKeyCode();
			         if( keyCode == KeyEvent.VK_ENTER)
			        	 search.actionPerformed(null);
				}
			});
			return b;
		} else return null;
	}
	public AmbitObject selectItem(int index, boolean editable) {
		
		AmbitObject o = super.selectItem(index, editable);
		if ((table != null) && (o != null) && (currentIndex >=0) && (currentIndex< table.getRowCount())) {
			table.setRowSelectionInterval(currentIndex, currentIndex);
			table.scrollRectToVisible(table.getCellRect(currentIndex, 0, true));
			
		} 
		return o;
	}
	protected TableColumnModel createColumnsModel(TableModel model) {
	    TableColumnModel m = new DefaultTableColumnModel();
	    ColorTableCellRenderer r = new ColorTableCellRenderer();
	    int[] widths = {64,64,Integer.MAX_VALUE};
	    int[] minwidths = {32,32,200};
	    int start = 1;
	    
	    for (int i=0;i<model.getColumnCount();i++) {
	        TableColumn t = new TableColumn(i);
	        t.setPreferredWidth(widths[(i+start) % 3]);
	        t.setMinWidth(minwidths[(i+start) % 3]);
	        t.setHeaderValue(model.getColumnName(i));
	        if (i>1)
	        	t.setCellRenderer(r);
	        m.addColumn(t);
	    }
	    return m;
	}
	
	public void setAmbitList(AmbitList list) {
		super.setAmbitList(list);
		if (model != null)
			model.setList(list);
	}
}


