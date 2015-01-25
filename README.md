http://ambit.sourceforge.net

This is multi module maven project.

#Build

Prerequisites for building AMBIT2
JDK 1.6 or higher 
MySQL 5.5 or higher (also compatible with MySQL 5.1 )
Maven 2.x or 3.x

1) to build ambit2 libraries:  

Retrieve sources from SVN
svn checkout svn://svn.code.sf.net/p/ambit/code/trunk/ambit2-all ambit-all

ambit2-all>mvn package

2) to build applications - AMBIT REST web services (war):

>cd ambit2-apps
mvn clean buildnumber:create package -P http -P ambit-release -P aa-enabled -P aa-admin-disabled -P license-in-text-column

(See ambit2-all/ambit2-apps/README.txt for options)

NOTE 1: The build of applications (ambit2-ambitxt and ambit2-www) depends on Toxtree jars. 
All AMBIT and Toxtree artifacts are available at

http://ambit.uni-plovdiv.bg:8083/nexus/content/repositories/releases

http://ambit.uni-plovdiv.bg:8083/nexus/content/repositories/snapshots

Optionally, to build Toxtree version on your own, get the source 
svn checkout http://svn.code.sf.net/p/toxtree/svn/trunk/toxtree toxtree and run mvn install.


NOTE 2: The build process includes mandatory database tests and may take a while.  Use -DskipTests=true option to skip the tests.

##Database configuration 

To be able to access mysql database, there should be "settings.xml" file in your ".m2" directory with the following configuration:

Root DB password is used only for creating a new database via dbunit tests , and if a web request to create a new database succeeds.
Regular user and password are guest/guest by default and are used in database queries/writes.


````
<settings xmlns="http://maven.apache.org/POM/4.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <profiles>
   <profile>
      <id>ambit-build</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
	 <!-- Write RDF via jena or stax Could be altered via ?rdfwriter=jena|stax query paramater -->
         <rdf.writer>stax</rdf.writer>

	<!-- the database name, default ambit2  -->
	<ambit.db>ambit2</ambit.db>

	<!-- db root password - for creating the database during tests -->
        <ambit.db.user.root.password></ambit.db.user.root.password>

	<!-- database user password and name -->
        <ambit.db.user.test>guest</ambit.db.user.test>
        <ambit.db.user.test.password>guest</ambit.db.user.test.password>

	 <!-- Allow creating the dtabase via POST  -->
	 <ambit.db.create.allow>true</ambit.db.create.allow>

         <!-- OpenTox AA services-->
	 <aa.opensso>http://opensso.in-silico.ch/opensso/identity</aa.opensso>
        <aa.policy>http://opensso.in-silico.ch/Pol/opensso-pol</aa.policy>

	<!-- 
		Google analytics code for AMBIT REST web services (ambit2-www)
	-->
	<google.analytics>UA-...</google.analytics>

    <!-- Include license as a separate column in CSV and TXT files , default is false. 
    Alternatively, use predefined profiles 
    -P no-license-in-text-column
    -P license-in-text-column
    --> 
    <license.intextfiles>false</license.intextfiles>
      </properties>
    </profile>
 	
    <profile>
      <!-- use this profile via mvn -P aa-enabled to have the REST services protected by OpenTox AA -->
      <id>aa-enabled</id>
      <activation>
        <activeByDefault>false</activeByDefault>
      </activation>
	<properties>
	 <!-- OpenTox AA services-->
        <aa.opensso>http://opensso.in-silico.ch/opensso/identity</aa.opensso>
        <aa.policy>http://opensso.in-silico.ch/Pol/opensso-pol</aa.policy>
	<!-- default user for tests only -->
        <aa.user></aa.user>
        <aa.pass></aa.pass>
	<aa.enabled>true</aa.enabled>
	</properties>
    </profile>
  </profiles>
</settings>
````

To skip the tests, use -DskipTests=true option

>mvn install [other options] -DskipTests=true

----------------------------------------------------------------------
#CHANGELOG

AMBIT2 2.7.3-SNAPSHOT
Current version at trunk

AMBIT2 2.7.2
ambit2-www: UI for Read across workflow
ambit2-www: Improved API-DOCS (swagger-ui)
ambit2-db: Database schema 8.7 
ambit2-db: Database and REST API support for Read across assessments
ambit2-smarts: Improved SMIRKS parser
ambit2-*: Various dependencies updated
ambit2-*:  Bug fixes
 
AMBIT2 2.7.1
ambit2-db: Database schema 8.6 
ambit2-db: Database and REST API support for Read across assessments 
ambit2-db: Fixed to enable configurations of the users database name
ambit2-*: Various dependencies updated
ambit2-www: Cache headers
ambit2-www: improved API-DOCS (swagger-ui)
ambit2-tautomers: improved tautomer generation

AMBIT2 2.7.0 
ambit2-db: Database schema 8.5
ambit2-db: Tuple tables removed
ambit2-sln: Improved SLN parser
ambit2-db: Refactoring to use modbcum dependencies
ambit2-*: Bug fixes

AMBIT2 2.5.9  
ambit2-www: Password change by admin is enabled (see User management page).  Does not depend on email notification
ambit2-www: Small Javascript fixes at the query page  
   
AMBIT2 2.5.8
ambit2-www: Improved user interface
ambit2-db: Substances and experimental data support (import, retrieve, Support for local users database and access rights)
ambit2-db: Database schema 8.4
   
AMBIT2 2.5.7
ambit2-db: Database schema 8.2
ambit2-db: Mysql Connector/J 5.1.31
ambit2-db: DBUnit 2.4.9
ambit2-loom: moved as modeule of ambit2-apps
ambit2-loom: loom 1.0.2 dependency
ambit2-users: functional local user management with role-based policies, users db 2.2.
ambit2-sln: updated SLN parser
ambit2-groupcontribution: updated, under development
ambit2-namestructure : OPSIN 1.6
ambit2-dbsubstance: non IUCLID5 substance importers moved to a separate module
all: refactored to use interfaces from modbcum-i package https://github.com/vedina/modbcum
ambit2-www: more pages updated to use recent jQueryui and drop down menu
ambit2-www: JS library (jsToxKit) updated 
ambit2-www: updated query page
ambit2-www: updated toxtree prediction page /ui/toxtree
ambit2-www: /query/similarity & /query/smarts now support ?dataset_uri parameter
ambit2-www: Substances by (multiple) endpoint search

AMBIT2 2.5.6
ambit2-www: Configurable drop down menus at the top
ambit2-www: JS library (jsToxKit) updated 
ambit2-www: Substances import via custom 12 line header CSV (as described in https://github.com/ideaconsult/Protein_Corona)
ambit2-db: Database schema 8.1
ambit2-db: Substance studies serialization by OpenTox Dataset API (ARFF and JSON)

ambit2-tautomers: 

AMBIT2 2.5.5
ambit2-db: Database schema 8.0. User table cleaned. 
ambit2-db:  SQL script to create ambit_users.
ambit2-user: new module dealing with user management (user defined in ambit_users database). Depends on https://github.com/vedina/RESTNet
ambit2-dbi5: IUCLID5 web services (retrieve container). Importing .i5z substances into AMBIT database
ambit2-dbi5: JSON and RDF reader for substances (experimental)
ambit2-www: QA filtering for IUCLID5 input (both file and web service)
ambit2-www: Users management UI support.
ambit2-www: IUCLID5 server settings added to ambit2.pref
ambit2-www: JME structure diagram editor replaced by Ketcher (https://github.com/ideaconsult/Toxtree.js wrapper)
ambit2-smarts: Recursive SMARTS support. 
ambit2-xt: removed AMBIT-XT related modules.
ambit2-plugin-analogs: removed
ambit2-plugin-dbtools: removed
ambit2-plugin-pbt: removed
ambit2-plugin-search: removed
ambit2-plugin-usermgr: removed

AMBIT2 2.5.4
ambit2-www: i5 0.0.5 (43 endpoints and QA filtering criteria)
ambit2-www: jtoxkit.js update; configurable dataset browser and study tabs improvements
ambit2-www: multiple i5z files upload
ambit2-www: large download icons; 
ambit2-www: theme black-tie
ambit2-db: Support for substance external identifiers and reference owner DB schema 7.2
ambit2-smarts: better stereochemistry support
ambit2-sln: Sybyl Line Notation (in progress)

AMBIT2 2.5.3
ambit2-core: IUCLID5 endpoint study records (via https://github.com/ideaconsult/i5 0.0.4 )
ambit2-descriptors: Descriptor to retrieve OpenPhacts pharmacology count per compound
ambit2-db: Substance endpoint study records support. DB Schema 7.1
ambit2-www: jtoxkit.js for rendering substance endpoint study records
ambit2-www: DEREK friendly SDF export (Options to unify line separators and generate 2D coordinates)
ambit2-www: Improved statistcis resource /admin/stats
ambit2-groupcontribution : new modlule for group contribution methods
ambit2-sln : new modlule for SLN parser

AMBIT2 2.5.2
ambit2-db: Substance study support. DB Schema 7.1
ambit2-descriptors: New descriptors
ambit2-www: x-frame-origin headers added
ambit2-www: Substance study pages JSON and UI (makes use of jToxKit)
ambit2-www: i5 dependency 0.0.3
ambit2-sln: Sybyl Line Notation initial commits

AMBIT2 2.5.1

ambit2-www: Security improvements
ambit2-www: JSONP support for /model
ambit2-www: Optional CORS support (allowed.origins property at /ambit2/rest/config/prop)


AMBIT2 2.5.0


ambit2-core: Zip reader classes use File instead of stream where possible, otherwise the zip table of content is not always correct
ambit2-core: Specific parser for SDF file with DEREK predictions

ambit2-namestructure: Upgraded to OPSIN 1.5.0

ambit2-db: Database schema 7.0 (IUCLID5 substances support)
ambit2-db: Database schema 6.8 Chemical landscape / activity cliffs support (see http://toxmatch.sf.net )
ambit2-db: Database schema 6.7 Support for storing structure relations (e.g. tautomers, metabolites)

ambit2-smarts: Fixed a bug in SMARTS writing.
ambit2-smarts: Improved parsing of ring closures

ambit2-www: Status reporting in debug or production mode; uses Freemarker template to generate HTML
ambit2-www: Improved input sanitisation, framed responses are no more allowed; autocomplete off for web forms
ambit2-www: Improved JS broswer compatibility
ambit2-www: Initial support for IUCLID5 substances and substance composition. Uses https://github.com/ideaconsult/i5
ambit2-www: Human readable names for aggregated features in JSON 
ambit2-www: JSON serialization for property annotations
ambit2-www: Web services and UI for chemical landscape and activity cliffs ( /qmap and /toxmatch ; see http://toxmatch.sf.net)
ambit2-www: Web service to generate and store tautomers (OpenTox Algorithm API /algorithm/tautomergen ) 

AMBIT2 2.4.12

ambit2-rendering, ambit2-ui: Fixes a regression where the 2D depiction in swing UI is written on top of the previous molecule
ambit2-db: support for relations between structures (to be used for tautomers, metabolites, additives, impurities, etc.)
ambit2-db: Flag to reuse existing structure record on import
ambit2-db: New tables for fast activity cliffs calculations 
ambit2-tautomers: Added code for calculation of average descriptor values based on the tautomer information
ambit2-www: SMILES, InChI, InChI key and formula included in the compound json serialization
ambit2-www: Json serialization for property annotations
ambit2-www: Jsonp support for the /algorithm resource
ambit2-www: Support for /compound/{inchikey} in addition to /compound/{id}
ambit2-www: InChI Key query option /query/compound/inchikey/all?search=INCHIKEY
ambit2-www: Tautomers generation via /algorithm and /model services.
ambit2-www: The tautomers are stored in the DB and can be retrieved via /query/tautomer?dataset_uri=COMPOUNDURI
ambit2-www: Tautomers depict fixed to accept InChI in addition to SMILES
ambit2-db, ambit2-www: Merged ambit-chemspace branch implementing http://www.ncbi.nlm.nih.gov/pubmed/23110534
ambit2-www: Activity cliffs: precomputed g2 counts and activity cliffs ordering by /qmap/{id} 
ambit2-db, ambit2-nano: Merged ambit-nano branch implementing support for nano materials via conditional dependency
ambit2-reactions: Initial functionality for retro synthesis


AMBIT2 2.4.11

SVN URLs changed due to SourceForge platform upgrade
ambit2-rendering: Atom labels rendered using alpha transparency channel 
ambit2-www: Extended /compound REST reosurce to allow adding and setting property values by HTTP PUT
ambit2-www: Fix for json breaking url encoded URI parameter if it contains #
ambit2-www: Support to return statistics per compound when searching by structure (no of of collections). JSON support only
ambit2-www: Fixed the procedure of adding (POST) entries to an existing /dataset/R{id}
ambit2-www: Added another view on /dataset/RNo  - read only  /collection/{folder}/{datasetname}
ambit2-www: ?mol=true option supported by all structure retrieval resources (returns MOL inside JSON)
ambit2-www: compound json generator refactored
ambit2-www: Modified to return compound uri with features upon /compound POST/PUT
ambit2-www: New algorithms - Mopac with OBabel and Balloon(to generate starting structure)
ambit2-www: Added support for custom RDF data types to represent ToxicityCategories
ambit2-www: Version tooltip in the HTML rendering
ambit2-descriptors: Klekota-Roth fingerprinter wrapper
ambit2-descriptors: Added atom environment support for atom types Het, Hev, Hal (Sybyl atom types)
ambit2-descriptors: Atom environment matrix descriptor
ambit2-mopac: less strict default settings for MOPAC and 30 min timeout
ambit2-mopac: Wrapper for Mopac with OBabel and Balloon(to generate starting structure)

Toxtree 2.5.9-SNAPSHOT

AMBIT2 2.4.10-SNAPSHOT
Freemarker support. HTML pages refactored to use Freemarker templates and Javascript
JSON support extended to more resources (tasks, facets, models, algorithms, admin)
Improved logging. Configurable logging via WEB-INF/classes/ambit/rest/config/logging.props
Improved HTML rendering, using skeleton.css.
New /ui resources allowing flexible querying and data collation within web browser
Ambit DB schema 6.4 
Image map support for compounds, allowing atom highlighting
Support for exact , substructure and similarity search by (base64 encoded) mol file
Added "folder" option to  application/x-www-form-urlencoded POST /dataset  
openTox client 1.0.4-SNAPSHOT
Conditional inclusion of JNI related jars into the war. Enable -P no-jni profile to obtain war without those jars.
Further work on tautomer package.
Various bug fixes.
Dependencies upgraded:
Toxtree 2.5.8-SNAPSHOT (with new Ames mutagenicity plugin)
OPSIN 1.4.0 
MySQL Connector/J 5.1.22

AMBIT2 2.4.9
JSON & JSONP (optional) support
Tautomers package 
Bug fix when importing structures (regression from 2.4.8) 
More machine learning algorithms

CHANGELOG
AMBIT2 2.4.8
Database schema 6.0
Exact structure lookup via InChI 
Import uses stored procedure findByProperty. If upgrading the database, run 
grant execute on procedure findByProperty to "guest"@"localhost";
grant execute on procedure findByProperty to "guest"@"127.0.0.1";
(replace with the proper mysql user name)
Support for stored procedures in QueryExecutor

AMBIT2 2.4.7
.r4267
cdk 1.4.11
jchempaint 3.3-1206
Improved procedure for kekulisation.
Experimental tautomer generation, /depict/tautomer
Improved HTML rendering with Javascript
JSON serialisation for most services.
Fixed bug preventing reading features if running under HTTPS
Simple pairwise similarity calculation at /depict/pairwise
Dropped foreign keys to table users.  Now user names are not necessary related to mysql users. 
G+ buttons at the depiction page
Added PubChem image generation via http://pubchem.ncbi.nlm.nih.gov/pug_rest/
Smarts & similarity queries now accept names and InChI in addition to smarts & smiles

AMBIT2 2.4.6-SNAPSHOT 
Added optional simple local aa, activated via aa-local Profile. Edit WEB-INF/classes/ambit2/rest/config/config.prop to customize
Removed QMRF code, it is moved to a separate project http://qmrf.sf.net
JQuery UI for HTML rendering
Updated binaries smi23d svn rev 420
Set "optimized" type upon successful mopac optimisation
MopacShell modified to handle disconnected structures
Updated JChemPaint dependency to 3.3.0
Fixed Mopacreader to read charges
Fixed atom environment code
Added QMRF editor code from Ambit 1 https://ambit.svn.sourceforge.net/svnroot/ambit/trunk/ambit1/src/ambit/data/qmrf
Added algorithm for replacing the structure with the preferred one
Consensus labels query resource
New resource for consensus stats 
New resource for qlabel stats  /query/struc_label summary 
Better support for folders inside zip file, and support for uploading zip to /dataset
Finder algorithm: another lookup option  "Lookup only empty structures and add the result as additional structure representation" 
Added dnabinding and proteinbinding toxtree algorithms
Wrapped several IFingerprint classes as descriptor, added to /algorithm REST services
cdk 1.4.5

AMBIT2 2.4.4-SNAPSHOT
.r3856
site update
fixeded NPE in facet resource to handle prefixed compound uris , e.g. /dataset/id/compound 
added subclass to handle dragon descriptors, when dragon home is set via maven config
profiles to enable/disable AA on /admin resources


AMBIT2 2.4.3
.r3853
Database schema 5.1
Feature annotations
Dataset licenses
Dragon descriptor wrapper  ( set .m2/settings.xml property <DRAGON_HOME>...</DRAGON_HOME> to point to the Dragon6 directory  ) 
OpenBabel depiction ( set .m2/settings.xml property <OBABEL_HOME> to point to the OpenBabel directory )
Toxtree 2.5.0
More Weka learning algorithms
Improvements
Bug fixes


AMBIT2 2.4.0-SNAPSHOT
.r3436
 cdk 1.3.8 and cdk-jchempaint-18 
jchempaint 3.1.3 only in ambit2-jchempaint and AmbitXT
MySQL 5.5

Changelog: 

AMBIT 2.2.0
.r2845
 cdk 1.3.7 and jchempaint 3.1.3

AMBIT 2.1.4

Changelog: cdk 1.3.6 and jchempaint 3.1.3

-------------------------------------------------------
#Database

If not using the automatic DB create via REST services , grant the following privileges (change the user if necessary)

````
GRANT ALL ON `ambit2`.* TO 'guest'@'localhost' IDENTIFIED BY 'guest';
GRANT ALL ON `ambit2`.* TO 'guest'@'127.0.0.1' IDENTIFIED BY 'guest';
GRANT ALL ON `ambit2`.* TO 'guest'@'::1' IDENTIFIED BY 'guest';
GRANT TRIGGER ON `ambit2`.* TO 'guest'@'localhost';
GRANT TRIGGER ON `ambit2`.* TO 'guest'@'127.0.0.1';
GRANT TRIGGER ON `ambit2`.* TO 'guest'@'::1';
-- 
GRANT CREATE ROUTINE ON `ambit2`.* TO 'guest'@'localhost';
GRANT CREATE ROUTINE ON `ambit2`.* TO 'guest'@'127.0.0.1';
GRANT CREATE ROUTINE ON `ambit2`.* TO 'guest'@'::1';
-- To be able to import datasets
GRANT EXECUTE ON PROCEDURE `ambit2`.findByProperty TO 'guest'@'localhost';
GRANT EXECUTE ON PROCEDURE `ambit2`.findByProperty TO 'guest'@'127.0.0.1';
GRANT EXECUTE ON PROCEDURE `ambit2`.findByProperty TO 'guest'@'::1';
-- To be able to remove datasets
GRANT EXECUTE ON PROCEDURE `ambit2`.deleteDataset TO 'guest'@'localhost';
GRANT EXECUTE ON PROCEDURE `ambit2`.deleteDataset TO 'guest'@'127.0.0.1';
GRANT EXECUTE ON PROCEDURE `ambit2`.deleteDataset TO 'guest'@'::1';
````

test database ambit-test
````
GRANT ALL ON `ambit-test`.* TO 'guest'@'localhost' IDENTIFIED BY 'guest';
GRANT ALL ON `ambit-test`.* TO 'guest'@'127.0.0.1' IDENTIFIED BY 'guest';
GRANT ALL ON `ambit-test`.* TO 'guest'@'::1' IDENTIFIED BY 'guest';
GRANT TRIGGER ON `ambit-test`.* TO 'guest'@'localhost';
GRANT TRIGGER ON `ambit-test`.* TO 'guest'@'127.0.0.1';
GRANT TRIGGER ON `ambit-test`.* TO 'guest'@'::1';
GRANT EXECUTE ON PROCEDURE `ambit-test`.findByProperty TO 'guest'@'localhost';
GRANT EXECUTE ON PROCEDURE `ambit-test`.findByProperty TO 'guest'@'127.0.0.1';
GRANT EXECUTE ON PROCEDURE `ambit-test`.findByProperty TO 'guest'@'::1';

GRANT EXECUTE ON PROCEDURE `ambit-test`.deleteDataset TO 'guest'@'localhost';
GRANT EXECUTE ON PROCEDURE `ambit-test`.deleteDataset TO 'guest'@'127.0.0.1';
GRANT EXECUTE ON PROCEDURE `ambit-test`.deleteDataset TO 'guest'@'::1';
````

UPDATE mysql.proc SET definer = 'guest@localhost' WHERE db = 'ambit2';
UPDATE mysql.proc SET definer = 'guest@localhost' WHERE db = 'ambit-test';
