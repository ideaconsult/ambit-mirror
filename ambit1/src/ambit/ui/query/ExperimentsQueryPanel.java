/* QueryPanel.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-12 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit.ui.query;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.Enumeration;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import ambit.database.query.ExperimentConditionsQuery;
import ambit.database.query.ExperimentQuery;
import ambit.database.query.TemplateFieldQuery;
import ambit.ui.AmbitColors;
import ambit.ui.actions.AmbitAction;
import ambit.ui.actions.search.DbExperimentsSearchAction;
import ambit.ui.data.experiment.StudyListModel;

/**
 * USer interface to specify query by experimental data. See example for {@link ambit.database.data.AmbitDatabaseToolsData}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-12
 */
public class ExperimentsQueryPanel extends JPanel implements ChangeListener   {
    AmbitAction studyAction = null;
    AmbitAction experimentAction = null;
	protected ExperimentsQueryTableModel tableModel;
	protected ExperimentsQueryTableModel conditionsTableModel;
	protected StudyListModel studyModel;
    protected JScrollPane scrollPane;
    protected JTabbedPane tPane;
    protected JTable studyTable; 
    protected ExperimentQuery experiments;
    protected JTable table;
    /**
     * 
     */
    public ExperimentsQueryPanel(ExperimentQuery list, ExperimentConditionsQuery conditions,  ActionMap actions) {
        super();
        addWidgets(list,conditions,actions);
    }

    /**
     * @param isDoubleBuffered
     */
    public ExperimentsQueryPanel(ExperimentQuery list, ExperimentConditionsQuery conditions,  ActionMap actions,boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        addWidgets(list,conditions,actions);
    }

    /**
     * @param layout
     */
    public ExperimentsQueryPanel(ExperimentQuery list, ExperimentConditionsQuery conditions,  ActionMap actions,LayoutManager layout) {
        super(layout);
        addWidgets(list,conditions,actions);
    }

    /**
     * @param layout
     * @param isDoubleBuffered
     */
    public ExperimentsQueryPanel(ExperimentQuery list,  ExperimentConditionsQuery conditions, ActionMap actions,LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
        addWidgets(list,conditions,actions);
    }
    protected void addWidgets(ExperimentQuery list, ExperimentConditionsQuery conditions, ActionMap actions) {
    	this.experiments = list;

        setLayout(new BorderLayout());
        add(new JLabel("Search by experiment results"),BorderLayout.NORTH);
        setBackground(AmbitColors.BrightClr);
        setForeground(AmbitColors.DarkClr);

        tPane = new JTabbedPane();
        
        tableModel = new ExperimentsQueryTableModel(list);
        table = new JTable(tableModel,getColumnModel(tableModel));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowSelectionAllowed(false);
        table.setRowHeight(24);
        table.setToolTipText("Specify test results query");
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(false);
        scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(420,360));
        scrollPane.setMinimumSize(new Dimension(370,120));        
        scrollPane.setAutoscrolls(true);
        tPane.addTab("Test results",scrollPane);

        conditionsTableModel = new ExperimentsQueryTableModel(conditions);
        JTable tableConditions = new JTable(conditionsTableModel,getColumnModel(conditionsTableModel));
        tableConditions.getTableHeader().setReorderingAllowed(false);
        tableConditions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableConditions.setRowSelectionAllowed(false);
        tableConditions.setRowHeight(24);
        tableConditions.setToolTipText("Specify test results query");
        tableConditions.setShowHorizontalLines(true);
        tableConditions.setShowVerticalLines(false);
        JScrollPane scrollPane1 = new JScrollPane(tableConditions);
        scrollPane1.setPreferredSize(new Dimension(420,360));
        scrollPane1.setMinimumSize(new Dimension(370,120));        
        scrollPane1.setAutoscrolls(true);
        tPane.addTab("Test conditions",scrollPane1);        
        tPane.addChangeListener(this);


        studyAction = null;
        if (actions != null) {
            experimentAction = (AmbitAction) actions.get("Search by experiments");
        	add(new DescriptorSearchActionPanel(list,
        	        experimentAction,"Search by experiments"),BorderLayout.SOUTH);
        	studyAction = (AmbitAction)actions.get("Experiments by Study");
        }
        
        
        studyModel = new StudyListModel(list.getStudyList());
        studyTable = new JTable(studyModel);
        studyTable.getTableHeader().setReorderingAllowed(false);
        studyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    ListSelectionModel m = new DefaultListSelectionModel();
	    studyTable.setSelectionModel(m);
	    m.addListSelectionListener(new ListSelectionListener() {
	       /* (non-Javadoc)
         * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
         */
        public void valueChanged(ListSelectionEvent e) {
 	        if (e.getValueIsAdjusting()) return;

 	        ListSelectionModel lsm =
 	            (ListSelectionModel)e.getSource();
 	        if (!lsm.isSelectionEmpty()) { 
 	            
 	        	experiments.getStudyList().setSelectedIndex(lsm.getMinSelectionIndex());
 	        	experimentsByStudy();
 	        	
 	        }
	    }});
        JScrollPane listScroller = new JScrollPane(studyTable);
        listScroller.setPreferredSize(new Dimension(300, 100));
        listScroller.setBorder(BorderFactory.createTitledBorder("Available studies"));
        //p.add(listScroller,BorderLayout.CENTER);
        //p.add(new JButton(studyAction),BorderLayout.EAST);
        
        JSplitPane p = new JSplitPane(JSplitPane.VERTICAL_SPLIT,listScroller,tPane);
        p.setDividerLocation(150);
        
        add(p,BorderLayout.CENTER);

        
    }    

    protected TableColumnModel getColumnModel(AbstractTableModel tableModel) {
		TableColumnModel columnModel = new DefaultTableColumnModel();
		TableColumn column = new TableColumn(0);
		columnModel.addColumn(column);
        column.setHeaderValue(tableModel.getColumnName(0));
		column = new TableColumn(1);
        column.setHeaderValue(tableModel.getColumnName(1));
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
        	public Component getTableCellRendererComponent(JTable table, Object arg1, boolean arg2, boolean arg3, int row, int column) {
        		Component c = super.getTableCellRendererComponent(table, arg1, arg2, arg3, row, column);
        		//Object o = table.getValueAt(row, column);
        		setToolTipText(table.getValueAt(row, column).toString());
        		return c;
        	}
        };
       
        column.setCellRenderer(renderer);
		columnModel.addColumn(column);
        
		TableColumn conditionColumn = new TableColumn(2);
        conditionColumn.setHeaderValue(tableModel.getColumnName(2));
        JComboBox comboBox = new JComboBox();
        for (int i=0; i < TemplateFieldQuery.conditions.length;i++)
            comboBox.addItem(TemplateFieldQuery.conditions[i]);
        conditionColumn.setCellEditor(new DefaultCellEditor(comboBox));
        renderer = new DefaultTableCellRenderer();
        renderer.setToolTipText("Click here ");
        conditionColumn.setCellRenderer(renderer);
        renderer = new BorderCellRenderer();
        renderer.setToolTipText("Double click here to edit ");
        columnModel.addColumn(conditionColumn);
        
        
        column = new TableColumn(3);
        column.setCellRenderer(renderer);
        column.setHeaderValue(tableModel.getColumnName(3));
        columnModel.addColumn(column);

        column = new TableColumn(4);
        column.setHeaderValue(tableModel.getColumnName(4));
        columnModel.addColumn(column);

        column = new TableColumn(5);
        column.setCellRenderer(renderer);
        column.setHeaderValue(tableModel.getColumnName(5));
        columnModel.addColumn(column);
        
        

        
        
        /*
		int[] pSize = {32,240,64,32,32,32};
		int[] mSize = {24,120,64,32,32,32};
		Enumeration columns = columnModel.getColumns();
		int i=0;
		while (columns.hasMoreElements()) {
			TableColumn col = (TableColumn) columns.nextElement();
			col.setPreferredWidth(pSize[i]);		col.setMinWidth(mSize[i]);			
			columnModel.addColumn(col);
			i++;
		}
        re
        */
        return columnModel;
    }


	public void setExperiments(ExperimentQuery experiments,ExperimentConditionsQuery conditions) {
		this.experiments = experiments;
		((ExperimentsQueryTableModel) tableModel).setExperiments(experiments);
		((ExperimentsQueryTableModel) conditionsTableModel).setExperiments(conditions);
		studyModel.setStudyList(experiments.getStudyList());
		//TODO Update List
		//listbox. = new JList(studyList.getStudyList().toArray()); //data has type Object[]
	}
	protected void experimentsByStudy() {
	    if (studyAction !=null) {
	    	studyAction.run(null);
	    	studyAction.done();
	    }
	}
	/* (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        ((DbExperimentsSearchAction) experimentAction).setResultsQuery(tPane.getSelectedIndex()==0);
    }
}


