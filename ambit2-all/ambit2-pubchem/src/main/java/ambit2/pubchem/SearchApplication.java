package ambit2.pubchem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.pubchem.NCISearchProcessor.METHODS;

public class SearchApplication {
	protected int timeout=60000;
	public void setTimeout(int ms) {
		timeout = ms;
	}
	public void processCactus(String file,String query,NCISearchProcessor.METHODS output,long wait,String host) {
		NCISearchProcessor p = new NCISearchProcessor();
		p.setTimeout(timeout);
		if (host != null) p.setHost(host);
		p.setWait_ms(wait);
		if (file != null) {
			BufferedReader reader=null;
			try {
				reader = new BufferedReader(new FileReader(new File(file)));
				String line ="";
                while((line = reader.readLine()) != null) {
                	if ("".equals(line.trim())) continue;
                	
        			try {
        				p.setOption(output);
        				String result = p.process(line);
        				System.out.println(result);
        			} catch (FileNotFoundException x) {
        				//skip
        			} catch (Exception x) {
        				System.err.print(line);
        				System.err.print(',');
        				System.err.println(x.getMessage());

        			}              	
                }
			
			} catch (Exception x) {
				System.err.println(x.getMessage());				
			} finally {
				try {reader.close();} catch (Exception x) {}
			}
			Runtime.getRuntime().exit(0);	
		} else {
			try {
				p.setOption(output);
				System.out.println(p.process(query));
			} catch (FileNotFoundException x) {
				//skip
				System.err.print(query);
				System.err.print(',');
				System.err.println(x.getMessage());						
			} catch (Exception x) {

				System.err.print(query);
				System.err.print(',');
				System.err.println(x.getMessage());				
				/*
				System.err.print(',');
				System.err.print(output);
				System.err.print(',');			
				System.err.print(x.getMessage());
				x.printStackTrace();
				*/
			}
		}
	}
	public static void main(String[] args) {
		String query=null;
		METHODS output=null;
	    String source=null;
	    String einecs=null;
	    String file= null ;
	    int timeout = 0;
	    long wait = 0;
	    boolean retrieve_sdf = true;
	    boolean retrieve_conformers = true; 
		Options cli = createOptions();
		CommandLineParser parser = new PosixParser();
		try {
		    CommandLine line = parser.parse( cli, args,false );
		    timeout = getTimeout(line);
		    file = getFile(line);		    
		    query = getQuery(line);
		    output = getOutput(line);
		    wait = getWait(line);
		    if (output!=null) {
		    	SearchApplication app = new SearchApplication();
		    	app.setTimeout(timeout);
		    	app.processCactus(file,query,output,wait,getHost(line));
		    	Runtime.getRuntime().exit(0);	
		    }
		    source = getSource(line);
		    einecs = getEinecs(line);

		    retrieve_conformers = getRetrieveConformers(line);
		    retrieve_sdf = getRetrieveSDF(line);
		    if (line.hasOption("h") || (((file==null) && (einecs==null)))) {
	    	    HelpFormatter formatter = new HelpFormatter();
	    	    formatter.printHelp( SearchApplication.class.getName(), cli );
	    	    Runtime.getRuntime().runFinalization();						 
	    		Runtime.getRuntime().exit(0);
		    }		    
		} catch (Exception x ) {
			System.err.println(x);
    	    HelpFormatter formatter = new HelpFormatter();
    	    formatter.printHelp( SearchApplication.class.getName(), cli );
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
        				test.setTimeout(timeout);
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
				test.setTimeout(timeout);
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
   protected static String getQuery(CommandLine line) {
	    if( line.hasOption( 'q' ) ) 
	    	return line.getOptionValue( 'q' );
	    else return null;
  }
   protected static METHODS getOutput(CommandLine line) {
	    if( line.hasOption( 'o' ) ) 
	    	return METHODS.valueOf(line.getOptionValue( 'o' ).toLowerCase());
	    else return null;
 }   
   protected static String getFile(CommandLine line) {
	    if( line.hasOption( 'f' ) ) 
	    	return line.getOptionValue( 'f' );
	    else return null;
  }   
   protected static long getWait(CommandLine line) {
	    if( line.hasOption( 'w' ) ) 
	    	try {
	    	return Long.parseLong(line.getOptionValue( 'w' ));
	    	} catch (Exception x) {return 0;}
	    else return 0;
 }      
   protected static int getTimeout(CommandLine line) {
	    if( line.hasOption( 't' ) ) 
	    	try {
	    	return Integer.parseInt(line.getOptionValue( 't' ));
	    	} catch (Exception x) {return 0;}
	    else return 60000;
}  
   protected static String getHost(CommandLine line) {
	    if( line.hasOption( 'u' ) ) 
	    	try {
	    	return line.getOptionValue( 'u' );
	    	} catch (Exception x) {return null;}
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
        /*
    	Option repository   = OptionBuilder.withLongOpt("repository")
        .hasArg()
        .withArgName("repository")
        .withDescription("pubchem or cactus.nci.nih.gov, default pubchem")
        .create( "r" );
    	*/
    	Option einecs   = OptionBuilder.withLongOpt("einecs")
        .hasArg()
        .withArgName("einecs")
        .withDescription("The EINECS to be searched for. (pubchem)")
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
        .withArgName("txt file with results numbers")
        .withDescription("TXT file with EINECS numbers (pubchem) or TXT file with query strings (e.g. CAS) for http://cactus.nci.nih.gov, one per row")        
        .create( "f" );    	
    	
    	Option cid   = OptionBuilder
        .hasArg()
        .withLongOpt("cidonly")
        .hasArg(false)
        .withDescription("If true, retrieves Pubchem CIDs  only, otherwise retrieves SDFs (pubchem)")        
        .create( "c" );    	    	
    	
    	Option d3   = OptionBuilder
        .hasArg()
        .withLongOpt("d3")
        .hasArg(false)
        .withDescription("Retrieve conformers (pubchem)")        
        .create( "d" );  
    	
    	Option query   = OptionBuilder
        .hasArg()
        .withLongOpt("query")
        .withArgName("Query string")
        .withDescription("Query string (cas,smiles,inchi) to be submitted to http://cactus.nci.nih.gov")        
        .create( "q" );     	
    	
    	StringBuilder b = new StringBuilder();
    	String d = "";
    	for (METHODS m : METHODS.values()) {
    		b.append(d);
    		b.append(m.toString());
    		d = ",";
    	}
    	Option output   = OptionBuilder
        .hasArg()
        .withLongOpt("output")
        .withArgName("type of data to be retrieved")
        .withDescription(b.toString() + " (cactus.nci.nih.gov)")        
        .create( "o" ); 
    	
    	Option help   = OptionBuilder
        .withLongOpt("help")
        .withDescription("Search PubChem for EINECS numbers or http://cactus.nci.nih.gov for smiles or inchi")              
        .create( "h" );      
    	
    	Option wait   = OptionBuilder
        .hasArg()    	
        .withLongOpt("wait interval, ms")
        .withArgName("wait interval, ms")
        .withDescription("Waiting interval (ms) between subsequent queries (cactus.nci.nih.gov) default 0")              
        .create( "w" );    
    	
    	Option timeout   = OptionBuilder
        .hasArg()    	
        .withLongOpt("timeout, ms")
        .withArgName("timeout, ms")
        .withDescription("HTTP connection timeout, ms")              
        .create( "t" );        	

    	
    	Option host   = OptionBuilder
        .withLongOpt("host")
        .withDescription("Host (http://cactus.nci.nih.gov)")              
        .create( "u" );       	
    	
     	options.addOption(help);
     	options.addOption(einecs);
     	options.addOption(source);
     	options.addOption(file);
     	options.addOption(cid);
     	options.addOption(d3);
     	options.addOption(query);
     	options.addOption(output);
     	options.addOption(wait);
     	options.addOption(host);
     	options.addOption(timeout);

    	return options;
    }	   
}
