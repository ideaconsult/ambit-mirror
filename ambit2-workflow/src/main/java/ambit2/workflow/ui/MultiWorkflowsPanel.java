/*
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
package ambit2.workflow.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.Action;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import nplugins.core.Introspection;
import nplugins.shell.INPluginUI;
import nplugins.shell.INanoPlugin;
import ambit2.core.data.ClassHolder;
import ambit2.ui.editors.ClassHolderEditor;
import ambit2.workflow.IMultiWorkflowsPlugin;

import com.jgoodies.binding.list.IndirectListModel;
import com.microworkflow.process.Workflow;

/**
 * User interface for multiple workflows plugins. 
 * Workflows are arranged as icons and instantiated on double click.
 * @author nina
 *
 */
public class MultiWorkflowsPanel<P extends IMultiWorkflowsPlugin> extends JPanel implements
		INPluginUI<INanoPlugin> {
	protected IMultiWorkflowsPlugin plugin;
    private IndirectListModel<ClassHolder> listModel;
    private JList       objectList;	
    protected Action action;
    protected int cellSize = 64;
	public int getCellSize() {
		return cellSize;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2128986015636595519L;

	public MultiWorkflowsPanel(IMultiWorkflowsPlugin plugin,Action action) {
		this(plugin,action,64);
	}
	public MultiWorkflowsPanel(IMultiWorkflowsPlugin plugin,Action action, int cellSize) {
	        super(new BorderLayout());
	        this.cellSize = cellSize;
	        this.action = action;
	        setBackground(Color.white);
	        setPlugin(plugin);	        
	        setObject(plugin.getWorkflows());
	        add(new JScrollPane(objectList));
	        setToolTipText("Double click to run the selected workflow");
	        objectList.setToolTipText(getToolTipText());
	}

	public void setObject(List<ClassHolder> object) {
		if (listModel == null)
			listModel = new IndirectListModel<ClassHolder>(object);
		else {
			listModel.setList(object);
		}
		if (objectList == null) {
			objectList = new JList(listModel) ;
			/*{
				@Override
				public String getToolTipText(MouseEvent evt) {
			           // Get item index
		            int index = locationToIndex(evt.getPoint());
		            ClassHolder item = (ClassHolder)getModel().getElementAt(index);
		            return item.getDescription();

				}
			};
			*/
			objectList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			objectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			objectList.setCellRenderer(ClassHolderEditor.createListCellRenderer(getCellSize()));
			objectList.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount() == 2){
					     int index = objectList.locationToIndex(e.getPoint());
					     ListModel dlm = objectList.getModel();
					     Object item = dlm.getElementAt(index);;
					     objectList.ensureIndexIsVisible(index);
					     
					     try {
					    	 runWorkflow((ClassHolder)item);
					     } catch (Exception x) {
					    	 x.printStackTrace();
					     }
					     }

				}
			});
		}
		

	}	
	
	protected void runWorkflow(ClassHolder clazz) throws Exception  {
		Object o = Introspection.loadCreateObject(clazz.getClazz()); 
		if ( o instanceof Workflow) {
			plugin.getWorkflow().setDefinition(((Workflow) o).getDefinition());
			try {
				action.setEnabled(false);
				action.actionPerformed(null);
			} catch (Exception x) {
				throw new Exception(x);
			} finally {
				action.setEnabled(true);
			}
		}
	}
	public Component getComponent() {
		return this;
	}

	public IMultiWorkflowsPlugin getPlugin() {
		return plugin;
	}

	public void setPlugin(INanoPlugin plugin) {
		this.plugin = (IMultiWorkflowsPlugin)plugin;
		
	}
	@Override
	public String toString() {

		return "Options";
	}
}
