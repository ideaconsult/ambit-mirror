/*
 * Created on 2005-9-5
 *
 */
package ambit.ui.batch;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import ambit.io.batch.BatchProcessingException;
import ambit.io.batch.DefaultBatchProcessing;
import ambit.io.batch.IBatch;
import ambit.io.batch.IBatchStatistics;
import ambit.io.batch.IJobStatus;
import ambit.ui.LabelErrorViewer;


/**
 * UI for batch processing {@link ambit.io.batchold.IBatchProcessing}
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-9-5
 */
public class BatchProcessingDialog extends AbstractJobProcessingDialog implements Observer {
	JPanel readerEditor, writerEditor, processorEditor;
	
	JLabel  stateLabel, progressLabel;
	//JProgressBar progressBar;
	IBatch batch;
	protected LabelErrorViewer errorViewer;
	
	protected TitledBorder border;

	
	

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3314347684369161157L;

	/**
	 * @throws HeadlessException
	 */
	public BatchProcessingDialog(IBatch batch) throws HeadlessException {
		super();
		setBatch(batch);

	}

	/**
	 * @param owner
	 * @throws HeadlessException
	 */
	public BatchProcessingDialog(IBatch batch, Dialog owner) throws HeadlessException {
		super(owner);
		setBatch(batch);
	}

	/**
	 * @param owner
	 * @param modal
	 * @throws HeadlessException
	 */
	public BatchProcessingDialog(IBatch batch,Dialog owner, boolean modal)
			throws HeadlessException {
		super(owner, modal);
		setBatch(batch);
	}

	/**
	 * @param owner
	 * @throws HeadlessException
	 */
	public BatchProcessingDialog(IBatch batch,Frame owner) throws HeadlessException {
		super(owner);
		setBatch(batch);
	}

	/**
	 * @param owner
	 * @param modal
	 * @throws HeadlessException
	 */
	public BatchProcessingDialog(IBatch batch,Frame owner, boolean modal)
			throws HeadlessException {
		super(owner, modal);
		setBatch(batch);
	}

	/**
	 * @param owner
	 * @param title
	 * @throws HeadlessException
	 */
	public BatchProcessingDialog(IBatch batch,Dialog owner, String title)
			throws HeadlessException {
		super(owner, title);
		setBatch(batch);
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws HeadlessException
	 */
	public BatchProcessingDialog(IBatch batch,Dialog owner, String title, boolean modal)
			throws HeadlessException {
		super(owner, title, modal);
		setBatch(batch);
	}

	/**
	 * @param owner
	 * @param title
	 * @throws HeadlessException
	 */
	public BatchProcessingDialog(IBatch batch,Frame owner, String title)
			throws HeadlessException {
		super(owner, title);
		setBatch(batch);
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws HeadlessException
	 */
	public BatchProcessingDialog(IBatch batch,Frame owner, String title, boolean modal)
			throws HeadlessException {
		super(owner, title, modal);
		setBatch(batch);
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 * @throws HeadlessException
	 */
	public BatchProcessingDialog(IBatch batch,Dialog owner, String title, boolean modal,
			GraphicsConfiguration gc) throws HeadlessException {
		super(owner, title, modal, gc);
		setBatch(batch);
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 */
	public BatchProcessingDialog(IBatch batch,Frame owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		setBatch(batch);
	}

	/* (non-Javadoc)
	 * @see toxTree.ui.AbstractJobProcessingDialog#createMainPanel()
	 */
	protected JPanel createMainPanel() {
		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		
		JPanel p = new JPanel();
		border = BorderFactory.createTitledBorder("Batch processing options");
		p.setBorder(border);
		Dimension d = new Dimension(400,300);
		p.setPreferredSize(d);
		p.setMinimumSize(d);
		
		p.setLayout(new BorderLayout());
		//p.setLayout(new BorderLayout());
		d = new Dimension(Integer.MAX_VALUE,64);
	
		readerEditor = new JPanel(new BorderLayout());
		readerEditor.setMinimumSize(d);
		readerEditor.setMaximumSize(d);
		readerEditor.setPreferredSize(d);
		readerEditor.setBorder(BorderFactory.createTitledBorder("Input data"));
		p.add(readerEditor,BorderLayout.NORTH);
		
		processorEditor =  new JPanel(new BorderLayout());
		processorEditor.setMinimumSize(d);
		processorEditor.setMaximumSize(d);
		processorEditor.setPreferredSize(d);
		p.add(processorEditor,BorderLayout.CENTER);
		processorEditor.setBorder(BorderFactory.createTitledBorder("Processing"));
		
		writerEditor =  new JPanel(new BorderLayout());
		writerEditor.setMinimumSize(d);
		writerEditor.setMaximumSize(d);
		writerEditor.setPreferredSize(d);
		p.add(writerEditor,BorderLayout.SOUTH);
		writerEditor.setBorder(BorderFactory.createTitledBorder("Output data"));
        
		JPanel progressPanel = new JPanel(new BorderLayout());
        
        progressPanel.setBorder(BorderFactory.createTitledBorder("Batch status"));
        progressPanel.setLayout(new BorderLayout());

        Dimension d1 = new Dimension(150,24);
        stateLabel = new JLabel("");
        stateLabel.setMinimumSize(d1);
        stateLabel.setPreferredSize(d1);
        stateLabel.setVisible(true);
        progressLabel = new JLabel("");
        progressLabel.setMinimumSize(d1);
        progressLabel.setPreferredSize(d1);
        progressLabel.setVisible(true);
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(stateLabel,BorderLayout.WEST);
        JLabel l = new JLabel("Records processed ");
        l.setVisible(true);
        l.setPreferredSize(d1);
        topPanel.add(l,BorderLayout.CENTER);
        topPanel.add(progressLabel,BorderLayout.EAST);
        topPanel.setMinimumSize(new Dimension(400,24));
        topPanel.setPreferredSize(new Dimension(450,24));
                
        
        progressPanel.add(topPanel,BorderLayout.CENTER);
        if (errorViewer == null)  errorViewer = new LabelErrorViewer();
        progressPanel.add(errorViewer.getView(),BorderLayout.EAST);
        
        /*
        progressBar = new JProgressBar();
        progressPanel.add(progressBar,BorderLayout.SOUTH);
        progressBar.setIndeterminate(false);
        progressBar.setVisible(false);
        */
        main.add(p,BorderLayout.CENTER);
        main.add(progressPanel,BorderLayout.SOUTH);
		return main;
	}

	/* (non-Javadoc)
	 * @see toxTree.ui.AbstractJobProcessingDialog#cancelAction()
	 */
	protected void cancelAction() {
		
		try {
			if (batch != null) {
				batch.cancel();
				if (batch instanceof Observable)
					((Observable)batch).deleteObservers();
			JOptionPane.showMessageDialog(null,
					batch.toString().substring(1,100)+ "...","Batch processing aborted!",JOptionPane.OK_OPTION,null);
			}
		} catch (BatchProcessingException x) {
			JOptionPane.showMessageDialog(null,
					x.getMessage(),"Batch processing aborted!",JOptionPane.OK_OPTION,null);
			x.printStackTrace();
		}
		super.cancelAction();
	}

	/* (non-Javadoc)
	 * @see toxTree.ui.AbstractJobProcessingDialog#okAction()
	 */
	protected void okAction() {
		super.okAction();
		enableDataControls(false);
        batch();
	}
	/* (non-Javadoc)
	 * @see toxTree.ui.AbstractJobProcessingDialog#pauseAction()
	 */
	protected void pauseAction() {
		try {
			batch.pause();
			super.pauseAction();
		} catch (BatchProcessingException x) {
			x.printStackTrace();
		}

	}
	/* (non-Javadoc)
	 * @see toxTree.ui.AbstractJobProcessingDialog#addWidgets()
	 */
	protected void addWidgets() {
		super.addWidgets();
		okButton.setText("Start");
	}
	protected void batch() {
		if (batch instanceof Observable) ((Observable)batch).addObserver(this);
		/*
		try {
		
			if (batch == null) {
				DefaultBatchProcessing b = new DefaultBatchProcessing(openFile,saveFile,processor,null);
				this.batch = b;
			} else if (batch instanceof Observable) ((Observable) batch).deleteObservers();
		
			if (batch instanceof Observable) ((Observable)batch).addObserver(this);
		} catch (BatchProcessingException x) {
			batch  = null;
			jobFinished(x);
			setErrorMessage(x);
			
			return;
		}
		*/
    	
        final ambit.ui.GUIWorker worker = new ambit.ui.GUIWorker() {
            public Object construct() {
            	try {
            		setErrorMessage(null);
            		batch.start();
            	} catch (BatchProcessingException x) {
            		x.printStackTrace();
            		batch  = null;
            		jobFinished(x);
            		setErrorMessage(x);
            		JOptionPane.showMessageDialog(null,x.getMessage(),"Error on batch processing",JOptionPane.OK_OPTION);
            	}               	
                return batch;
            }
            //Runs on the event-dispatching thread.
            public void finished() {
            	try {
            	    /*
            		if (batch != null) {
            			batch.closeInput();
            			batch.closeOutput();
            		}
            		*/	
            		jobFinished(null);
            	} catch (Exception x) {
            		x.printStackTrace();
            		JOptionPane.showMessageDialog(null,x.getMessage(),"Error on batch processing",JOptionPane.OK_OPTION);
            	}
        		    	
            }
        };
        worker.start(); 	
	}
	protected void jobFinished(Exception x) {
		if ((x == null) && (batch != null)) {
			if (batch.getStatus().isPaused()) {
				okButton.setVisible(true);
				okButton.setText("Continue");
				pauseButton.setVisible(false);
				cancelButton.setVisible(true);
				
			} else {
				if (batch instanceof Observable) ((Observable)batch).deleteObservers();
				okButton.setVisible(true);
				pauseButton.setVisible(false);
				cancelButton.setVisible(false);
				result = JOptionPane.OK_OPTION;
				/*
				if (!batch.getStatus().isCancelled())
					JOptionPane.showMessageDialog(getParent(),
							"Batch completed!",						
							batch.toString(),
						    JOptionPane.INFORMATION_MESSAGE);
						   */ 				
				setVisible(false);
			}
		} else { //stopped because of exception	
			if (batch != null) {
				if (batch instanceof Observable) ((Observable)batch).deleteObservers();
				batch = null;
			}
			enableDataControls(true);
			okButton.setVisible(true);
			pauseButton.setVisible(false);
			cancelButton.setVisible(true);
			
		}
	}
	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {

		if (batch == null) return;
		if (o instanceof IJobStatus) {
			IJobStatus b = (IJobStatus) o;
			boolean running = (b.isStatus(IJobStatus.STATUS_RUNNING));
			/*
			progressBar.setIndeterminate(running);
			progressBar.setVisible(running || b.isStatus(IJobStatus.STATUS_PAUSED));
			*/
			if (errorViewer != null)
				errorViewer.setError(b.getError());
			stateLabel.setText(b.toString());
			progressLabel.setToolTipText("");
			pauseButton.setVisible(running || b.isStatus(IJobStatus.STATUS_PAUSED));
		} else if (o instanceof IBatchStatistics) {
		    IBatchStatistics b = (IBatchStatistics) o;
		    long r = b.getRecords(IBatchStatistics.RECORDS_PROCESSED);
		    if ((r % 10) == 0)
		        progressLabel.setText(Long.toString(r));

			/*
			if (b.getConfigFile() != null)
			configLabel.setText("Batch configuration: " +
					new SimpleDateFormat("M/d/y,H:m",Locale.US).format(b.getDateLastProcessed()) + 
					" " + b.getConfigFile().getName());
					*/
		}

	}
	/**
	 * @return Returns the batch.
	 */
	public synchronized IBatch getBatch() {
		return batch;
	}
	/**
	 * @param batch The batch to set.
	 */
	public void setBatch(IBatch batch) {
		
		this.batch = batch;
		((Observable) batch.getStatus()).addObserver(this);
		((Observable) batch.getBatchStatistics()).addObserver(this);
		if (batch != null) 
			if (batch instanceof DefaultBatchProcessing) {
				DefaultBatchProcessing b = (DefaultBatchProcessing) batch;
					cleanPausedBatch();
   					
   					this.batch = b;
   					JLabel l = new JLabel(b.getInput().toString());
   					l.setPreferredSize(new Dimension(200,32));
   					l.setMinimumSize(new Dimension(100,24));
   					readerEditor.add(l,BorderLayout.CENTER);
   					l = new JLabel(b.getOutput().toString());
   					l.setPreferredSize(new Dimension(200,32));
   					l.setMinimumSize(new Dimension(100,24));
   					writerEditor.add(l,BorderLayout.CENTER);
   					processorEditor.add(b.getProcessor().getEditor().getJComponent(),BorderLayout.CENTER);
   					if (batch.getStatus().isPaused()) {
   						okButton.setVisible(true);
   						okButton.setText("Continue");
   						pauseButton.setVisible(false);
   						cancelButton.setVisible(true);
   					}

			}
			
	}
	public void enableDataControls(boolean enable) {

	}
	protected void cleanPausedBatch() {
		/*
		if ((batch !=null) && (batch.isPaused())) {
			try {
				batch.cancel();
				batch.close();
			} catch (BatchProcessingException x) {
				setErrorMessage(x);
				x.printStackTrace();
				batch = null;
			}
		}
		*/
	}
	protected void setErrorMessage(BatchProcessingException x) {
		if (x != null) { 
			stateLabel.setText("Error");
			progressLabel.setText(x.getMessage());
			progressLabel.setToolTipText(x.getMessage());
		} else {
			
			stateLabel.setText("");
			progressLabel.setText("");
			progressLabel.setToolTipText("");			
		}
	}


	public void showAndStart() {
		show();
		okAction();
	}
	
}
