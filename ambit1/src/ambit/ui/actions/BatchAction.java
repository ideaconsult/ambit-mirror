/*
 * Created on 2005-10-23
 *
 */
package ambit.ui.actions;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.io.listener.IReaderListener;
import org.openscience.cdk.io.setting.IOSetting;

import ambit.data.ISharedData;
import ambit.database.data.AmbitDatabaseToolsData;
import ambit.database.writers.QueryResults;
import ambit.io.AmbitFileFilter;
import ambit.io.AmbitSettingsListener;
import ambit.io.FileInputState;
import ambit.io.FileOutputState;
import ambit.io.HTMLTableWriter;
import ambit.io.IteratingFileReader;
import ambit.io.MyIOUtilities;
import ambit.io.PDFWriter;
import ambit.io.XLSFileWriter;
import ambit.io.batch.DefaultBatchProcessing;
import ambit.io.batch.DefaultBatchStatistics;
import ambit.io.batch.EmptyBatchConfig;
import ambit.io.batch.IBatch;
import ambit.io.batch.IBatchStatistics;
import ambit.processors.DefaultAmbitProcessor;
import ambit.processors.IAmbitProcessor;
import ambit.ui.DelimitersPanel;
import ambit.ui.batch.BatchProcessingDialog;

/**
 * An {@link ambit.ui.actions.AmbitAction} that can be used to create and starts a batch {@link ambit.io.batch.IBatch}.
 * Provides methods to specify 
 * {@link IIteratingChemObjectReader}, {@link IChemObjectWriter} and {@link ambit.processors.IAmbitProcessor}
 * in order to create the  {@link ambit.io.batch.IBatch}. <br> 
 * If {@link #isInteractive()} is true, {@link ambit.ui.batch.BatchProcessingDialog} will be displayed waiting for the user to press start button. 
 * Otherwise the batch will start automatically.
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-10-23
 */
public abstract class BatchAction extends AmbitAction {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -552838223008338517L;
	protected boolean interactive = true;
	protected IBatch batch;
	protected QueryResults queryResults=null;

	/**
	 * 
	 * @param userData Arbitrary data
	 * @param mainFrame {@link JFrame} parent frame
	 */
	public BatchAction(Object userData, Container mainFrame) {
		this(userData,mainFrame,"Batch processing");
		
	}

	/**
	 * 
	 * @param userData Arbitrary data
	 * @param mainFrame {@link JFrame} parent frame
	 * @param name {@link javax.swing.Action} name
	 */
	public BatchAction(Object userData, Container mainFrame, String name) {
		this(userData,mainFrame,name,null);
	}

	/**
	 * 
	 * @param userData Arbitrary data
	 * @param mainFrame {@link JFrame} parent frame
	 * @param name {@link javax.swing.Action} name
	 * @param icon
	 */
	public BatchAction(Object userData, Container mainFrame, String name, Icon icon) {
		super(userData,mainFrame,name, icon);
		putValue(AbstractAction.MNEMONIC_KEY,new Integer(KeyEvent.VK_B));
		putValue(AbstractAction.SHORT_DESCRIPTION,"");		
	}

	public abstract IIteratingChemObjectReader getReader();
	public abstract IChemObjectWriter getWriter();
	public IAmbitProcessor getProcessor() {
		IAmbitProcessor processor = new DefaultAmbitProcessor();
				
		return processor;
	}
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void run(ActionEvent arg0) {
		try {
			
		    IBatchStatistics stats = getBatchStatistics();
		    long now = System.currentTimeMillis();
		    
			IIteratingChemObjectReader reader = getReader();
			stats.setTimeElapsed(IBatchStatistics.RECORDS_READ, System.currentTimeMillis()-now);
			if (reader != null) {
				if (queryResults == null) queryResults = new QueryResults();
				queryResults.setName(getValue(AbstractAction.NAME).toString());
				now = System.currentTimeMillis();
				IChemObjectWriter writer = getWriter();
				stats.setTimeElapsed(IBatchStatistics.RECORDS_WRITTEN, System.currentTimeMillis()-now);
				if (writer != null) {
					now = System.currentTimeMillis();
					IAmbitProcessor processor = getProcessor();
					stats.setTimeElapsed(IBatchStatistics.RECORDS_PROCESSED, System.currentTimeMillis()-now);
					if (processor != null) {

					    stats.clear();
						batch = null;
						try {
						    super.run(arg0);
							batch = new DefaultBatchProcessing(reader,writer,processor,new EmptyBatchConfig(),stats);
							if (interactive) {
								BatchProcessingDialog d = new BatchProcessingDialog(batch,(JFrame)mainFrame,true);
								if (mainFrame != null) {
									Dimension dim = mainFrame.getToolkit().getScreenSize();
									Rectangle abounds = mainFrame.getBounds();
							  	    d.setLocation((dim.width - abounds.width) / 2,  (dim.height - abounds.height) / 2);
								}
								d.show();			
							} else  batch.start();
						} catch (Exception x ) {
							((ISharedData) userData).getJobStatus().setError(x);
							x.printStackTrace();
						} finally {
						    completed();
						    ((ISharedData)userData).getJobStatus().setError(batch.getStatus().getError());
						}
					}
				}
			}

		} catch (Exception ex) {
			((ISharedData) userData).getJobStatus().setError(ex);
			//JOptionPane.showMessageDialog(mainFrame,ex.toString(),"Error ",JOptionPane.OK_OPTION); 
			ex.printStackTrace();
		}
	}

    public void completed() {
    	
    }
    public IIteratingChemObjectReader getFileReader(String defaultDir) {
        if (userData instanceof ISharedData) defaultDir = ((ISharedData) userData).getDefaultDir();
        DelimitersPanel accessory = new DelimitersPanel();
        File file = MyIOUtilities.selectFile(mainFrame,null,
                defaultDir,
                FileInputState.extensions,FileInputState.extensionDescription,true,accessory);
        return getFileReader(file,defaultDir,accessory.getFormat());
    }
    public IIteratingChemObjectReader getFileReader(File file,String defaultDir) {
    	return getFileReader(file,defaultDir);
    }
	public IIteratingChemObjectReader getFileReader(File file,String defaultDir,IChemFormat format) {
		if (file != null) {
			String s = file.getAbsolutePath();
			int p = s.lastIndexOf(File.separator);
			if (p > 0)
			    defaultDir = s.substring(0,p);
			else defaultDir = s;
			try {
				IIteratingChemObjectReader reader = new IteratingFileReader(file,format);
				reader.addChemObjectIOListener(getReaderListener());
				if (userData instanceof ISharedData) ((ISharedData) userData).setDefaultDir(defaultDir);
				return reader;
			} catch (Exception x) {
				logger.error(x);
				return null;
			}
		} else return null;
	}
	protected IReaderListener getReaderListener() {
	    return new AmbitSettingsListener(mainFrame,IOSetting.LOW);
	}	
	public IChemObjectWriter getFileWriter(String defaultDir) {
		File file = MyIOUtilities.selectFile(mainFrame, null,defaultDir, 
				FileOutputState.extensions,FileOutputState.extensionDescription, false);
		if (file != null) {
			try {
				String e = AmbitFileFilter.getSuffix(file);
                if (e == null) return  FileOutputState.getWriter(
                        new FileOutputStream(new File(file.getAbsoluteFile()+".sdf")),".sdf");
				if (e.toLowerCase().equals(".html")) return new HTMLTableWriter(new FileOutputStream(file));
				if (e.toLowerCase().equals(".pdf")) return new PDFWriter(new FileOutputStream(file));
				if (e.toLowerCase().equals(".xls")) return new XLSFileWriter(new FileOutputStream(file));
				else return FileOutputState.getWriter(new FileOutputStream(file),file.getName());
			} catch (Exception x) {
				logger.error(x);	
			}	
		}
		return null;
	}
    public IBatchStatistics getBatchStatistics() {
    	IBatchStatistics bs = null;
    	if (userData instanceof AmbitDatabaseToolsData) 
			bs = ((AmbitDatabaseToolsData) userData).getBatchStatistics();
		else bs = new DefaultBatchStatistics();
    	bs.setResultCaption("Processed ");
    	return bs;
    }
    public synchronized boolean isInteractive() {
        return interactive;
    }
    public synchronized void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

}
