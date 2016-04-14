#ambitcli

A [command line Java application](download_ambitcli.html) used for processing chemical files, [structure standardisation](./ambitcli_standardisation.html), import into AMBIT database and processing AMBIT database entries.  

## Download

* Latest release <a href="https://sourceforge.net/projects/ambit/files/Ambit2/AMBIT%20applications/ambitcli/ambitcli-3.0.2/ambitcli-3.0.2.jar/download">ambitcli-3.0.2</a>

* <a href="http://sourceforge.net/projects/ambit/files/Ambit2/AMBIT%20applications/ambitcli/">All releases</a>

* Pre-release <a href="https://sourceforge.net/projects/ambit/files/Ambit2/AMBIT%20applications/ambitcli/ambitcli-3.0.2">ambitcli-3.0.2-{buildnumber}.jar</a> 

* Pre-release <a href="https://www.ideaconsult.net/downloads/ambitcli/ambitcli-3.0.1-20151130.jar">ambitcli-3.0.1-20151130.jar</a> | <a href="https://www.ideaconsult.net/downloads/ambitcli/ambitcli-3.0.1-20160205.jar">ambitcli-3.0.1-20160205.jar (build:7259 1454688887571)</a>

* Development <a href="http://ambit.uni-plovdiv.bg:8083/nexus/#nexus-search;gav~~ambit2-dbcli~~jar~">Maven repository</a>


## Usage

````sh
$java -jar ambitcli-VERSION.jar -help
INFO   ambitcli-3.0.2 build:7472 1460532616351
http://ambit.sourceforge.net/download_ambitcli.html
usage: ambitcli-{version}
 -a,--command <command>          Commands:
                                 import|preprocessing|dataset|split|standardize|fingerprint|help|
 -c,--config <file>              Config file (DB connection parameters)
 -d,--data <data>                Command specific parameters (multiple).
                                 Use -a cmd -m help to list available
                                 parameters
 -h,--help                       This help
 -i,--input <file>               Input SDF file
 -m,--subcommand <subcommand>    Subcommands. Use -a cmd -m help to list
                                 subcommands of a specific command.
 -o,--output <file>              Output file
 -r,--restartConnection <msec>   Restart SQL connection every ? msec
                                 (default 1h= 3600000 msec)
````

Logging configuration can be specified via  -Djava.util.logging.config.file option, specifying logging.properties file. If not specified, the [default logging.properties](https://svn.code.sf.net/p/ambit/code/trunk/ambit2-all/ambit2-apps/ambit2-dbcli/src/main/resources/ambit2/dbcli/logging.properties) is used.

````sh
$java -jar -Djava.util.logging.config.file=myLoggingConfigFilePath .... other options ....
````

### Supported file formats 

 * SDF filename.sdf <a href="https://en.wikipedia.org/wiki/Chemical_table_file#SDF">Structure Data Format</a>. 
 
 * Gzipped files (*.gz) expect SDF content.

 * Molfile <a href="https://en.wikipedia.org/wiki/Chemical_table_file#Molfile">MOL</a>

 * Chemical Markup Language <a href="https://en.wikipedia.org/wiki/Chemical_Markup_Language">CML</a> 

 * Protein Data Bank <a href="https://en.wikipedia.org/wiki/Protein_Data_Bank_(file_format)">PDB</a>
 
 * CSV filename.csv Comma delimited text file,with mandatory header row. A column,containing SMILES, should have title "SMILES". 
 The number,titles and order of the columns are arbitrary. Example below.
 
````
NAME,CAS,SMILES
Acetic acid,64-19-7,CC(O)=O
Acetoin,513-86-0,CC(O)C(C)=O
````

 * TXT filename.txt Tab delimited text file. A column,containing SMILES, should have title "SMILES".  The number,titles and order of the columns are arbitrary.
 
 * Excel spreadsheets XLS and <a href="https://en.wikipedia.org/wiki/Office_Open_XML">XLSX</a>. A column,containing SMILES, should have title "SMILES".  The number,titles and order of the columns are arbitrary.
 
 * <a href="https://en.wikipedia.org/wiki/XYZ_file_format">XYZ</a>, <a href="http://wiki.jmol.org/index.php/File_formats/Formats/HIN">HIN</a>
 
 * ZIP archives <a href="https://en.wikipedia.org/wiki/Zip_(file_format)">.zip</a>. Expect any of the supported file formats as archive content.
 
 * IUCLID5 <a href="http://iuclid.eu/index.php?fuseaction=home.format">.i5z</a> 
 
## Commands

The application is organized around set of commands (`option -a`) , subcommands (option -m) and command parameters (multiple options -d).
This mimics a REST API with structure `/command -X POST -d "option1=value1" -d "option2=value2"`   

* To list available commands use `-a help`

````sh
$java -jar ambitcli.jar -a help
ambitcli -a {command} -m {subcommand} -d {options}
	(use -m help to list subcommands and options per command)
````

* Split

````sh
-a split	Splits an SDF into chunks of predefined size (-i inputfile -o outputfile).
	Example:	ambitcli  -a split -m post -d chunk=1000	
````

* Standardize 

````sh
-a standardize	Chemical structure standardization (-i inputfile.sdf -o outputfile.sdf , recognized by extensions .sdf , .csv, .cml , .txt)
	Example:	ambitcli  -a standardize -m post -d smirks=null -d splitfragments=true -d implicith=true -d stereo=false -d tautomers=true -d inchi=false -d smiles=false -d smilescanonical=false -d page=0 -d pagesize=20000 -d tag_inchi=InChI -d tag_inchikey=InChIKey -d tag_smiles=SMILES -d tag_rank=RANK	
````

### Example 1:

````
 -d fpclass=CircularFingerprinter,PubchemFingerprinter,MACCSFingerprinter -d page=0 -d pagesize=20000 -d inputtag_smiles=SMILES -d tag_tokeep=InChIKey -d write_count=false -d write_raw=false -d sdftitle=null
````

### Example 2:
````
 -a fingerprint -m post  -i "input.txt" -o "fp/Compound_XYZ_" -d pagesize=-1 -d "fpclass=CircularFingerprinter,PubchemFingerprinter" -d tag_tokeep=AMBIT_InChIKey  -d inputtag_smiles=AMBIT_SMILES -d inputtag_inchikey=AMBIT_InChIKey -d inputtag_inchi=AMBIT_InChI
````

Available since ambitcli-3.0.2-SNAPSHOT build:7349
````

* Import 

````sh
-a dataset	Dataset import into AMBIT database (with normalisation). The database connection settings are read from -c {file}.
	Example:	ambitcli  -a dataset -m post	
````

* Import
 
````sh
-a import	Quick import into AMBIT database (No normalisation!). Input file (-i file). The database connection settings are read from -c {file}
	Example:	ambitcli  -a import -m post	
````

* Database preprocessing 

````sh
-a preprocessing	Preprocessing of structures in AMBIT database (depends on options, default inchi). The database connection settings are read from -c {file}
	Example:	ambitcli  -a preprocessing -m post -d inchi=false -d atomprops=false -d fp1024=false -d sk1024=false -d cf1024=false -d smarts=false -d similarity=false -d pagesize=5000000	
````

* Atom environment

````sh
-a atomenvironments	Generates atom environments matrix descriptors from SDF file (-i inputfile -o outputfile)
	Example:	ambitcli  -a atomenvironments -m post -d id_tag=ID -d activity_tag=Activity -d merge_results_file=null -d generate_csv=false -d generate_mm=false -d generate_json=false -d generate_vw=true -d normalize=true -d laplace_smoothing=null -d cost_sensitive=true -d levels_as_namespace=false -d toxtree=false	
````

````sh
-a help	List all commands
````

### Command specific help

````
$java -jar ambitcli.jar -a {command} help
````

e.g. 

````
$java -jar ambitcli.jar -a standardize -m help
````

## <a name="chemfiles">Processing chemical files</a>

### <a name="standardize"></a>-a standardize 

  [Chemical structure standardisaion options](ambitcli_standardisation.html)

### -a fingerprint 

````
    >java -jar ambitcli-3.0.2.jar -a fingerprint -m help
INFO   ambitcli-3.0.2 build:7472 1460532616351
http://ambit.sourceforge.net/download_ambitcli.html
-a fingerprint -m post -d <parameters>

"Fingerprint calculation. Writes multiple files per fingerprint, all files start with prefix given by -o prefix). Fingerpritns are written in a sparse format"
   -a fingerprint -m post
 -d fpclass=CircularFingerprinter,PubchemFingerprinter,MACCSFingerprinter       // Comma delimited list of class names i mplementing org.openscience.cdk.fingerprint.IFingerprinter, e.g. KlekotaRothFingerprinter. If not fully qualified will prepend 'org.openscience.cdk.fingerprint.' 
 -d page=0      // Start page (first page = 0) 
 -d pagesize=20000      // Page size (in number of records)  
 -d inputtag_smiles=SMILES      // Specifies the name of the column, containing SMILES in the input file
 -d inputtag_inchi=InChI        // Specifies the name of the column, containing InChI in the input file
 -d inputtag_inchikey=InChIKey  // Specifies the name of the column, containing InChIKey in the input file
 -d tag_tokeep=InChIKey // Specifies which tags to keep, comma delimited list. Everything else will be removed. To keep all the tags, leave this empty.
 -d write_count=false   // Whether to write the counts of getCountFingerprint() (in [.vw](http://hunch.net/~vw/) format)
 -d write_raw=false     // Whether to write the raw fingerprint (getRawFingerprint)
 -d sdftitle=null       // Specifies which field to write in the first SDF line


Example
 -d fpclass=CircularFingerprinter,PubchemFingerprinter,MACCSFingerprinter -d page=0 -d pagesize=20000 -d inputtag_smiles=SMILES -d inputtag_inchi=InChI -d inputtag_inchikey=InChIKey -d tag_tokeep=InChIKey -d write_count=true 

````

### -a split

Splits SD file.

````
-a split -m post -d <parameters> -i input.sdf -o outputfolder

"Splits an SDF into chunks of predefined size (-i inputfile -o outputfolder)"
   -a split -m post 
 -d chunk=1000	// 	[type:Integer, mandatory:false]

Example
 -d chunk=1000
````

## Import into AMBIT database 

Import and preprocessing are available via AMBIT REST web services API and web application interface. This command line application provides additional facilities, mainly to facilitate import of large files. 

### -a import 

Quick import.

````
-a import -m post -i input.sdf -c config/ambit.properties

"Quick import into AMBIT database (No normalisation!). Input file (-i file). The database connection settings are read from -c {file}"
   -a import -m post

Database connection:	config/ambit.properties

````

The database connection parameters are expected in properties file format as below

````
DriverClassName=com.mysql.jdbc.Driver
Host=host.com
Scheme=jdbc\:mysql
Port=3306
Database=dbname
User=theuser
Password=thepassword
````

### -a dataset 
Dataset import

````
-a dataset -m post -i input.sdf -c config/ambit.properties

"Dataset import into AMBIT database (with normalisation). The database connection settings are read from -c {file}."
   -a dataset -m post

Database connection:	config/ambit.properties

````

### -a preprocessing
 
AMBIT database preprocessing

````
-a preprocessing -m post -d <parameters>

"Preprocessing of structures in AMBIT database (depends on options, default inchi). The database connection settings are read from -c {file}"
   -a preprocessing -m post
 -d inchi=false	// Generates InChIs in chemicals table	[type:Boolean, mandatory:false]
 -d atomprops=false	// Stores precalculated aromaticity/ring information in the structure table	[type:Boolean, mandatory:false]
 -d fp1024=false	// Hashed 1024 bit fingerprints, used for similarity searching anf substructure search prescreening	[type:Boolean, mandatory:false]
 -d sk1024=false	// Structure fingerprints, used for substructure search prescreening	[type:Boolean, mandatory:false]
 -d cf1024=false	// Pubchem fingerprints	[type:Boolean, mandatory:false]
 -d smarts=false	// Everything needed for substructure search prescreening - atomprops,fp1024,sk1024	[type:Boolean, mandatory:false]
 -d similarity=false	// Everything needed for similarity search - atomprops,fp1024	[type:Boolean, mandatory:false]
 -d pagesize=5000000	// query size	[type:Integer, mandatory:false]


Example
 -d inchi=false -d atomprops=false -d fp1024=false -d sk1024=false -d cf1024=false -d smarts=false -d similarity=false -d pagesize=5000000

Database connection:	config/ambit.properties

````

## Source code 

* Command line application [ambit2-dbcli package](https://svn.code.sf.net/p/ambit/code/trunk/ambit2-all/ambit2-apps/ambit2-dbcli)
* Standardizer [ambit2.tautomers.processor.StructureStandardizer](https://svn.code.sf.net/p/ambit/code/trunk/ambit2-all/ambit2-tautomers/src/main/java/ambit2/tautomers/processor/StructureStandardizer.java)
* SMIRKS transformation [ambit2.dbcli.smirks.SmirksProcessor](https://svn.code.sf.net/p/ambit/code/trunk/ambit2-all/ambit2-apps/ambit2-dbcli/src/main/java/ambit2/dbcli/smirks/SMIRKSProcessor.java)
 