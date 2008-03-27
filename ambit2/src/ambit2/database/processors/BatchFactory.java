/* BatchFactory.java
 * Author: Nina Jeliazkova
 * Date: 2006-6-20 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.database.processors;

import java.sql.Connection;

import toxTree.exceptions.DecisionMethodException;
import toxTree.tree.cramer.CramerRules;
import verhaar.VerhaarScheme;
import ambit2.database.query.DescriptorQueryList;
import ambit2.log.AmbitLogger;
import ambit2.processors.Builder3DProcessor;
import ambit2.processors.IAmbitProcessor;
import ambit2.processors.ProcessorsChain;
import ambit2.processors.results.FingerprintProfile;
import ambit2.processors.structure.FingerprintGenerator;
import ambit2.processors.structure.FingerprintProfileGenerator;
import ambit2.processors.structure.SmilesGeneratorProcessor;
import ambit2.processors.toxtree.ToxTreeProcessor;
import ambit2.ui.editors.IdentifiersProcessor;
import ambit2.database.aquire.DbAquireProcessor;
import ambit2.database.data.AmbitDatabaseToolsData;
import ambit2.database.data.ISharedDbData;

/**
 * Used in {@link ambit2.ui.actions.file.FileBatchProcessingAction}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-20
 */
public class BatchFactory {
    protected static AmbitLogger logger = new AmbitLogger(BatchFactory.class);
    /**
     * 
     */
    protected BatchFactory() {
        super();
    }
    public static IAmbitProcessor getIdentifiersProcessors(
            Connection connection,
            ProcessorsChain processors) {
        
	        processors.add(new IdentifiersProcessor());
	        
			IAmbitProcessor p = new CASSmilesLookup(connection,true);
			processors.addProcessor(p);
			p = new SmilesGeneratorProcessor(5*60*1000);
			processors.addProcessor(p);			
			try {
				p = new FindUniqueProcessor(connection);
				p.setEnabled(true);
				processors.addProcessor(p);
			} catch (Exception x) {
				logger.error(x);
			}
        
    		return processors;
    }		
    public static IAmbitProcessor getCalculationProcessors(
            Connection connection,
            DescriptorQueryList descriptors,
            ProcessorsChain processors) {
    		
    		IAmbitProcessor p;
    		
    		
    		if (connection != null) {
    			p = new ReadDescriptorsProcessor(
    					descriptors,connection
    					);
    			p.setEnabled(false);
    			processors.addProcessor(p);
    		}
    		try {
    			p = new ToxTreeProcessor(new CramerRules());
    			p.setEnabled(false);
    			processors.addProcessor(p);
    		} catch (DecisionMethodException x) {
    			logger.error(x);
    		}
    		try {
    			p = new ToxTreeProcessor(new VerhaarScheme());
    			p.setEnabled(false);
    			processors.addProcessor(p);
    		} catch (DecisionMethodException x) {
    			logger.error(x);		
    		}		
    		
 
    		return processors;
    }
    
    public static IAmbitProcessor getFingerprintsProcessors(
            Connection connection,ProcessorsChain processors
            ) {
		IAmbitProcessor p = new FingerprintGenerator();
		p.setEnabled(false);
		processors.addProcessor(p);
		
		IAmbitProcessor processor = new FingerprintProfileGenerator();
		FingerprintProfile profile = new FingerprintProfile("Fingerprint profile ");
		processor.setResult(profile);						
    	processors.addProcessor(processor);     
		return processors;
    }



}
