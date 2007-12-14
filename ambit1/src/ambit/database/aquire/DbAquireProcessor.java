/*
Copyright (C) 2005-2006  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit.database.aquire;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;

import ambit.data.IAmbitEditor;
import ambit.data.experiment.AQUIREStudy;
import ambit.data.experiment.AquireTemplate;
import ambit.database.ConnectionPool;
import ambit.database.processors.DefaultDbProcessor;
import ambit.database.search.DbExactSearchReader;
import ambit.exceptions.AmbitException;
import ambit.exceptions.PropertyNotInTemplateException;
import ambit.misc.AmbitCONSTANTS;
import ambit.test.ITestDB;

public class DbAquireProcessor extends DefaultDbProcessor {
    public static final String aquire_select = 
    	"select aquire.AquireLocation,TestCAS,chemicals.Chemical_Name,\n" +
    	"Endpoint,effects.definition as Effect,exposuretypecodes.description as Exposure,\n"+
    	"durationunits,TestDuration,TestDurationOp,MinDuration,MinDurationOp,MaxDuration,MaxDurationOp,\n"+
    	"ConcentrationTypeCodes.description as ConcentrationType,\n"+
    	"ion1,concentration1mean,concentration1min,concentration1max,\n"+
    	"ion2,concentration2mean,concentration2min,concentration2max,\n"+
    	"BCF1MeanOp, BCF1Mean, BCF1MinOp, BCF1Min, BCF1MaxOp, BCF1Max,\n"+
        "BCF2MeanOp, BCF2Mean, BCF2MinOp, BCF2Min, BCF2MaxOp, BCF2Max,\n"+
    	"ConcentrationUnits,\n"+
    	"ReferenceDB,ReferenceType,Author,Title,Source,PublicationYear,\n"+
    	"TestFormulation,TestRadioLabel,TestCharacteristics,Trend,Tissue,WaterType,TestLocation,OrganicCarbonType,LatinName\n"+
    	"from aquire\n"+
    	"left join effects on aquire.effect=effects.code\n"+
    	"left join ConcentrationTypeCodes on aquire.Concentration1Type=ConcentrationTypeCodes.code\n"+
    	"left join exposuretypecodes on aquire.ExposureType=exposuretypecodes.code\n"+
    	"left join reference on aquire.ReferenceNumber=reference.referenceNumber\n"+
    	"left join chemicals on aquire.testcas = chemicals.cas\n"+
    	"left join chemicalinfo on aquire.AquireLocation=chemicalinfo.AquireLocation\n"+
    	"left join species_data on aquire.SpeciesNumber=species_data.SpeciesID\n"+
    	"where TestCAS=? ";
    protected PreparedStatement ps = null;
    
    protected ConnectionPool pool;
    protected Connection aquireConnection;
    protected Connection connection;
    protected boolean useSimpleTemplate = true;
    protected AquireTemplate aquireTemplate;
    protected AQUIREStudy aquireStudy;
    //protected FindUniqueProcessor findUnique;
    
    public DbAquireProcessor(Connection connection) throws AmbitException {
    	this(connection,ITestDB.host,ITestDB.port,"root","",null,null,true);
    }
	public DbAquireProcessor(Connection connection, 
			String aquireHost, String aquirePort,  String aquireUser, String aquirePassword, 
			String endpoint, String species, boolean useSimpleTemplate) throws AmbitException {
		super(connection);
		this.useSimpleTemplate = useSimpleTemplate;
		aquireTemplate = new AquireTemplate(useSimpleTemplate);
		aquireStudy = new AQUIREStudy(AmbitCONSTANTS.AQUIRE,-1,aquireTemplate);
		try {
			pool = new ConnectionPool(
			        aquireHost,aquirePort,"aquire",aquireUser,aquirePassword,1,1);
			aquireConnection = pool.getConnection();
			
	        
	        try {
		        ps = aquireConnection.prepareStatement(createSql(endpoint, species));

	        } catch (SQLException x) {
	        	throw new AmbitException(x);


	        }
			
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	@Override
	public void prepare(Connection connection) throws AmbitException {
		this.connection = connection;
			//findUnique = new FindUniqueProcessor(connection);
	}
	protected String createSql(String endpoint, String species) {
        StringBuffer sql = new StringBuffer();
        sql.append(aquire_select);
        /*
        if (cas != null) {
        	sql.append("where ");
            sql.append(aquire_seek_cas);
            sql.append('"');
            sql.append(cas.toString().replaceAll("-",""));
            sql.append('"');
            w = true;
        }
        */
        if (endpoint != null) {
        	sql.append(" and ");
            sql.append(" Endpoint=");
            sql.append('"');
            sql.append(endpoint.toString());
            sql.append('"');
        }
        if (species != null) {
        	sql.append(" and "); 
            sql.append(" LatinName = \"");
            sql.append(species);
            sql.append('"');
        }                
        /*
        sql.append(aquire_limit);
        sql.append(Integer.toString(page));
        sql.append(",");
        sql.append(Integer.toString(pagesize));
        
        */
        //System.out.println(sql.toString());
        return sql.toString();

	}
	public boolean lookupCAS(IAtomContainer mol, Connection connection) {
	    boolean ok = false;
		if (mol.getProperty(CDKConstants.CASRN) == null) {
		    try {
		        
			    DbExactSearchReader lookup = new DbExactSearchReader(
			            connection,mol,null,0,100);
			    while (lookup.hasNext()) {
			        IMolecule m = (IMolecule) lookup.next();
			        if (m.getProperty(CDKConstants.CASRN) != null) {
			            mol.setProperties(m.getProperties());
			            ok = true;
			            break;
			        }
			    }
			    lookup.close();
		    } catch (Exception x) {
		        
		    }
		} else ok = true;
		return ok;	    
		
	}	
	@Override
	public Object process(Object object) throws AmbitException {
		if (object instanceof IAtomContainer) {
			IAtomContainer ac = (IAtomContainer) object;
			Object cas = ac.getProperty(CDKConstants.CASRN);
			if (cas == null) {
				lookupCAS(ac, connection);
				cas = ac.getProperty(CDKConstants.CASRN);
				if (cas == null) return object;
			}
			try {
				ps.clearParameters();
				ps.setString(1, cas.toString().replaceAll("-",""));
				ResultSet rs = ps.executeQuery();
				
				ResultSetMetaData rsmd = rs.getMetaData();
				int numCols = rsmd.getColumnCount();
				Object ar = ac.getProperty(AmbitCONSTANTS.AQUIRE);
				
				AquireRecords aquireRecords = null;
				if ((ar != null) && (ar instanceof AquireRecords)) aquireRecords = (AquireRecords)ar;
				
				while (rs.next()) {
			     	AquireExperiment props = new AquireExperiment(null, aquireStudy,null);
		        	for (int i =1; i <= numCols; i++) {
		    			try {
		    				Object o = rs.getObject(i);
		    				if (o != null)
		    					props.setResult(rsmd.getColumnName(i),o);
		    			} catch (PropertyNotInTemplateException x) {
		    				logger.warn(x);
		    			} catch (Exception x) {
		    				if (logger.isDebugEnabled()) logger.debug(x);
		    				else
		    				logger.error(x.getMessage());
		    			}
		    			
		    		}
		        	//Aquirelocation
                    //ac.setProperty(props.retrieveResult("Endpoint")+"_"+"AQUIRE_"+rs.getString(1),props);
		        	if (useSimpleTemplate)
		        		ac.setProperty(props.retrieveResult("Endpoint")+"_"+"AQUIRE_"+rs.getString(1),props);
		        	else {
		        		if (aquireRecords==null) aquireRecords = new AquireRecords();
		        		aquireRecords.addItem(props);
		        	}
		        		
				}
				if (!useSimpleTemplate && (aquireRecords!=null))
					ac.setProperty(AmbitCONSTANTS.AQUIRE,aquireRecords);
				rs.close();
				return ac;
			} catch (SQLException x) {
				logger.error(x);
				throw new AmbitException(x);
			}
		} else return null;
	}
	
	@Override
	public void close() {
		super.close();
		connection = null;
		try {
			aquireConnection.close();
		} catch (Exception x) {
			logger.error(x);
		}
		pool.returnConnection(aquireConnection);
		pool = null;
		
	}
	@Override
	public String toString() {
		return "Retrieves AQUIRE data";
	}
	public boolean isUseSimpleTemplate() {
		return useSimpleTemplate;
	}
	public void setUseSimpleTemplate(boolean useSimpleTemplate) {
		this.useSimpleTemplate = useSimpleTemplate;
		aquireTemplate.setSimple(useSimpleTemplate);
	}
	@Override
	public IAmbitEditor getEditor() {
		return new AquireProcessorEditor(this);
	}
}

class AquireProcessorEditor extends JPanel implements IAmbitEditor {
	protected DbAquireProcessor processor;
	public AquireProcessorEditor(final DbAquireProcessor processor) {
		super();
		this.processor = processor;
		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		ButtonGroup g = new ButtonGroup();
		JRadioButton boxSimple = new JRadioButton("Retrieve main fields only");		
		JRadioButton boxAll = new JRadioButton("Retrieve all available data per measurement");
		
		boxSimple.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Object source = e.getItemSelectable();
				JRadioButton box = (JRadioButton)source;
				processor.setUseSimpleTemplate(box.isSelected()); 
			}
		});
		boxAll.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Object source = e.getItemSelectable();
				JRadioButton box = (JRadioButton)source;
				processor.setUseSimpleTemplate(!box.isSelected()); 
			}
		});
		g.add(boxAll);
		g.add(boxSimple);
		boxSimple.setSelected(true);
		add(boxSimple);
		add(boxAll);
		setBorder(BorderFactory.createTitledBorder("AQUIRE database"));
	}
	public JComponent getJComponent() {
		return this;
	}

	public boolean isEditable() {
		return true;
	}

	public void setEditable(boolean editable) {

		
	}

	public boolean view(Component parent, boolean editable, String title) throws AmbitException {
		return JOptionPane.showConfirmDialog(parent,this) == JOptionPane.OK_OPTION;
	}

}

