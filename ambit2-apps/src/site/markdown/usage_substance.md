
#Quick user guide (Substance search)

---

## Menu: Search

###Search substances by identifiers

While the [**Search/Search structures and associated data**](usage.html) allows to search for chemical structures and retrieve associated substances and data; the next two menu items allow to search for chemical substances by various criteria.

<a name="composition"/>The **Search/Search substances by identifiers** menu allows to search substances by UUID,  names, external identfiiers and various criteria. The result is a list of chemical substances, composition and other details can be displayed through the folder icon.

![Search substances by name](images/screenshots/substance_search_name.png "Search substances by name")

<a name="substance"/>Clicking on the *Substance UUID* link leads to a substance page, displaying substance identifiers, composition, physicochemical, ecotoxicological, environmental date and toxicological properties. 

![Substance display](images/screenshots/substance_formaldehyde.png "Substance display")

Clicking on the *Owner* link leads to a page, showing all substances by the selected owner.

![Substances by owner](images/screenshots/substance_byowner.png "Substances by owner")

---

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

## Menu: Search

###Search structures and associated data

[Quick user guide](usage.html)	

## Menu: Import
 
[Quick user guide (Datasets)](usage_dataset.html)	   

---

**Chemical substance**, a material with a definite chemical composition. 

The [REACH Guide]( http://echa.europa.eu/documents/10162/13643/nutshell_guidance_substance_en.pdf) defines *Mono-constituent* (a substance with one main constituent) and  *Multi-constituent*  substances (a substance with two or more main constituents). 

- The *Main constituent* is defined as a constituent, not being an *additive* or *impurity*, in a substance that makes up a significant part of that substance. Contributes to the naming of the substance. Concentration of the main constituent(s) is equal to the purity of the substance. 

- An *Additive* is a substance that has been intentionally added to stabilise the substance. Contributes to the substance composition (but not to the naming). 

- An *Impurity* is an unintended constituent present in a substance, as produced. Does not contribute to the naming of the substance.

**Supported Endpoint study records / [IUCLID5.5. XML Schema)](http://iuclid.eu/index.php?fuseaction=home.format)**

<table>
<tr><td>4.1.   </td><td>Appearance GI_GENERAL_INFORM</td>
</tr><tr><td>4.2.   </td><td>Melting point / freezing point PC_MELTING</td>
</tr><tr><td>4.3.	</td><td>Boiling point	PC_BOILING</td>

</tr><tr><td>4.5.	</td><td>Particle size distribution (Granulometry)	PC_GRANULOMETRY</td>

</tr><tr><td>4.6.	</td><td>Vapour pressure	PC_VAPOUR</td>
</tr><tr><td>4.7.	</td><td>Partition coefficient	PC_PARTITION</td>
</tr><tr><td>4.8.	</td><td>Water solubility	PC_WATER_SOL</td>
</tr><tr><td>4.9.	</td><td>Solubility in organic solvents	PC_SOL_ORGANIC</td>
</tr><tr><td>4.20.	</td><td>pH	PC_NON_SATURATED_PH</td>
</tr><tr><td>4.21.	</td><td>Dissociation constant	PC_DISSOCIATION</td>

</tr><tr><td>4.24.	</td><td>Agglomeration/aggregation	AGGLOMERATION_AGGREGATION</td>	
</tr><tr><td>4.25.	</td><td>Crystalline phase	CRYSTALLINE_PHASE</td>
</tr><tr><td>4.26.	</td><td>Crystallite and grain phase	CRYSTALLITE_AND_GRAIN_SIZE</td>	
</tr><tr><td>4.27.	</td><td>Aspect ratio/shape	ASPECT_RATIO_SHAPE</td>	
</tr><tr><td>4.28.	</td><td>Specific surface area	SPECIFIC_SURFACE_AREA</td>	
</tr><tr><td>4.29.	</td><td>Zeta potential	ZETA_POTENTIAL</td>	
</tr><tr><td>4.30.	</td><td>Surface chemistry	SURFACE_CHEMISTRY</td>	
</tr><tr><td>4.31.	</td><td>Dustiness	DUSTINESS</td>	
</tr><tr><td>4.32.	</td><td>Porosity	POROSITY</td>	
</tr><tr><td>4.33.	</td><td>Nanomaterial pour density	POUR_DENSITY</td>	
</tr><tr><td>4.34.	</td><td>Nanomaterial photocatalytic activity	PHOTOCATALYTIC_ACTIVITY</td>	
</tr><tr><td>4.36.	</td><td>Nanomaterial catalytic activity	CATALYTIC_ACTIVITY</td>	


</tr><tr><td>5.1.1.	</td><td>Phototransformation in Air	TO_PHOTOTRANS_AIR</td>
</tr><tr><td>5.1.2.	</td><td>Hydrolysis	TO_HYDROLYSIS</td>
</tr><tr><td>5.2.1.	</td><td>Biodegradation in water - screening tests	TO_BIODEG_WATER_SCREEN</td>
</tr><tr><td>5.2.2.	</td><td>Biodegradation in water and sediment: simulation tests	TO_BIODEG_WATER_SIM</td>
</tr><tr><td>5.2.3.	</td><td>Biodegradation in Soil	EN_STABILITY_IN_SOIL</td>
</tr><tr><td>5.3.1.	</td><td>Bioaccumulation: aquatic / sediment	EN_BIOACCUMULATION</td>
</tr><tr><td>5.3.2.	</td><td>Bioaccumulation: terrestrial	EN_BIOACCU_TERR</td>
</tr><tr><td>5.4.1.	</td><td>Adsorption / Desorption	EN_ADSORPTION</td>
</tr><tr><td>5.4.2.	</td><td>Henry's Law constant	EN_HENRY_LAW</td>

</tr><tr><td>6.1.1.	</td><td>Short-term toxicity to fish	EC_FISHTOX</td>
</tr><tr><td>6.1.2.	</td><td>Long-term toxicity to fish	EC_CHRONFISHTOX</td>
</tr><tr><td>6.1.3.	</td><td>Short-term toxicity to aquatic inverterbrates	EC_DAPHNIATOX</td>
</tr><tr><td>6.1.4.	</td><td>Long-term toxicity to aquatic inverterbrates	EC_CHRONDAPHNIATOX</td>
</tr><tr><td>6.1.5.	</td><td>Toxicity to aquatic algae and cyanobacteria	EC_ALGAETOX</td>
</tr><tr><td>6.1.7.	</td><td>Toxicity to microorganisms	EC_BACTOX</td>
</tr><tr><td>6.2.	</td><td>Sediment toxicity	EC_SEDIMENTDWELLINGTOX</td>
</tr><tr><td>6.3.1.	</td><td>Toxicity to soil macroorganisms	EC_SOILDWELLINGTOX</td>
</tr><tr><td>6.3.2.	</td><td>Toxicity to terrestrial arthropods	EC_HONEYBEESTOX</td>
</tr><tr><td>6.3.3.	</td><td>Toxicity to terrestrial plants	EC_PLANTTOX</td>
</tr><tr><td>6.3.4.	</td><td>Toxicity to soil microorganisms	EC_SOIL_MICRO_TOX</td>

</tr><tr><td>7.2.1.	</td><td>Acute toxicity - oral	TO_ACUTE_ORAL</td>
</tr><tr><td>7.2.2.	</td><td>Acute toxicity - inhalation	TO_ACUTE_INHAL</td>
</tr><tr><td>7.2.3.	</td><td>Acute toxicity - dermal	TO_ACUTE_DERMAL</td>
</tr><tr><td>7.3.1.	</td><td>Skin irritation / Corrosion	TO_SKIN_IRRITATION</td>
</tr><tr><td>7.3.2.	</td><td>Eye irritation	TO_EYE_IRRITATION</td>
</tr><tr><td>7.4.1.	</td><td>Skin sensitisation	TO_SENSITIZATION</td>
</tr><tr><td>7.5.1.	</td><td>Repeated dose toxicity - oral	TO_REPEATED_ORAL</td>
</tr><tr><td>7.5.2.	</td><td>Repeated dose toxicity - inhalation	TO_REPEATED_INHAL</td>
</tr><tr><td>7.5.3.	</td><td>Repeated dose toxicity - dermal	TO_REPEATED_DERMAL</td>
</tr><tr><td>7.6.1.	</td><td>Genetic toxicity in vitro	TO_GENETIC_IN_VITRO</td>
</tr><tr><td>7.6.1.	</td><td>Genetic toxicity in vivo	TO_GENETIC_IN_VIVO</td>
</tr><tr><td>7.7.	</td><td>Carcinogenicity	TO_CARCINOGENICITY</td>
</tr><tr><td>7.8.1.	</td><td>Toxicity to reproduction	TO_REPRODUCTION</td>
</tr><tr><td>7.8.2.	</td><td>Developmental toxicity / teratogenicity	TO_DEVELOPMENTAL</td>


</tr>
</table>


---
