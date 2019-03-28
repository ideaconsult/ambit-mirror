openapi: 3.0.0
<#include "/apidocs2/profile/${menu_profile}/info.ftl" >

servers:
  - url: ${ambit_root}

paths:
<#include "/apidocs2/investigation.ftl" >

<#include "/apidocs2/substance.ftl" >

<#include "/apidocs2/query_substance.ftl" >

<#include "/apidocs2/query.ftl" >

<#include "/apidocs2/query_compound.ftl" >



<#include "/apidocs2/solr.ftl" >

components:
  schemas:
<#include "/apidocs2/schema_investigation.ftl" >

<#include "/apidocs2/schema_substance.ftl" >

<#include "/apidocs2/schema_solr.ftl" >

<#include "/apidocs2/schema_dataset.ftl" >

  requestBodies:
  
<#include "/apidocs2/request_body_solr.ftl" >

tags:          
  - name: Chemical structures
    description: Chemical structures
    externalDocs:
      url: https://github.com/ideaconsult/apps-ambit/blob/master/ambit-json-docs/      
  - name: Substances
    description: Chemical substances and (nano)materials
    externalDocs:
      url: https://github.com/ideaconsult/apps-ambit/blob/master/ambit-json-docs/substance.md
  - name: Studies
    description: Measurements or calculations attached to a substance
    externalDocs:
      url: https://github.com/ideaconsult/apps-ambit/blob/master/ambit-json-docs/substance.md#study      
  - name: Data analysis
    description: Machine learning algorithms and models, property prediction models
    externalDocs:
      url: https://github.com/ideaconsult/apps-ambit/blob/master/ambit-json-docs
  - name: Facets
    description: Summaries
    externalDocs:
      url: https://github.com/ideaconsult/apps-ambit/blob/master/ambit-json-docs          
  - name: Task service
    description: Asynchronous jobs
    externalDocs:
      url: https://github.com/ideaconsult/apps-ambit/blob/master/ambit-json-docs
  - name: default
    description: Free text and faceted search
        