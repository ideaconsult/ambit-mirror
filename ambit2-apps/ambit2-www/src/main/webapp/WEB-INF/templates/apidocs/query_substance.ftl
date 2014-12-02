{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json",
        "text/csv",
        "text/plain"
    ],		
    "resourcePath": "/query",
	"apis": [
     	{
            "path": "/query/substance/study/protocol/{term}",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Search substances by study protocol parameters",
                    "notes": "Search substances by study protocol parameters",
                    "type": "Substance",
                    "nickname": "searchByStudyProtocol",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
			            {
			              "name": "term",
			              "description": "",
			              "required": true,
			              "type": "string",
			              "enum" : ["citation", "guideline", "topcategory","endpointcategory","params"],
			              "defaultValue" : "search",
			              "paramType": "path",
			              "allowMultiple"  : false
			            },      
			            {
			              "name": "search",
			              "description": "Search value",
			              "required": true,
			              "type": "string",
			              "paramType": "query",
			              "allowMultiple"  : false
			            },				            
			            {
				              "name": "bundle_uri",
				              "description": "Retrieves if selected in this bundle",
				              "required": false,
				              "type": "string",
				              "paramType": "query",
				              "defaultValue": "${ambit_root}/bundle/1",
				              "allowMultiple"  : false
				        },					            
						<#include "/apidocs/parameters_page.ftl" >
                    ],
                    "responseMessages": [
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
            "path": "/query/substance/study/experiment/{term}",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Search substances by protocol application parameters",
                    "notes": "Search substances by protocol application parameters",
                    "type": "Substance",
                    "nickname": "searchByStudyProtocolApplication",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
			            {
			              "name": "term",
			              "description": "",
			              "required": true,
			              "type": "string",
			              "enum" : ["name","uuid","studyResultType","purposeFlag","reliability","isRobustStudy","externalid"],
			              "defaultValue" : "name",
			              "paramType": "path",
			              "allowMultiple"  : false
			            },      
			            {
			              "name": "search",
			              "description": "Search value",
			              "required": true,
			              "type": "string",
			              "paramType": "query",
			              "allowMultiple"  : false
			            },				    
			            {
				              "name": "bundle_uri",
				              "description": "Retrieves if selected in this bundle",
				              "required": false,
				              "type": "string",
				              "paramType": "query",
				              "defaultValue": "${ambit_root}/bundle/1",
				              "allowMultiple"  : false
				        },					                    
						<#include "/apidocs/parameters_page.ftl" >
                    ],
                    "responseMessages": [
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
            "path": "/query/substance/study/owner/{term}",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Search substances by study owner",
                    "notes": "Search substances by study owner",
                    "type": "Substance",
                    "nickname": "searchByStudyOwner",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
			            {
			              "name": "term",
			              "description": "",
			              "required": true,
			              "type": "string",
			              "enum" : ["name","uuid"],
			              "defaultValue" : "name",
			              "paramType": "path",
			              "allowMultiple"  : false
			            },      
			            {
			              "name": "search",
			              "description": "Search value",
			              "required": true,
			              "type": "string",
			              "paramType": "query",
			              "allowMultiple"  : false
			            },				  
			            {
				              "name": "bundle_uri",
				              "description": "Retrieves if selected in this bundle",
				              "required": false,
				              "type": "string",
				              "paramType": "query",
				              "defaultValue": "${ambit_root}/bundle/1",
				              "allowMultiple"  : false
				        },					                      
						<#include "/apidocs/parameters_page.ftl" >
                    ],
                    "responseMessages": [
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
            "path": "/query/substance/related",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Search substances by related structures",
                    "notes": "Search substances by related structures",
                    "type": "Substance",
                    "nickname": "searchByRelatedCompound",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
			            {
			              "name": "compound_uri",
			              "description": "Compound URI",
			              "required": false,
			              "type": "string",
			              "paramType": "query",
			              "defaultValue": "${ambit_root}/compound/1",
			              "allowMultiple"  : false
			            },
			            {
				              "name": "bundle_uri",
				              "description": "Retrieves if selected in this bundle",
				              "required": false,
				              "type": "string",
				              "paramType": "query",
				              "defaultValue": "${ambit_root}/bundle/1",
				              "allowMultiple"  : false
				        },			   
			            {
				              "name": "filterbybundle",
				              "description": "Restricts the search within this bundle",
				              "required": false,
				              "type": "string",
				              "paramType": "query",
				              "allowMultiple"  : false
				        },		
				        			                 
						<#include "/apidocs/parameters_page.ftl" >
                    ],
                    "responseMessages": [
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
            "path": "/query/substance/reference",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Search substances by reference structures",
                    "notes": "Search substances by reference structures",
                    "type": "Substance",
                    "nickname": "searchByReferenceCompound",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
			            {
			              "name": "compound_uri",
			              "description": "Compound URI",
			              "required": false,
			              "type": "string",
			              "paramType": "query",
			              "defaultValue": "${ambit_root}/compound/1",
			              "allowMultiple"  : false
			            },
			            {
				              "name": "bundle_uri",
				              "description": "Retrieves if selected in this bundle",
				              "required": false,
				              "type": "string",
				              "paramType": "query",
				              "defaultValue": "${ambit_root}/bundle/1",
				              "allowMultiple"  : false
				        },			   
			            {
				              "name": "filterbybundle",
				              "description": "Restricts the search within this bundle",
				              "required": false,
				              "type": "string",
				              "paramType": "query",
				              "allowMultiple"  : false
				        },            
						<#include "/apidocs/parameters_page.ftl" >
                    ],
                    "responseMessages": [
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
            "path": "/query/substance/facet",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Search substances by study owner",
                    "notes": "Search substances by study owner",
                    "type": "Substance",
                    "nickname": "searchByStudyOwner",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
			            {
			              "name": "category",
			              "description": "Endpoint category",
			              "required": true,
			              "type": "string",
			              "paramType": "query",
			              "allowMultiple"  : true,
						   <#include "/apidocs/parameter_categoryfacet_enum.ftl">			              
			            },			
			            {
			              "name": "endpoint.{category}",
			              "description": "Endpoint name",
			              "required": false,
			              "type": "string",
			              "paramType": "query",
			              "allowMultiple"  : true			              
			            },				         
			            {
			              "name": "lovalue.{category}",
			              "description": "Lower value",
			              "required": false,
			              "type": "number",
			              "paramType": "query",
			              "allowMultiple"  : true			              
			            },				            	            
			            {
			              "name": "upvalue.{category}",
			              "description": "Upper value",
			              "required": false,
			              "type": "number",
			              "paramType": "query",
			              "allowMultiple"  : true			              
			            },				            	            
			            {
			              "name": "loqlf.{category}",
			              "description": "Lower qualifier",
			              "required": false,
			              "type": "string",
			              "paramType": "query",
			              "enum" : ["",">=", ">", "="],
			              "allowMultiple"  : true			              
			            },				            	            
			            {
			              "name": "upqlf.{category}",
			              "description": "Upper qualifier",
			              "required": false,
			              "type": "string",
			              "paramType": "query",
			              "enum" : ["","<=", "<", "="],
			              "allowMultiple"  : true			              
			            },				 
			            {
			              "name": "unit.{category}",
			              "description": "Units",
			              "required": false,
			              "type": "string",
			              "paramType": "query",
			              "allowMultiple"  : true			              
			            },		
			            {
			              "name": "iresult.{category}",
			              "description": "Interpretation result",
			              "required": false,
			              "type": "string",
			              "paramType": "query",
			              "allowMultiple"  : true			              
			            },				
			            {
				              "name": "bundle_uri",
				              "description": "Retrieves if selected in this bundle",
				              "required": false,
				              "type": "string",
				              "paramType": "query",
				              "defaultValue": "${ambit_root}/bundle/1",
				              "allowMultiple"  : false
				        },					            			            		                          	            
						<#include "/apidocs/parameters_page.ftl" >
                    ],
                    "responseMessages": [
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
	"models" : {
      "Substance" : <#include "/apidocs/json_schema_substance.ftl" >   	  
    },    
	<#include "/apidocs/profile/${menu_profile}/info.ftl" >  
}