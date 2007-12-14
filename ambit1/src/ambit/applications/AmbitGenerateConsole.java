/**
 * Created on 2004-11-26
 *
 */
package ambit.applications;

import java.sql.SQLException;

import ambit.benchmark.Benchmark;
import ambit.database.old.DbGamut;
import ambit.exceptions.AmbitException;

/**
 * A console application to generate SMILES and Fingerprints
 * @deprecated
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class AmbitGenerateConsole {
	
	public static void main(String[] args) {
		
		String[] modes = {"gamut","smiles","fingerprint","SSSR"};
		String[] smiModes = {"null","notcanonical"};
		//print
		if (args.length == 0) {
			System.out.print(AmbitGenerateConsole.class.getName());
			System.out.print(" [");
			for (int i = 0; i < modes.length; i++) {
			    if ( i > 0) System.out.print(" | "); 
			    System.out.print("-G");
			    System.out.print(modes[i]);
			}
			System.out.print("] ");
			System.out.print(" -Cn ");
			System.out.print(" [");
			for (int i = 0; i < smiModes.length; i++) {
			    if ( i > 0) System.out.print(" | "); 
			    System.out.print("-S");
			    System.out.print(smiModes[i]);
			}
			System.out.print("] ");
			System.out.println();	
			System.out.println(" -Cn \t\t\t\t: SMILES or fingerprint generation only for compounds with number of cyclic bonds <= n");
			System.out.println(" -Snull \t\t\t: SMILES will be generated only for compounds with null SMILES");
			System.out.println(" -Snotcanonical \t: SMILES will be generated for compounds with non canonical SMILES (even if a non null SMILES exists)");
			return;
		}
		
		String param = "";
		char option; 
		
		int mode = -1;
		int cb = 37;
		boolean nullSmilesOnly = true;
		
		for (int p=0; p < args.length; p++) {
		    param = args[p];
			option = args[p].charAt(0);
			if (option != '-') continue; 

		    param = args[p].substring(2);
			option = args[p].charAt(1);
			switch (option) {
			case 'G': {
				for (int i=0; i < modes.length; i++) {
					if (param.equals(modes[i])) {
						mode = i;
						break;
					}
				}
				break;
			}
			case 'C': {
			    try {
			        cb = Integer.parseInt(param);
			    } catch (Exception x) {
			        cb = 37;
			        System.out.println(param + " is not a valid integer");
			    }
				break;
			}
			case 'S': {
				for (int i=0; i < modes.length; i++) {
					if (param.equals(smiModes[i])) {
					    nullSmilesOnly = (i==0);
						break;
					}
				}
				break;
			}
			}
		}
		if (mode == -1) {
			System.out.println(AmbitGenerateConsole.class.getName()+":\tUnknown option '"+param+"'");
			return;
		}
		DbGamut gamut = new DbGamut("localhost","3306","ambit","lri","cefic");
		gamut.setCyclicBonds(cb);
		gamut.setNullSmilesOnly(nullSmilesOnly);
		Thread t = new Thread(gamut);
		try {
			Benchmark b = new Benchmark();
			b.mark();
			gamut.open();
			// 0 - gamut, 1 - smiles, 2 - fingerprints
			gamut.setMode(mode);
			t.start();
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();				
			}
			
			gamut.close();
			b.record();
			b.report();
		} catch (AmbitException e) {
			e.printStackTrace();
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
