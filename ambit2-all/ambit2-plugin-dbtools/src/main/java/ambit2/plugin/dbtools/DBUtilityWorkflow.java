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

import java.util.BitSet;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.DbReader;
import ambit2.db.processors.DescriptorsCalculator;
import ambit2.db.processors.FP1024Writer;
import ambit2.db.processors.ProcessorMissingDescriptorsQuery;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.structure.MissingFingerprintsQuery;
import ambit2.descriptors.processors.DescriptorsFactory;
import ambit2.workflow.ActivityPrimitive;
import ambit2.workflow.DBProcessorPerformer;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.QueryInteraction;
import ambit2.workflow.UserInteraction;
import ambit2.workflow.library.DatasetSelection;
import ambit2.workflow.library.LoginSequence;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;

public class DBUtilityWorkflow extends Workflow {
	protected static final String DESCRIPTORS_NEWQUERY = "ambit2.plugin.dbtools.DBUtilityWorkflow.DESCRIPTORS_NEWQUERY";
	public DBUtilityWorkflow() {
		

    
        Sequence seq=new Sequence();
        seq.setName("[Calculator]");    	
        seq.addStep(new QueryInteraction(new MissingFingerprintsQuery()));
        seq.addStep(addCalculationFP());
        
        //seq.addStep(new QueryInteraction(q));
	    DescriptorsFactory factory = new DescriptorsFactory();
	    Profile<Property> descriptors;
	    try {
	    	descriptors = factory.process(null);
	    } catch (Exception x) {
	    	x.printStackTrace();
	    	descriptors = new Profile<Property>();
	    }
	    UserInteraction<Profile<Property>> defineDescriptors = new UserInteraction<Profile<Property>>(
        		descriptors,
        		DBWorkflowContext.DESCRIPTORS,
        		"Select descriptor(s)");	
	    seq.addStep(defineDescriptors);
        Primitive q = new Primitive(DBWorkflowContext.DESCRIPTORS,DESCRIPTORS_NEWQUERY,new Performer() {
        	@Override
        	public Object execute() throws Exception {
                //QueryDataset q = new QueryDataset("Default");
        		ProcessorMissingDescriptorsQuery p = new ProcessorMissingDescriptorsQuery();
        		//TODO set scope - dataset
        		//scope - entire db, query, dataset!! to be used elsewhere
        		return p.process((Profile)getTarget());
        	}
        	@Override
        	public String toString() {
        
        		return "Select descriptors";
        	}
        	
        });
        q.setName("Select descriptor(s)");
        seq.addStep(q);	    
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
	//TODO extract in a class and reuse in other workflows
	protected Primitive addCalculationD() {

		final DescriptorsCalculator calculator = new DescriptorsCalculator();
		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p1 = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();
		p1.add(new ProcessorStructureRetrieval());		
		p1.add(calculator);
		
		DbReader<IStructureRecord> batch1 = new DbReader<IStructureRecord>();
		batch1.setProcessorChain(p1);
		
		DBProcessorPerformer<DbReader<IStructureRecord>,IQueryRetrieval<IStructureRecord>,IBatchStatistics> performer = 
			new DBProcessorPerformer<DbReader<IStructureRecord>,IQueryRetrieval<IStructureRecord>,IBatchStatistics>(batch1,false) {
			public IBatchStatistics execute() throws Exception {
				Object o = getContext().get(DBWorkflowContext.DESCRIPTORS);
				calculator.setDescriptors((Profile)o);
				return super.execute();
			}
		};	
		
		Primitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics> ap1 = 
			new Primitive<IQueryRetrieval<IStructureRecord>,IBatchStatistics>( 
				DESCRIPTORS_NEWQUERY,
				DBWorkflowContext.BATCHSTATS,
				performer);
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
    	IAtomContainer a = reader.process(target);
    	//CDKHueckelAromaticityDetector d = new CDKHueckelAromaticityDetector();
    	try {
    	//d.detectAromaticity(a);
    	} catch (Exception x) {}
    	long mark = System.currentTimeMillis();
    	BitSet bitset = fp.process(a);
    	target.setProperty(Property.getInstance(AmbitCONSTANTS.Fingerprint,AmbitCONSTANTS.Fingerprint),bitset);	
    	target.setProperty(Property.getInstance(AmbitCONSTANTS.FingerprintTIME,AmbitCONSTANTS.Fingerprint),System.currentTimeMillis()-mark);

    	return target;

    }
	
}

