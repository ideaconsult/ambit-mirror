package ambit2.ml.io.app;

import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import ambit2.ml.io.IMLOutput;
import ambit2.ml.io.OutputARFF;
import ambit2.ml.io.OutputVW;
import ambit2.ml.io.OutputVW_LDA;

public class AmbitML {

	public void convert(String folder, String file, Set<out_formats> of, int columnid, int maxrecords)
			throws Exception {
		long now = System.currentTimeMillis();
		if (of.isEmpty())
			of.add(out_formats.vw);
		FileReader reader = new FileReader(new File(folder, file));

		CSVFormat format = CSVFormat.DEFAULT.withDelimiter(',').withAllowMissingColumnNames();
		CSVParser parser = null;

		IMLOutput[] writers = new IMLOutput[] { new OutputARFF(folder, file, of.contains(out_formats.arff)),
				new OutputVW(folder, file, of.contains(out_formats.vw)),
				new OutputVW_LDA(folder, file, of.contains(out_formats.vw_lda)) };
		// writers
		for (IMLOutput writer : writers) {
			writer.open();
		}

		try {
			parser = format.parse(reader);
			Iterator<CSVRecord> i = parser.iterator();

			while (i.hasNext()) {
				CSVRecord record = i.next();
				if ((record.getRecordNumber() % 100000) == 0)
					System.out.println(record.getRecordNumber());

				for (IMLOutput writer : writers) {
					writer.print(record, columnid);
				}
				if ((maxrecords > 0) && record.getRecordNumber() > maxrecords)
					break;
			}

		} finally {
			if (parser != null)
				parser.close();
			for (IMLOutput writer : writers)
				try {
					writer.close();
				} catch (Exception xx) {
				}
			System.out.print("Completed in ");
			System.out.print(System.currentTimeMillis() - now);
			System.out.println("msec.");
		}
	}

	protected static void printHelp(Options options, String message) {
		if (message != null)
			System.out.println(message);

		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(AmbitML.class.getName(), options);
		// subcommand2Option
		Runtime.getRuntime().runFinalization();
		Runtime.getRuntime().exit(0);
	}

	public enum out_formats {
		arff, vw, vw_lda
	};

	public static void main(String[] args) {
		Options options = new Options();
		Option command = Option.builder("f").hasArgs().longOpt("folder").desc("Input file folder").hasArgs()
				.argName("foldername").build();
		options.addOption(command);
		command = Option.builder("i").hasArgs().longOpt("inputfile").desc("Input file name").argName("filename")
				.required().build();
		options.addOption(command);
		command = Option.builder("o").longOpt("output format")
				.desc("Output formats (comma delimited list) : arff,vw,vw_lda").hasArgs().argName("format").build();
		options.addOption(command);
		command = Option.builder("s").longOpt("pagesize").desc("max number of records to read").hasArgs()
				.argName("number").build();
		options.addOption(command);
		command = Option.builder("d").longOpt("identifier")
				.desc("The index of column containing an identifier, zero based. Default 0").hasArgs().argName("column")
				.build();
		options.addOption(command);
		command = Option.builder().argName("h").longOpt("help").build();
		options.addOption(command);
		CommandLineParser parser = new DefaultParser();
		try {
			String file;
			String folder = "";
			Set<out_formats> of = new TreeSet<out_formats>();
			CommandLine line = parser.parse(options, args, false);
			int maxrecords = -1;

			if (args.length == 0 || line.hasOption('h'))
				printHelp(options, "AmbitML");

			if (line.hasOption('s'))
				try {
					maxrecords = Integer.parseInt(line.getOptionValue('s'));
				} catch (Exception x) {
				}
			if (line.hasOption('i')) {
				file = line.getOptionValue('i');
				if (line.hasOption('f'))
					folder = line.getOptionValue('f');
				else {
					File f = new File(file);
					folder = f.getParentFile().getAbsolutePath();
					file = f.getName();
				}
				if (line.hasOption('o')) {
					String[] formats = line.getOptionValue('o').split(",");
					for (String format : formats)
						try {
							of.add(out_formats.valueOf(format));
						} catch (Exception x) {
						}
				}
				int columnid = 0;
				if (line.hasOption('d'))
					try {
						columnid = Integer.parseInt(line.getOptionValue('d'));
					} catch (Exception x) {
					}
				AmbitML cli = new AmbitML();
				cli.convert(folder, file, of, columnid, maxrecords);
			}
		} catch (Exception x) {
			printHelp(options, x.getMessage());
		}
	}
}
