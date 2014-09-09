{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json",
        "text/uri-list",
        "image/png",
        "application/x-javascript"
    ],		
    "resourcePath": "/substance",
	"apis": [
			{
			    "path": "/substance",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "List substances",
			            "notes": "Returns a list of substances, according to the search criteria",
			            "type": "Substance",
			            "nickname": "getSubstances",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "search",
							    "description": "Search parameter",
							    "required": false,
							    "type": "string",
							    "paramType": "query",
							    "allowMultiple": false,
							    "defaultValue": "formaldehyde"
							},
							{
							    "name": "type",
							    "description": "Query type",
							    "required": true,
							    "type": "string",
							    "paramType": "query",
							    "allowMultiple": false,
							    "defaultValue": "name",
							    "enum" : [
							       "name",
							       "uuid",
							       "CompTox",
							       "DOI",
							       "reliability",
							       "purposeFlag",
							       "studyResultType",
							       "isRobustStudy",
							       "citation",
							       "topcategory",
							       "endpointcategory",
							       "params",
							       "owner_name",
							       "owner_uuid",
							       "related",
							       "reference",
							       "facet"
							    ]
							},	
							{
							    "name": "compound_uri",
							    "description": "If type=related finds all substances containing this compound; if type=reference - finds all substances with this compound as reference structure",
							    "required": false,
							    "type": "string",
							    "paramType": "query",
							    "allowMultiple": false
							},							
							<#include "/apidocs/parameters_page.ftl" >			            
			            ],
			            "responseMessages": [
			     			{
			     				"code": 200,
			     				 "message": "OK. Substance(s) found"
			     			},				                                 
			                {
			                    "code": 404,
			                    "message": "Substances not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }
			    ]
			},
			{
			    "path": "/substance/{uuid}",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get a substance",
			            "notes": "Returns substance representation",
			            "type": "Substance",
			            "nickname": "getSubstanceByUUID",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
			                <#include "/apidocs/parameter_substance_uuid.ftl" >
			                ,
							{
							    "name": "property_uris[]",
							    "description": "Property URIs",
							    "required": false,
							    "type": "string",
							    "paramType": "query",
							    "allowMultiple": true
							}							
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid substance identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Substance not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }
			    ]
			},
			{
			    "path": "/substance/{uuid}/studysummary",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get study summary for the substance",
			            "notes": "Study summary",
			            "type": "StudySummary",
			            "nickname": "getSubstanceStudySummary",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
			                  <#include "/apidocs/parameter_substance_uuid.ftl" >,
								{
								    "name": "top",
								    "description": "Top endpoint category",
								    "required": false,
								    "type": "string",
								    "paramType": "query",
								    "allowMultiple": false,
								    "enum" : ["P-CHEM","ENV FATE","ECOTOX","TOX"]
								},
								{
								    "name": "category",
								    "description": "Endpoint category (The value in the facet.category.title field)",
								    "required": false,
								    "type": "string",
								    "paramType": "query",
								    "allowMultiple": false,
								    "defaultValue" :""
								},
								{
								    "name": "property",
								    "description": "Property UUID,  see Property service",
								    "required": false,
								    "type": "string",
								    "paramType": "query",
								    "allowMultiple": false
								},
								{
								    "name": "property_uri",
								    "description": "Property URI ${ambit_root}/property/{UUID} , see Property service",
								    "required": false,
								    "type": "string",
								    "paramType": "query",
								    "allowMultiple": false
								}			                  
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid substance identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Substance not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }
			    ]
			},
			{
			    "path": "/substance/{uuid}/study",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get substance study",
			            "notes": "Substance study",
			            "type": "SubstanceStudy",
			            "nickname": "getSubstanceStudy",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
			                  <#include "/apidocs/parameter_substance_uuid.ftl" >,
								{
								    "name": "top",
								    "description": "Top endpoint category",
								    "required": false,
								    "type": "string",
								    "paramType": "query",
								    "allowMultiple": false,
								    "enum" : ["P-CHEM","ENV FATE","ECOTOX","TOX"]
								},
								{
								    "name": "category",
								    "description": "Endpoint category (The value in the protocol.category.code field)",
								    "required": false,
								    "type": "string",
								    "paramType": "query",
								    "allowMultiple": false,
								    "defaultValue" :""
								},
								{
								    "name": "property",
								    "description": "Property UUID",
								    "required": false,
								    "type": "string",
								    "paramType": "query",
								    "allowMultiple": false
								},
								{
								    "name": "property_uri",
								    "description": "Property URI ${ambit_root}/property/{UUID} , see Property service",
								    "required": false,
								    "type": "string",
								    "paramType": "query",
								    "allowMultiple": false
								},
								<#include "/apidocs/parameters_page.ftl" >			            
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid substance identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Substance not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }
			    ]
			},			
			{
			    "path": "/substance/{uuid}/composition",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get substance composition",
			            "notes": "Substance composition",
			            "type": "SubstanceComposition",
			            "nickname": "getSubstanceComposition",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
			                  <#include "/apidocs/parameter_substance_uuid.ftl" >,
							  <#include "/apidocs/parameters_page.ftl" >			            
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid substance identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Substance not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }
			    ]
			},
			{
			    "path": "/substance/{uuid}/structures",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get substance composition as a dataset",
			            "notes": "Substance composition",
			            "type": "Dataset",
			            "nickname": "getSubstanceComposition",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
			                  <#include "/apidocs/parameter_substance_uuid.ftl" >,
  							  <#include "/apidocs/parameters_page.ftl" >			            
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid substance identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Substance not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }
			    ]
			}
				
    ],
	<#include "/apidocs/info.ftl" >  
}