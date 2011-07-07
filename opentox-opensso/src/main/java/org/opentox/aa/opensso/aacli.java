package org.opentox.aa.opensso;

import java.util.Hashtable;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.opentox.aa.IOpenToxUser;
import org.opentox.aa.OpenToxUser;
import org.opentox.aa.opensso.AAServicesConfig.CONFIG;
import org.opentox.aa.policy.IPolicyHandler;

public class aacli {
	protected String authService;
	protected String policyService;
	protected OpenSSOToken ssotoken;
	protected String policyId;
	protected IOpenToxUser user;
	protected String uri;
	protected policy_command command = policy_command.authorize;
	
	enum policy_command {
		authorize,
		list,
		delete,
		create
	}
	public aacli() {
		super();
		authService = AAServicesConfig.getSingleton().getConfig(CONFIG.opensso);
		policyService = AAServicesConfig.getSingleton().getConfig(CONFIG.policy);
		user = new OpenToxUser();
		user.setUserName(AAServicesConfig.getSingleton().getConfig(CONFIG.user));
		user.setPassword(AAServicesConfig.getSingleton().getConfig(CONFIG.pass));
	}
	
	public void login() throws Exception {
		Hashtable<String, String> results = new Hashtable<String, String>();
		System.out.println(String.format("Using %s: %s",_option.authn.getDescription(),authService));
		System.out.println(String.format("Using %s: %s",_option.authz.getDescription(),policyService));
		
		if (ssotoken==null) ssotoken = new OpenSSOToken(authService);
		if (ssotoken.getToken()==null) {
			ssotoken.login(user);
			System.out.println(String.format("Logged as %s token %s",user.getUsername(),ssotoken.getToken()));
		} else {
			System.out.println(String.format("Using provided token %s",ssotoken.getToken()));
			if (!ssotoken.isTokenValid()) {
				throw new Exception("Invalid token");
			}
		}
		ssotoken.getAttributes(new String[] {"uid"},results);
		System.out.println(results);
		
	}
	public void logout() throws Exception {
		if ((ssotoken!=null) && (ssotoken.getToken()!=null)) ssotoken.logout();
		System.out.println("Logout. Token invalidated.");
	}
	protected void log(policy_command command, String message) {
		System.out.println(String.format("%s> %s",command, message));
	}
	public int run() throws Exception {
		final OpenSSOPolicy policy = new OpenSSOPolicy(policyService);
		
		switch (command) {
		case authorize: {
			if (uri==null) throw new Exception("Empty URI");
			log(command,String.format("URI: %s",uri));
			String[] mm = new String[] {"GET","POST","PUT","DELETE"};
			for (String m : mm)
				try {
				log(command,String.format("%s: %s %s",uri,m,ssotoken.authorize(uri, m)?"Allow":"Deny"));
				} catch (Exception x) {
					log(command,String.format("%s: %s %s",uri,m,x.getMessage()));
				}
			break;	
		}
		case list: {
		
			IPolicyHandler handler = new IPolicyHandler() {
				@Override
				public void handleOwner(String owner) throws Exception {
					if (owner !=null)
					log(command,String.format("Owner: %s",owner));
					
				}				
				@Override
				public void handlePolicy(String policyID) throws Exception {
					 log(command,String.format("PolicyID: %s",policyID));
				}
				@Override
				public void handlePolicy(String policyID, String content)
						throws Exception {
					log(command,String.format("PolicyID: %s \n %s",policyID,content));
					
				}
			};			
			if (uri!=null) {
				log(command,String.format("URI: %s",uri));
				OpenToxUser owner = new OpenToxUser();
				int code = policy.getURIOwner(ssotoken, uri, owner, handler);
				
				log(command,String.format("HTTP result code: %d",code));
				if (policyId!=null) {
					log(command,String.format("Retrieve XML of policyId: %s",policyId));
					try {
						code = policy.listPolicy(ssotoken, policyId, handler);
						log(command,String.format("HTTP result code: %d",code));
						if (code ==401) log(command,"Error: Only the policy creator can retrieve its content.");
					} catch (Exception x) {
						log(command,x.getMessage());
					}
				}
			} else {
				if (policyId!=null) {
					log(command,String.format("Searching for PolicyID: %s",policyId));
					try {
						int code = policy.listPolicy(ssotoken, policyId, handler);
						log(command,String.format("HTTP result code: %d",code));
						if (code ==401) log(command,"Error: Only the policy creator can retrieve its content.");
					} catch (Exception x) {
						log(command,x.getMessage());
					}
				} else {
					log(command,"Retrieving all policies for the current user");
					policy.listPolicies(ssotoken,handler);
				}			
			}
			break;
		}
		case delete: {
			IPolicyHandler deleteHandler = new IPolicyHandler() {
				@Override
				public void handleOwner(String owner) throws Exception {
					if (owner !=null)
					log(command,String.format("Owner: %s",owner));
					
				}
				@Override
				public void handlePolicy(String policyID) throws Exception {
					log(command,String.format("Deleting PolicyID: %s",policyID));
					 try {
						 int code = policy.deletePolicy(ssotoken, policyID);
							if (code == 200)
								log(command,String.format("Deleted PolicyID: %s",policyID));
							else
								log(command,String.format("HTTP result code: %d",code));							 
					 } catch (Exception x) {
						log(command,String.format("ERROR: %s",x.getMessage()));
					 }
				}
				@Override
				public void handlePolicy(String policyID, String content)
						throws Exception {
				}
			};
			
			int code = 0;
			if (policyId!=null) {
				log(command,"Deleting single policy");
				deleteHandler.handlePolicy(policyId);
			} else if (uri!=null) {
				log(command,String.format("Deleting all policies for the URI %s",uri));
				OpenToxUser owner = new OpenToxUser();
				policy.getURIOwner(ssotoken, uri, owner, deleteHandler);
			} else {	
				log(command,String.format("Deleting all policies per user %s",user.getUsername()));
				policy.listPolicies(ssotoken, deleteHandler);
		
			}
			return 0;
		} 
		default : throw new Exception(String.format("%s not supported",command));
		}
		return 0;
	}
	
	public void setOption(_option option, String argument) throws Exception {
		if (argument!=null) argument = argument.trim();
		switch (option) {
		case authn: {
			if ((argument==null) || "".equals(argument.trim())) return;
			if ((argument==null) || !argument.startsWith("http")) 
				throw new IllegalArgumentException("Not a valid HTTP URI "+argument);
			this.authService = argument;
		}
		case authz: {
			if ((argument==null) || "".equals(argument.trim())) return;
			if ((argument==null) || !argument.startsWith("http")) 
				throw new IllegalArgumentException("Not a valid HTTP URI "+argument);
			this.policyService = argument;
		}
		case user: {
			if ((argument==null) || "".equals(argument.trim())) throw new IllegalArgumentException("Empty user name!");
			user.setUserName(argument);
			break;
		}
		case password: {
			user.setPassword(argument);
			break;
		}
		case subjectid: {
			if ((argument==null) || "".equals(argument.trim())) throw new IllegalArgumentException("Empty token!");
			this.ssotoken = new OpenSSOToken(authService);
			this.ssotoken.setToken(argument);
			break;
		}
		case policyid: {
			//if ((argument==null) || "".equals(argument.trim())) throw new IllegalArgumentException("Empty policy identifier!");
			this.policyId = argument;
			break;
		}		
		case uri: {
			if ((argument==null) || !argument.startsWith("http")) 
				throw new IllegalArgumentException("Not a valid HTTP URI "+argument);
			this.uri = argument;
			break;
		}			
		case command: {
			this.command = policy_command.valueOf(argument);
			break;
		}			
		default: 
		}
	}
	public static void main(String[] args) {
    	Options options = createOptions();
    	
    	aacli cli = new aacli();
    	CommandLineParser parser = new PosixParser();
		try {
		    CommandLine line = parser.parse( options, args,false );
		    if (line.hasOption(_option.help.name())) {
		    	printHelp(options, null);
		    	return;
		    }
		    	
	    	for (_option o: _option.values()) 
	    		if (line.hasOption(o.getShortName())) try {
	    			cli.setOption(o,line.getOptionValue(o.getShortName()));
	    		} catch (Exception x) {
	    			printHelp(options,x.getMessage());
	    			return;
	    		}
	    	
	    	cli.login();	
	    	cli.run();	
	    		
/*
		    
		    File file = new File(input);
		    if (file.exists()) {
		    	OpenToxAAcli cropper = new OpenToxAAcli();
		    	if (file.isDirectory()) {
		    		cropper.crop(file.listFiles());
		    	} else cropper.crop(file);
		    } else {
		    	printHelp(options,String.format("File not found %s", input));
		    }
		
		    if (line.hasOption("h") || (input==null)) {
		    	printHelp(options,null);
		    }		
		       */    
		} catch (Exception x ) {
			printHelp(options,x.getMessage());
		} finally {
			try { 
				cli.logout(); 
			} catch (Exception xx) {
				printHelp(options,xx.getMessage());
			}
		}
	}
	
	enum _option {

		authn {
			@Override
			public String getArgName() {
				return "URI";
			}
			@Override
			public String getDescription() {
				return "URI of OpenSSO/OpenAM service";
			}
			@Override
			public String getShortName() {
				return "n";
			}
			@Override
			public String getDefaultValue() {
				return AAServicesConfig.getSingleton().getConfig(CONFIG.opensso);
			}
		},
		authz {
			@Override
			public String getArgName() {
				return "URI";
			}
			@Override
			public String getDescription() {
				return "URI of OpenTox policy service";
			}
			@Override
			public String getShortName() {
				return "z";
			}
			@Override
			public String getDefaultValue() {
				return AAServicesConfig.getSingleton().getConfig(CONFIG.policy);
			}
		},		
		user {
			@Override
			public String getArgName() {
				return "username";
			}
			@Override
			public String getDescription() {
				return "OpenTox user name";
			}
			@Override
			public String getShortName() {
				return "u";
			}
		},
		password {
			@Override
			public String getArgName() {
				return "password";
			}
			@Override
			public String getDescription() {
				return "OpenTox user password";
			}
			@Override
			public String getShortName() {
				return "p";
			}
		},		
		subjectid {
			@Override
			public String getArgName() {
				return "token";
			}
			@Override
			public String getDescription() {
				return "OpenSSO/OpenAM token. If the token is present, user and password are ignored.";
			}
			@Override
			public String getShortName() {
				return "s";
			}
		},	
		policyid {
			@Override
			public String getArgName() {
				return "policyid";
			}
			@Override
			public String getDescription() {
				return "An OpenSSO/OpenAM policy identifier";
			}
			@Override
			public String getShortName() {
				return "i";
			}
			
		},		
		uri {
			@Override
			public String getArgName() {
				return "URI";
			}
			@Override
			public String getDescription() {
				return "URI of an OpenTox resource";
			}
			@Override
			public String getShortName() {
				return "r";
			}
	
		},			
		command {
			@Override
			public String getArgName() {
				return "the command";
			}
			@Override
			public String getDescription() {
				StringBuilder b = new StringBuilder();
				b.append("The command to be performed. One of ");
				String d = "";
				for (policy_command pc : policy_command.values()) {
					b.append(d);
					b.append(pc);
					d = "|";
				}
				return b.toString();
			}
			@Override
			public String getShortName() {
				return "c";
			}
			@Override
			public String getDefaultValue() {
				return policy_command.authorize.name();
			}
		},			
		help {
			@Override
			public String getArgName() {
				return null;
			}
			@Override
			public String getDescription() {
				return "OpenTox Authentication and Authorization client";
			}
			@Override
			public String getShortName() {
				return "h";
			}
			@Override
			public String getDefaultValue() {
				return null;
			}
		}				
		;
		public abstract String getArgName();
		public abstract String getDescription();
		public abstract String getShortName();
		public String getDefaultValue() { return null; }
			
		public Option createOption() {
			String defaultValue = getDefaultValue();
	    	Option option   = OptionBuilder.withLongOpt(name())
	        .hasArg()
	        .withArgName(getArgName())
	        .withDescription(String.format("%s %s %s",getDescription(),defaultValue==null?"":"Default value: ",defaultValue==null?"":defaultValue))
	        .create(getShortName());
	    	return option;
		}
	}
	protected static Options createOptions() {
    	Options options = new Options();
    	for (_option o: _option.values()) {
    		options.addOption(o.createOption());
    	}

    	
    	return options;
	}	
	
	protected static String exampleRetrievePoliciesPerURI() {
		return
		"Retrieve all policies per URI:\n"+
		"\tjava -jar aacli\n"+
		"\t-n http://opensso.in-silico.ch/opensso/identity\n"+
		"\t-z http://opensso.in-silico.ch/Pol/opensso-pol\n"+
		"\t-u guest\n"+
		"\t-p guest\n"+
		"\t-r http://blabla.uni-plovdiv.bg:8080/ambit2/dataset/999\n"+
		"\t-c list";

	}	
	
	protected static String exampleRetrievePolicyContent() {
		return
		"Retrieve policy content by policy id\n"+
		"\tjava -jar aacli\n"+
		"\t-n http://opensso.in-silico.ch/opensso/identity\n"+
		"\t-z http://opensso.in-silico.ch/Pol/opensso-pol\n"+
		"\t-u guest\n"+
		"\t-p guest\n"+
		"\t-i member_rohttpsambit.uni-plovdiv.bg8443ambit2dataset1\n"+
		"\t-c list";

	}
	
	protected static String exampleDeletePolicyURI() {
		return
		"Delete all policies per URI:\n"+
		"\tjava -jar aacli\n"+
		"\t-u guest\n"+
		"\t-p guest\n"+
		"\t-r http://blabla.uni-plovdiv.bg:8080/ambit2/dataset/999\n"+
		"\t-c delete";

	}
	
	protected static String exampleDeletePolicyUser() {
		return
		"Delete all policies per user:\n"+
		"\tjava -jar aacli\n"+
		"\t-u guest\n"+
		"\t-p guest\n"+
		"\t-c delete";

	}
	
	protected static String exampleDeletePolicy() {
		return
		"Delete policy by policy id:\n"+
		"\tjava -jar aacli\n"+
		"\t-u guest\n"+
		"\t-p guest\n"+
		"\t-i guest_c35ceda9-e548-47d6-a377-ac2cae708100\n"+
		"\t-c delete";

	}	
	
	protected static String exampleAuth() {
		return
		"Verify authorization:\n"+
		"\tjava -jar aacli\n"+
		"\t-n http://opensso.in-silico.ch/opensso/identity\n"+
		"\t-z http://opensso.in-silico.ch/Pol/opensso-pol\n"+			
		"\t-u guest\n"+
		"\t-p guest\n"+
		"\t-r https://ambit.uni-plovdiv.bg:8443/ambit2/dataset/1\n"+
		"\t-c authorize";
	}
	protected static void printHelp(Options options,String message) {
		if (message!=null) System.out.println(message);
		System.out.println("An OpenTox Authentication and Authorization client.");
	    HelpFormatter formatter = new HelpFormatter();
	    formatter.printHelp( aacli.class.getName(), options );
	    System.out.println("Examples:");
	    System.out.println(exampleAuth());
	    System.out.println(exampleRetrievePoliciesPerURI());
	    System.out.println(exampleRetrievePolicyContent());
	    System.out.println(exampleDeletePolicy());
	    System.out.println(exampleDeletePolicyURI());
	    System.out.println(exampleDeletePolicyUser());
	    Runtime.getRuntime().runFinalization();						 
		Runtime.getRuntime().exit(0);	
	}
}
