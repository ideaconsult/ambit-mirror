package org.opentox.fastox.ambit.ui;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.opentox.rdf.OT.DataProperty;
import org.opentox.rdf.OT.OTClass;
import org.opentox.rdf.OT.OTProperty;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import ambit2.base.io.DownloadTool;
import ambit2.rest.rdf.RDFObjectIterator;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class RDFStructuresTableModel extends RDFTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1716786056288801268L;
	protected RDFTableModel features = new RDFTableModel(OTClass.Feature);
	
	public RDFStructuresTableModel() {
		super(OTClass.DataEntry);
	}
	@Override
	public void setRecords(OntModel records) {
		features.setRecords(records);
		super.setRecords(records);
	}
	@Override
	public int getColumnCount() {
		return features.getRowCount()+2;
	}
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (records == null) return null;
		switch (columnIndex) {
		case 0: {
			if (selected!=null) {
				Boolean b = selected.get(rowIndex);
				if (b==null) return false; else return b;
			}
			else return false;
		}
		case 1: 
			try { 
				Statement compound = resources.get(rowIndex).getProperty(OTProperty.compound.createProperty(records));
				BufferedImage image = getImage(new Reference(String.format("%s?w=250&h=250",compound.getObject().toString())));
				return image;
			}catch (Exception x) { 
				return super.getValueAt(rowIndex, columnIndex);
			}
		default: {
			StmtIterator values =  records.listStatements(resources.get(rowIndex),OTProperty.values.createProperty(records),(RDFNode)null);
			try {
				while (values.hasNext()) {
					StmtIterator st =  records.listStatements((Resource)values.next().getObject(),OTProperty.feature.createProperty(records),(RDFNode)features.getValueAt(columnIndex-2,0));
					try {
						while (st.hasNext()) {
							Resource r = st.next().getSubject();
							RDFNode value = r.getProperty(DataProperty.value.createProperty(records)).getObject();
							if (value.isLiteral()) {
								RDFDatatype datatype = ((Literal)value).getDatatype();
								if (XSDDatatype.XSDdouble.equals(datatype)) 
									return ((Literal)value).getDouble();
								else if (XSDDatatype.XSDfloat.equals(datatype)) 
									return ((Literal)value).getFloat();
								else if (XSDDatatype.XSDinteger.equals(datatype)) 
									return ((Literal)value).getInt();		
								else if (XSDDatatype.XSDstring.equals(datatype)) 
									return ((Literal)value).getString();
							} 
							return value.toString();	
						}
					} finally {
						st.close();
					}
				}
			} finally {
				values.close();
			}
			return null;
		}
		}
	}
    @Override
    public String getColumnName(int column) {
		switch (column) {
		case 0: return "Select";
		case 1: return "Structure";
		default: 
			try {
				return RDFObjectIterator.getTitle((RDFNode)features.getValueAt(column-2,0));
			} catch (Exception x) {
				return features.getValueAt(column-2,0).toString();
			}
		}
    }
    protected BufferedImage getImage(Reference reference) {
    	try {
			File file = new File(String.format("%s.png", Reference.encode(reference.toString())));
			if (!file.exists()) {
		    	ClientResource client = new ClientResource(reference);
		    	Representation r = client.get(MediaType.IMAGE_PNG);
		    	DownloadTool.download(r.getStream(), file);
			}
			return ImageIO.read(file);
    	} catch (Exception x) {
    		return null;
    	}
    	
    }
}
