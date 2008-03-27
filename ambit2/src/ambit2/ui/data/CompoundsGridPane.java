/*
 * Created on 2006-3-5
 *
 */
package ambit2.ui.data;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;

import ambit2.ui.editors.JournalEntryEditor;
import ambit2.ui.editors.LiteratureEntryEditor;
import ambit2.ui.editors.SourceDatasetEditor;
import ambit2.data.AmbitCellEditor;
import ambit2.data.AmbitList;
import ambit2.data.AmbitObject;
import ambit2.data.literature.JournalEntry;
import ambit2.data.literature.LiteratureEntry;
import ambit2.data.molecule.AmbitPoint;
import ambit2.data.molecule.Compound;
import ambit2.data.molecule.CompoundsList;
import ambit2.data.molecule.SourceDataset;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-3-5
 */
public class CompoundsGridPane extends JScrollPane {
    protected JTable table = null;
    /**
     * 
     */
    public CompoundsGridPane(TableModel model,Dimension cellSize) {
        super(addWidgets(model,cellSize));
        table = (JTable)getViewport().getComponent(0);
    }    
    public CompoundsGridPane(CompoundsList list, int columns) {
        this(new GridTableModel(list,columns),new Dimension(200,200));
    }
    public CompoundsGridPane(CompoundsList list, int columns, Dimension cellSize) {
        super(addWidgets(new GridTableModel(list,columns),cellSize));
        table = (JTable)getViewport().getComponent(0);
    }    


    protected static JTable addWidgets(TableModel model, Dimension cellSize) {

        JTable table = new JTable(model);
        table.setTableHeader(null);
        for (int i=0; i < table.getColumnCount(); i++) {
	        TableColumn column = table.getColumnModel().getColumn(i);
	        column.setPreferredWidth(cellSize.width);
        }
        table.setRowHeight(cellSize.height + 24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(true);

        
        table.setDefaultRenderer(Compound.class, new MoleculeGridCellRenderer(cellSize));
        table.setDefaultRenderer(IAtomContainer.class, new MoleculeGridCellRenderer(cellSize));
        table.setDefaultRenderer(IMolecule.class, new MoleculeGridCellRenderer(cellSize));
        table.setDefaultRenderer(AmbitPoint.class, new MoleculeGridCellRenderer(cellSize));
        table.setDefaultRenderer(Image.class, new ImageCellRenderer());
        
        table.setDefaultRenderer(SourceDataset.class, new SourceDatasetEditor("Dataset",null));
        table.setDefaultRenderer(LiteratureEntry.class, new LiteratureEntryEditor());
        table.setDefaultRenderer(JournalEntry.class, new JournalEntryEditor());
        //table.setDefaultRenderer(AuthorEntries.class, new AuthorEntriesEditor());
        
        table.setDefaultEditor(AmbitObject.class,new AmbitCellEditor());
        
        table.setPreferredScrollableViewportSize(new Dimension(cellSize.width*3, (cellSize.height+30)*2));
        return table;
    }
    public AmbitList getList() {
        return ((ICompoundsListTableModel) table.getModel()).getList();
    }
    public void setList(AmbitList list) {
        ((ICompoundsListTableModel) table.getModel()).setList(list);
    }    
    public AmbitObject getSelected() {
    	try {
    		Object o = table.getModel().getValueAt(table.getSelectedRow(),table.getSelectedColumn());
    		return (AmbitObject) o;
    	} catch (Exception x) {
    		return null;
    	}
    }
}
