#AMBIT Application Programming Interface (API)

## AMBIT API extensions

[AMBIT]((http://ambit.sf.net)) implements [OpenTox API](./api_opentox.html) as described in [doi:10.1186/1758-2946-3-18](http://www.jcheminf.com/content/3/1/18).

Since the 2011 publication a number of enhancements were implemented, including [JSON](http://www.json.org/) serialisation of all REST resources and support for Substances and experimental data.  

### Interactive API Documentation by Swagger-UI 
* [AMBIT API documentation](http://ideaconsult.github.io/examples-ambit/apidocs/)

### [How to](https://github.com/ideaconsult/examples-ambit/tree/master/ambit-json-docs) 
* How to access [Datasets](https://github.com/ideaconsult/examples-ambit/blob/master/ambit-json-docs/dataset.md)
* How to [search chemical compounds](https://github.com/ideaconsult/examples-ambit/blob/master/ambit-json-docs/query.md)
* How to access [Algorithms](https://github.com/ideaconsult/examples-ambit/blob/master/ambit-json-docs/algorithm.md) 
* How to access [Models](https://github.com/ideaconsult/examples-ambit/blob/master/ambit-json-docs/model.md)
* How to access [Substances](https://github.com/ideaconsult/examples-ambit/blob/master/ambit-json-docs/substance.md)
* How to access [Bundles (of substances and studies)](https://github.com/ideaconsult/examples-ambit/blob/master/ambit-json-docs/bundle.md)
* How to [run Toxtree predictions](https://github.com/ideaconsult/examples-ambit/blob/master/ambit-json-docs/toxtree.md) using REST web services and JSON.
* The JSON or [JSONP](https://en.wikipedia.org/wiki/JSONP) representation could be retrieved via HTTP Accept headers **"application/json"** or **"application/x-javascript"** respectively. The callback parameter when using JSONP is 'callback'.* As a workaround for web browsers restriction, the URI parameter **?media=application/json** or **?media=application/x-javascript** could be used.

### Examples  
* Try [Toxtree web edition](http://toxtree.sf.net/predict)
* Nanomaterials support, most recently published in [doi:10.3762/bjnano.6.165](http://www.beilstein-journals.org/bjnano/single/articleFullText.htm?publicId=2190-4286-6-165).

##OpenTox REST API

The [OpenTox API](./api_opentox.html) mandatory representation of REST resources is [RDF](http://www.w3.org/RDF/) (W3C Resource Description Framework). 
Examples: [1](http://opentox.org/dev/apis/api-1.2/dataset),[2](http://ambit.sourceforge.net/api_dataset.html).

* API Documentation http://opentox.org/dev/apis/api-1.2 
* OpenTox API discussion, new ideas and examples at https://github.com/opentox-api

[OpenTox](http://opentox.org) is an European Commission Framework Program 7 funded project (2008-2011), 
aims to develop distributed framework for predictive toxicology. 
The building blocks considered are : data, chemical structures, algorithms and models. 
The framework allows to build models, apply models, validate models, access and query data in various ways.
Technologies used are [REST](http://ambit.sourceforge.net/rest.html) style web services and [W3C Resource Description Framework](http://ambit.sourceforge.net/rdf.html) for description of services.


