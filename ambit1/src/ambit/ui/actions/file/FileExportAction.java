package ambit.ui.actions.file;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.listener.IChemObjectIOListener;
import org.openscience.cdk.io.setting.IOSetting;

import ambit.data.IDataContainers;
import ambit.data.ISharedData;
import ambit.data.molecule.PropertyTranslator;
import ambit.io.FileWithHeaderWriter;
import ambit.io.batch.IBatchStatistics;
import ambit.ui.UITools;
import ambit.ui.actions.BatchAction;
import ambit.ui.data.SelectPropertiesPanel;

/**
 * Exports query results to a file. See example at {@link ambit.database.data.AmbitDatabaseToolsData}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class FileExportAction extends BatchAction {
	protected ArrayList<String> selected = null;
	public FileExportAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Save");
	}

	public FileExportAction(Object userData, JFrame mainFrame, String name) {
		this(userData, mainFrame, name,UITools.createImageIcon("ambit/ui/images/save_16.png"));
		
	}

	public FileExportAction(Object userData, JFrame mainFrame, String name,
			Icon icon) {
		super(userData, mainFrame, name, icon);
		 selected = new ArrayList<String>();
		interactive = false;
		putValue(AbstractAction.SHORT_DESCRIPTION, "Saves molecules from \"Molecule Browser\" to a file.");
	}
	public void completed() {
		if (userData instanceof ISharedData)  
			((IDataContainers) userData).getMolecules().setEnabled(true);
		super.completed();
	}
	public IIteratingChemObjectReader getReader() {
    	if (userData instanceof ISharedData) { 
    		((IDataContainers) userData).getMolecules().setEnabled(false);
			return ((IDataContainers) userData).getMolecules().getReader();
    	} else	return null;
	}

	public IChemObjectWriter getWriter() {
		final IChemObjectWriter writer = getFileWriter("");
		writer.addChemObjectIOListener(new IChemObjectIOListener() {
			public void processIOSettingQuestion(IOSetting arg0) {
				setFields(writer);	
			}
		});
		return writer;
	}
	   public IBatchStatistics getBatchStatistics() {
	    	
	    	IBatchStatistics bs = super.getBatchStatistics();
	    	bs.setResultCaption("Saved ");
	    	return bs;
	    }
	   
	  protected void setFields(IChemObjectWriter writer) {
		  if (!(writer instanceof FileWithHeaderWriter)) return; 
			if (((FileWithHeaderWriter)writer).getHeader() != null) return; 
			
			
	    	if (userData instanceof ISharedData) { 
	    		
	    		PropertyTranslator pt = ((IDataContainers) userData).getMolecules().getContainers().getAvailableProperties();
  	        SelectPropertiesPanel sp = new SelectPropertiesPanel(
  	        		pt
  	        		,new String[] {"identifiers","descriptors","endpoint"},
  	        		selected);
  	        if (JOptionPane.showConfirmDialog(mainFrame,sp,"Select data fields to export",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)       
  	                ==JOptionPane.OK_OPTION) {
  	            selected = sp.getFields();
  	            if (selected.size()>0) {
  	            	if (writer instanceof FileWithHeaderWriter) {
  	            		ArrayList<Object> header = new ArrayList();
  	                    for (int i= 0; i < selected.size();i++) {
  	                    	Object o = null;
  	                    	Hashtable h = pt.getDescriptorProperties();
  	                    	if (h!= null) {
  	                    		o = h.get(selected.get(i));
  	                    		if (o != null) header.add(o);
  	                    	}	
  	                    	h = pt.getIdentifiers();
  	                    	if (h!= null) {
  	                    		o = h.get(selected.get(i));  	                    	
  	                    		if (o != null) header.add(o);
  	                    	}	
  	                    	h=pt.getProperties(PropertyTranslator.type_results);
  	                    	if (h!= null) {
  	                    		o = h.get(selected.get(i));
  	                    		if (o != null) header.add(o);
  	                    	}	
  	                    }
  	                        
  	                    
  	            		((FileWithHeaderWriter) writer).setHeader(header);
  	            	}
  	            }
  	            	
  	        }
	    		
	    	}
	  }

}
