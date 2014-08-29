/* EntrezSearchProcessor.java
 * Author: nina
 * Date: Mar 20, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
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

package ambit2.pubchem;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.NotFoundException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorException;

/**
 * Searches PubChem for structures given search terms (via Entrez) and returns SDF files. 
 * @author nina
 *
 */
public class EntrezSearchProcessor extends HTTPRequest<String, List<IStructureRecord>> {
	
	public static String entrezURL = "http://www.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pccompound&maxret=1&term=%s";
	//protected String term = "\"EINECS %s\"[All Fields] AND \"ChemIDplus\"[SourceName]";

	protected final EntrezESearchParser parser = new EntrezESearchParser();
	protected final PUGProcessor pug = new PUGProcessor();
	protected boolean retrieve_sdf = true;
	
	public boolean isRetrieve_conformers() {
		return pug.isRetrieveConformers();
	}
	public void setRetrieve_conformers(boolean retrieve_conformers) {
		pug.setRetrieveConformers(retrieve_conformers);
	}
	public boolean isRetrieve_sdf() {
		return retrieve_sdf;
	}
	/**
	 * If true (default) retrieves SDF via two stage query (Entrez + PUG).
	 * if false, retrieves only Pubchem CIDs by Entrez query. 
	 * @param retrieve_sdf
	 */
	public void setRetrieve_sdf(boolean retrieve_sdf) {
		this.retrieve_sdf = retrieve_sdf;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -8215370335359483817L;

	@Override
	protected List<IStructureRecord> parseInput(String target, InputStream in)
			throws ProcessorException {
		try {
			 return parser.process(in);
		} catch (AmbitException x) {
			throw new ProcessorException(this,x);
		}
	}
	@Override
	protected void prepareOutput(String target, OutputStream out)
			throws ProcessorException {
		
	}
	public List<IStructureRecord>  process(String target) throws AmbitException {
		try {
			setUrl(String.format(entrezURL,URLEncoder.encode(target, "US-ASCII")));
			List<IStructureRecord> records = super.process(target);
			if ((records == null) || (records.size()==0))
				throw new NotFoundException(target);
			for (int i=records.size()-1;i>=0; i--)
				if (!records.get(i).getFormat().equals(PUGProcessor.PUBCHEM_CID))
					throw new ProcessorException(this,records.get(i).getFormat()+records.get(i).getContent());
			if (retrieve_sdf)
				return pug.process(records);
			else return records;
		} catch (Exception x) {
			throw new ProcessorException(this,x);
		}

	}
	protected String format(IStructureRecord record, String source, String einecs) {
		String content = record.getContent();
		String reportedsource = source;
		StringBuilder b = new StringBuilder();
		if ((source==null) || ("".equals(source)))
			reportedsource = "PUBCHEM-Any";
		if (retrieve_sdf) {
			String replace =
					"\n> <EINECS>\n"+
					einecs+"\n"+
					"\n"+
					"> <ENTREZ_SOURCE>\n"+
					reportedsource+"\n\n";
			int index = content.indexOf("$$$$");
			b.append(content.substring(0,index-1));
			b.append(replace);
			b.append(content.substring(index));
		} else {
			b.append(content.toString());
			b.append(',');
			b.append(einecs);
			b.append(',');
			b.append(reportedsource);
			b.append('\n');
		}
		return b.toString();
	}
	/*
	public static void main(String[] args) {
	    String source=null;
	    String einecs=null;
	    String file= null ;
	    boolean retrieve_sdf = true;
	    boolean retrieve_conformers = true; 
		Options cli = createOptions();
		CommandLineParser parser = new PosixParser();
		try {
		    CommandLine line = parser.parse( cli, args,false );
		    source = getSource(line);
		    einecs = getEinecs(line);
		    file = getFile(line);
		    retrieve_conformers = getRetrieveConformers(line);
		    retrieve_sdf = getRetrieveSDF(line);
		    if (line.hasOption("h") || (((file==null) && (einecs==null)))) {
	    	    HelpFormatter formatter = new HelpFormatter();
	    	    formatter.printHelp( EntrezSearchProcessor.class.getName(), cli );
	    	    Runtime.getRuntime().runFinalization();						 
	    		Runtime.getRuntime().exit(0);
		    }		    
		} catch (Exception x ) {
			System.err.println(x);
    	    HelpFormatter formatter = new HelpFormatter();
    	    formatter.printHelp( EntrezSearchProcessor.class.getName(), cli );
    	    Runtime.getRuntime().runFinalization();						 
    		Runtime.getRuntime().exit(0);			
		}
		
		EntrezSearchOptions options = new EntrezSearchOptions();
		if (file != null) {
			BufferedReader reader=null;
			try {
				reader = new BufferedReader(new FileReader(new File(file)));
				einecs="";
                while((einecs = reader.readLine()) != null) {
                	if ("".equals(einecs.trim())) continue;
                	options.setTerm(einecs.trim(),source);
        			try {
        				EntrezSearchProcessor test = new EntrezSearchProcessor();
        				test.setRetrieve_sdf(retrieve_sdf);
        				test.setRetrieve_conformers(retrieve_conformers);
        				List<IStructureRecord> records = test.process(options.getTerm());
        				for (IStructureRecord record:records) {
        					System.out.print(test.format(record,source, einecs));
        				}
        			} catch (Exception x) {
        				System.err.print(einecs);
        				System.err.print(',');
        				System.err.println(x.getMessage());
        			}                	
                }
			} catch (Exception x) {
				System.err.print(",");
				System.err.println(x.getMessage());				
			} finally {
				try {reader.close();} catch (Exception x) {}
			}
			Runtime.getRuntime().exit(0);	
		} else {
			options.setTerm(einecs,source);
			try {
				EntrezSearchProcessor test = new EntrezSearchProcessor();
				test.setRetrieve_sdf(retrieve_sdf);
				test.setRetrieve_conformers(retrieve_conformers);
				List<IStructureRecord> records = test.process(options.getTerm());
				for (IStructureRecord record:records) {
					System.out.print(test.format(record,source, einecs));
				}
				Runtime.getRuntime().exit(0);			
			} catch (Exception x) {
				System.err.print(einecs);
				System.err.print(',');
				System.err.println(x.getMessage());
				Runtime.getRuntime().exit(0);	
			}
		}
	}
	

   protected static String getSource(CommandLine line) {
	    if( line.hasOption( 's' ) ) 
	    	return line.getOptionValue( 's' );
	    else return null;
   }
   protected static String getEinecs(CommandLine line) {
	    if( line.hasOption( 'e' ) ) 
	    	return line.getOptionValue( 'e' );
	    else return null;
   }
   protected static String getFile(CommandLine line) {
	    if( line.hasOption( 'f' ) ) 
	    	return line.getOptionValue( 'f' );
	    else return null;
  }   
   protected static boolean getRetrieveSDF(CommandLine line) {
	   return ! line.hasOption( 'c' ); 
   }     
   protected static boolean getRetrieveConformers(CommandLine line) {
	    return line.hasOption( 'd' ); 
  }        
	protected static Options createOptions() {
    	Options options = new Options();
        
    	Option einecs   = OptionBuilder.withLongOpt("einecs")
        .hasArg()
        .withArgName("einecs")
        .withDescription("The EINECS to be searched for.")
        .create( "e" );
    	
    	Option source   = OptionBuilder
        .hasArg()
        .withLongOpt("source")
        .withArgName("Pubchem source")
        .withDescription("Pubchem source (e.g. ChEBI, ChemidPlus, etc. Optional")        
        .create( "s" );
    	
    	Option file   = OptionBuilder
        .hasArg()
        .withLongOpt("file")
        .withArgName("txt file with EINECS numbers")
        .withDescription("TXT file with EINECS numbers, one per row")        
        .create( "f" );    	
    	
    	Option cid   = OptionBuilder
        .hasArg()
        .withLongOpt("cidonly")
        .hasArg(false)
        .withDescription("If true, retrieves Pubchem CIDs  only, otherwise retrieves SDFs")        
        .create( "c" );    	    	
    	
    	Option d3   = OptionBuilder
        .hasArg()
        .withLongOpt("d3")
        .hasArg(false)
        .withDescription("Retrieve conformers")        
        .create( "d" );  
    	
    	Option help   = OptionBuilder
        .withLongOpt("help")
        .withDescription("Search PubChem for EINECS numbers")              
        .create( "h" );       	
    	
     	options.addOption(help);
     	options.addOption(einecs);
     	options.addOption(source);
     	options.addOption(file);
     	options.addOption(cid);
     	options.addOption(d3);

    	return options;
    }	   
    */
}
