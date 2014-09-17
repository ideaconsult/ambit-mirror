#Quick user guide (Datasets)

## Menu: Datasets

###Datasets

The [datasets service](api_dataset.html) provides access to chemical compounds and their features (e.g. structural, physical-chemical, biological, toxicological properties). 
Uploading a file of chemical structures and properties will result in the creation of a new dataset (**Datasets/Import a dataset**). 
The list of datasets is available through the menu **Datasets/All datasets** or at http://host:port/ambit2/dataset.

![Datasets](images/screenshots/datasets_all.png "Datasets")

The dataset content can be downloaded in different formats (through the links in the right column). 
Each dataset can be browsed via the **New view mode** link, which will display the dataset content in a similar mode as the [structure search results](usage.html) described in the previous sections.

The dataset metadata (title, license, etc.) can be edited via the **Metadata** link. 

###Import a dataset

The **Import/Import a dataset** page allows to upload a file with chemical structures and properties. The major chemical formats such as [SDF](http://en.wikipedia.org/wiki/Chemical_table_file#SDF), [MOL (v2000)](http://en.wikipedia.org/wiki/Chemical_table_file#Molfile), SMI, [CML](http://en.wikipedia.org/wiki/Chemical_Markup_Language), [ToXML](http://toxml.org/) are supported and accepted. Spreadsheet like files such as XLS (but not XSLX), CSV and TXT files are also supported. To import structures from these type of files, a column named SMILES, containing SMILES notation, or a column named InChI with the InChI representation of the chemical should be present in the file. 

-	Select the file for upload from your local computer.

-	The example below uses the EPA FHM SDF file, downloaded from http://www.epa.gov/ncct/dsstox/sdf_epafhm.html

-	Specify the dataset name and the source URI (if relevant).

-	Specify how the structures will be matched with the existing compounds in the database. The default is by CAS with fallback to a structure match, in case the CAS number is missing.

-	Click the **Submit** button.

![Import a dataset](images/screenshots/dataset_import.png "Import a dataset")

-	The data upload is protected and requires user name and password. The type of protection depends on the AMBIT2 [web application configuration](install_ambitrest.html).

-	If the log in was successful, the import procedure is assigned a URI with the following form: http://YOURHOST:YOURPORT/ambit2/task/{long-string-identifying-the-task} 

-	Clicking on the **Ready. Results available** link leads to the dataset browsrr page, which displays the structures and proeprties from the newly uploaded EPA FHM dataset.

###Import properties

The **Import/Import properties** page is similar as the **Import dataset** page, but is generally used to import properties only, which are assigned to existing structures in the database.
 

## Menu: Search

###Search structures and associated data

[Quick user guide](usage.html)

###Substances search

[Quick user guide (substance search)](usage_substance.html)	 

	  
