package ambit.database.search;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.BitSet;

import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.CMLWriter;

import ambit.data.molecule.Compound;
import ambit.data.molecule.MoleculeTools;
import ambit.data.molecule.SourceDataset;
import ambit.exceptions.AmbitException;


/**
 * Finds structures similar to the specified by fingerprint (Tanimoto) similarity. Calculates fingerprints by  {@link org.openscience.cdk.fingerprint.Fingerprinter} only for the 
 * query structure and compares it with the fingerprints stored in the database. 
 * Will not work if there are no fingerprints in the database! To generate fingerprints for the structures in database (once) use {@link ambit.ui.actions.dbadmin.DBGenerator} with fingerprints option.
 * <pre>
 * 	public void testSimilarity() {
		try {
			ConnectionPool pool = new ConnectionPool(
			        ITestDB.host,ITestDB.port,ITestDB.database,ITestDB.user,"",1,1);
			Connection conn = pool.getConnection();
			DbSimilarityByFingerprintsReader reader = new DbSimilarityByFingerprintsReader(
					conn,
					MoleculeFactory.makeBenzene(), //search for structures similar to benzene
					null, //search entire database
					0,  //first page
					10  //10 results per page
					);
					
			ReadCASProcessor casProcessor = new ReadCASProcessor(conn); //read CAS numbers
			ReadSMILESProcessor smilesProcessor = new ReadSMILESProcessor(conn); //read SMILES
			//uncomment to read the structure, 
			//otherwise the molecule object will have zero atoms and contain only properties
			//ReadSubstanceProcessor substanceProcessor = new ReadSubstanceProcessor(conn);
			
			System.out.println("Tanimoto distance\tCAS RN\tSMILES");
			
			while (reader.hasNext()) {
			    Object object = reader.next();
			    System.out.print(((IChemObject) object).getProperty("Tanimoto"));
			    System.out.print('\t');
			            
			    object = casProcessor.process(object);
			    System.out.print(((IChemObject) object).getProperty(CDKConstants.CASRN));
			    System.out.print('\t');
			    
			    object = smilesProcessor.process(object);
			    System.out.print(((IChemObject) object).getProperty(AmbitCONSTANTS.SMILES));
			    System.out.print('\n');
			}
			pool.returnConnection(conn);
			conn.close();
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}

	}
 * </pre>
 * The output from the code above is something like : (depends on the database content)
 * <pre>
Tanimoto distance	CAS RN		SMILES
1.0					71-43-2		c1ccccc1
0.67				91-20-3		c1ccc2ccccc2(c1)
0.67				53-70-3		c1ccc5c(c1)ccc4cc3c(ccc2ccccc23)cc45
0.67				218-01-9	c1ccc2c(c1)ccc3c4ccccc4(ccc23)
0.67				50-32-8		c1ccc2c(c1)cc3ccc4cccc5ccc2c3c45
0.5					108-88-3	Cc1ccccc1
0.46				108-95-2	Oc1ccccc1
0.46				62-53-3		Nc1ccccc1
0.46				106-42-3	Cc1ccc(C)cc1
0.43				123-31-9	Oc1ccc(O)cc1
</pre>
See also {@link ambit.ui.actions.search.DbSimilaritySearchAction}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class DbSimilarityByFingerprintsReader extends DbSearchReader {
	protected Fingerprinter fingerprinter;
	
	public DbSimilarityByFingerprintsReader(Connection connection,
			IAtomContainer mol, SourceDataset srcDataset,double threshold, int page, int pagesize)
			throws AmbitException {
		super(connection, mol, srcDataset,threshold,  page,pagesize);
		setSimilarityLabel("Tanimoto");
		
	}

	protected PreparedStatement prepareSQLStatement(Connection conn,
			IAtomContainer query,int page, int pagesize, double threshold) throws AmbitException {

		boolean hasQuery = (query != null) &&
					(query.getAtomCount() > 0)	;
		try {
			if (hasQuery) {

				/*
                StringWriter w = new StringWriter();
    			CMLWriter cmlWriter = new CMLWriter(w);						
    			cmlWriter.write(query);
    			cmlWriter = null;
    			IMolecule mol = Compound.readMolecule(w.toString());
    			*/
				IAtomContainer mol = query;
    			
			    fingerprinter = new Fingerprinter(1024);
				BitSet bs = fingerprinter.getFingerprint(mol);
				long[] h16 = new long[16];
				MoleculeTools.bitset2Long16(bs,64,h16);
				String[] fp = new String[16];
				for (int i = 0; i < 16; i++) {
					fp[i] = Long.toString(h16[i]);
				}
				int bc = bs.cardinality();
				StringBuffer b = new StringBuffer();
					b.append("select cbits,bc,?");
					//b.append(bc);
					b.append(" as NA,round(cbits/(bc+?");
					//b.append(bc);
					b.append("-cbits),2) as similarity,smiles,formula,molweight,L.idsubstance  from (");
					b.append("select fp1024.idsubstance,(");
					for (int h=0; h < 16; h++) {
						b.append("bit_count("); 
						b.append(fp[h]); 
						b.append("& fp");
						b.append(Integer.toString(h+1));
						b.append(")");
						if (h<15) b.append(" + "); else b.append(") ");
					}
					b.append(" as cbits,bc from fp1024 ");
					if ((srcDataset != null) && (srcDataset.getId()>=0)) {
						b.append(" join structure using(idsubstance) join struc_dataset using(idstructure) where id_srcdataset=");
						b.append(srcDataset.getId());
					}
					b.append (") as L, substance ");
					b.append("where bc > 0 and cbits > 0 and (cbits/(bc+?-cbits)>?) and L.idsubstance=substance.idsubstance order by similarity desc limit ?,?");
//select fp1024.idsubstance,bc,fp1 from fp1024 
//join structure using(idsubstance) join struc_dataset using(idstructure) where id_srcdataset=?;					
			
				PreparedStatement ps = conn.prepareStatement(b.toString());
				ps.setInt(1,bc);
				ps.setInt(2,bc);
				ps.setInt(3,bc);
				ps.setDouble(4,threshold);
				ps.setInt(5,page*pagesize);
				ps.setInt(6,pagesize);
				System.out.println(ps);
				return ps;

			} else return null;

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
    @Override
    protected void setPropertyStructure(IAtomContainer mol) {
        //avoid reading nonexisting field idstructure
    }
	public String toString() {
		return "Search for similar compounds by fingerprints ";
	}
    
    protected boolean accept(Double similarity) {
    	return true;
    	/*
        try {
            if (similarity == null) return true;
            return (similarity.doubleValue() >= threshold);
        } catch (Exception x) {
            logger.error(x);
            return true;
        }
        */
    }
    public boolean hasNext() {
        try {
            if (resultset != null) {
                boolean r = resultset.next();
                if (r) {
                    return accept(getSimilarity());
                }
                return r;
            }
            else return false;
        } catch (Exception x) {
            logger.error(x);
            return false;
        }
    }
    

}
