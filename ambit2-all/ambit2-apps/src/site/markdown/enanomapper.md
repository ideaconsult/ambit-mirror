#### [eNanoMapper database][frontpage]

![Endpoint search](./images/enanomapper/screenshots/frontpage.png "enanoMapper database front page")

eNanoMapper database  a substance database for nanomaterial safety information

#### [Data collections][data_collection]

![Data collections](./images/enanomapper/screenshots/bundles.png "Data collections")

Data collections

#### [Endpoint search][endpoint_search]

![Endpoint search](./images/enanomapper/screenshots/search_size.png "Search nanomaterials by size")

Search nanomaterials by physchem characterisation or biological effects

#### [Nanomaterials][physchem]

![Nanomaterial physicochemical characterisaion](./images/enanomapper/screenshots/nano-pchem.png "Nanomaterial physicochemical characterisaion")

Nanomaterial physicochemical characterisaion

#### [Nanomaterial studies][bundle]

![Nanomaterial studies](./images/enanomapper/screenshots/pcorona-dataset.png "Protein corona")

Nanomaterial studies (Protein corona)

#### [Free text search][text_search]

![Free text search](./images/enanomapper/screenshots/search.png "Free text search")

Free text search on eNanoMapper and cananoLab databases

[frontpage]: enanomapper.html
[endpoint_search]: enanomapper_usage.html
[physchem]: enanomapper_usage_substance.html
[bundle]: enanomapper_usage_substance.html
[text_search]: enanomapper.html
[data_collection]: enanomapper.html

---

## The [eNanoMapper prototype database](http://data.enanomapper.net/)

is part of the computational infrastructure for toxicological data management
 of engineered nanomaterials, developed within the [EU FP7 eNanoMapper](http://www.enanomapper.net) project. 
 
* Provides support for upload, search and retrieval of nanomaterials and experimental data through a REST web services [API](http://enanomapper.github.io/API/) and a web browser interface. 
 
* Implemented by a customized version of [AMBIT](./index.html) web services. 

## [Download and install](./enanomapper_install.html)

The eNanoMapper prototype database is an open source web application, which can be [downloaded, 
 installed]((./enanomapper_install.html)) and hosted by individual researchers or labs, and as such presents an open distributed platform for nanomaterials data management.
 
  [**Docker image**](http://projects.bigcat.unimaas.nl/download/enanomapper.tar)
  
  [**Web application (.war)**](https://sourceforge.net/projects/ambit/files/Ambit2/AMBIT%20REST%20web%20services/custom%20releases/enanomapper)
  
  [**Install guide**](./enanomapper_install.html)
  
  [**Feedback**](https://github.com/enanomapper/data.enanomapper.net/issues)
 
## [Publications](http://www.beilstein-journals.org/bjnano/content/6/1/165)

* N. Jeliazkova et al., The eNanoMapper database for nanomaterial safety information, Beilstein J. Nanotechnol., vol. 6, pp. 1609–1634, Jul. 2015. [10.3762/bjnano.6.165](http://www.beilstein-journals.org/bjnano/content/6/1/165)

* N. Jeliazkova et al., The first eNanoMapper prototype: A substance database to support safe-by-design, in 2014 IEEE International Conference on Bioinformatics and Biomedicine (BIBM), 2014, pp. 1–9. [doi:10.1109/BIBM.2014.6999367](http://ieeexplore.ieee.org/lpdocs/epic03/wrapper.htm?arnumber=6999367)

* N. Kochev, R. Grafstrom, N. Jeliazkova How to store nanomaterial safety data: meet eNanoMapper database, [poster](http://f1000research.com/posters/4-870) at [SENN 2015](http://www.ttl.fi/partner/senn2015/programme/pages/default.aspx)
 
---

## Quick Start

Try the public web services, datasets and models : 

-   <form action="http://search.data.enanomapper.net" method="GET"><input type="text" id="search" name="search" value="zinc oxide"/><input type='submit' value='Search'/></form>

-   [**Search nanomaterials by identifier**](https://apps.ideaconsult.net/enanomapper/substance?search=NM-111%26)

-   [**Search nanomaterials by citation**](https://apps.ideaconsult.net/enanomapper/substance?type=citation%26search=10.1073)

-   [**Search nanomaterials by physchem characterisation or biological effects**](https://apps.ideaconsult.net/enanomapper/query/study)

-   [**Search nanomaterials by composition**](https://apps.ideaconsult.net/enanomapper/ui/_search?search=SiO2)

-   [**Browse nanomaterials and studies**](https://apps.ideaconsult.net/enanomapper/substance)

-   [**Free text search**](https://search.data.enanomapper.net)

Browse and try eNanoMapper database REST API

-   [**API-Docs**](http://enanomapper.github.io/API/)	

---

### About

Acknowledgements

>[**CEFIC LRI**](http://www.cefic-lri.org/)

>[**FP7 OpenTox**](http://opentox.org/)

>[**FP7 eNanoMapper**](http://enanomapper.net)

Related pages

> [Toxtree](http://toxtree.sf.net/) | [Toxmatch](http://toxmatch.sf.net/) |  [(Q)MRF](http://qmrf.sf.net/) | 
 [AMBIT/OpenTox API client library](https://github.com/ideaconsult/opentox-cli) | [OpenTox AA client library](https://github.com/vedina/opentox-aa-cli) | 
 [jToxKit](https://github.com/ideaconsult/Toxtree.js) | [I5 library](https://github.com/ideaconsult/i5) | [ToxPredict](http://toxpredict.org) |
 [FP7 ToxBank](http://toxbank.net)

**Social**

[![Twitter](./images/twitter.png)](https://twitter.com/enanomapper)  [![AMBIT on Google+](./images/googleplus.png)](https://plus.google.com/116849658963631645389/posts)

