package ambit2.dbcli;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;

import net.idea.modbcum.i.processors.IProcessor;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.node.ObjectNode;
import org.restlet.data.Reference;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.dbcli.exceptions.InvalidCommand;
import ambit2.descriptors.processors.BitSetGenerator.FPTable;
import ambit2.rendering.StructureEditorProcessor._commands;

public class CliOptions {
    public enum _commandmode {
	imports {
	    @Override
	    public String toString() {
		return "import";
	    }
	},
	preprocessing, dataset, split;
	public String toString() {
	    return name();
	};
    };

    public enum _subcommandmode {
	dataset, post, put, get, delete, params, help, json, html
    };

    public enum _fields {
	name, connection, params, sql, order, value, processor
    };

    public enum _preprocessingoptions {
	inchi {
	    @Override
	    public String getDescription() {
		return "Generate InChI (enables exact search)";
	    }
	},
	atomprops {
	    @Override
	    public String getDescription() {
		return "Precalculated aromaticity and ring properties. Speeds up search";
	    }
	},
	fp1024 {
	    @Override
	    public String getDescription() {
		return "Generates hashed fingerprints (necessary to enables similarity and substructure search)";
	    }
	},
	sk1024 {
	    @Override
	    public String getDescription() {
		return "Generates structure keys (necessary to enables substructure search)";
	    }
	},
	smarts {
	    @Override
	    public String getDescription() {
		return "Enables substructure search by running smarts_accelerator & fp1024 & sk1024 preprocessing";
	    }

	    public FPTable[] getOption() throws Exception {
		return new FPTable[] { FPTable.smarts_accelerator, FPTable.fp1024, FPTable.sk1024 };
	    }
	},
	similarity {
	    @Override
	    public String getDescription() {
		return "Enables similarity search by running smarts_accelerator & fp1024 preprocessing";
	    }

	    public FPTable[] getOption() throws Exception {
		return new FPTable[] { FPTable.smarts_accelerator, FPTable.fp1024 };
	    }
	};
	public FPTable[] getOption() throws Exception {
	    return new FPTable[] { FPTable.valueOf(name()) };
	}

	@Override
	public String toString() {
	    return ":" + name();
	}

	public String getDescription() {
	    return name();
	}
    }

    protected String apiConfig = "config/api.json";
    protected String cmd;

    public String getCmd() {
	return cmd;
    }

    long connectionLifeTime = 3600000; // 1h
    private ObjectMapper mapper;
    protected ObjectNode api;
    protected String delimiter;
    protected boolean ignorecert = false;
    protected File configFile;

    public File getConfigFile() {
	return configFile;
    }

    protected String input;
    protected String output;
    protected String media = "csv";
    protected ObjectNode command;
    protected _subcommandmode subcommand = _subcommandmode.dataset;

    public _subcommandmode getSubcommand() {
	return subcommand;
    }

    public CliOptions() {
	mapper = new ObjectMapper();
    }

    protected void parseCommands() throws Exception {
	InputStream in = getClass().getClassLoader().getResourceAsStream(apiConfig);
	if (in == null)
	    throw new IOException("Error loading " + apiConfig);
	try {
	    api = (ObjectNode) mapper.readTree(in);
	} catch (Exception x) {
	    throw x;
	} finally {
	    try {
		in.close();
	    } catch (Exception x) {
	    }
	}

    }

    public boolean parse(String[] args) throws Exception {
	final Options options = createOptions();
	CommandLineParser parser = new PosixParser();
	try {
	    CommandLine line = parser.parse(options, args, false);
	    input = getInput(line);
	    output = getOutput(line);
	    media = getMedia(line);
	    delimiter = getDelimiter(line);
	    connectionLifeTime = getConnectionLifeTime(line);
	    setConfig(getConfig(line));
	    if (line.hasOption("h")) {
		printHelp(options, null);
		return false;
	    } else {
		// to cope with OPS expired certificate ...
		ignorecert = getIgnoreCert(line);
		parseCommands();
		String cmd = getCommand(line);
		setCommand(cmd);
		try {
		    String s = getSubcommand(line);
		    if (s == null)
			subcommand = _subcommandmode.help;
		    else
			subcommand = _subcommandmode.valueOf(s);
		    switch (subcommand) {
		    case json: {
			if ("help".equals(cmd)) {
			    Writer writer = new OutputStreamWriter(System.out);
			    printCommand(cmd, api, true, new OutputStreamWriter(System.out));
			    writer.flush();
			} else {
			    Writer writer = new OutputStreamWriter(System.out);
			    printCommand(cmd, command, true, new OutputStreamWriter(System.out));
			    writer.flush();
			}
			return false;
		    }
		    case html: {

			Writer writer = new OutputStreamWriter(System.out);
			writer.write("<html>");
			writer.write("<head>");
			writer.write("<title>AMBIT log</title>");
			writer.write("<style type=\"text/css\">");
			writer.write("body {FONT-FAMILY: Trebuchet MS, Verdana, Arial;background:#fff;color:#000000 }\n");
			writer.write("h1, h2, h3 {color: #344049;FONT-FAMILY: Trebuchet MS, Verdana, Arial;font-weight: bold; }\n");
			writer.write("h4, h5, h6 {color: #344049;	FONT-FAMILY: Trebuchet MS, Verdana, Arial;font-weight: normal; }\n");
			writer.write("</style>");
			writer.write("</head>");
			writer.write("<body>");
			ObjectNode commands = (ObjectNode) api.get("commands");
			writer.write("<a id='commands'>&nbsp;</a>");
			Iterator<String> i = commands.getFieldNames();
			while (i.hasNext()) {
			    String key = i.next();
			    writer.write("<a href='#" + key + "'>" + key + "</a> |");
			}
			writer.write("<br/>");
			if ("help".equals(cmd)) {
			    i = commands.getFieldNames();
			    while (i.hasNext()) {
				String key = i.next();
				printCommand2html(key, (ObjectNode) commands.get(key), false, writer, true);
			    }
			    writer.write("</body>");
			    writer.write("</html>");
			    writer.flush();
			} else {
			    printCommand2html(cmd, (ObjectNode) commands.get(cmd), false, writer, true);
			    writer.write("</body>");
			    writer.write("</html>");
			    writer.flush();
			}
			return false;
		    }
		    case help: {
			if ("help".equals(cmd)) {
			    Writer writer = new OutputStreamWriter(System.out);
			    ObjectNode commands = (ObjectNode) api.get("commands");
			    Iterator<String> i = commands.getFieldNames();
			    writer.write("API commands (use -m help to list subcommands)\n");
			    while (i.hasNext()) {
				String key = i.next();
				writer.write(commands.get(key).get("name").getTextValue());
				writer.write("\t");
				writer.write(commands.get(key).get("description").getTextValue());
				writer.write("\n");
				for (_subcommandmode subcommand : _subcommandmode.values()) {
				    JsonNode scmd = commands.get(key).get(subcommand.name());
				    if (scmd == null)
					continue;
				    ObjectNode params = (ObjectNode) scmd.get("params");
				    StringBuilder prm = new StringBuilder();
				    StringBuilder prmrest = new StringBuilder();

				    prm.append(" -a " + key + " -m " + subcommand.name());
				    prmrest.append("/api/" + key + "/" + subcommand.name());
				    if (params != null) {
					Iterator<String> n = params.getFieldNames();
					String d = "?";
					while (n.hasNext()) {
					    String param = n.next();
					    prm.append(" -d " + param.replace(":", "") + "="
						    + params.get(param).get("value").asText() + "");
					    prmrest.append(d + param.replace(":", "") + "="
						    + Reference.encode(params.get(param).get("value").asText()));
					    d = "&";
					}
					writer.write(prm.toString());
					writer.write("\t");
					writer.write(prmrest.toString());
					writer.write("\t");
					/*
					switch (subcommand) {
					}
					*/
					writer.write("\n");
				    }

				    // //
				}
			    }
			    writer.flush();
			} else {
			    Writer writer = new OutputStreamWriter(System.out);
			    printCommand(cmd, command, false, writer);
			    writer.flush();
			}
			return false;
		    }
		    case params: {
			System.out.println(printParameters(cmd, _subcommandmode.dataset));
			printHelp(options, null);
			return false;
		    }
		    default: {
			setParams(getData(line, options));
		    }
		    }
		} catch (Exception x) {
		    x.printStackTrace();
		}
		return true;
	    }
	} catch (Exception x) {
	    printHelp(options, x.getMessage());
	    throw x;
	} finally {

	}
    }

    protected String printParameters(String cmd, _subcommandmode mode) {
	StringBuilder b = new StringBuilder();
	b.append(cmd);
	b.append("/");
	b.append(mode.name());
	b.append("\nParameters:\t");
	b.append(command.get(mode.name()).get(_fields.params.name()));
	return b.toString();
    }

    protected static String[] getData(CommandLine line, Options options) {
	Option opt = options.getOption("data");
	System.err.println(opt);
	if (line.hasOption('d')) {
	    return line.getOptionValues('d');
	} else
	    return null;
    }

    protected static long getConnectionLifeTime(CommandLine line) {
	if (line.hasOption('r'))
	    return Long.parseLong(line.getOptionValue('r').trim());
	else
	    return 60 * 60 * 1000;
    }

    protected static String getDelimiter(CommandLine line) {
	if (line.hasOption('l'))
	    return line.getOptionValue('l').trim();
	else
	    return "|";
    }

    protected static boolean getIgnoreCert(CommandLine line) {
	if (line.hasOption('k'))
	    return true;
	else
	    return false;
    }

    protected static String getSubcommand(CommandLine line) {
	if (line.hasOption('m'))
	    return line.getOptionValue('m').trim();
	else
	    return null;
    }

    protected static String getCommand(CommandLine line) {
	if (line.hasOption('a'))
	    return line.getOptionValue('a').trim();
	else
	    return null;
    }

    protected static String getMedia(CommandLine line) {
	if (line.hasOption('f'))
	    return line.getOptionValue('f');
	else
	    return "csv";
    }

    protected static String getOutput(CommandLine line) {
	if (line.hasOption('o'))
	    return line.getOptionValue('o');
	else
	    return null;
    }

    protected static String getInput(CommandLine line) {
	if (line.hasOption('i'))
	    return line.getOptionValue('i');
	else
	    return null;
    }

    protected static String getConfig(CommandLine line) {
	if (line.hasOption('c'))
	    return line.getOptionValue('c');
	else
	    return null;
    }

    protected void setConfig(String config) {
	if (config != null) {
	    this.configFile = new File(config);
	    if (!this.configFile.exists())
		config = null;
	}
	if (config == null) {
	    this.configFile = null;
	    System.err.println("Config file not specified or do not exist, using default "
		    + getClass().getClassLoader().getResource("config/ambit.properties"));
	}
    }

    public ObjectNode getCommand() {
	return command;
    }

    public void setParams(String[] params) throws Exception {
	if (command == null)
	    throw new InvalidCommand("(Not specified)");
	if (params == null)
	    return;
	try {
	    for (int i = 0; i < params.length; i += 2)
		try {
		    ObjectNode param = (ObjectNode) command.get(subcommand.name()).get(_fields.params.name())
			    .get(":" + params[i]);
		    if (param != null) {
			if ("Integer".equals(param.get("type").getTextValue()))
			    param.put(_fields.value.name(), Integer.parseInt(params[i + 1]));
			else if ("Boolean".equals(param.get("type").getTextValue()))
			    param.put(_fields.value.name(), Boolean.parseBoolean(params[i + 1]));
			else
			    param.put(_fields.value.name(), params[i + 1]);
		    }
		} catch (Exception x) {
		    // ignore
		}

	} catch (Exception x) {
	    throw x;
	}
    }

    public ObjectNode getSQLParams() throws Exception {
	if (command == null)
	    throw new InvalidCommand("(Not specified)");
	try {
	    return (ObjectNode) command.get(subcommand.name()).get(_fields.params.name());
	} catch (Exception x) {
	    throw x;
	}
    }

    public String getSQLResource() throws Exception {
	if (command == null)
	    throw new InvalidCommand("(Not specified)");
	try {
	    return command.get(subcommand.name()).get(_fields.sql.name()).getTextValue();
	} catch (Exception x) {
	    throw x;
	}
    }

    public String getSQLConfig() throws Exception {
	if (command == null)
	    throw new InvalidCommand("(Not specified)");
	try {
	    return command.get(_fields.connection.name()).getTextValue();
	} catch (Exception x) {
	    throw x;
	}
    }

    public void setCommand(String command) throws Exception {
	this.cmd = command;
	JsonNode node = api.get("commands").get(command);
	if (node == null)
	    throw new InvalidCommand(command == null ? "(Not specified)" : command);
	else if (node instanceof ObjectNode) {
	    this.command = (ObjectNode) node;
	} else
	    throw new InvalidCommand(command);
    }

    protected Options createOptions() {
	StringBuilder b = new StringBuilder();
	b.append("Commands: ");
	for (_commandmode cm : _commandmode.values()) {
	    b.append(cm.toString());
	    b.append("|");
	}

	Options options = new Options();
	Option command = OptionBuilder.withLongOpt("command").hasArg().withArgName("command")
		.withDescription(b.toString()).create("a");

	b = new StringBuilder();
	b.append("Subcommands: ");
	for (_subcommandmode cm : _subcommandmode.values()) {
	    b.append(cm);
	    b.append("|");
	}

	Option subcommand = OptionBuilder.withLongOpt("subcommand").hasArg().withArgName("subcommand")
		.withDescription(b.toString()).create("m");

	Option input = OptionBuilder.hasArg().withLongOpt("input").withArgName("file")
		.withDescription("Input SDF file").create("i");

	Option output = OptionBuilder.hasArg().withLongOpt("output").withArgName("file").withDescription("Output file")
		.create("o");

	b = new StringBuilder();
	b.append("Command parameters\t");
	b.append("preprocessing options\t");
	for (_preprocessingoptions p : _preprocessingoptions.values()) {
	    b.append(p.toString().replace(":", " "));
	    b.append("=");
	    b.append("true|false");
	}
	Option data = OptionBuilder.hasArgs().withValueSeparator('=').withLongOpt("data").withArgName("data")
		.withDescription(b.toString())
		// "Command parameters, e.g. -d \"chunk=1000000\" \"-d inchi=false\" \"-d fp1024=true\" \"-d pagesize=5000000\"")
		.create("d");

	Option config = OptionBuilder.hasArg().withLongOpt("config").withArgName("file")
		.withDescription("Config file (DB connection parameters)").create("c");

	Option restartConnection = OptionBuilder.hasArg().withLongOpt("restartConnection").withArgName("msec")
		.withDescription("Restart SQL connection every ? msec (default 1h= 3600000 msec)").create("r");

	Option help = OptionBuilder.withLongOpt("help").withDescription("This help").create("h");

	options.addOption(command);
	options.addOption(subcommand);
	options.addOption(input);
	options.addOption(output);
	options.addOption(data);
	options.addOption(restartConnection);
	options.addOption(config);
	options.addOption(help);

	return options;
    }

    protected static void printHelp(Options options, String message) {
	if (message != null)
	    System.out.println(message);

	HelpFormatter formatter = new HelpFormatter();
	formatter.printHelp(AmbitCli.class.getName(), options);
	Runtime.getRuntime().runFinalization();
	Runtime.getRuntime().exit(0);
    }

    protected void printCommand(String cmd, ObjectNode cmdDef, boolean json, Writer out) throws IOException,
	    JsonMappingException, JsonGenerationException {
	// ObjectWriter writer = mapper.defaultPrettyPrintingWriter();
	// ***IMPORTANT!!!*** for Jackson 2.x use the line below instead of the
	// one above:
	if (json) {
	    ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
	    System.out.println(writer.writeValueAsString(cmdDef));
	    System.out.println(writer.writeValueAsString(cmdDef));
	} else {
	    out.write("====\n");
	    out.write("Query\t");
	    out.write(cmdDef.get("name").asText());
	    out.write("\nDescription\t");
	    out.write(cmdDef.get("description").asText());
	    out.write("\n");

	    for (_subcommandmode subcommand : _subcommandmode.values()) {
		JsonNode scmd = cmdDef.get(subcommand.name());
		if (scmd == null)
		    continue;

		ObjectNode params = (ObjectNode) scmd.get("params");
		// System.out.println(cmdDef.get("connection").asText());
		out.write("API\t");
		out.write(cmd + "/" + subcommand);
		if (scmd.get("description") != null) {
		    out.write("\n\tDescription\n\t\t");
		    out.write(scmd.get("description").asText());
		    out.write("\n");
		}
		out.write("\n\tCommand line\n");

		StringBuilder example = new StringBuilder();
		example.append("\t\tjava -jar ambit2cli.jar -a " + cmd + " -m " + subcommand.name());
		out.write(example.toString());

		if (params != null) {
		    Iterator<String> i = params.getFieldNames();
		    while (i.hasNext()) {
			String key = i.next();
			out.write(" -d " + key.replace(":", "") + "={" + params.get(key).get("type").asText() + "}");
			example.append(" -d " + key.replace(":", "") + "=" + params.get(key).get("value").asText() + "");
		    }
		    out.write("\n\tExample\n");
		    out.write(example.toString());
		}

		out.write("\n\n");
		out.write("\tREST API\n");
		example = new StringBuilder();
		example.append("\t\thttp://localhost:8080/ambit2/api/" + cmd + "/" + subcommand.name());
		out.write(example.toString());

		if (params != null) {
		    String d = "?";
		    Iterator<String> i = params.getFieldNames();
		    while (i.hasNext()) {
			String key = i.next();
			out.write(d + key.replace(":", "") + "={" + params.get(key).get("type").asText() + "}");
			example.append(d + key.replace(":", "") + "="
				+ Reference.encode(params.get(key).get("value").asText()));
			d = "&";
		    }
		    out.write("\n\tExample\n");
		    out.write(example.toString());
		}

		out.write("\n\n\tImplementation details\n");
		if (cmdDef.get("connection") != null) {
		    out.write("\t\tDatabase connection:\t");
		    out.write("\t" + cmdDef.get("connection").asText());
		    out.write("\n");
		}
		if (scmd.get("sql") != null) {
		    // not implemented
		}
		if (scmd.get("processor") != null) {
		    out.write("\tFor every record run:");
		    out.write("\t" + scmd.get("processor").asText());
		}
		out.write("\n\n");
	    }

	}
    }

    protected void printCommand2html(String cmd, ObjectNode cmdDef, boolean json, Writer out, boolean runExample)
	    throws IOException, JsonMappingException, JsonGenerationException {
	// ObjectWriter writer = mapper.defaultPrettyPrintingWriter();
	// ***IMPORTANT!!!*** for Jackson 2.x use the line below instead of the
	// one above:
	if (json) {
	    ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
	    System.out.println(writer.writeValueAsString(cmdDef));
	    System.out.println(writer.writeValueAsString(cmdDef));
	} else {
	    out.write("\n<hr><h2><a id='" + cmd + "'>Query&nbsp;");
	    out.write(cmdDef.get("name").asText() + "  (" + cmd + ")");
	    out.write("</a></h2>");
	    out.write("\n<h4>");
	    out.write(cmdDef.get("description").asText());
	    out.write("</h4>\n");

	    out.write("\n<div style='margin: 10 20 10 20;padding: 0 0 0 0;'>\n");
	    out.write(cmd + " API:&nbsp;");
	    for (_subcommandmode subcommand : _subcommandmode.values()) {
		JsonNode scmd = cmdDef.get(subcommand.name());
		if (scmd == null)
		    continue;
		out.write("\n<a href='#" + cmd + "_" + subcommand + "'>/api/" + cmd + "/" + subcommand + "</a> |");
	    }
	    for (_subcommandmode subcommand : _subcommandmode.values()) {
		JsonNode scmd = cmdDef.get(subcommand.name());
		if (scmd == null)
		    continue;
		out.write("\n<div style='margin: 5 50px 5 50px;'>\n");
		ObjectNode params = (ObjectNode) scmd.get("params");
		//
		out.write("\n<h3><a id='" + cmd + "_" + subcommand + "'>/api/");
		out.write(cmd + "/" + subcommand);
		out.write("</a></h3>");
		if (scmd.get("description") != null) {
		    out.write("<label>");
		    ;
		    out.write(scmd.get("description").asText());
		    out.write("</label>");
		}
		out.write("\n<div style='margin: 5 50px 5 50px;'>\n");
		out.write("\n<label>Command line</label>");

		StringBuilder example = new StringBuilder();
		example.append("java -jar ambit2cli.jar -a " + cmd + " -m " + subcommand.name());
		out.write("<pre>");
		out.write(example.toString());

		if (params != null) {
		    Iterator<String> i = params.getFieldNames();
		    while (i.hasNext()) {
			String key = i.next();
			out.write(" -d " + key.replace(":", "") + "={" + params.get(key).get("type").asText() + "}");
			example.append(" -d " + key.replace(":", "") + "=" + params.get(key).get("value").asText() + "");
		    }
		    out.write("</pre>");

		    out.write("\n<label>Example</label>");
		    out.write("\n<pre>");
		    out.write(example.toString());
		    out.write("\n</pre>");
		} else
		    out.write("\n</pre>");

		out.write("<label>REST API</label>");
		example = new StringBuilder();
		example.append("http://localhost:8080/ambit2/api/" + cmd + "/" + subcommand.name());
		out.write("\n<pre>");
		out.write(example.toString());

		if (params != null) {
		    String d = "?";
		    Iterator<String> i = params.getFieldNames();
		    while (i.hasNext()) {
			String key = i.next();
			out.write(d + key.replace(":", "") + "={" + params.get(key).get("type").asText() + "}");
			example.append(d + key.replace(":", "") + "="
				+ Reference.encode(params.get(key).get("value").asText()));
			d = "&";
		    }
		    out.write("\n</pre>");

		    out.write("\n<label>Example</label><br/>");
		    out.write("<pre>");
		    out.write(example.toString());
		    out.write("</pre>");
		} else
		    out.write("</pre>");

		if (runExample) {
		    out.write("\n<label>Example " + media.toUpperCase() + " output</label>\n");
		    JsonNode node = scmd.get("runexample");
		    long timeelapsed = 0;
		    if (node != null && node.asBoolean()) {
			out.write("<div class='example' style='width: 99%; height: 300px; border-style:solid; border-width:1px; overflow-x: auto; overflow-y: scroll;'>");
			out.write("<pre>");
			out.flush();
			try {
			    CliOptions options = new CliOptions();
			    String[] args = { "-a", cmd, "-m", subcommand.name(), "-l", ",", "-f", media };
			    options.parse(args);
			    AmbitCli cli = new AmbitCli(options);
			    timeelapsed = cli.go(cmd, subcommand.name());
			} catch (Exception x) {
			    x.printStackTrace();
			} finally {
			    out.flush();
			    out.write("</pre>");
			    out.write("</div>");
			    out.write("\n<h6>Retrieved in <a id='time_" + cmd + "_" + subcommand + "'>" + timeelapsed
				    + "</a> msec</h6>");
			}
		    }
		}

		out.write("\n<h4>Implementation details</h4>\n<ul>");
		if (cmdDef.get("connection") != null) {
		    out.write("\n<li>Database connection:&nbsp;");
		    out.write(cmdDef.get("connection").asText());
		    out.write("</li>");
		}
		if (scmd.get("sql") != null) {
		    // not implemented

		}
		if (scmd.get("processor") != null) {
		    out.write("\n<li>For every record run:");
		    out.write(scmd.get("processor").asText());
		    out.write("\n</li>");
		}
		out.write("\n</ul>");
		out.write("\n</div>\n");

		out.write("\n<a href='#commands'>Top</a> | ");
		out.write("\n<a href='#" + cmd + "' title='" + cmdDef.get("name").asText() + "'>" + cmd + "</a>");
		out.write("\n</div>\n");
	    }
	    out.write("\n</div>");

	}
    }

    public IProcessor<IStructureRecord, IStructureRecord> getProcessor() throws Exception {
	if (command == null)
	    return null;
	try {
	    JsonNode sc = command.get(subcommand.name());
	    JsonNode proc = sc.get(_fields.processor.name());
	    if (proc == null)
		return null;
	    String className = proc.asText();
	    Class clazz = Class.forName(className);
	    IProcessor<IStructureRecord, IStructureRecord> p = (IProcessor<IStructureRecord, IStructureRecord>) clazz
		    .newInstance();

	    return p;
	} catch (Exception x) {
	    throw x;
	}
    }

    public Object getParam(String name) {
	try {
	    JsonNode scmd = command.get(subcommand.name());
	    JsonNode scommand = scmd.get("params");
	    JsonNode chunkNode = scommand.get(name);
	    if ("Boolean".equals(chunkNode.get("type").getTextValue()))
		return chunkNode.get("value").getBooleanValue();
	    else if ("Integer".equals(chunkNode.get("type").getTextValue()))
		return chunkNode.get("value").getIntValue();
	    else
		return chunkNode.get("value").getTextValue();
	} catch (Exception x) {
	    return null;
	}
    }
}
