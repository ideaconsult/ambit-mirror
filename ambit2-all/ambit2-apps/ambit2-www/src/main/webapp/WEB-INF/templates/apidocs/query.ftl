{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json",
        "text/n3",
        "application/rdf+xml",
        "text/uri-list"
    ],		
    "resourcePath": "/query",
	"apis": [
     	{
            "path": "/query/study",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Search endpoint summary",
                    "notes": "Returns endpoint summary",
                    "type": "Facet",
                    "nickname": "getEndpointSummary",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
						{
							    "name": "topcategory",
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
							    "defaultValue" :"",
							    <#include "/apidocs/parameter_endpointcategory_enum.ftl" >
						},                    
			            {
				              "name": "filterbybundle",
				              "description": "Bundle URI",
				              "required": false,
				              "type": "string",
				              "paramType": "query",
				              "defaultValue": "${ambit_root}/bundle/1",
				              "allowMultiple"  : false
				        },			     
			            {
				              "name": "selected",
				              "description": "Applicable if filterbybundle is present",
				              "required": false,
				              "type": "string",
				              "paramType": "query",
				              "defaultValue": "endpoints",
				              "enum" : ["endpoints","substances"],
				              "allowMultiple"  : false
				        },					               
						<#include "/apidocs/parameters_page.ftl" >				            			            
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Not found"
                        },
						<#include "/apidocs/error_aa.ftl" >,
						<#include "/apidocs/error_500.ftl" >                            
                    ]
                }
            ]
     	},
     	{
            "path": "/query/type_strucs",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Structure type statistics",
                    "notes": "Returns structure type statistics (SMILES, 2D, 3D, etc)",
                    "type": "Facet",
                    "nickname": "getStrucTypeSummary",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
						<#include "/apidocs/parameters_page.ftl" >				            			            
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Not found"
                        },
						<#include "/apidocs/error_aa.ftl" >,
						<#include "/apidocs/error_500.ftl" >                            
                    ]
                }
            ]
     	},
     	{
            "path": "/query/label_compounds",
            "operations": [
                {
                    "method": "GET",
                    "summary": "QA labels statistics",
                    "notes": "Statistics of compound quality labels",
                    "type": "Facet",
                    "nickname": "getQALabelCompoundSummary",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
						<#include "/apidocs/parameters_page.ftl" >				            			            
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Not found"
                        },
						<#include "/apidocs/error_aa.ftl" >,
						<#include "/apidocs/error_500.ftl" >                            
                    ]
                }
            ]
     	},     	
     	{
            "path": "/query/label_strucs",
            "operations": [
                {
                    "method": "GET",
                    "summary": "QA labels statistics (for structures)",
                    "notes": "Statistics of structures quality labels (OK, ProbablyOK, ProbablyERROR, Error, Unknown)",
                    "type": "Facet",
                    "nickname": "getLabelStrucsSummary",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
						<#include "/apidocs/parameters_page.ftl" >				            			            
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Not found"
                        },
						<#include "/apidocs/error_aa.ftl" >,
						<#include "/apidocs/error_500.ftl" >                            
                    ]
                }
            ]
     	},   
     	{
            "path": "/query/consensuslabel",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Compounds with specified consensus label",
                    "notes": "Statistics of structures quality labels (OK, ProbablyOK, ProbablyERROR, Error, Unknown)",
                    "type": "Dataset",
                    "nickname": "getConsensusLabel",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
                        {
            			   "name": "search",
            			   "description": "Consensus label",
            			   "required": false,
            			   "type": "string",
            			   "paramType": "query",
            			   "allowMultiple": false,
            			   "defaultValue" : "Consensus",
            			   "enum" : ["Consensus","Majority","Unconfirmed","Ambiguous","Unknown"]
            			},                                        
						<#include "/apidocs/parameters_page.ftl" >				            			            
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Not found"
                        },
						<#include "/apidocs/error_aa.ftl" >,
						<#include "/apidocs/error_500.ftl" >                            
                    ]
                }
            ]
     	},     
     	{
            "path": "/query/ncompound_value",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Number of distinct values per feature in a dataset",
                    "notes": "Number of distinct values per feature in a dataset",
                    "type": "Facet",
                    "nickname": "getNCompoundValue",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
                        {
            			   "name": "feature_uris[]",
            			   "description": "Feature URI",
            			   "required": true,
            			   "type": "string",
            			   "paramType": "query",
            			   "allowMultiple": false,
            			   "defaultValue" : "${ambit_root}/feature/1"
            			},            
                        {
             			   "name": "dataset_uri",
             			   "description": "Dataset URI",
             			   "required": true,
             			   "type": "string",
             			   "paramType": "query",
             			   "allowMultiple": false,
             			   "defaultValue" : "${ambit_root}/dataset/1"
             			},                  			
						<#include "/apidocs/parameters_page.ftl" >				            			            
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Not found"
                        },
						<#include "/apidocs/error_aa.ftl" >,
						<#include "/apidocs/error_500.ftl" >                            
                    ]
                }
            ]
     	},         	
     	{
            "path": "/query/ndatasets_endpoint",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Number of datasets containing specific endpoint data",
                    "notes": "Number of datasets containing specific endpoint data",
                    "type": "Facet",
                    "nickname": "getNDatasetEndpoint",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
                        {
            			   "name": "feature_uris[]",
            			   "description": "Feature URI",
            			   "required": true,
            			   "type": "string",
            			   "paramType": "query",
            			   "allowMultiple": false,
            			   "defaultValue" : "${ambit_root}/feature/1"
            			},            
                        {
             			   "name": "dataset_uri",
             			   "description": "Dataset URI",
             			   "required": true,
             			   "type": "string",
             			   "paramType": "query",
             			   "allowMultiple": false,
             			   "defaultValue" : "${ambit_root}/dataset/1"
             			},                  			
						<#include "/apidocs/parameters_page.ftl" >				            			            
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Not found"
                        },
						<#include "/apidocs/error_aa.ftl" >,
						<#include "/apidocs/error_500.ftl" >                            
                    ]
                }
            ]
     	},     
     	{
            "path": "/query/ndatasets_nameprefix",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Number of datasets with name starting with",
                    "notes": "Number of datasets with name starting with",
                    "type": "Facet",
                    "nickname": "getNDatasetsNamePrefix",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
						<#include "/apidocs/parameters_page.ftl" >				            			            
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Not found"
                        },
						<#include "/apidocs/error_aa.ftl" >,
						<#include "/apidocs/error_500.ftl" >                            
                    ]
                }
            ]
     	}        	
    ],
    "models" : {
    	"DatasetMetadata" : <#include "/apidocs/json_schema_datasetmeta.ftl" >,   
        "Facet"     : <#include "/apidocs/json_schema_facet.ftl" >,
        "StudySummaryFacet"     : <#include "/apidocs/json_schema_studysummaryfacet.ftl" >,
        "Task" : <#include "/apidocs/json_schema_task.ftl" >,
        "Feature" : <#include "/apidocs/json_schema_feature.ftl" >
      },    
	<#include "/apidocs/profile/${menu_profile}/info.ftl" >  
}