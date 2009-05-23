package ambit2.plugin.pbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;

import javax.swing.Action;
import javax.swing.table.AbstractTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nplugins.core.Introspection;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.processors.structure.CloneProcessor;
import ambit2.core.processors.structure.HydrogenAdderProcessor;
import ambit2.descriptors.processors.DescriptorCalculationProcessor;
import ambit2.descriptors.processors.DescriptorResultFormatter;
import ambit2.plugin.pbt.processors.ProcessorProxy;

public class PBTTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6027199356653273448L;
	public static final String ATTRIBUTE_ROWS="rows";
	public static final String ATTRIBUTE_COLUMNS="columns";
	public static final String ATTRIBUTE_ROW="row";
	public static final String ATTRIBUTE_COL="col";
	public static final String ATTRIBUTE_COLSPAN="colspan";
	public static final String ATTRIBUTE_VALUE="value";
	
	public static final String NODE_STRUCTURE="structure";
	public static final String NODE_TITLE="title";
	public static final String NODE_ACTION="action";	
	public static final String NODE_SECTION="section";
	public static final String NODE_LIST="list";
	public static final String NODE_FORMULA="formula";
	public static final String NODE_INPUT="input";
	public static final String NODE_ERROR="error";
	
	protected Hashtable<Cell, Object> table;
	public Hashtable<Cell, Object> getTable() {
		return table;
	}

	protected int rows=0;
	protected int columns = 0;
	protected Cell query = new Cell(0,0);
	
	public PBTTableModel() {
		super();
		table = new Hashtable<Cell, Object>();
	}
	public int getColumnCount() {
		return columns;
	}

	public void setColumnCount(int columns) {
		this.columns = columns;
		fireTableStructureChanged();
	}
	public void setRowCount(int rows) {
		this.rows = rows;
		fireTableStructureChanged();
	}	
	public int getRowCount() {
		return rows;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		query.setRow(rowIndex);
		query.setColumn(columnIndex);
		Object value;
		if ((value = table.get(query)) != null) 
			return value;
		return null;
	}
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		
		Cell cell = new Cell(rowIndex,columnIndex);
		if (value == null) table.remove(cell);
		else table.put(cell,value);	
		fireTableDataChanged();
	}
	public void setDefinition(String definition) throws IOException, ParserConfigurationException, SAXException {
	    InputStream in = PBTTableModel.class.getClassLoader().getResourceAsStream(definition);
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	     
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document doc = builder.parse(new InputSource(new InputStreamReader(in)));
	    
	    Element root = doc.getDocumentElement();
	    setRowCount(Integer.parseInt(root.getAttribute(ATTRIBUTE_ROWS)));
	    setColumnCount(Integer.parseInt(root.getAttribute(ATTRIBUTE_COLUMNS)));
	    
	    addNode(root);
	    fireTableStructureChanged();
//	    System.out.println(table);
	} 
	
	public void addNode(Element node) {
		try {
			int row = Integer.parseInt(node.getAttribute(ATTRIBUTE_ROW));
			int col = Integer.parseInt(node.getAttribute(ATTRIBUTE_COL));
			int colspan = Integer.parseInt(node.getAttribute(ATTRIBUTE_COLSPAN));
			
			if (NODE_TITLE.equals(node.getNodeName())) {
				try {
					Cell cell = new Cell(row,col,colspan);
					table.put(cell,node.getTextContent().trim());
				} catch (NumberFormatException x) {
					x.printStackTrace();
				}
			} else
			if (NODE_ACTION.equals(node.getNodeName())) {
					Cell cell = new Cell(row,col,colspan);
					try {
						table.put(cell,node.getTextContent().trim());
						table.put(cell,createAction(node));
						cell.setMode(Cell.CELL_MODE.ACTION);						
						
					} catch (NumberFormatException x) {
						x.printStackTrace();
						table.put(cell,node.getTextContent().trim());
					}
			} else						
			if (NODE_STRUCTURE.equals(node.getNodeName())) {
					try {
						Cell cell = new Cell(row,col,colspan);
						table.put(cell,NoNotificationChemObjectBuilder.getInstance().newMolecule());
						cell.setMode(Cell.CELL_MODE.STRUCTURE);
					} catch (NumberFormatException x) {
						x.printStackTrace();
					}
			} else				
			if (NODE_ERROR.equals(node.getNodeName())) {
				try {
					Cell cell = new Cell(row,col,colspan);
					table.put(cell,node.getTextContent().trim());
					cell.setMode(Cell.CELL_MODE.ERROR);
				} catch (NumberFormatException x) {
					x.printStackTrace();
				}
			} else				
			if (NODE_LIST.equals(node.getNodeName())) {
					try {
						Cell cell = new Cell(row,col,colspan);
						cell.setMode(Cell.CELL_MODE.LIST);
						NodeList items = node.getChildNodes();
						ArrayList<String> pickup = new ArrayList<String>();
						for (int i=0; i < items.getLength();i++) {
							if (items.item(i).getNodeType() == Node.ELEMENT_NODE)
								pickup.add(items.item(i).getTextContent().trim());
						}
						//table.put(cell,node.getTextContent().trim());
						table.put(cell,pickup);
					} catch (NumberFormatException x) {
						x.printStackTrace();
					}
			} else		
			if (NODE_FORMULA.equals(node.getNodeName())) {
				try {
					Cell cell = new Cell(row,col,colspan);
					cell.setMode(Cell.CELL_MODE.FORMULA);
					table.put(cell,node.getTextContent().trim());
				} catch (NumberFormatException x) {
					x.printStackTrace();
				}
			} else
			if (NODE_INPUT.equals(node.getNodeName())) {
				try {
					Cell cell = new Cell(row,col,colspan);
					cell.setMode(Cell.CELL_MODE.INPUT);
					table.put(cell,node.getTextContent().trim());
				} catch (NumberFormatException x) {
					x.printStackTrace();
				}
			} else
				if (NODE_SECTION.equals(node.getNodeName())) {
					try {
						Cell cell = new Cell(row,col,colspan);
						cell.setMode(Cell.CELL_MODE.SECTION);
						
						table.put(cell,node.getAttribute(ATTRIBUTE_VALUE).trim());
					} catch (NumberFormatException x) {
						x.printStackTrace();
					}				
				
		}
		} catch (Exception x) {
			
		}			
		    NodeList nodes = node.getChildNodes();
		    for (int i=0; i < nodes.getLength();i++) {
		    	if (nodes.item(i) instanceof Element)
		    		addNode((Element)nodes.item(i));
		    }
		}

	
	protected WorksheetAction createAction(Element node) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		final WorksheetAction<IAtomContainer, String> action = new WorksheetAction<IAtomContainer, String>(node.getTextContent());
		action.setResultRow(Integer.parseInt(node.getAttribute("resultrow")));
		action.setResultCol(Integer.parseInt(node.getAttribute("resultcol")));
		action.setTargetRow(Integer.parseInt(node.getAttribute("sourcerow")));
		action.setTargetCol(Integer.parseInt(node.getAttribute("sourcecol")));
		action.setErrorRow(Integer.parseInt(node.getAttribute("errorRow")));
		action.setErrorCol(Integer.parseInt(node.getAttribute("errorCol")));		
		action.setResultExtended("extended".equals(node.getAttribute("result")));
		action.setSourceExtended("extended".equals(node.getAttribute("source")));
		action.putValue(Action.SHORT_DESCRIPTION, node.getAttribute("hint"));
		int resultIndex;
		try {
			resultIndex = Integer.parseInt(node.getAttribute("index").toString());
		} catch (Exception x) {
			resultIndex = -1;
		}
		
		ProcessorsChain<IAtomContainer, String, IProcessor> chain = new ProcessorsChain<IAtomContainer, String, IProcessor>();
		chain.setAbortOnError(true);
		if ((node.getAttribute("descriptor") != null) && !"".equals(node.getAttribute("descriptor")) ) {
			Object d = Introspection.loadCreateObject(node.getAttribute("descriptor"));
			if (d instanceof IMolecularDescriptor) {
				chain.add(new CloneProcessor());				
				chain.add(new HydrogenAdderProcessor());
				
				if ((node.getAttribute("builder3d") != null) && (node.getAttribute("builder3d").equals("true"))) {
					//add 3d builder
					try {
						ProcessorProxy proxy = new ProcessorProxy();
						proxy.setClassName("ambit2.mopac.MopacShell");
						chain.add(proxy);
					} catch (Throwable x) {
						x.printStackTrace();
					}
				}
				DescriptorCalculationProcessor descriptors = new DescriptorCalculationProcessor((IMolecularDescriptor)d);
				chain.add(descriptors);
				chain.add(new DescriptorResultFormatter(Locale.US,resultIndex));
			}
		} else 	if (node.getAttribute("processor") != null) {
			ProcessorProxy proxy = new ProcessorProxy();
			proxy.setClassName(node.getAttribute("processor"));
			chain.add(proxy);
			/*
			Object p = Introspection.loadCreateObject(node.getAttribute("processor"));
			if (p instanceof IProcessor)
				chain.add((IProcessor)p);
				*/
		}	
		action.setProcessor(chain);	
		return action;
	}	
}

class Cell implements Comparable<Cell> {
	public enum CELL_MODE {
		SECTION,TITLE,FORMULA,LIST,INPUT,ERROR,STRUCTURE,ACTION
	};
	protected CELL_MODE mode;
	int row;
	int column;
	int colspan = 0;
	int rowspan=1;
	
	public int getRowspan() {
		return rowspan;
	}
	public void setRowspan(int rowspan) {
		this.rowspan = rowspan;
	}
	public int getColspan() {
		return colspan;
	}
	public void setColspan(int colspan) {
		this.colspan = colspan;
	}
	public CELL_MODE getMode() {
		return mode;
	}
	public void setMode(CELL_MODE mode) {
		this.mode = mode;
	}
	public Cell(int row,int col) {
		this(row,col,1);
	}
	public Cell(int row,int col, int colspan) {
		setRow(row);
		setColumn(col);
		setColspan(colspan);
		setMode(CELL_MODE.TITLE);
	}	
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}
	@Override
	public String toString() {
		return Integer.toString(row) + "," + Integer.toString(column);
	}
	@Override
	public int hashCode() {
	    	int hash = 7;
	    	int var_code = row;
	    	hash = 31 * hash + var_code; 
	    	var_code = column;
	    	hash = 31 * hash + var_code; 
	
	    	return hash;
	}
	public int compareTo(Cell o) {
		int r = row -o.row;
		if (r == 0)
			return column - o.column;
		else return r;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Cell) {
			return (row ==((Cell)obj).row) && 
			(column == ((Cell)obj).column);
		} else return false;
	}

}