#Quick user guide (Substance search)

## Menu: Search

###Search substances by identifiers

While the [**Search/Search structures and associated data**](usage.html) allows to search for chemical structures and retrieve associated substances and data; the next two menu items allow to search for chemical substances by various criteria. 

The **Search/Search substances by identifiers** menu allows to search substances by UUID,  names, external identfiiers and various criteria. The result is a list of chemical substances, composition and other details can be displayed through the folder icon.

![Search substances by UUID](images/screenshots/substance_search_uuid.png "Search substances by UUID")

Clicking on the *Substance UUID* link leads to the substance page, displaying substance identifiers, composition, physicochemical, ecotoxicological, environmental date and toxicological properties. 

![Substance display](images/screenshots/substance_formaldehyde.png "Substance display")

Clicking on the *Owner* link leads to a page, showing all substances by the selected owner.

![Substances by owner](images/screenshots/substance_byowner.png "Substances by owner")
 
###Search substances by endpoint data

The **Search/Search substances by endpoint data** menu allows to search substances by endpoint data. 

The available endpoints are listed on the left, grouped in four categories (*P-Chem*, *Env Fate*, *Eco Tox*, *Tox*).  Check one or more checkboxes (can be from several categories) and click the **Update results** button to show the substances, having the selected type of data. 
The endpoints are combined by *AND*. The screenshot below shows there are only two substances having data for the three selected endpoints (*Appearance*, *Melting point* and *Dissociation constant*), although there are 16 substances with data for appearance, 36 substances with melting point values and 15 substances with dissociation constant (the first figure in the brackets is the number of substances, the figure in the square brackets is the number of values).

![Search substances by endpoint data](images/screenshots/search_endpoints.png "Search substances by endpoint data")

The folder icon at each endpoint row allows to set detailed search criteria for each endpoint. The following four screenshots show the available search criteria for phys chem properties, environmental fate endpoints, ecotox and toxicological endpoints..

![P-Chem endpoints search criteria expanded](images/screenshots/endpoint_pchem_search.png "P-Chem endpoints search criteria expanded")

P-Chem endpoints search criteria expanded

![ECO TOX endpoints search criteria expanded](images/screenshots/endpoint_ecotox_search.png "ECO TOX endpoints search criteria expanded")

ECO TOX endpoints search criteria expanded

![Env Fate endpoints search criteria expanded](images/screenshots/endpoint_env_fate_search.png "Env Fate endpoints search criteria expanded")

Env Fate endpoints search criteria expanded

![TOX endpoints search criteria expanded](images/screenshots/endpoint_tox_search.png "TOX endpoints search criteria expanded")

TOX endpoints search criteria expanded

All text boxes support autocompletion, i.e. the available values will be displayed and can be selected by either pressing an arrow down button (to list all available values) or by entering the first letters of a possible value.

![Env. Autocomplete (biodegradation parameter)](images/screenshots/envfate_autocomplete.png "Env. Autocomplete (biodegradation parameter)")

Env. Autocomplete (biodegradation parameter). The number in the brackets is the number of values available.
 
>Note: when using endpoint selection criteria, the endpoint checkboxes should be checked; otherwise the endpoint will be ignored. Once the criteria are filled in, press **Update results** button. The list of substances will appear similar to the substances list described above. 

## Menu: Import
 
[Quick user guide (datasets)](usage_dataset.html)	  
