package ambit2.workflow.ui;

import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IAmbitEditor;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.core.data.MoleculeTools;
import ambit2.core.processors.structure.MoleculeWriter;
import ambit2.jchempaint.editor.StructureDiagramEditor;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.microworkflow.ui.MapTableModel;


public class StructureRecordEditor  implements IAmbitEditor<StructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7065417033780468740L;
	protected StructureRecord record;
	protected MapTableModel<Property, Object> propertiesModel;
	protected StructureDiagramEditor strucDiagram;
	protected JComponent component;
	
	public StructureRecordEditor() {
		this(null);
	}	
	public StructureRecordEditor(StructureRecord record) {
		setObject(record);
		component = buildPanel();
	}
	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
	            "100dlu,3dlu,300dlu",
				"pref,top:pref:grow");        
	     PanelBuilder panel = new PanelBuilder(layout);
	     panel.setDefaultDialogBorder();

	     CellConstraints cc = new CellConstraints();   

	     strucDiagram = new StructureDiagramEditor();
	     
	     panel.addSeparator("Identifiers", cc.xywh(1,1,1,1));
	     panel.addSeparator("Structure diagram", cc.xywh(3,1,1,1));
	     
	     JTable table = new JTable(propertiesModel) {
	    	 @Override
	    	public TableCellRenderer getCellRenderer(int row, int column) {
	    		TableCellRenderer r = super.getCellRenderer(row, column);
	    		/*
	    		if (r instanceof JLabel) {
	    			((JLabel) r).setHorizontalAlignment((column==0)?SwingConstants.RIGHT:SwingConstants.LEFT);
	    		}
	    		*/
	    		return r;
	    	}
	     };
	     
	     table.setRowSelectionAllowed(false);
	     table.setCellSelectionEnabled(false);
	     table.setRowHeight(24);
	     table.setTableHeader(null);
	     table.setShowGrid(false);
	     table.setBackground(panel.getPanel().getBackground());

	     //table.getColumnModel().getColumn(0).setCellRenderer(new Format)
	     JScrollPane pane = new  JScrollPane(table);
	     pane.setBorder(BorderFactory.createEmptyBorder());
	     panel.add(pane, cc.xywh(1,2,1,1));
	     panel.add(strucDiagram.getJComponent(), cc.xywh(3,2,1,1));
	     //c = BasicComponentFactory.createTextField(presentati9onModel.getModel("name"));
     
	     return panel.getPanel();
	}			
	
	public boolean confirm() {
		IMoleculeSet set = strucDiagram.getObject();
		IMolecule mol = MoleculeTools.newMolecule(NoNotificationChemObjectBuilder.getInstance());
		Iterator<IAtomContainer> i = set.molecules().iterator();
		while (i.hasNext())	mol.add(i.next());
		MoleculeWriter writer = new MoleculeWriter();
		try {
			getObject().setContent(writer.process(mol));
			getObject().setFormat(MOL_TYPE.SDF.toString());
			return true;
		} catch (Exception x) {
			getObject().setContent(null);
			getObject().setFormat(MOL_TYPE.SDF.toString());
			
			return false;
		}
		
	}

	public JComponent getJComponent() {
		return component;
	}

	public StructureRecord getObject() {
		return record;
	}

	public boolean isEditable() {
		return true;
	}

	public void setEditable(boolean editable) {
	}

	public void setObject(StructureRecord record) {
	
		this.record = record;
		if (propertiesModel == null) 
			propertiesModel = new MapTableModel<Property, Object>() {
				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return columnIndex>0;
				}
				@Override
				public void setValueAt(Object value, int rowIndex,
						int columnIndex) {
			        if (map !=null)
			        	map.put(getLabel(rowIndex),value);     
				}
			};
		
		if (record!= null) {
			propertiesModel.setMap(record.getMap());
			for (Property p : record.getProperties())
				propertiesModel.keyChange(p,null);
		} else propertiesModel.setMap(null);
		
	}

}

