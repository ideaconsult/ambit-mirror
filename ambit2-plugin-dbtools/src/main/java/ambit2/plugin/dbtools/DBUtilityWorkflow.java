/*
Copyright (C) 2005-2008  

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

package ambit2.plugin.dbtools;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;

import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.data.IStructureRecord;
import ambit2.core.data.Profile;
import ambit2.core.data.Property;
import ambit2.core.exceptions.AmbitException;
import ambit2.core.processors.DefaultAmbitProcessor;
import ambit2.core.processors.IProcessor;
import ambit2.core.processors.ProcessorsChain;
import ambit2.core.processors.batch.IBatchStatistics;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.core.processors.structure.HydrogenAdderProcessor;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.AbstractDBProcessor;
import ambit2.db.DbReader;
import ambit2.db.SessionID;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.DbDescriptorValuesWriter;
import ambit2.db.processors.FP1024Writer;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.IQueryObject;
import ambit2.db.search.MissingFingerprintsQuery;
import ambit2.db.search.QueryDataset;
import ambit2.descriptors.processors.DescriptorsFactory;
import ambit2.descriptors.processors.PropertyCalculationProcessor;
import ambit2.workflow.ActivityPrimitive;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.QueryInteraction;
import ambit2.workflow.library.DatasetSelection;
import ambit2.workflow.library.LoginSequence;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;

public class DBUtilityWorkflow extends Workflow {
	public DBUtilityWorkflow() {


    
        Sequence seq=new Sequence();
        seq.setName("[Calculator]");    	
        seq.addStep(new QueryInteraction(new MissingFingerprintsQuery()));
        seq.addStep(addCalculationFP());
        
        seq.addStep(new Primitive("NEWQUERY","NEWQUERY",new Performer() {
        	@Override
        	public Object execute() throws Exception {
                QueryDataset q = new QueryDataset("Default");

        		return q;
        	}
        	@Override
        	public String toString() {
        
        		return "Select descriptors";
        	}
        	
        }));

        //seq.addStep(new QueryInteraction(q));
        seq.addStep(addCalculationD());        
        
//        DbSrcDatasetWriter TODO
        setDefinition(new LoginSequence(new DatasetSelection(seq)));

	}
	protected ActivityPrimitive addCalculationFP() {
			ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p = 
				new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
			p.add(new ProcessorStructureRetrieval());		
			p.add(new FPGenerator());
			p.add(new FP1024Writer());
			DbReader<IStructureRecord> batch = new DbReader<IStructureRecord>();
			batch.setProcessorChain(p);
			ActivityPrimitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics> ap = 
				new ActivityPrimitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics>( 
					DBWorkflowContext.QUERY,
					DBWorkflowContext.BATCHSTATS,
					batch,false);
		    ap.setName("Fingerprint calculations");	
		    return ap;
	}	
	protected ActivityPrimitive addCalculationD() {

        
		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p1 = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
		p1.add(new ProcessorStructureRetrieval());		
		p1.add(new Calculator());
		
		DbReader<IStructureRecord> batch1 = new DbReader<IStructureRecord>();
		batch1.setProcessorChain(p1);
		ActivityPrimitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics> ap1 = 
			new ActivityPrimitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics>( 
				"NEWQUERY",
				DBWorkflowContext.BATCHSTATS,
				batch1,false);
	    ap1.setName("Descriptor calculations");		
	    return ap1;
	}
}
//this is a hack
class FPGenerator extends DefaultAmbitProcessor<IStructureRecord,IStructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2912324506031402660L;
	MoleculeReader reader = new MoleculeReader();
	AtomConfigurator c = new AtomConfigurator();
    FingerprintGenerator fp = new FingerprintGenerator();

    
    public IStructureRecord process(IStructureRecord target)
    		throws AmbitException {
    	if (target.getProperties() == null) target.setProperties(new Hashtable());
    	IAtomContainer a = reader.process(target);
    	//CDKHueckelAromaticityDetector d = new CDKHueckelAromaticityDetector();
    	try {
    	//d.detectAromaticity(a);
    	} catch (Exception x) {}
    	long mark = System.currentTimeMillis();
    	BitSet bitset = fp.process(a);
    	target.getProperties().put(AmbitCONSTANTS.Fingerprint,bitset);	
    	target.getProperties().put(AmbitCONSTANTS.FingerprintTIME,System.currentTimeMillis()-mark);

    	return target;

    }
	
}

class Calculator extends AbstractDBProcessor<IStructureRecord,IStructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2912324506031402660L;
	MoleculeReader reader = new MoleculeReader();
	HydrogenAdderProcessor ha = new HydrogenAdderProcessor();
    DescriptorsFactory d = new DescriptorsFactory();
    Profile descriptors = null;
    DbDescriptorValuesWriter writer = new DbDescriptorValuesWriter();
    
    public IStructureRecord process(IStructureRecord target)
    		throws AmbitException {
    	if (target.getProperties() == null) target.setProperties(new Hashtable());
    	IAtomContainer a = reader.process(target);
    	ha.process(a);
    	writer.setStructure(target);
    	if (descriptors==null)	descriptors = d.process(null);
		PropertyCalculationProcessor calc = new PropertyCalculationProcessor();
		Iterator<Property> i = descriptors.getProperties(true);
		int count = 0;
		int countValues = 0;
		while (i.hasNext()) {
			try {
				Property p = i.next();
				if (p.isEnabled()) {
					calc.setProperty(i.next());
					DescriptorValue value = calc.process(a);
					writer.write(value);
				}
			} catch (Exception x) {
				x.printStackTrace();
			}
			
		}    	
    	
    	return target;

    }
	public void open() throws DbAmbitException {
		writer.open();
	}
	@Override
	public void close() throws SQLException {
		super.close();
		writer.close();
	}
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		writer.setConnection(connection);
	}
	@Override
	public void setSession(SessionID sessionID) {

		super.setSession(sessionID);
		writer.setSession(sessionID);
	}
}
