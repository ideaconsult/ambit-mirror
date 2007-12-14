package ambit.database.old;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ambit.database.DbConnection;
import ambit.database.data.ISharedDbData;
import ambit.ui.actions.AmbitAction;

/**
 * 
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * @deprecated
 * <b>Modified</b> 2006-6-5
 */
public class DbHousekeeping extends AmbitAction {

	public DbHousekeeping(Object userData, JFrame mainFrame) {
		super(userData, mainFrame,"SMILES/Fingerprints/Atom environments generation");
		// TODO Auto-generated constructor stub
	}

	public DbHousekeeping(Object userData, JFrame mainFrame, String arg0) {
		super(userData, mainFrame, arg0);
		// TODO Auto-generated constructor stub
	}

	public DbHousekeeping(Object userData, JFrame mainFrame, String arg0,
			Icon arg1) {
		super(userData, mainFrame, arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public void run(ActionEvent arg0) {
		if (userData instanceof ISharedDbData) {
		    ISharedDbData dbaData = ((ISharedDbData) userData);
			DbConnection conn = dbaData.getDbConnection();
			if ((conn ==null) || conn.isClosed()) JOptionPane.showMessageDialog(mainFrame, "Use Database/Open first");
			else {
				// 0 - gamut, 1 - smiles, 2 - fingerprints 3- SSSR
				Object[] possibilities = {"SMILES", "Fingerprints","AtomEnvironments"};
				String s = (String)JOptionPane.showInputDialog(
				                    mainFrame,"Options",
				                    "Select what to generate",
				                    JOptionPane.PLAIN_MESSAGE,
				                    null,
				                    possibilities,
				                    "SMILES");

//				If a string was returned, say so.
				if ((s != null) && (s.length() > 0)) try {
					int mode= -1;
					for (int i=0;i < possibilities.length;i++)
						if (s.equals(possibilities[i])) {
							mode = i;
							break;
						}
							
					if (mode >= 0) {
//						super.run(arg0);
						try {
							//TODO generate fingreprints from structure, not smiles
							if (mode ==2) {
								Object[] scope = {"All", "Only newly added compounds"};
								String x = (String)JOptionPane.showInputDialog(
					                    mainFrame,"Options",
					                    "Generate Atom Environments for",
					                    JOptionPane.PLAIN_MESSAGE,
					                    null,
					                    scope,
					                    scope[1]);
								
								
								super.run(arg0); 
						        DbFPAtomEnvironments fpae = new DbFPAtomEnvironments(conn);
						        if (x.equals(scope[0])) 
						        	fpae.invalidateExisting();
								
						        fpae.setSqlSubstances("select substance.idsubstance,status from substance left join fpaeid using(idsubstance) where (status is null) or (status='invalid') limit 1000;");
						        fpae.setAction(fpae);
						        fpae.setLimit(1000);
						        fpae.setMaxRows(0);
						        while (true) {
						            fpae.run();
						            if (fpae.getRowsProcessed() == 0)
						                break;
						        }
						        fpae = null;
							} else {
								super.run(arg0);
								DbGamut gamut = new DbGamut(conn);
								//TODO UI for options 
								gamut.setCyclicBonds(37);
								gamut.setNullSmilesOnly(true);
			
								gamut.setMode(0);
								gamut.run();
								gamut.setMode(mode+1);
								gamut.run();
								gamut = null;
							}
							JOptionPane.showMessageDialog(mainFrame,s + " generation completed.");
						} catch (Exception x) {
							logger.error(x);
							JOptionPane.showMessageDialog(mainFrame,x.getMessage());	
							done();
						}
					}
				    return;
				} catch (Exception x) {
					logger.error(x);
					JOptionPane.showMessageDialog(mainFrame,x.getMessage());
					done();
				}
			}
		}
	}

}
