package ambit2.ui.actions.process;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ambit2.database.DbConnection;
import ambit2.external.DescriptorMopacShell;
import ambit2.data.descriptors.DescriptorDefinition;
import ambit2.data.descriptors.DescriptorFactory;
import ambit2.data.descriptors.DescriptorGroups;
import ambit2.data.literature.LiteratureEntry;
import ambit2.data.literature.ReferenceFactory;
import ambit2.database.data.ISharedDbData;
import ambit2.database.readers.DbStructureMissingDescriptorsReader;
import ambit2.database.writers.PropertyDescriptorWriter;
import ambit2.io.batch.DefaultBatchProcessing;
import ambit2.io.batch.EmptyBatchConfig;
import ambit2.io.batch.IBatchStatistics;
import ambit2.processors.descriptors.MopacProcessor;
import ambit2.ui.actions.AmbitAction;

/**
 * Reads structures without {@link ambit2.processors.descriptors.MopacProcessor} descriptors from the database, calculates descriptors and writes back to the database.
 * seems like a duplicate for {@link ambit2.ui.actions.process.CalculateMOPACAction} ...
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class DbMOPACAction extends AmbitAction {

	public DbMOPACAction(Object userData, JFrame mainFrame) {
		super(userData, mainFrame,"Calculate electronic descriptors by WinMOPAC");
		// TODO Auto-generated constructor stub
	}

	public DbMOPACAction(Object userData, JFrame mainFrame, String arg0) {
		super(userData, mainFrame, arg0);
		// TODO Auto-generated constructor stub
	}

	public DbMOPACAction(Object userData, JFrame mainFrame, String arg0, Icon arg1) {
		super(userData, mainFrame, arg0, arg1);
		// TODO Auto-generated constructor stub
	}
    /* (non-Javadoc)
     * @see ambit2.ui.actions.AmbitAction#run(java.awt.event.ActionEvent)
     */
    public void run(ActionEvent arg0) {
		if (userData instanceof ISharedDbData) {
		    ISharedDbData dbaData = ((ISharedDbData) userData);
			DbConnection conn = dbaData.getDbConnection();
			if ((conn ==null) || conn.isClosed()) JOptionPane.showMessageDialog(mainFrame, "Use Database/Open first");
			
			try {
				DescriptorMopacShell shell = new DescriptorMopacShell();
				MopacProcessor processor = new MopacProcessor(shell);
			    DbStructureMissingDescriptorsReader reader;

			    
			    DescriptorGroups g = new DescriptorGroups();
			    g.addItem(DescriptorFactory.createDescriptorElectronicGroup());
			    LiteratureEntry ref = ReferenceFactory.createEmptyReference();
			    DescriptorDefinition descriptor = new DescriptorDefinition("MOPAC",ref);
			    
			  			    
			    DefaultBatchProcessing batch;
			    while (true) {
			        reader = new DbStructureMissingDescriptorsReader(conn,descriptor,1,1000);
			        PropertyDescriptorWriter writer = new PropertyDescriptorWriter(conn,"EHOMO",descriptor);

			        //DescriptorDefinition d = Des.createDescriptor_eLumo(g,ref);
			        
				    writer.addDescriptorPair("ELUMO",descriptor);
				    writer.addDescriptorPair("ELUMO",descriptor);
				    /*
			        //d.setRemark("Energy of the highest occupied molecular orbital");
				    writer.addDescriptorPair("FINAL HEAT OF FORMATION",MopacShell.createDescriptor_FinalHeatOfFormation(g,ref));
				    writer.addDescriptorPair("TOTAL ENERGY",MopacShell.createDescriptor_TotalEnergy(g,ref));
				    writer.addDescriptorPair("ELECTRONIC ENERGY",MopacShell.createDescriptor_ElectronicEnergy(g,ref));
				    writer.addDescriptorPair("CORE-CORE REPULSION",MopacShell.createDescriptor_CoreCoreRepulsion(g,ref));
				    writer.addDescriptorPair("IONIZATION POTENTIAL",MopacShell.createDescriptor_IonizationPotential(g,ref));
				    writer.addDescriptorPair("MOLECULAR WEIGHT",MopacShell.createDescriptor_MolWeight(g,ref));
						*/
			        batch = new DefaultBatchProcessing(
						reader,
						writer,
						processor,
						new EmptyBatchConfig());
			    	//BatchProcessingDialog d = new BatchProcessingDialog(batch,mainFrame,true);
			        batch.start();
					//d.show();
			        
			        long records = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_READ);
			        reader = null;
			        writer = null;
			        if (records == 0) 
			            break;
			        
			    }	
			    
			} catch (Exception x) {
				x.printStackTrace();
			    JOptionPane.showMessageDialog(mainFrame, x.toString());
			}
		}	
    }

}
