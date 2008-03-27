package ambit2.database.search;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.openscience.cdk.interfaces.IChemObject;

import ambit2.database.DbConnection;
import ambit2.exceptions.AmbitException;
/*
/*
 * 
select d1.idstructure, 
d1.value, d2.value,d3.value,d4.value,d5.value,d6.value ,distance
from dvalues as d1
join dvalues as d2 using(idstructure) 
join dvalues as d3 using(idstructure) 
join dvalues as d4 using(idstructure) 
join dvalues as d5 using(idstructure) 
join dvalues as d6 using(idstructure) 
join atom_structure as s using(idstructure)
join atom_distance using(iddistance)
where 
d1.iddescriptor=1 and (d1.value between 0 and 2) and
d2.iddescriptor=2 and (d2.value between -11 and -10) and
d3.iddescriptor=3 and (d3.value between 200 and 300) and
d4.iddescriptor=4 and (d4.value between 10 and 11) and
d5.iddescriptor=5 and (d5.value between -2 and -1) and
d6.iddescriptor=6  and (d6.value between -10000 and -9000) and
atom1="C" and atom2="N"  and distance between 2 and 3
order by idstructure
limit 30;

 */
import ambit2.data.molecule.SourceDataset;
import ambit2.database.query.DescriptorQuery;
import ambit2.database.query.DescriptorQueryList;
import ambit2.database.query.DistanceQuery;
import ambit2.database.readers.DbStructureReader;

/**
 * Search by descriptors (ranges, equation, inequality, distances}. The query is specified by {@link ambit2.database.query.DescriptorQueryList}.
 * <br>Example:
 <pre>
 	    try {
	        DbConnection conn = new DbConnection(ITestDB.host,ITestDB.port,ITestDB.database,ITestDB.user,"");
			conn.open(true);
			//Loads available in the database descriptors into DescriptorQueryList
	        DbDescriptors dbd = new DbDescriptors(conn);
			dbd.initialize();
			dbd.initializeInsert();
			DescriptorQueryList descriptors = new DescriptorQueryList();
			dbd.loadQuery(descriptors);
			
//			enable only the first descriptor
			descriptors.getDescriptorQuery(0).setEnabled(true);
			descriptors.getDescriptorQuery(0).setCondition("between");
			descriptors.getDescriptorQuery(0).setMinValue(2);
			descriptors.getDescriptorQuery(0).setMaxValue(3);			
			
			//Launch visualisation of the query
			
			DescriptorQueryPanel panel = new DescriptorQueryPanel(descriptors,null,false);
			panel.setPreferredSize(new Dimension(600,300));
			if (JOptionPane.showConfirmDialog(null,panel,"Descriptors",JOptionPane.YES_NO_OPTION)   == JOptionPane.NO_OPTION)
			    return;
			    
			//Create the reader    

			DbSearchDescriptors reader = new DbSearchDescriptors(
					conn,
					descriptors,
					null,
					0,10
					);

			//We'll need CAS, SMILES , NAME and aliases in addition to descriptor values.
			ProcessorsChain processors = new ProcessorsChain();
			processors.add(new ReadSubstanceProcessor(conn.getConn()));
			processors.add(new ReadCASProcessor(conn.getConn()));
			processors.add(new ReadSMILESProcessor(conn.getConn()));
			processors.add(new ReadNameProcessor(conn.getConn()));
			processors.add(new ReadAliasProcessor(conn.getConn()));
			
			while (reader.hasNext()) {
			    Object object = processors.process(reader.next());
			    System.out.println(((IChemObject) object).getProperties());
			}
			reader.close();
			
			conn.close();
	    } catch (Exception x) {
			x.printStackTrace();
			fail();
		}
 </pre>
 * The output of the example (depends on the database content):
 * <pre>
{SMILES=O=C1c3ccccc3(C(=O)c2cc(N)ccc12), CasRN=117-79-3, AMBIT_ID=5, XLogPDescriptor=2.7200, NSC=5}
{SMILES=O=C1C(Cl)=C(C(=O)c2ccccc12)N(C)C, CasRN=5350-26-5, AMBIT_ID=7, XLogPDescriptor=2.5140, NSC=7}
{SMILES=O=C2c3ccccc3(C(=O)c1c2(ccc(c1[N+](=O)[O-])C)), CasRN=129-15-7, AMBIT_ID=8, XLogPDescriptor=2.2560, NSC=8}
{SMILES=Clc2ccc1c(nccc1(N))c2, CasRN=1198-40-9, AMBIT_ID=13, XLogPDescriptor=2.0590, NSC=13}
{SMILES=S(CC)CCCC, CasRN=638-46-0, AMBIT_ID=19, XLogPDescriptor=2.7430, NSC=19}
{SMILES=n1ccc(cc1N=Cc2ccccc2)C, CasRN=5350-40-3, AMBIT_ID=23, XLogPDescriptor=2.9360, NSC=23}
{SMILES=c1c(cc(c(c1C)C[N+](C)(C)C)C)C, CasRN=5350-44-7, AMBIT_ID=27, XLogPDescriptor=2.6820, NSC=27}
{SMILES=Nc1c(cc(cc1C)C)C, CasRN=6334-11-8, AMBIT_ID=31, XLogPDescriptor=2.5230, NSC=31}
{SMILES=Clc1c2c(ncc1C)cccc2C, CasRN=5350-51-6, AMBIT_ID=36, XLogPDescriptor=2.8990, NSC=36}
{SMILES=ON=Cc1ccc(OC)cc1, CasRN=3235-04-9, AMBIT_ID=44, XLogPDescriptor=2.2290, NSC=44}

 * </pre>
 * The reader can be attached to {@link ambit2.io.batch.DefaultBatchProcessing} which uses for example 
 * {@link ambit2.io.DelimitedFileWriter} to write the results above into comma delimited file.<br>
 * See also {@link ambit2.ui.actions.search.DbDescriptorsSearchAction}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class DbSearchDescriptors extends DbStructureReader {
	protected DescriptorQueryList descriptors;
	protected Statement stmt;
	/**
	 * 
	 * @param connection
	 * @param descriptors  The query {@link ambit2.database.query.DescriptorQueryList}
	 * @param srcDataset The dataset {@link SourceDataset}. if null, searches the entire database
	 * @param page
	 * @param pagesize
	 * @throws AmbitException
	 */
	public DbSearchDescriptors(DbConnection connection, DescriptorQueryList descriptors,
			SourceDataset srcDataset, int page, int pagesize) throws AmbitException {
		super();
		openResultSet(connection, descriptors,srcDataset,page,pagesize);
	}
	
	protected void openResultSet(DbConnection connection, DescriptorQueryList descriptors,
			SourceDataset srcDataset, int page, int pagesize) throws AmbitException {
		setPage(page);
		setPagesize(pagesize);
		this.descriptors = descriptors;
		if ((descriptors==null) || (descriptors.size()==0)) throw new AmbitException("Empty query!");

		try {
			stmt = connection.getConn().createStatement(java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			String sql = descriptors.toSQL(srcDataset,page,pagesize);
			System.out.println(sql);
			if (sql.equals("")) throw new AmbitException("Empty query");
			//System.out.println(sql);
			setResultset(stmt.executeQuery(sql));
		} catch (SQLException x) {
			resultset = null;
			stmt = null;
			throw new AmbitException(x);
		}
	}
	public Object next() {
		Object o = super.next();
		
		if (o instanceof IChemObject) {
    
			if (descriptors.isCombineWithAND()) {
				int k = 2;
				try {
				for (int i=0; i < descriptors.size();i++) {
					DescriptorQuery q = descriptors.getDescriptorQuery(i);
					if (q.isEnabled() && !(q instanceof DistanceQuery )) {
						((IChemObject) o).setProperty(q,resultset.getString(k));
						k++;					
					}			
				}	
				} catch (SQLException x) {
					x.printStackTrace();
				}
			}
		}
		return o;
	}
	public void close() throws IOException {
		super.close();
		try {
			if (stmt != null) stmt.close();
		} catch (Exception x) {
			
		}
	}

}


