package ambit.ui.actions.search;

import java.awt.event.KeyEvent;
import java.sql.Connection;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.smiles.SmilesParser;

import ambit.database.data.ISharedDbData;
import ambit.database.processors.ReadPlanarSubstances;
import ambit.database.search.DbSubstructurePrescreenReader;
import ambit.exceptions.AmbitException;
import ambit.processors.IAmbitProcessor;
import ambit.processors.ProcessorsChain;
/**
 * Search for substructure "c1ccc2ccccc2(c1)".
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class DbConjugatedSystemsSearch extends DbSubstructureSearchAction {

	public DbConjugatedSystemsSearch(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Search");

	}

	public DbConjugatedSystemsSearch(Object userData, JFrame mainFrame,
			String arg0) {
		this(userData, mainFrame, arg0,null);

	}

	public DbConjugatedSystemsSearch(Object userData, JFrame mainFrame,
			String arg0, Icon arg1) {
		super(userData, mainFrame, arg0, arg1);
		putValue(AbstractAction.MNEMONIC_KEY,new Integer(KeyEvent.VK_J));
		putValue(AbstractAction.SHORT_DESCRIPTION,"Search for planar compounds with conjugated systems (substructure search for naphtyl group and spherosity < 0.2)");

	}
	public IIteratingChemObjectReader getSearchReader(Connection connection, Object query, String limit) throws AmbitException{
	    SmilesParser p = new SmilesParser();
	    try {
		    IMolecule mol = p.parseSmiles("c1ccc2ccccc2(c1)");
			if (mol != null)
			return new DbSubstructurePrescreenReader(connection,mol,null,1,8000);
			else return null;
	    } catch (Exception x) {
	        throw new AmbitException(x);
	    }
	}		
    public IAmbitProcessor getProcessor() {
        if (userData instanceof ISharedDbData) {
        	ProcessorsChain p = new ProcessorsChain();
        	ISharedDbData dbaData = (ISharedDbData) userData;
	        //p.add(new ReadSubstanceProcessor(dbaData.getDbConnection().getConn()));
        	try {
	        p.add(new ReadPlanarSubstances(dbaData.getDbConnection().getConn()));
        	} catch (AmbitException x) {
        	    
        	}
	        //p.add(new PlanarityFilter());
	        return p;
		} else return null;

    }	
	/*
	public IteratingChemObjectReader getSearchReader(Connection connection, Object query, String limit) throws AmbitException{
		Molecule mol = (Molecule)FunctionalGroups.createAtomContainer("c1ccc2ccccc2(c1)",false);
		if (mol != null)
			return new DbSubstructureSearchReader(connection,mol,null, limit);
		else return null;
	}
	*/		
	/*
	public IAmbitProcessor getProcessor() {
		if (userData instanceof AmbitDatabaseToolsData) {
			AmbitDatabaseToolsData dbaData = ((AmbitDatabaseToolsData) userData);		
			DbConnection conn = dbaData.getDbConnection();		
			DescriptorQueryList descriptors = new DescriptorQueryList();
			descriptors.addItem(DescriptorFactory.createAmbitDescriptorFromCDKdescriptor(
					new SpherosityDescriptor(), null,"",""));
			
			DbDescriptors dbd = new DbDescriptors(conn);
			try {
				dbd.initialize();
				dbd.initializeInsert();
				for (int i=0; i < descriptors.size()-1;i++) {
					dbd.getDescriptorByName(descriptors.getDescriptorDef(i));
				}
				dbd.close();
			} catch (Exception x) {
				return null;
			}
			return new ReadDescriptorsProcessor(descriptors,conn.getConn()) {
				public Object process(Object object) throws AmbitException {
					try {
						Object o =  super.process(object);
						Object v = ((ChemObject) o).getProperty("ambit.data.descriptors.SpherosityDescriptor");
						if (v != null) {
							if (Double.parseDouble(v.toString()) < 0.1) return object;
						}
					} catch (Exception x) {
						return null;
					}
					return null;
				}
			};
		} return null;	
	}
	*/
	/*
	public IAmbitProcessor getProcessor() {
		return new DefaultAmbitProcessor() {
			protected SpherosityDescriptor descriptor = null;
			
			public Object process(Object object) throws AmbitException {
				if (descriptor == null) descriptor = new SpherosityDescriptor();
				if (object instanceof AtomContainer) {
					try {
						DescriptorValue value =  descriptor.calculate((AtomContainer) object);
						double v = ((DoubleResult) value.getValue()).doubleValue();	
						//if (v < 0.1)  {
							((AtomContainer) object).setProperty(descriptor, Double.toString(
									v
									));
							return object;
						//}
						
					} catch (CDKException x) {
						return null;
					}
				}	
				return null;
			}
		};
	}
	*/
}
