package ambit2.workflow.test;

import javax.swing.JOptionPane;

import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.workflow.ui.StructureRecordEditor;

public class StructureRecordEditorTest {
	
	public static void main(String[] args) {
		
		StructureRecord record = new StructureRecord();
		record.setProperty(Property.getNameInstance(),"formaldehyde");
		record.setProperty(Property.getCASInstance(),"50-00-0");
		record.setProperty(Property.getEINECSInstance(),"??");
		StructureRecordEditor editor = new StructureRecordEditor(null);
		editor.setObject(record);
		JOptionPane.showMessageDialog(null,editor.getJComponent());
		record.setProperty(Property.getNameInstance(),"formaldessssssssssse");
		editor.setObject(record);
		JOptionPane.showMessageDialog(null,editor.getJComponent());		
	}
}
