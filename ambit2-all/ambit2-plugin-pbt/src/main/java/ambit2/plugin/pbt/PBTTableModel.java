package ambit2.plugin.pbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
	public static final String ATTRIBUTE_LIST="list";
	public static final String ATTRIBUTE_TYPE="type";
	public static final String ATTRIBUTE_VALUE="value";
	
	public enum NODES {
		NODE_STRUCTURE {
			@Override
			public String getName() {
				return "structure";
			}
		},
		NODE_TITLE {
			@Override
			public String getName() {
				return "title";
			}
			
		},
		NODE_ACTION {
			@Override
			public String getName() {
				return "action";
			}
			
		},
		NODE_SECTION {
			@Override
			public String getName() {
				return "section";
			}
		},
		NODE_LIST {
			@Override
			public String getName() {
				return "list";
			}
		},		
		NODE_FORMULA {
			@Override
			public String getName() {
				return "formula";
			}
		},		
		NODE_INPUT {
			@Override
			public String getName() {
				return "input";
			}			
		},		
		NODE_ERROR {
			@Override
			public String getName() {
				return "error";
			}			
		};
		protected Object object = null;
		public abstract String getName();
		public Object getObject() {
			return object;
		}
		public void setObject(Object object) {
			this.object = object;
		}
	}
	
	protected List<Cell> table;
	public List<Cell> getTable() {
		return table;
	}

	protected int rows=0;
	protected int columns = 0;
	protected Cell query = new Cell(0,0);
	
	public PBTTableModel() {
		super();
		table = new ArrayList<Cell>();
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
		int index = table.indexOf(query);
		if (index >= 0) return table.get(index);
		else return null;
	}
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		query.setRow(rowIndex);
		query.setColumn(columnIndex);		
		int index = Collections.binarySearch(table,query,new Comparator<Cell>() {
			public int compare(Cell o1, Cell o2) {
				return o1.compareTo(o2);
			}
		});
		Cell oldValue = (index>=0)?table.get(index):null;
		if (oldValue == null) {
			Cell cell = new Cell(rowIndex,columnIndex);
			cell.setObject(value);
			table.add(cell);
			Collections.sort(table);
		} else ((Cell)oldValue).setObject(value);
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
	} 
	
	public void addNode(Element node) {
		try {
			int row = Integer.parseInt(node.getAttribute(ATTRIBUTE_ROW))-1;
			int col = Integer.parseInt(node.getAttribute(ATTRIBUTE_COL))-1;
			int colspan = Integer.parseInt(node.getAttribute(ATTRIBUTE_COLSPAN));
			
			NODES type = null;
			for (NODES nodeType: NODES.values())
				if (nodeType.getName().equals(node.getNodeName())) {
					type = nodeType;
					break;
				}
			if (type == null) return;
			
			switch (type) {
			case NODE_TITLE: 
				try {
					table.add(new Cell<String>(row,col,colspan,node.getTextContent().trim()));
					Collections.sort(table);
					break;
				} catch (NumberFormatException x) {
					x.printStackTrace();
					break;
				}
			case NODE_ACTION: {
					
					try {
						Cell<WorksheetAction> cell = new Cell<WorksheetAction>(row,col,colspan,createAction(node));
						cell.setMode(type);	
						table.add(cell);
						Collections.sort(table);
					} catch (NumberFormatException x) {
						x.printStackTrace();
					}
					break;
			} 
			case NODE_STRUCTURE: {
					try {
						Cell<IAtomContainer> cell = new Cell<IAtomContainer>(row,col,colspan,NoNotificationChemObjectBuilder.getInstance().newMolecule());
						cell.setMode(type);
						table.add(cell);
						Collections.sort(table);
						break;
					} catch (NumberFormatException x) {
						x.printStackTrace();
						break;
					} 
			} 
			case NODE_ERROR: {
				try {
					Cell<String> cell = new Cell<String>(row,col,colspan,node.getTextContent().trim());
					table.add(cell);
					Collections.sort(table);
					cell.setMode(type);
					break;
				} catch (NumberFormatException x) {
					x.printStackTrace();
					break;
				}
			}
			case NODE_LIST: {
					try {

						NodeList items = node.getChildNodes();
						ArrayList<String> pickup = new ArrayList<String>();
						for (int i=0; i < items.getLength();i++) {
							if (items.item(i).getNodeType() == Node.ELEMENT_NODE)
								pickup.add(items.item(i).getTextContent().trim());
						}
						Cell cell = new Cell(row,col,colspan,pickup);
						cell.setMode(type);
						table.add(cell);
						Collections.sort(table);
						break;
					} catch (NumberFormatException x) {
						x.printStackTrace();
						break;
					}
			} 
			case NODE_FORMULA: {
				try {
					Cell<String> cell = new Cell<String>(row,col,colspan,node.getTextContent().trim());
					cell.setMode(type);
					table.add(cell);
					Collections.sort(table);
					break;					
				} catch (NumberFormatException x) {
					x.printStackTrace();
					break;
				}
			} 
			case NODE_INPUT:
				try {
					Cell cell = new Cell(row,col,colspan);
					cell.setMode(type);
					if ("true".equals(node.getAttribute(ATTRIBUTE_LIST))) {
						cell.setObject(new ArrayList<String>());
					} 
					table.add(cell);
					Collections.sort(table);
					break;					
				} catch (NumberFormatException x) {
					x.printStackTrace();
					break;
				}
			case NODE_SECTION:
				 try {
						Cell<String> cell = new Cell<String>(row,col,colspan,node.getAttribute(ATTRIBUTE_VALUE).trim());
						cell.setMode(type);
						
						table.add(cell);
						Collections.sort(table);
						break;						
				} catch (NumberFormatException x) {
					x.printStackTrace();
					break; }				
				
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
		action.setResultRow(Integer.parseInt(node.getAttribute("resultrow"))-1);
		action.setResultCol(Integer.parseInt(node.getAttribute("resultcol"))-1);
		action.setTargetRow(Integer.parseInt(node.getAttribute("sourcerow"))-1);
		action.setTargetCol(Integer.parseInt(node.getAttribute("sourcecol"))-1);
		action.setErrorRow(Integer.parseInt(node.getAttribute("errorRow"))-1);
		action.setErrorCol(Integer.parseInt(node.getAttribute("errorCol"))-1);		
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
						ProcessorProxy<IAtomContainer,IAtomContainer> proxy = new ProcessorProxy<IAtomContainer,IAtomContainer>();
						proxy.setClassName("ambit2.mopac.MopacShell");
						chain.add(proxy);
					} catch (Throwable x) {
						x.printStackTrace();
					}
				}
				DescriptorCalculationProcessor descriptors = new DescriptorCalculationProcessor((IMolecularDescriptor)d);
				chain.add(descriptors);
				chain.add(new DescriptorResultFormatter(false,resultIndex));
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

