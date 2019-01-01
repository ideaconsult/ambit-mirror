#Quick user guide

Open the following URI in your browser, replacing YOURHOST and YOURPORT with the relevant values from your configuration.

````
http://YOURHOST:YOURPORT/ambit2
````

All AMBIT web application pages contain a common menu bar at the top, containing the following top menu items:  **Search**, **Assessments**, **Import**, **Enhanced functions**, **Admin**, **Help**. Clicking on each top menu item displays of a dropdown list box of hierarchically organized menu subitems.

###Simple search

Type in *caffeine* (or any chemical identifier) and press the **Search** button. The search results are displayed in the Structure search page.

![AMBIT main screen](images/screenshots/main.png "AMBIT main screen")

## Menu: Search

The AMBIT structure search page allows to query and retrieve chemical structures, substances and related data.

###Search structures and associated data

The **Search/Search structures and associated data** menu, allows to search chemical structures by exact structure, similarity or substructure. The rightmost button toggles the visibility of a structure diagram editor.

![Search by identifier](images/screenshots/search_bar.png "Search by identifier")
![Similarity search](images/screenshots/search_bar_similarity.png "Similarity search")
![Substructure search](images/screenshots/search_bar_substructure.png "Substructure search")

####Search by identifiers or exact search  

The exact search option accepts any chemical identifier (SMILES, InChI, chemical name, CAS, EINECS) or a structure drawn through the structure diagram editor (the rightmost buton with a pen icon). If the search string submitted is not a chemical identifier (this is automatically determined), AMBIT searches for available properties with value equal to this string. For example, it is possible to search for all the chemicals with REACH registration date 31.05.2018 by specifying *31.05.2018* as a search string. An example search by chemical name (e.g. *caffeine*) is shown below.

![Search structures and associated data – results](images/screenshots/search_structures_1.png "Search structures and associated data – results")

The folder icon can be used to show or hide details about the chemical compound, or show/hide the lists of related substances.

![Search results (compound details)](images/screenshots/search_structures_2.png "Search results (compound details)")

The **Substances** tab shows the substances related to the chemical structure, and the role of the chemical structure (last column , e.g. *Constituent*, *Impurity*, *Additive*).

![Search results (substances)](images/screenshots/search_structures_3.png "Search results (substances)")

The substance details can be shown using the substance folder icon.

![Search results (substance details)](images/screenshots/search_structures_4.png "Search results (substance details)") 

####Sidebar

The vertical sidebar at the left is activated with mouse hover and allows collating information from multiple dataset and predictive models with the search results. 

The sidebar contains lists of available datasets and models. To add columns from a dataset or a model, check the relevant checkboxes and click the search button again.

![Selecting datasets and model predictions to display](images/screenshots/search_structures_5.png "Selecting datasets and model predictions to display")

###Similarity search 

The similarity search option retrieves chemical structures based on *Tanimoto similarity* with hashed fingerprints. The default similarity threshold is 0.9 and can be selected through a dropdown box. The similarity search text box accepts (*SMILES*, *InChI*, or *chemical name*), or structure .

![Similarity search for  SMILES O=C1C2=C(N=CN2C)N(C(=O)N1C)C](images/screenshots/search_similarity_1.png "Similarity search for  SMILES O=C1C2=C(N=CN2C)N(C(=O)N1C)C")

Using the sidebar: Merging datasets and model predictions electing datasets and model predictions to display

![Merging datasets and model predictions](images/screenshots/search_similarity_2.png "Merging datasets and model predictions")

####Substructure search 

The substructure search query can be defined by drawing the structure, selecting a [*SMARTS*](http://www.daylight.com/dayhtml/doc/theory/theory.smarts.html) from the predefined *list of SMARTS*, or entering a *SMARTS*, *SMILES* or *chemical name* in the text box.

![Substructure search for caffeine](images/screenshots/search_substructure_1.png "Substructure search for caffeine")

The [structure editor](http://ggasoftware.com/opensource/ketcher) is JavaScript based (replaces the previous Java Applet [Java Molecular Editor - JME](http://www.molinspiration.com/jme/)). Its visibility can be toggled through the rightmost button on the search bar (the pen icon). 

-	To use the drawn structure for search, click the *Use* button.
-	To show the structure, specified as SMILES in the search bar, click the *Draw* button.

![Substructure search results](images/screenshots/search_substructure_4.png "Substructure search results") 

Substructure search, using a from a predefined *list of SMARTS*.

![Substructure search for acrylamides SMARTS](images/screenshots/search_substructure_2.png "Substructure search for acrylamides SMARTS")

###Export

The search results can be downloaded as files in various file formats ([SDF](http://en.wikipedia.org/wiki/Chemical_table_file#SDF) ,[CML](http://en.wikipedia.org/wiki/Chemical_Markup_Language) , SMI, InChI, CSV, TXT, [ARFF](http://en.wikipedia.org/wiki/Weka_(machine_learning)#ARFF_file) , [RDF](http://www.w3.org/RDF/) , [JSON](http://en.wikipedia.org/wiki/JSON) ), together with the selected columns.

![Search results export](images/screenshots/search_export.png "Search results export")

###Substances search

[Quick user guide (substance search)](usage_substance.html)	 
 