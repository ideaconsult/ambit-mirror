openapi: 3.0.0
<#include "/apidocs2/profile/${menu_profile}/info.ftl" >

servers:
  - url: ${ambit_root}

paths:
<#include "/apidocs2/investigation.ftl" >
<#include "/apidocs2/solr.ftl" >

components:
  schemas:
<#include "/apidocs2/schema_investigation.ftl" >
<#include "/apidocs2/schema_substance.ftl" >
<#include "/apidocs2/schema_solr.ftl" >