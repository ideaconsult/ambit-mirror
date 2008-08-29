package ambit2.db.search;

import java.util.List;

import ambit2.core.exceptions.AmbitException;

public class QueryAquire extends QueryExperiment {
	   protected static final String sql = "select distinct(cas),Chemical_Name,Endpoint from chemicals join aquire on chemicals.cas=aquire.TestCAS join species_data on species_data.SpeciesID=aquire.SpeciesNumber ";
	public String getSQL() throws AmbitException {
		throw new AmbitException("Not implemented");
	}

	public List<QueryParam> getParameters() throws AmbitException {
		throw new AmbitException("Not implemented");
	}
	/*
        srcDataset = new SourceDataset("AQUIRE",
                ReferenceFactory.createDatasetReference("EPA Aquire database","http://mountain.epa.gov/ecotox/"));

   protected String prepareSQL(String endpoint, String species,int page,int pagesize) {
    	String where = " where ";
    	StringBuffer sql1 = new StringBuffer();
    	sql1.append(sql);
        if (endpoint != null) { 
        	sql1.append(where);
        	where = " and ";
        	sql1.append(" Endpoint='");
        	sql1.append(endpoint);
        	sql1.append("'");
            
        }
        if (species != null) { 
        	sql1.append(where);
        	where = " and ";
        	sql1.append(" LatinName = '");
        	sql1.append(species);
        	sql1.append("'");
            
        }        
        sql1.append(" limit ");
        sql1.append(page);
        sql1.append(",");
        sql1.append(pagesize);
        System.out.println(sql1);
        return sql1.toString();
        
    }


    public Object next() {
        try {
            IMolecule m = DefaultChemObjectBuilder.getInstance().newMolecule();
            m.setProperty(CDKConstants.NAMES,resultset.getString(2));            
            m.setProperty(CDKConstants.CASRN,
                    IdentifiersProcessor.hyphenateCAS(resultset.getString(1)));
            m.setProperty(AmbitCONSTANTS.DATASET,srcDataset);
            return m;
        } catch (Exception x) {
            logger.error(x);
            return null;
        }
    } 
	 */
    public String toString() {
        return "Retrieve compounds with AQUIRE data for the specified endpoint";
    }	
}
