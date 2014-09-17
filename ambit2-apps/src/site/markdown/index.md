

![Substructure search](images/screenshots/search_substructure_2.png "AMBIT Substructure search")

---

## Web services

AMBIT offers chemoinformatics functionality via [*OpenTox API*](api.html) compliant [*REST*](rest.html) web services. 

- Chemical compounds and data storage in [*MySQL*](http://mysql.com) database; 

- Substructure, similarity and other queries ([*The CDK*](http://cdk.sf.net); [*AMBIT SMARTS, SMIRKS, tautomers*](pubs_citeambit.html));

- Descriptor calculation and predictive models building ([*Weka*](http://www.cs.waikato.ac.nz/ml/weka/), [*Toxtree*](http://toxtree.sf.net)).

## Libraries

AMBIT consists of multiple [*Maven modules*](http://maven.apache.org/) with well defined dependencies, which can be used in a stand alone mode or embedded in other applications.
   
- [Download AMBIT libraries](./download_ambitlibs.html)

## Download

AMBIT REST web services are distributed as web archive (war file) and can be deployed in an [Apache Tomcat](http://tomcat.apache.org/) application server or any other compatible [servlet](http://en.wikipedia.org/wiki/Java_Servlet) container. Download and install your own AMBIT instance :

-   [**Download** web application](./download_ambitrest.html)

-   [**Install** web application](./install_ambitrest.html)

-   [**Import** data](./usage_dataset.html)

---

## Quick Start

Try the public web services, datasets and models : 

-   [Datasets](http://apps.ideaconsult.net:8080/data/dataset?pagesize=100) : [**ONS Melting point 33**](http://apps.ideaconsult.net:8080/data/ui/_dataset?dataset_uri=http%3A%2F%2Fapps.ideaconsult.net%3A8080%2Fdata%2Fdataset%2F45) | [**CPDBAS**](http://apps.ideaconsult.net:8080/data/ui/_dataset?dataset_uri=http%3A%2F%2Fapps.ideaconsult.net%3A8080%2Fdata%2Fdataset%2F10) | [**Tox21**](http://apps.ideaconsult.net:8080/data/ui/_dataset?dataset_uri=http%3A%2F%2Fapps.ideaconsult.net%3A8080%2Fdata%2Fdataset%2F36) | [**Inventory of Cosmetic Ingredients**](http://apps.ideaconsult.net:8080/data/ui/_dataset?dataset_uri=http%3A%2F%2Fapps.ideaconsult.net%3A8080%2Fdata%2Fdataset%2F1) | **WikiPathways structure browser**: at [*GitHub*](http://ideaconsult.github.io/Toxtree.js) and at [*BiGCaT Maastricht University*](http://www.bigcat.unimaas.nl/~egonw/wpm/) |  [Bioconcentration factor **(BCF) Gold Standard Database**](http://ambit.sourceforge.net/euras/)
     
-   Search: [**Structure search**](http://apps.ideaconsult.net:8080/data/ui/_search) | [**Substances**](http://apps.ideaconsult.net:8080/data/substances) | [**Nanomaterials**](http://apps.ideaconsult.net:8080/enanomapper/substances) 
   
-   Models: [**Toxtree online**](http://apps.ideaconsult.net:8080/data/ui/toxtree)

Source code examples

-   Examples how to use: [**ambit2-tautomer** package](https://github.com/ideaconsult/examples-ambit/tree/master/tautomers-example) | [**ambit2-SMIRKS** implementation](https://github.com/ideaconsult/examples-ambit/tree/master/smirks-example) | [**applicability domain methods** in AMBIT](https://github.com/ideaconsult/examples-ambit/tree/master/appdomain-example)

Browse and try AMBIT REST API

-   [**API-Docs**](http://ideaconsult.github.io/examples-ambit/apidocs/)	

User guide

-   [**Main features at a glance**](intro.html)

-   Quick user guides: [**Structure search**](usage.html) | [**Substance search**](usage_substance.html) | [**Datasets**](usage_dataset.html)

---

### About

Acknowledgements

>[**CEFIC LRI**](http://www.cefic-lri.org/)

>[**FP7 OpenTox**](http://opentox.org/)

Related pages

> [Toxtree](http://toxtree.sf.net/) | [Toxmatch](http://toxmatch.sf.net/) |  [(Q)MRF](http://qmrf.sf.net/) | 
 [AMBIT/OpenTox API client library](https://github.com/ideaconsult/opentox-cli) | [OpenTox AA client library](https://github.com/vedina/opentox-aa-cli) | 
 [jToxKit](https://github.com/ideaconsult/Toxtree.js) | [I5 library](https://github.com/ideaconsult/i5) | [ToxPredict](http://toxpredict.org) |
 [FP7 ToxBank](http://toxbank.net) | [FP7 eNanoMapper](http://enanomapper.net)

**Social**

[![Twitter](./images/twitter.png)](https://twitter.com/10705013)  [![AMBIT on Google+](./images/googleplus.png)](https://plus.google.com/116849658963631645389/posts)

