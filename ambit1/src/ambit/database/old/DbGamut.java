/**
 * Created on 2004-11-26
 *
 */
package ambit.database.old;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.BitSet;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smiles.smarts.SMARTSParser;
import org.openscience.cdk.tools.MFAnalyser;

import ambit.benchmark.Benchmark;
import ambit.data.molecule.Compound;
import ambit.data.molecule.MoleculeConvertor;
import ambit.data.molecule.MoleculeTools;
import ambit.database.DbConnection;
import ambit.database.core.DbSQL;
import ambit.exceptions.AmbitException;


/**
 * OLD Database API - to be removed !!!!!!!!!!!<br>
 * Create SMILES and fingerprints 
 * @author Nina Jeliazkova <br>
 * @deprecated
 * <b>Modified</b> 2005-4-7
 */
public class DbGamut extends DbExport implements Runnable {
//	 table to convert a nibble to a hex char.
	private int mode = 0;
	private int cyclicBonds = 37;
	private boolean nullSmilesOnly = true;  
	private long timeoutPerMolecule = 30 * 60 * 1000; // 30 min
	protected Fingerprinter fp ;
	
	/**
	 * 
	 */
	public DbGamut(String database) {
		super(database);
		fp = new Fingerprinter(1024);
	}
	public DbGamut(DbConnection conn) throws AmbitException {
		super(conn);
		fp = new Fingerprinter(1024);
	}	
	public DbGamut(String host,String port, String database, String user, String password) {
		super(host,port, database,user,password);
		fp = new Fingerprinter(1024);
	}
	public int subSearch(String smarts, long limit) throws SQLException {
		QueryAtomContainer query = null;
		try {
			query = SMARTSParser.parse(smarts);
		} catch (CDKException e) {
			e.printStackTrace();
			return 0;
		}
		
		int rowsFound = 0,rowsProcessed = 0;
		MoleculeConvertor mc = new MoleculeConvertor();
		mc.setMolecule(query);
		mc.createGamut();
		//mc.countRings();
		long fp = 0;
		try {
			fp = MoleculeTools.bitset2Long(this.fp.getFingerprint(query));
		} catch (Exception e) {
			e.printStackTrace();
		}
		String subsearchSQL = mc.gamutToSubSearchString()+
				"bit_count(fingerprint & 0x" + Long.toHexString(fp) +")= bit_count(0x8020000)";
		logger.debug(subsearchSQL);
		
		
		Statement stmt = createBidirectional();
		String sql;
		sql = "select substance.idsubstance,cas,formula from substance join gamut on substance.idsubstance=gamut.idsubstance where " + subsearchSQL + " order by bonds_single LIMIT " + limit + ";";
		logger.debug(sql);
		
		
		String sqlStruc = "SELECT idstructure,uncompress(structure) from structure where idsubstance=? order by idstructure limit 1;";
		PreparedStatement ps = getConn().prepareStatement(sqlStruc);
		ResultSet rs = null;
		
		int idsubstance = 0;
		ResultSet rsSubstances = stmt.executeQuery(sql);
		IMolecule mol = null;
		
		while (rsSubstances.next() ) {
			idsubstance = rsSubstances.getInt(1);
			ps.setInt(1,idsubstance);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				try {
					mol = Compound.readMolecule(rs.getString(1));					
					if (UniversalIsomorphismTester.isSubgraph(mol,query)) {
						logger.info("Found:\t"+idsubstance + "\t" + rsSubstances.getString(2) + "\t" + rsSubstances.getString(3));
						rowsFound++;
					} 
				} catch (CDKException e) {
					e.printStackTrace();
					continue;
				}
			}
			rs.close();
			rowsProcessed++;
		}			
		
		return rowsProcessed;
	}

	private void updateGamut(ResultSet rsUpdatable, int idSubstance, MoleculeConvertor mc) {
		
			
		try {
			rsUpdatable.updateInt(1,idSubstance);
			//atoms typeValue
			int[] No = mc.getAtomsNo();
			String[] Id = MoleculeConvertor.getAtomsID();			
			int na = No.length;			
			for (int i = 0; i < na ; i++) 
				rsUpdatable.updateInt(Id[i],No[i]);
			//bonds order
			No = mc.getBondsTypeNo();
			Id = MoleculeConvertor.getBondsType();			
			for (int i = 0; i < (No.length) ; i++) 
				rsUpdatable.updateInt(Id[i],No[i]);
			//atoms typeValue
			No = mc.getAtomsOrder();
			Id = MoleculeConvertor.getAtomsOrderID();			
			for (int i = 0; i < (No.length) ; i++) 
				rsUpdatable.updateInt(Id[i],No[i]);
			//clear
			No = null;
			Id = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/*
	public void testRingsFinder(int idsubstance) throws SQLException {
		String sql = "SELECT idstructure,uncompress(structure) from structure where idsubstance=? order by idstructure limit 1;";
		PreparedStatement ps = getConn().prepareStatement(sql);
		ps.setInt(1,idsubstance);
		ResultSet rsStructure = ps.executeQuery();
		Molecule mol;
		AllRingsFinderFast arff = new AllRingsFinderFast();
		while (rsStructure.next()) {
			try {
				System.out.print(idsubstance);
				mol = Compound.readMolecule(rsStructure.getString(2));
				arff.testAllRingsFinder(mol,false); 
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}			
	}
	*/
	/*
	public void testSpanningTree(int idsubstance) throws SQLException {
		String sql = "SELECT idstructure,uncompress(structure) from structure where idsubstance=? order by idstructure limit 1;";
		PreparedStatement ps = getConn().prepareStatement(sql);
		ps.setInt(1,idsubstance);
		ResultSet rsStructure = ps.executeQuery();
		Molecule mol;
		SpanningTree sp = new SpanningTree();
		MFAnalyser mfa;
		while (rsStructure.next()) {
			try {
				System.out.print(idsubstance);					
				mol = Compound.readMolecule(rsStructure.getString(2));				
				//sp.display(mol,false);
				mfa = new MFAnalyser(mol);
				
				mol = (Molecule) mfa.removeHydrogensPreserveMultiplyBonded();
				sp.testSpanningTree((org.openscience.cdk.AtomContainer)mol);
				
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}			
	}
	*/
	public int generateNullSmilesGamut(boolean updateExisting,long limit) throws SQLException {
		return generateGamut(DbSQL.AMBIT_SubstancesIDNullSmiles(limit),updateExisting,limit);
	}		
	
	public int generateNonExistingGamut(boolean updateExisting,long limit) throws SQLException {
		return generateGamut(DbSQL.AMBIT_SubstancesIDNullGamut(limit),updateExisting,limit);
	}		
	public int generateGamut(String substances,boolean updateExisting,long limit) throws SQLException {
		Statement stmt = createBidirectional();			
		ResultSet rsSubstances = stmt.executeQuery(substances);
		ResultSet rsStructure = null;		
		String sql = "SELECT idstructure,uncompress(structure) from structure where idsubstance=? order by idstructure limit 1;";
		PreparedStatement ps = getConn().prepareStatement(sql);
		
		String sqlGamut = "select idsubstance,C,O,N,S,P,Cl,F,Br,I,Si,B,aa,b1,bar,b2,b3,ab,cb,st,o1,o2,o3,o4,oo from gamut where idsubstance=? order by idsubstance;"; 
		PreparedStatement psGamut = getConn().prepareStatement(sqlGamut,
						ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_UPDATABLE);
		
		
		ResultSet rsGamut = null;
		
		int rows = 0; int rowsUpdated = 0;
		int idsubstance = 0, idstructure =0;
		IMolecule mol = null;
		MoleculeConvertor mc = new MoleculeConvertor();
		getConn().setAutoCommit(false);
		long base_time = System.currentTimeMillis();		
		try {
		while (rsSubstances.next()) {
			
			idsubstance = rsSubstances.getInt(1);
			ps.setInt(1,idsubstance);
			
			rsStructure = ps.executeQuery();
			
			while (rsStructure.next()) {
				try {
					//System.out.println(idsubstance);
					mol = Compound.readMolecule(rsStructure.getString(2));					
					
					mc.setMolecule(mol);

					psGamut.setInt(1,idsubstance);
					rsGamut = psGamut.executeQuery();
					if (!rsGamut.next()) {
						mc.createGamut();
						//mc.countRings();
						logger.warn(idsubstance+"\t"+mc.isDisconnected()+"\t"+mc.gamutToEqualString());
						rsGamut.moveToInsertRow();
						updateGamut(rsGamut,idsubstance,mc);
					    rsGamut.insertRow();
					    rsGamut.last();
					    rowsUpdated++;
						//System.out.println("insert\t"+(nPoints+1) + "\t" + idsubstance+"\t"+rsSubstances.getString(2));
					    
					} else {
						if (updateExisting) {
							mc.createGamut();
							logger.warn(idsubstance+"\t"+mc.isDisconnected()+"\t"+mc.gamutToEqualString());							
							updateGamut(rsGamut,idsubstance,mc);
							rsGamut.updateRow();
							//rsGamut.last();
							rowsUpdated++;
							//System.out.println("\tupdate\t"+(nPoints+1) + "\t" + idsubstance+"\t"+rsSubstances.getString(2));							
						}
					}
					rsGamut.close();


				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				break;
			}
			rsStructure.close();
			rsStructure = null;
			rows++;
			if ((System.currentTimeMillis() - base_time ) > 300000) {
				logger.debug(rows+"\t");					
				commit();
				base_time = System.currentTimeMillis();
			}
		}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		mc = null;
		mol = null;
		getConn().commit();
		if (rsStructure != null) rsStructure.close();
		rsSubstances.close();
		stmt.close();
		ps.close();
		return rows;
	}

	public int generateFingerprintsAE(String substances,boolean updateExisting,long limit) throws SQLException {
		Statement stmt = createBidirectional();			
		ResultSet rsSubstances = stmt.executeQuery(substances);
		ResultSet rsStructure = null;		
		String sql = "SELECT idstructure,uncompress(structure) from structure where idsubstance=? order by idstructure limit 1;";
		PreparedStatement ps = getConn().prepareStatement(sql);
		
		String sqlGamut = "select idsubstance,C,O,N,S,P,Cl,F,Br,I,Si,B,aa,b1,bar,b2,b3,ab,cb,st,o1,o2,o3,o4,oo from gamut where idsubstance=? order by idsubstance;"; 
		PreparedStatement psGamut = getConn().prepareStatement(sqlGamut,
						ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_UPDATABLE);
		
		
		ResultSet rsGamut = null;
		
		int rows = 0; int rowsUpdated = 0;
		int idsubstance = 0, idstructure =0;
		IMolecule mol = null;
		MoleculeConvertor mc = new MoleculeConvertor();
		getConn().setAutoCommit(false);
		long base_time = System.currentTimeMillis();		
		try {
		while (rsSubstances.next()) {
			
			idsubstance = rsSubstances.getInt(1);
			ps.setInt(1,idsubstance);
			
			rsStructure = ps.executeQuery();
			
			while (rsStructure.next()) {
				try {
					//System.out.println(idsubstance);
					mol = Compound.readMolecule(rsStructure.getString(2));					
					
					mc.setMolecule(mol);

					psGamut.setInt(1,idsubstance);
					rsGamut = psGamut.executeQuery();
					if (!rsGamut.next()) {
						mc.createGamut();
						//mc.countRings();
						logger.warn(idsubstance+"\t"+mc.isDisconnected()+"\t"+mc.gamutToEqualString());
						rsGamut.moveToInsertRow();
						updateGamut(rsGamut,idsubstance,mc);
					    rsGamut.insertRow();
					    rsGamut.last();
					    rowsUpdated++;
						//System.out.println("insert\t"+(nPoints+1) + "\t" + idsubstance+"\t"+rsSubstances.getString(2));
					    
					} else {
						if (updateExisting) {
							mc.createGamut();
							logger.warn(idsubstance+"\t"+mc.isDisconnected()+"\t"+mc.gamutToEqualString());							
							updateGamut(rsGamut,idsubstance,mc);
							rsGamut.updateRow();
							//rsGamut.last();
							rowsUpdated++;
							//System.out.println("\tupdate\t"+(nPoints+1) + "\t" + idsubstance+"\t"+rsSubstances.getString(2));							
						}
					}
					rsGamut.close();


				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				break;
			}
			rsStructure.close();
			rsStructure = null;
			rows++;
			if ((System.currentTimeMillis() - base_time ) > 300000) {
				System.out.println(rows+"\t");					
				commit();
				base_time = System.currentTimeMillis();

			}
		}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		mc = null;
		mol = null;
		getConn().commit();
		if (rsStructure != null) rsStructure.close();
		rsSubstances.close();
		stmt.close();
		ps.close();
		return rows;
	}
	//TODO rewrite
	public int generateSmiles(String substances,long limit) throws SQLException {
	    System.out.println("Timeout per molecule " + timeoutPerMolecule  + " ms.");
		System.out.println(substances);
		PreparedStatement psSubstance = getConn().prepareStatement(substances);
		ResultSet rsSubstance = psSubstance.executeQuery();
		
		PreparedStatement psStructure = getConn().prepareStatement(DbSQL.AMBIT_StructureBySubstance);		
		ResultSet rsStructure = null;
		
		PreparedStatement psUpdateSmiles = getConn().prepareStatement(DbSQL.AMBIT_UpdateSmilesByID);
		
		int rows = 0; int rowsUpdated = 0;
		int idsubstance = 0, idstructure =0;
		IMolecule mol = null;
      	MFAnalyser mfa = null;
      	IAtomContainer ac = null;
      	AllRingsFinder arf = new AllRingsFinder();
      	arf.setTimeout(timeoutPerMolecule);
      	SmilesGenerator  gen = new SmilesGenerator(DefaultChemObjectBuilder.getInstance());
      	gen.setRingFinder(arf);
      	
		getConn().setAutoCommit(false);
		String smiles = "";
		long base_time = System.currentTimeMillis();
		long smi_time;
		try {
		while (rsSubstance.next()) {
			idsubstance = rsSubstance.getInt(1);
			logger.debug((rows+1)+"\t"+idsubstance); 
			psStructure.clearParameters();
			psStructure.setInt(1,idsubstance);
			rsStructure = psStructure.executeQuery();
			int usmiles = -1;
			while (rsStructure.next()) {
			try {
				mol = Compound.readMolecule(rsStructure.getString(3));
				
		      	try {
		      	    if (mol != null) {
		      			mfa = new MFAnalyser(mol);
		      	    
			      		try {
			      			ac = mfa.removeHydrogensPreserveMultiplyBonded();
			      		} catch (Exception e) {
			      			//TODO verify what's going on if no H
			      			logger.error(e);
			      			e.printStackTrace() ;
			      			ac = mol;
			      		}
			      		mfa = null;
			      		smi_time = System.currentTimeMillis();
			      		//try {
			      		    smiles = gen.createSMILES((Molecule) ac);
			      		    /*
			      		} catch (CDKException x) {
			      		    smiles = "error: timeout " + timeoutPerMolecule;
			      		}
			      		*/
			      		smi_time = System.currentTimeMillis() - smi_time;
			      		psUpdateSmiles.clearParameters();
						psUpdateSmiles.setString(1,smiles);		      	
						psUpdateSmiles.setInt(2,1);
						psUpdateSmiles.setLong(3,smi_time);
						psUpdateSmiles.setInt(4,idsubstance);					      		
		      	    } else {
			      		psUpdateSmiles.clearParameters();
						psUpdateSmiles.setString(1,"error");		      	
						psUpdateSmiles.setInt(2,1);
						psUpdateSmiles.setNull(3,Types.INTEGER);
						psUpdateSmiles.setInt(4,idsubstance);				      	        
		      	    }
					// "UPDATE substance set smiles=?,usmiles=?,time_elapsed=? where idsubstance=?";		      		
		
					rowsUpdated += psUpdateSmiles.executeUpdate();
					
					
		      	} catch (Exception e) {
		      		smiles = "defaultError";
		      		logger.error(e.toString());
		      		e.printStackTrace();
		      	}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
			}
			
			logger.debug("\t" + rsSubstance.getString(2)+"\t"+smiles+"\n");					
			if ((System.currentTimeMillis() - base_time ) > 100000) {
				getConn().commit();
				base_time = System.currentTimeMillis();
				System.out.println(rows+"\t");				
			}
			rows++;
			
		}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e);
		}
		mol = null;
		commit();
		if (rsStructure != null) rsStructure.close();
		if (rsSubstance != null) rsSubstance.close();		
		psStructure.close();
		psSubstance.close();
		psUpdateSmiles.close();
		return rows;
	}
	public int generateSSSRstats(String substances,long limit) throws SQLException {
		Statement stmt = createBidirectional();			
		ResultSet rsSubstances = stmt.executeQuery(substances);
		ResultSet rsStructure = null;		
		String sql = "SELECT idstructure,uncompress(structure) from structure where idsubstance=? order by type_structure desc limit 1;";
		PreparedStatement ps = getConn().prepareStatement(sql);
		
		String sqlGamut = "update gamut set rno=?, rmax=?, time=? where idsubstance=?;"; 
		PreparedStatement psGamut = getConn().prepareStatement(sqlGamut,
						ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_UPDATABLE);
		
		
		ResultSet rsGamut = null;
		
		int rows = 0; int rowsUpdated = 0;
		int idsubstance = 0, idstructure =0;
		IMolecule mol = null;
		SSSRFinder sssrf; 
		
		getConn().setAutoCommit(false);
		long base_time = System.currentTimeMillis();	
		long sssr_time;
		int[] rings = null;
		int rno = 0;
		int rmax = 0;
		
		try {
		while (rsSubstances.next()) {
			
			idsubstance = rsSubstances.getInt(1);
			ps.setInt(1,idsubstance);
			
			rsStructure = ps.executeQuery();
			
			while (rsStructure.next()) {
				try {
					//System.out.println(idsubstance);
					mol = Compound.readMolecule(rsStructure.getString(2));
					
					sssr_time = System.currentTimeMillis();
					sssrf = new SSSRFinder(mol);
					rings = sssrf.getSSSRWeightVector();
					rno = rings.length;
					rmax = 0;
					if (rno > 0)
					    for (int i = 0; i < rings.length; i++)
					        if (rings[i] > rmax) rmax = rings[i];
					        
		      		sssr_time = System.currentTimeMillis() - sssr_time;
					sssrf = null;
					psGamut.setLong(1,rno);
					psGamut.setLong(2,rmax);
					psGamut.setLong(3,sssr_time);
					psGamut.setInt(4,idsubstance);
					psGamut.executeUpdate();

				} catch (Exception e) {
					e.printStackTrace();
					psGamut.setLong(1,-1);
					psGamut.setLong(2,-1);
					psGamut.setLong(3,-1);
					psGamut.setInt(4,idsubstance);
					psGamut.executeUpdate();					
					continue;
				}
				break;
			}
			rsStructure.close();
			rsStructure = null;
			rows++;
			if ((System.currentTimeMillis() - base_time ) > 300000) {
				System.out.println(rows+"\t");					
				commit();
				base_time = System.currentTimeMillis();

			}
		}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		sssrf = null;
		mol = null;
		getConn().commit();
		if (rsStructure != null) rsStructure.close();
		rsSubstances.close();
		stmt.close();
		ps.close();
		return rows;
	}
	
	private void printFP1024(long[] h16) {
		
		for (int i = 0; i < h16.length;i++) 
			System.out.print("["+(i+1)+"]\t0x"+Long.toHexString(h16[i])+"\t");
		System.out.println();
	}
	private void updateFP1024(ResultSet rsUpdatable, int idSubstance, long[] h16, long time, int bc, int status) {
		
		//printFP1024(h16);
		
		try {
			int L = h16.length;
			rsUpdatable.updateInt(1,idSubstance);
			rsUpdatable.updateLong(2,time);
			rsUpdatable.updateLong(3,bc);
			rsUpdatable.updateLong(4,status);
			for (int i = 0; i < L ; i++) 
				rsUpdatable.updateLong(i+5,h16[i]);

		} catch (SQLException e) {
			e.printStackTrace();
		}
				
	}
	
	public int generateFingerprints1024(boolean updateExisting, long limit) throws SQLException {
		
		
		PreparedStatement psStructures = getConn().prepareStatement(DbSQL.AMBIT_SubstancesStrucSimpleFPNull(limit,cyclicBonds));
		System.out.println(psStructures);
		ResultSet rsStructures = psStructures.executeQuery();
		
		String sqlFP1024 = "select idsubstance,time,bc,status,fp1,fp2,fp3,fp4,fp5,fp6,fp7,fp8,fp9,fp10,fp11,fp12,fp13,fp14,fp15,fp16 from fp1024 where idsubstance=? order by idsubstance";
		
		
		PreparedStatement psFP1024 = getConn().prepareStatement(sqlFP1024,
						ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_UPDATABLE);
		ResultSet rsFP1024 = null;
		
		int rows = 0; int rowsUpdated = 0;
		int idsubstance = 0, idstructure =0;
		Molecule mol = null;

		getConn().setAutoCommit(false);
		long base_time = System.currentTimeMillis(); 
		Benchmark b = new Benchmark();
		BitSet bs = null;
		String smiles = "";
		SmilesParser sp = new SmilesParser();
		int bitCount = 0;
		long bsTime;
		try {
		while (rsStructures.next()) {
			
			idsubstance = rsStructures.getInt(1);
			int status;
			long[] h16 = new long[16];
			//System.out.println(idsubstance);			
			try {
			    for (int i=0; i < 16; i++) h16[i]=0;
				smiles = rsStructures.getString(2);
				
				if (smiles == null) continue;
				try {
				    mol = sp.parseSmiles(smiles);
					b.clear();	b.mark();
					bs = fp.getFingerprint((IAtomContainer) mol);
					b.record(); 
					MoleculeTools.bitset2Long16(bs,64,h16); 		
					bitCount = bs.cardinality();
					bsTime = b.getElapsedTime();
				    status = 1;
				} catch (InvalidSmilesException x) {
					System.err.println(smiles);
					x.printStackTrace();
					status = -1;
					bitCount = 0;
					bsTime = 0;
					//continue;
				} catch (Exception xx) {
				    System.err.println("ERROR ON FINGERPRINT GENERATION\t"+idsubstance);
				    xx.printStackTrace();
				    status = -2;
				    bitCount = 0;
				    bsTime = 0;
				    //		continue;
				}
				
				psFP1024.clearParameters();
				psFP1024.setInt(1,idsubstance);
				rsFP1024 = psFP1024.executeQuery();
				if (!rsFP1024.next()) {
					rsFP1024.moveToInsertRow();
					updateFP1024(rsFP1024,idsubstance,h16,bsTime,bitCount,status);
				    rsFP1024.insertRow();   
				    rsFP1024.last();
				    rowsUpdated++;
				} else {
					if (updateExisting) {
						updateFP1024(rsFP1024,idsubstance,h16,bsTime,bitCount,status);
						rsFP1024.updateRow(); 			
						rowsUpdated++;
					}
				}
				mol = null;
				rsFP1024.close();
			} catch (Exception e) {
			    System.err.println("Unknown error "+idsubstance);
				e.printStackTrace();
			}
			rows++;
			if ((System.currentTimeMillis() - base_time) > 100000) { 
				commit();
				base_time= System.currentTimeMillis();
			}
		}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		sp = null;
		mol = null;
		commit();
		psFP1024.close();
		rsStructures.close();
		
		return rows;
	}	
	
	
	
	
	public void run() {
		int rows = 0;

		/* test bitset2long16
		BitSet bs = new BitSet();
		bs.clear();
		bs.set(0);
		bs.set(1);
		bs.set(1023);		
		bs.set(15);
		bs.set(63+15);
		//long[] h16 = new long[16];
		//bitset2Long16(bs,64,h16);
		long[] h16 = new long[16];
		bitset2Long16(bs,64,h16);		
		printFP1024(h16);
		*/
		
		try {
			//testRingsFinder(224824);
			//testRingsFinder(15402);
			//testSpanningTree(412);
			//testSpanningTree(224824);
			
			//nPoints = subSearch("C=C",250000);
			//nPoints = generateGamut(true);
			//while (generateFingerprints(false,1000) > 0);			
			//generateFingerprints1024(false,0);
			rows = 0;
			switch (mode) {
			//generate gamut
			case 0: {
				logger.info("Generate gamut");
				int r = 0;
				while ( (r=generateNonExistingGamut(false,100)) > 0) { rows +=r;};
				break;
			}
			//generate smiles
			case 1: {
			    logger.info("Generate smiles");
	
				int r = 0;
				generateGamut(DbSQL.AMBIT_SubstancesIDNullGamutNullSmiles(100),false,100);
				String sql;
				if (nullSmilesOnly) 
				    sql = DbSQL.AMBIT_SubstancesSimpleSmilesNull(100,cyclicBonds);
				else
				    sql = DbSQL.AMBIT_SubstancesSimpleSmilesNotcanonical(100,cyclicBonds);				
				while ( (r=generateSmiles(sql,100)) > 0) { 
					rows +=r;
				    generateGamut(DbSQL.AMBIT_SubstancesIDNullGamutNullSmiles(100),false,100);					
				};
				break;
			}
			case 2: {
			    logger.info("Generate fingerprints");
				int r = 0;
				//nPoints = generateFingerprints1024(false,10);
				while ( (r=generateFingerprints1024(true,1000)) > 0) { rows +=r;};
				break;
			}
			case 3: {
				logger.info("Generate SSSR statistics");
		
				int r = 0;
				//nPoints = generateFingerprints1024(false,10);
				
				while ( (r=generateSSSRstats("SELECT idsubstance from gamut where time is null limit 1000;",1000)
				        ) > 0) { rows +=r;};
				break;
			}			
			}
			
			
			System.out.println("nPoints processed " + rows);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
/**
 * @param mode The mode to set.
 */
public void setMode(int mode) {
	this.mode = mode;
}

    public int getCyclicBonds() {
        return cyclicBonds;
    }
    public void setCyclicBonds(int cyclicBonds) {
        this.cyclicBonds = cyclicBonds;
    }
    public boolean isNullSmilesOnly() {
        return nullSmilesOnly;
    }
    public void setNullSmilesOnly(boolean nullSmilesOnly) {
        this.nullSmilesOnly = nullSmilesOnly;
    }
    public long getTimeoutPerMolecule() {
        return timeoutPerMolecule;
    }
    public void setTimeoutPerMolecule(long timeoutPerMolecule) {
        this.timeoutPerMolecule = timeoutPerMolecule;
    }
}


