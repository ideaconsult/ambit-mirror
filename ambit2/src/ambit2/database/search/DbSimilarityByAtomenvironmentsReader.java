package ambit2.database.search;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.CMLWriter;

import ambit2.config.AmbitCONSTANTS;
import ambit2.exceptions.AmbitException;
import ambit2.processors.structure.AtomEnvironmentGenerator;
import ambit2.data.descriptors.AtomEnvironment;
import ambit2.data.descriptors.AtomEnvironmentDescriptor;
import ambit2.data.descriptors.AtomEnvironmentList;
import ambit2.data.molecule.Compound;
import ambit2.data.molecule.MoleculeTools;
import ambit2.data.molecule.SourceDataset;

/**
 * Finds structures similar to the specified by atom environment similarity. Calculates atom environments by  {@link AtomEnvironmentDescriptor} only for the 
 * query structure and compares it with the atom environments stored in the database. 
 * Will not work if there are no atom environments in the database! To generate atom environments (once) use {@link ambit2.ui.actions.dbadmin.DBGenerator} with atom environment option.<br>
 * Example: Search for structures similar to benzene by atom environments. 
 * Reads CAS, SMILES, NAme and aliases and writes structures with properties to SDF file.
 * <pre>
 	public void testSimilarityByAtomEnvironments() {
		try {
			ConnectionPool pool = new ConnectionPool(
			        ITestDB.host,ITestDB.port,ITestDB.database,ITestDB.user,"",1,1);
			Connection conn = pool.getConnection();
			DbSimilarityByAtomenvironmentsReader reader = new DbSimilarityByAtomenvironmentsReader(
					conn,
					MoleculeFactory.makeBenzene(),
					null,
					0,100
					);
			IChemObjectWriter writer = new MDLWriter(new FileOutputStream("data/misc/DbSimilarityByAtomenvironmentsReader.sdf"));
			ProcessorsChain processors = new ProcessorsChain();
			processors.add(new ReadSubstanceProcessor(conn));
			processors.add(new ReadCASProcessor(conn));
			processors.add(new ReadSMILESProcessor(conn));
			processors.add(new ReadNameProcessor(conn));
			processors.add(new ReadAliasProcessor(conn));
			
			DefaultBatchProcessing batch = new DefaultBatchProcessing(
					reader,
					writer,
					processors,
					new EmptyBatchConfig());
			batch.start();
			long rr = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_READ);
			long rp = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_PROCESSED);
			long rw = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_WRITTEN);
			long re = batch.getBatchStatistics().getRecords(IBatchStatistics.RECORDS_ERROR);
			assertEquals(100,rr);
			assertEquals(100,rp);
			assertTrue(rw <= rr);
			assertTrue(rw > 0);
			pool.returnConnection(conn);
			conn.close();
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
		
	}

 * </pre>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class DbSimilarityByAtomenvironmentsReader extends DbSearchReader {
	protected AtomEnvironmentGenerator generator;
	protected Object[] aeParams = null;
	protected int[] aeResult = null;
	protected int level = 3;
	/**
	 * 
	 * @param connection
	 * @param mol  The query
	 * @param srcDataset The dataset. If null, searches entire database
	 * @param page
	 * @param pagesize
	 * @throws AmbitException
	 */
	public DbSimilarityByAtomenvironmentsReader(Connection connection, IAtomContainer mol,
			SourceDataset srcDataset, double threshold, int page, int pagesize) throws AmbitException {
		super(connection, mol, srcDataset, threshold, page,pagesize);
        setSimilarityLabel("Atom environment Similarity");
        
	}
	
	/*
insert into tempsimilarity (idsubstance, sab,sa,sb)
select fpaeid.idsubstance,freq*f,f*f,freq*freq  from 
		structure join struc_dataset using(idstructure) join fpaeid using(idsubstance)
left join 
(
 select 245 as idfpae,1 as f
union  select 244 as idfpae,1 as f
union  select 2208 as idfpae,1 as f
union  select 2695 as idfpae,1 as f
union  select 2694 as idfpae,1 as f
union  select 2692 as idfpae,1 as f
union  select 2693 as idfpae,1 as f
) as L 
 using (idfpae) where 
 	id_srcdataset=1 and 
 status='valid' and level>=3
on duplicate key update sab=sab+ifnull(freq*f,0) ,sb=sb+ifnull(freq*freq,0),sa=sa+ifnull(f*f,0)

	 */
    protected PreparedStatement prepareSQLStatement(Connection conn,
            IAtomContainer query, int page, int pagesize, double threshold) throws AmbitException {
    	level = 3;
        boolean hasQuery = (query != null) &&
                    (query.getAtomCount() > 0)    ;
        if (!hasQuery) return null;

        try {
                AtomEnvironmentList aes = null;

                if (generator==null) generator = new AtomEnvironmentGenerator();
                //stupid way to overtake CML aromaticity 
                
                StringWriter w = new StringWriter();
    			CMLWriter cmlWriter = new CMLWriter(w);						
    			cmlWriter.write(query);
    			cmlWriter = null;
    			IMolecule mol = MoleculeTools.readCMLMolecule(w.toString());
    			
//    			IAtomContainer mol = query;
    			
                generator.process(mol);
                Object aenv = mol.getProperty(AmbitCONSTANTS.AtomEnvironment);
                if (aenv instanceof AtomEnvironmentList) aes = (AtomEnvironmentList) aenv;
                else throw new AmbitException("Atom environments not generated");

                StringBuffer b = new StringBuffer();
                
                b.append("insert into tempsimilarity (idsubstance, sab,sa,sb)\n");
                //Tanimoto distance
               	b.append("select fpaeid.idsubstance,freq*f,f*f,freq*freq  from\n");
               	
               	/*
        		if ((srcDataset != null) && (srcDataset.getId()>=0)) {
					b.append("struc_dataset join structure using(idstructure) join fpaeid using(idsubstance)\n");
				} else
				(
				*/ 
				b.append("fpaeid\n");
               	
                b.append(" left join(\n");
                
                PreparedStatement psae = conn.prepareStatement("select idfpae from fpae where ae=?");
                int row = 0;
                for (int i=0; i < aes.size();i++) {
                	AtomEnvironment ae = aes.get(i);
                	if (ae.getSublevel()<level) continue;
                    psae.clearParameters();
                    logger.debug(AtomEnvironment.atomFingerprintToString(ae.getAtom_environment(),'\t'));
                    psae.setString(1, AtomEnvironment.atomFingerprintToString(ae.getAtom_environment(),'\t'));
                    ResultSet rs = psae.executeQuery();
                    /*
                     * 
select 23035 as idfpae,6 as f
union
 select 23038,6 
union select 23040,6
union select 23663,6 as f
                     */
                    boolean found = false;		
                    while (rs.next()) {
                    	found = true;
                    	if (row>0) b.append("\nunion ");
                    	b.append(" select ");
                    	b.append(rs.getString(1));
                    	b.append(" as idfpae,");
                    	b.append(ae.getFrequency()); 
                    	b.append(" as f");
                    	row++;
                    }
                    rs.close();
                    if (!found) {
                    	if (row>0) b.append("\nunion ");
                    	b.append(" select -1");
                    	b.append(" as idfpae,");
                    	b.append(ae.getFrequency()); 
                    	b.append(" as f");
                    	row++;
                    }
                }
                psae.close();
                b.append("\n) as L "); 
                b.append("\n using (idfpae) where ");
                
                /*
                if ((srcDataset != null) && (srcDataset.getId()>=0)) { 
                	b.append("\nid_srcdataset=");
                	b.append(srcDataset.getId());
                	b.append(" and");
                }	
                */
                b.append("\nstatus='valid' and level>=");
                b.append(level);
                b.append('\n');
                
                if ((srcDataset != null) && (srcDataset.getId()>=0)) {
                	b.append("and fpaeid.idsubstance in\n");
                	b.append("(select idsubstance from structure join struc_dataset using(idstructure) where id_srcdataset =");
                	b.append(srcDataset.getId());
                	b.append(")\n");
                }
               	b.append("on duplicate key update sab=sab+ifnull(freq*f,0) ,sb=sb+ifnull(freq*freq,0),sa=sa+ifnull(f*f,0)");

               	
                Statement st = conn.createStatement();
                st.addBatch("CREATE TEMPORARY TABLE if not exists tempsimilarity (idsubstance int,sab double default 0, sa double default 0, sb double default 0, PRIMARY KEY  (idsubstance) )  engine=memory;");
                st.addBatch("truncate tempsimilarity;");
                
                
                st.addBatch(b.toString());
                long now = System.currentTimeMillis();
                int[] r = st.executeBatch();
                System.out.println(st.toString());
                for (int i=0; i < r.length;i++)
                	logger.debug(r[i] + " rows");
                logger.debug(System.currentTimeMillis()-now);
                
                PreparedStatement p;
               	p = conn.prepareStatement("select tempsimilarity.idsubstance,ifnull(sab/(sa+sb-sab),0) as similarity,smiles,formula,molweight from tempsimilarity join substance using(idsubstance) where ifnull(sab/(sa+sb-sab),0)>=? order by ifnull(sab/(sa+sb-sab),0) desc limit ?,?");
                p.setDouble(1,threshold);
                p.setInt(2,page*pagesize);
                p.setInt(3,pagesize);
                System.out.println(p.toString());
                return p;

//          }   
        } catch (Exception ex) {
            throw new AmbitException(ex);
        }

    }
	
	

	public String toString() {
		return "Search for similar compounds by data environments";
	}
	@Override
	protected boolean accept(Double similarity) {
	    return true;
	}
    @Override
    protected void setPropertyStructure(IAtomContainer mol) {
        // avoid reading idstructure
        
    }
}
