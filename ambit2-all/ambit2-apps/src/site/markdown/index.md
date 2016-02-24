#### [Structure search][smarts]

![Structure search](images/screenshots/search_substructure_2.png "AMBIT Substructure search")

Chemical structure search (exact structure, by identifiers, similarity and substructure)

#### [Chemical substances][composition]

![Chemical substances](images/screenshots/composition.png "Chemical substances composition")

Support for mono-constituent and multi-constituent  substances, including additives and impurities.

#### [Endpoint data][endpoint]

![Endpoint data](images/screenshots/endpoint_repeateddose_oral.png "Repeated dose toxicity")

Endpoint data (support for all REACH endpoints, IUCLID5 import) 

#### [NanoMaterials][nanomaterials]

![NanoMaterials](images/screenshots/mwcnt.png "Multiwalled Carbon Nanotubes")

Nanomaterial physicochemical and biological characterisation 

#### [Read across support][ra]

![Read across support](images/screenshots/ra_matrix.png "AMBIT Read across support")

Read across workflow - select structures, endpoints, create a data matrix.

#### [Toxtree online][toxtree]

![Toxtree online](images/screenshots/toxtree_1.png "Toxtree online")

Toxtree predictions online

[smarts]: usage.html
[endpoint]: usage_substance.html
[composition]: usage_substance.html
[ra]: http://apps.ideaconsult.net/data/ui/assessment
[nanomaterials]: enanomapper.html
[toxtree]: https://apps.ideaconsult.net/data/ui/toxtree

---

## Web services

AMBIT offers chemoinformatics functionality via [*OpenTox API*](api.html) compliant [*REST*](rest.html) web services. 

- Chemical compounds and data storage in [*MySQL*](http://mysql.com) database; 

- Substructure, similarity and other queries ([*The CDK*](http://cdk.sf.net); [*AMBIT SMARTS, SMIRKS, tautomers*](pubs_citeambit.html));

- Descriptor calculation and predictive models building ([*Weka*](http://www.cs.waikato.ac.nz/ml/weka/), [*Toxtree*](http://toxtree.sf.net)).

## News

- LRI Workshop on AMBIT and READ-ACROSS [**Video and slides**](http://cefic-lri.org/events/lri-workshop-on-ambit-and-read-across/)

- Try LRI AMBIT [**Read across workflow**](https://apps.ideaconsult.net/ambit3)  [Feedback](https://docs.google.com/forms/d/1hhKCBTeBTqvfQxW3g2vc_ShNk29hof_WDHtt-cimjGQ/viewform)
 
- [eNanoMapper database](./enanomapper.html) 

 <form action="http://enanomapper.github.io/data.enanomapper.net/" method="GET"><input type="text" id="search" name="search" value="nanotube"/><input type='submit' value='Search'/></form>
 
## Download

AMBIT REST web services are distributed as web archive (war file) and can be deployed in an [Apache Tomcat](http://tomcat.apache.org/) application server or any other compatible [servlet](http://en.wikipedia.org/wiki/Java_Servlet) container. Download and install your own AMBIT instance :

-   [**Download** web application and libraries](./downloads.html)

-   [**Install** web application](./install_ambitrest.html)

-   [**Import** data](./usage_dataset.html)

---

## Quick Start

Try the public web services, datasets and models : 

-   [Datasets](https://apps.ideaconsult.net/data/dataset?pagesize=100) : [**ONS Melting point 33**](https://apps.ideaconsult.net/data/ui/_dataset?dataset_uri=https%3A%2F%2Fapps.ideaconsult.net%2Fdata%2Fdataset%2F45) [<sup>?</sup>](http://precedings.nature.com/documents/6229/version/1) | [**CPDBAS**](https://apps.ideaconsult.net/data/ui/_dataset?dataset_uri=https%3A%2F%2Fapps.ideaconsult.net%2Fdata%2Fdataset%2F10) [<sup>?</sup>](http://www.epa.gov/ncct/dsstox/sdf_cpdbas.html) | [**Tox21**](https://apps.ideaconsult.net/data/ui/_dataset?dataset_uri=https%3A%2F%2Fapps.ideaconsult.net%2Fdata%2Fdataset%2F36) | [**Inventory of Cosmetic Ingredients**](https://apps.ideaconsult.net/data/ui/_dataset?dataset_uri=https%3A%2F%2Fapps.ideaconsult.net%2Fdata%2Fdataset%2F1) | [**WikiPathways structure browser** at *GitHub*](http://ideaconsult.github.io/Toxtree.js) and at [*BiGCaT Maastricht University*](http://www.bigcat.unimaas.nl/~egonw/wpm/) [<sup>?</sup>](http://wikipathways.org/index.php/WikiPathways) |  [Bioconcentration factor **(BCF) Gold Standard Database**](http://ambit.sourceforge.net/euras/) [<sup>?</sup>](http://www.cefic-lri.org/lri-toolbox/bcf)
     
-   Search: [**Structure search**](https://apps.ideaconsult.net/data/ui/_search) | [**Substances**](https://apps.ideaconsult.net/data/substances) | [**Nanomaterials**](https://apps.ideaconsult.net/enanomapper/substances) 
   
-   Models: [**Toxtree online**](https://apps.ideaconsult.net/data/ui/toxtree)

Source code examples

-   Examples how to use: [**ambit2-tautomer** package](https://github.com/ideaconsult/examples-ambit/tree/master/tautomers-example) | [**ambit2-SMIRKS** implementation](https://github.com/ideaconsult/examples-ambit/tree/master/smirks-example) | [**applicability domain methods** in AMBIT](https://github.com/ideaconsult/examples-ambit/tree/master/appdomain-example)

Browse and try AMBIT REST API

-   [**API-Docs**](http://ideaconsult.github.io/examples-ambit/apidocs/)	

User guide

-   [**Main features at a glance**](intro.html)

-   Quick user guides: [**Structure search**](usage.html) | [**Substance search**](usage_substance.html) | [**Datasets**](usage_dataset.html)

## Explore 

Chemical structures, chemical substances and endpoint data in AMBIT    

-   [**Structure search**](https://apps.ideaconsult.net/ambit3/ui/_search)

-   [**Substance online**](https://apps.ideaconsult.net/ambit3/substance)

-   [**Endpoint search**](https://apps.ideaconsult.net/ambit3/query/study)

-   [**Run Toxtree predictions**](https://apps.ideaconsult.net/ambit3/ui/toxtree)

-   [**Provide feedback**](https://docs.google.com/forms/d/1ncsW59uGAJfEUgSv8tvfbiU0XRXIik9RuRJ376WnkB0/viewform) for the [**Read across workflow support** (beta test)](https://apps.ideaconsult.net/ambit3/ui/assessment)   

---

### About

Acknowledgements

>[**CEFIC LRI**](http://www.cefic-lri.org/)

>[**FP7 OpenTox**](http://opentox.org/)

>[**FP7 eNanoMapper**](http://enanomapper.net)

>[**H2020 ExCAPE**](http://cordis.europa.eu/project/rcn/197542_en.html)

Related pages

> [Toxtree](http://toxtree.sf.net/) | [Toxmatch](http://toxmatch.sf.net/) |  [(Q)MRF](http://qmrf.sf.net/) | 
 [AMBIT/OpenTox API client library](https://github.com/ideaconsult/opentox-cli) | [OpenTox AA client library](https://github.com/vedina/opentox-aa-cli) | 
 [jToxKit](https://github.com/ideaconsult/Toxtree.js) | [I5 library](https://github.com/ideaconsult/i5) | [ToxPredict](http://toxpredict.org) |
 [FP7 ToxBank](http://toxbank.net)

**Social**

[![Twitter](./images/twitter.png)](https://twitter.com/10705013)  [![AMBIT on Google+](./images/googleplus.png)](https://plus.google.com/116849658963631645389/posts)

