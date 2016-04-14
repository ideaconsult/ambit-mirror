#ambitcli

A [command line Java application](download_ambitcli.html) used for processing chemical files, [structure standardisation](./ambitcli_standardisation.html), import into AMBIT database and processing AMBIT database entries.  

Chemical structure standardization. Available since AMBIT 3.0.0.

## Download

* Latest release <a href="https://sourceforge.net/projects/ambit/files/Ambit2/AMBIT%20applications/ambitcli/ambitcli-3.0.2/ambitcli-3.0.2.jar/download">ambitcli-3.0.2</a>

* <a href="http://sourceforge.net/projects/ambit/files/Ambit2/AMBIT%20applications/ambitcli/">All releases</a>

* Pre-release <a href="https://sourceforge.net/projects/ambit/files/Ambit2/AMBIT%20applications/ambitcli/ambitcli-3.0.2">ambitcli-3.0.2-{buildnumber}.jar</a> 

* Pre-release <a href="https://www.ideaconsult.net/downloads/ambitcli/ambitcli-3.0.1-20151130.jar">ambitcli-3.0.1-20151130.jar</a> | <a href="https://www.ideaconsult.net/downloads/ambitcli/ambitcli-3.0.1-20160205.jar">ambitcli-3.0.1-20160205.jar (build:7259 1454688887571)</a>

* Development <a href="http://ambit.uni-plovdiv.bg:8083/nexus/#nexus-search;gav~~ambit2-dbcli~~jar~">Maven repository</a>


## Usage

````sh

java -Xmx1536m -jar ambitcli{version}.jar -a standardize -i <inputfile> -m post -d page=page num -d pagesize=-1|page_size -o <output> -d tautomers=true -d splitfragments=true -d implicith=true -d smiles=false -d smilescanonical=true -d inchi=true -d neutralise=true -d isotopes=true
````

or in order to rename the default SMILES and InChI fields:

````sh
java -Xmx1536m -jar ambitcli.jar -a standardize -i <inputfile> -m post -d page=pagenum -d pagesize=-1|page_size -o <output> -d tautomers=true -d splitfragments=true -d implicith=true -d smiles=false -d smilescanonical=true -d inchi=true -d neutralise=true -d isotopes=true  -d tag_inchi=AMBIT_InChI -d tag_inchikey=AMBIT_InChIKey -d tag_smiles=AMBIT_SMILES -d tag_rank=TAUTOMER_RANK
````

### Logging

Logging configuration can be specified via  -Djava.util.logging.config.file option, specifying logging.properties file. If not specified, the [default logging.properties](https://svn.code.sf.net/p/ambit/code/trunk/ambit2-all/ambit2-apps/ambit2-dbcli/src/main/resources/ambit2/dbcli/logging.properties) is used.

````sh
$java -jar -Djava.util.logging.config.file=myLoggingConfigFilePath .... other options ....
````


### For options other than standardisation see [the main ambitcli page](download_ambitcli.html).

### Standardization specific help:

````sh
    $java -jar ambitcli-{version}.jar - a standardize -m post -d <parameters>

    "Chemical structure standardization (-i inputfile.sdf -o outputfile.sdf , recognized by extensions .sdf , .csv, .cml , .txt)"
    -a standardize -m post
    -d smirks=null	// JSON file with SMIRKS transformations	[type:String, mandatory:false]
    -d neutralise=false	// If true neutralises the molecule via set of predefined SMIRKS	[type:Boolean, mandatory:false]
    -d splitfragments=false	// If true keeps the largest fragment	[type:Boolean, mandatory:false]
    -d implicith=false	// If true converts hydrogens to implicit	[type:Boolean, mandatory:false]
    -d generatestereofrom2d=false	// If true uses org.openscience.cdk.stereo.StereoElementFactory to generate the stereochemistry from 2D coordinates	[type:Boolean, mandatory:false]
    -d isotopes=false	// If true clears isotopes	[type:Boolean, mandatory:false]
    -d generate2D=false	// Generate 2d coordinates (if no any)	[type:Boolean, mandatory:false]
    -d tautomers=false	// If true generates the top ranked tautomer	[type:Boolean, mandatory:false]
    -d inchi=true	// Generates InChIs. If -d tautomers=true InChI FixedH=true, otherwise generates standard InChI	[type:Boolean, mandatory:false]
    -d smiles=true	// Generates SMILES (isomeric, kekule).  Uses CDK SmilesGenerator.isomeric()	[type:Boolean, mandatory:false]
    -d smilescanonical=false	// Generates SMILES (canonical).  Uses CDK SmilesGenerator.absolute()	[type:Boolean, mandatory:false]
    -d smilesaromatic=false	// Generates aromatic SMILES.  Uses CDK SmilesGenerator.aromatic()	[type:Boolean, mandatory:false]
    -d page=0	// Start page (first page = 0)	[type:Integer, mandatory:false]
    -d pagesize=20000	// Page size (in number of records). Set to -1 to read all records.	[type:Integer, mandatory:false]
    -d inputtag_smiles=SMILES	// Specifies the name of the column, containing SMILES in the input file	[type:String, mandatory:false]
    -d inputtag_inchi=InChI	// Specifies the name of the column, containing InChI in the input file	[type:String, mandatory:false]
    -d inputtag_inchikey=InChIKey	// Specifies the name of the column, containing InChIKey in the input file	[type:String, mandatory:false]
    -d tag_inchi=InChI	// Specifies the tag to store the generated InChI	[type:String, mandatory:false]
    -d tag_inchikey=InChIKey	// Specifies the tag to store the generated InChIKey	[type:String, mandatory:false]
    -d tag_smiles=SMILES	// Specifies the tag to store the generated SMILES	[type:String, mandatory:false]
    -d tag_rank=RANK	// Specifies the tag to store the tautomer rank (energy based, less is better)	[type:String, mandatory:false]
    -d tag_tokeep=	// Specifies which tags to keep, comma delimited list. Everything else will be removed. To keep all the tags, leave this empty.	[type:String, mandatory:false]
    -d sdftitle=null	// Specifies which field to write in the first SDF line null|inchikey|inchi|smiles|any-existing-field	[type:String, mandatory:false]
    -d debugatomtypes=false	// Writes only structures with AtomTypes property set. For debug purposes	[type:boolean, mandatory:false]

````

### Examples:

* Generate SMILES, InChI and InChI key, retain only the PUBCHEM_CID from the fields in the input file

````
    java -jar ambitcli-3.0.2-7354.jar -a standardize -m post -a standardize -m post -d page=0 -d pagesize=-1 -d tautomers=false -d tag_tokeep=PUBCHEM_CID -d smilescanonical=false -d smiles=true -d inchi=true -i inputfile -o outputfile
````

* Full standardisation, write SMILES, InChI and InChI key into AMBIT_SMILES, AMBIT_InChI, AMBIT_InChIKey, retain only the PUBCHEM_CID from the fields in the input file

````
    java -jar ambitcli-3.0.2-7354.jar -a standardize -m post -a standardize -m post -d page=0 -d pagesize=-1 -d tag_smiles=AMBIT_SMILES -d tag_inchi=AMBIT_InChI -d tag_inchikey=AMBIT_InChIKey -d tautomers=true -d splitfragments=true -d implicith=true -d smiles=true -d smilescanonical=false -d inchi=true -d neutralise=true -d isotopes=true -d tag_tokeep=PUBCHEM_CID
````

### Options

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
    -d smiles=true	// Generates SMILES (isomeric, kekule).  Uses CDK SmilesGenerator.isomeric()	[type:Boolean, mandatory:false]
    -d smilescanonical=false	// Generates SMILES (canonical).  Uses CDK SmilesGenerator.absolute()	[type:Boolean, mandatory:false]
    -d smilesaromatic=false	// Generates aromatic SMILES.  Uses CDK SmilesGenerator.aromatic()	[type:Boolean, mandatory:false]
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

#### 12. Input tags   
 
 -d inputtag_smiles=SMILES	// Specifies the name of the column, containing SMILES	[type:String, mandatory:false] 