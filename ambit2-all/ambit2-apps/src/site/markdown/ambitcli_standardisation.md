#ambitcli

A [command line Java application](download_ambitcli.html) used for processing chemical files, [structure standardisation](./ambitcli_standardisation.html), import into AMBIT database and processing AMBIT database entries.  

## Download

* <a href="http://sourceforge.net/projects/ambit/files/Ambit2/AMBIT%20applications/ambitcli/">Releases</a> 

* Development 
  <a href="https://www.ideaconsult.net/downloads/ambitcli/ambitcli-3.0.1-20151130.jar">ambitcli-3.0.1-20151130.jar</a> | <a href="http://ambit.uni-plovdiv.bg:8083/nexus/#nexus-search;gav~~ambit2-dbcli~~jar~">Maven repository</a>

## Usage

````sh
$java -jar ambitcli.jar -help
INFO   ambitcli-3.0.1-SNAPSHOT build:7259 1454688887571
http://ambit.sourceforge.net/download_ambitcli.html
 -a,--command <command>          Commands:
                                 import|preprocessing|dataset|split|standa
                                 rdize|descriptor|help|
 -c,--config <file>              Config file (DB connection parameters)
 -d,--data <data>                Command specific parameters (multiple).
                                 Use -a cmd -m help to list available
                                 parameters
 -i,--input <file>               Input SDF file
 -m,--subcommand <subcommand>    Subcommands. Use -a cmd -m help to list
                                 subcommands of a specific command.
 -o,--output <file>              Output file
-h,--help                       This help      
 ````

Standardization specific help:

````sh
$java -jar ambitcli.jar -a standardize -m help
INFO   ambitcli-3.0.1-SNAPSHOT build:7259 1454688887571
http://ambit.sourceforge.net/download_ambitcli.html
-a standardize -m post -d <parameters>
"Chemical structure standardization (-i inputfile.sdf -o outputfile.sdf , recognized by extensions .sdf , .csv, .cml , .txt)"
   -a standardize -m post
 -d smirks=null	// JSON file with SMIRKS transformations	[type:String, mandatory:false]
 -d neutralise=false	// If true neutralises the molecule via set of predefined SMIRKS	[type:Boolean, mandatory:false]
 -d splitfragments=true	// If true keeps the largest fragment	[type:Boolean, mandatory:false]
 -d implicith=true	// If true converts hydrogens to implicit	[type:Boolean, mandatory:false]
 -d generatestereofrom2d=false	// If true uses org.openscience.cdk.stereo.StereoElementFactory to generate the stereochemistry from 2D coordinates	[type:Boolean, mandatory:false]
 -d isotopes=false	// If true clears isotopes	[type:Boolean, mandatory:false]
 -d generate2D=false	// Generate 2d coordinates (if no any)	[type:Boolean, mandatory:false]
 -d tautomers=true	// If true generates the top ranked tautomer	[type:Boolean, mandatory:false]
 -d inchi=false	// Generates InChIs. If -d tautomers=true InChI FixedH=true, otherwise generates standard InChI	[type:Boolean, mandatory:false]
 -d smiles=false	// Generates SMILES (isomeric, kekule)	[type:Boolean, mandatory:false]
 -d smilescanonical=false	// Generates SMILES (canonical). Set to true to generate stereo SMILES	[type:Boolean, mandatory:false]
 -d page=0	// Start page (first page = 0)	[type:Integer, mandatory:false]
 -d pagesize=20000	// Page size (in number of records)	[type:Integer, mandatory:false]
 -d tag_inchi=InChI	// Specifies the InChI tag	[type:String, mandatory:false]
 -d tag_inchikey=InChIKey	// Specifies the InChIKey tag	[type:String, mandatory:false]
 -d tag_smiles=SMILES	// Specifies the SMILES tag	[type:String, mandatory:false]
 -d tag_rank=RANK	// Specifies the tag to store the tautomer rank (energy based, less is better)	[type:String, mandatory:false]
 -d sdftitle=null	// Specifies which field to write in the first SDF line null|inchikey|inchi|smiles|any-existing-field	[type:String, mandatory:false]
 -d debugatomtypes=false	// Writes only structures with AtomTypes property set. For debug purposes	[type:boolean, mandatory:false]
````

Logging configuration can be specified via  -Djava.util.logging.config.file option, specifying logging.properties file. If not specified, the [default logging.properties](https://svn.code.sf.net/p/ambit/code/trunk/ambit2-all/ambit2-apps/ambit2-dbcli/src/main/resources/ambit2/dbcli/logging.properties) is used.

````sh
$java -jar -Djava.util.logging.config.file=myLoggingConfigFilePath .... other options ....
````

For options other than standardisation see [the main ambitcli page](download_ambitcli.html).

### <a name="standardize"></a>-a standardize

Chemical structure standardization. Available since AMBIT 3.0.0.
 
````sh
$java -jar ambitcli.jar -i input.sdf -o output.sdf -a standardize -m post -d <parameters>
````

Example options
````
 -d smirks=null -d splitfragments=true -d implicith=true -d generatestereofrom2d=false -d tautomers=true -d isotopes=true -d neutralise=true -d inchi=false -d smiles=false -d smilescanonical=false -d page=0 -d pagesize=20000 -d tag_inchi=InChI -d tag_inchikey=InChIKey -d tag_smiles=SMILES -d tag_rank=RANK
````

#### 1.Transformation
````sh
 -d smirks=null|file.json
````
Chemical structure transformation by [SMIRKS](http://daylight.com/dayhtml_tutorials/languages/smirks/index.html), implemented by [ambit2-smirks package](https://github.com/ideaconsult/examples-ambit/tree/master/smirks-example). 
The option expects either null (default) or a JSON file defining SMIRKS in the following format. Any number of transformations could be specified.    

````json
{
    "REACTIONS": [
        {
            "NAME": "Nitro group uncharged -> charged",
            "CLASS": "standardization",
            "SMIRKS": "[*:1][N:2](=[O:3])=[O:4]>>[*:1][N+:2](=[O:3])[O-:4]",
            "USE": true
        },
        {
            "NAME": "Nitro group charged -> uncharged",
            "CLASS": "standardization",
            "SMIRKS": "[*:1][N+:2](=[O:3])[O-:4]>>[*:1][N:2](=[O:3])=[O:4]",
            "USE": false
        }    
    ]
}
````
The example transformations above will convert the nitro groups from uncharged form to the charged one (if USE:false the transformation will be ignored). 

#### 2.Fragments

````sh
 -d splitfragments=true|false	
````
If true keeps the largest fragment. If false keeps the entire molecule, even if disconnected. Default is false.

#### 3.Isotopes

````
 -d isotopes=true|false	
````
If true clears isotopes.

#### 4.Neutralisation

````
 -d neutralise=true|false	
````
 If true neutralises the molecule via set of [predefined SMIRKS](https://svn.code.sf.net/p/ambit/code/trunk/ambit2-all/ambit2-smarts/src/main/resources/ambit2/smirks/smirks.json).
 This is an option for convenience only. Using the transformation option `-d smirks` with the same SMIRKS file will have the same effect.  
 
#### 5.Implicit hydrogens

````sh 
 -d implicith=true|false
````
If true converts hydrogens to implicit. If false leaves the structure as it is. Default is false.

#### 6.Stereochemistry
```` 
 -d generatestereofrom2d=true|false	
````sh
If true uses org.openscience.cdk.stereo.StereoElementFactory to generate the stereochemistry from 2D (stereo elements derived from 2D coordinates).

#### 7.Tautomers
````sh 
 -d tautomers=true|false		
 -d tag_rank=RANK	 
````
If true generates the top ranked tautomer via [ambit-tautomers](https://github.com/ideaconsult/examples-ambit/tree/master/tautomers-example) package [doi:10.1002/minf.201200133](http://onlinelibrary.wiley.com/doi/10.1002/minf.201200133/abstract). Default is false.
The tag_rank option specifies the tag to store the tautomer rank (energy based, less is better).

Note: this is the slowest operation within the standardisation options, as it generates all tautomers and selects the top ranked one. Typical processing time is 200-500 msec per chemical structure.

#### 8.InChI generation
````sh
 -d inchi=true|false
 -d tag_inchi=InChI	// Specifies the InChI tag	[type:String, mandatory:false]
 -d tag_inchikey=InChIKey	// Specifies the InChIKey tag	[type:String, mandatory:false]
````

Generates InChIs. If `-d tautomers=true` uses InChI option FixedH=true, otherwise generates standard InChI. If false does not generate InChI. Default is true.

#### 9.SMILES generation
````sh
 -d smiles=true|false	// If true generates SMILES (isomeric, kekule).	
 -d smilescanonical=true|false	// If true generates canonical SMILES.	
 -d tag_smiles=SMILES	// Specifies the SMILES tag	[type:String]
```` 

#### 10.Page/Pagesize
````sh
 -d page=0	// Start page (first page = 0)	[type:Integer]
 -d pagesize=20000	// Page size (in number of records)	[type:Integer]
```` 
Used to process specific part of the file (e.g. -d page=2 -d page=100 will skip the first 200 records).
 
#### 11. SDF file molecule name  
````sh
 -d "sdftitle=InChIKey"	
```` 
If the output is SDF file, will write the specified property in the first line 
 
 