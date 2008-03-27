package ambit2.database.writers;

import java.awt.Component;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;

import ambit2.config.AmbitCONSTANTS;
import ambit2.database.DbConnection;
import ambit2.exceptions.AmbitException;
import ambit2.exceptions.DescriptorCalculationException;
import ambit2.log.AmbitLogger;
import ambit2.ui.editors.DescriptorsHashtableEditor;
import ambit2.ui.editors.IAmbitEditor;
import ambit2.data.descriptors.DescriptorDefinition;
import ambit2.data.descriptors.DescriptorsHashtable;
import ambit2.database.core.DbDescriptorValues;
import ambit2.database.core.DbDescriptors;
import ambit2.database.exception.DbAmbitException;

/**
 * Abstract class to write descriptor values to database. Descriptors are expected as molecule properties
 *  
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public abstract class AbstractDescriptorWriter extends AmbitDatabaseWriter implements IAmbitEditor {
	public static AmbitLogger logger = new AmbitLogger(AbstractDescriptorWriter.class);
	public static final String ERR_NOVALUE="No descriptor value!\t";
	public static final String ERR_UNKNOWNTYPE="Unknown descriptor value type\t"; 	
	protected DescriptorsHashtable descriptorLookup;
	protected DbDescriptors dWriter;
	protected DbDescriptorValues vWriter;
	protected IAmbitEditor editor = null;

	public AbstractDescriptorWriter(DbConnection conn) { 
		this(conn,null,null);

	}
	
	public AbstractDescriptorWriter(DbConnection conn, Object descriptor, DescriptorDefinition ambitDescriptor) {
		super();
		initwriters(conn);
		descriptorLookup = new DescriptorsHashtable();
		descriptorLookup.addDescriptorPair(descriptor,ambitDescriptor);

	}
	public AbstractDescriptorWriter(DbConnection conn, DescriptorsHashtable descriptors) {
		super();
		initwriters(conn);
		descriptorLookup = descriptors;
	}	
	protected void initwriters(DbConnection conn) {
		try {
			dWriter = new DbDescriptors(conn);
			dWriter.initialize();
			dWriter.initializeInsert();
			vWriter = new DbDescriptorValues(conn);
			vWriter.initialize();
			vWriter.initializeInsert();
		} catch (DbAmbitException x) {
			dWriter = null;
			vWriter = null;
		}

	}
	public void addDescriptorPair(Object descriptor, DescriptorDefinition ambitDescriptor) {
		descriptorLookup.put(descriptor,ambitDescriptor);		
	}

	protected int getStructureId(IChemObject object) throws CDKException {
        Object id = object.getProperty(AmbitCONSTANTS.AMBIT_IDSTRUCTURE);
        if (id == null) throw new CDKException(AmbitCONSTANTS.AMBIT_IDSTRUCTURE+" not defined!");
        
        try {
        	int idstructure = ((Integer) id).intValue();
        	if (idstructure == -1) throw new CDKException(AmbitCONSTANTS.AMBIT_IDSTRUCTURE+" not defined!");
            return idstructure;
        } catch (Exception x) {
            throw new CDKException(AmbitCONSTANTS.AMBIT_IDSTRUCTURE+" not valid!");
        }
	}
	protected abstract double getDescriptorValue(IChemObject object,Object descriptor) throws AmbitException  ;

	public void write(IChemObject object) throws CDKException {
		write(getStructureId(object),object);
	}
	public void write(int idstructure,IChemObject object) throws CDKException {		
			double value;
			int iddescriptor = -1;
			Object key = null;
			DescriptorDefinition ambitDescriptor = null;
			
			Enumeration keys = descriptorLookup.keys();
			while (keys.hasMoreElements()) {
				key = keys.nextElement();
				try{
					try {
						ambitDescriptor = descriptorLookup.getAmbitDescriptor(key); 
						if (ambitDescriptor != null) {
							iddescriptor = dWriter.addDescriptor((DescriptorDefinition)ambitDescriptor);
							value = getDescriptorValue(object,key);
							writeDescriptorValue(iddescriptor, idstructure, value, 0);
							//vWriter.addValue(iddescriptor, idstructure, value, 0);
						}
					} catch (DescriptorCalculationException x) {
							writeNullDescriptorValue(iddescriptor, idstructure);
							//vWriter.addNullValue(iddescriptor, idstructure,0,0);
							logger.warn(x);
					}		
				} catch (Exception x) {
					logger.error(key,x);
				}				
			}
	}
	
	protected void writeDescriptorValue(int iddescriptor, int idstructure, double value, double error) throws DbAmbitException {
		vWriter.addValue(iddescriptor, idstructure, value, 0);
	}
	protected void writeNullDescriptorValue(int iddescriptor, int idstructure) throws DbAmbitException {
		vWriter.addNullValue(iddescriptor, idstructure, 0, 0);
	}	


	public void close() throws IOException {
		try {
			if (vWriter !=null) vWriter.close(); vWriter = null;
			if (dWriter != null) dWriter.close(); dWriter = null;
		} catch (SQLException x) {
			throw new IOException(x.toString());
		}

	}
	public String toString() {
		return "Writes desriptors to database";
	}
	/* (non-Javadoc)
     * @see ambit2.processors.IAmbitEditor#getJComponent()
     */
    public JComponent getJComponent() {
        if (editor==null) editor = new DescriptorsHashtableEditor(descriptorLookup);
        return editor.getJComponent();
    }
    /* (non-Javadoc)
     * @see ambit2.processors.IAmbitEditor#view(javax.swing.JComponent, boolean)
     */
    public boolean view(Component parent, boolean editable, String title) throws AmbitException {
        return JOptionPane.showConfirmDialog(parent,getJComponent(),toString(),JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;

    }
    public void setEditable(boolean editable) {
    	// TODO Auto-generated method stub
    	
    }
	public boolean isEditable() {
		return true;
	}    
}
