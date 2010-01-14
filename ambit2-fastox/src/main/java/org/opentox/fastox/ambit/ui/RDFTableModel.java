package org.opentox.fastox.ambit.ui;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.rest.rdf.RDFResourceIterator;
import ambit2.rest.rdf.OT.OTClass;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

public class RDFTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4024336889955365662L;
	protected OTClass openToxObject;
	protected List<Resource> resources;
	protected OntModel records;
	protected Hashtable<Integer,Boolean> selected = null;
	
	public RDFTableModel(OTClass openToxObject) {
		super();
		this.openToxObject = openToxObject;
		resources = new ArrayList<Resource>();
	}
	public OntModel getRecords() {
		return records;
	}

	public void setRecords(OntModel records) {
		try {
			if (selected==null) selected = new Hashtable<Integer,Boolean>(); else selected.clear();
			resources.clear();
			this.records = records;
			RDFResourceIterator iterator = new RDFResourceIterator(records,openToxObject.toString());
			while (iterator.hasNext()) {
				Resource resource  = iterator.next();
				resources.add(resource);
			}
			iterator.close();
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			fireTableStructureChanged();
		}
	}
	
	public int getColumnCount() {
		return resources==null?0:1;
	}

	public int getRowCount() {
		return resources==null?0:resources.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return resources.get(rowIndex);
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (columnIndex > 0) return;
		if (selected == null) selected = new Hashtable<Integer,Boolean>();
		selected.put(rowIndex,(Boolean) value);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex==0;
	}



}
