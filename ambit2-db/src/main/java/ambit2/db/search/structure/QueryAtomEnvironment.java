package ambit2.db.search.structure;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;

public class QueryAtomEnvironment extends QuerySimilarity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7235494968515516274L;
	public String getSQL() throws AmbitException {
		throw new AmbitException("not implemented");
	}

	public List<QueryParam> getParameters() throws AmbitException {
		throw new AmbitException("not implemented");
	}
/*
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
	
 */
	public String toString() {
		return "Search for similar compounds by data environments";
	}
}
