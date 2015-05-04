{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json",
        "text/n3",
        "application/rdf+xml",
        "text/csv",
        "text/plain",
        "chemical/x-mdl-sdfile",
        "chemical/x-cml",
        "chemical/x-daylight-smiles",
        "chemical/x-inchi",
        "text/x-arff",
        "text/x-arff-3col"
    ],		
    "resourcePath": "/query",
	"apis": [
     	{
            "path": "/query/compound/{term}/{representation}",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Exact compound search",
                    "notes": "Returns compounds found",
                    "type": "Search compound",
                    "nickname": "searchByIdentifier",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
			            {
			              "name": "term",
			              "description": "",
			              "required": true,
			              "type": "string",
			              "enum" : ["search", "url", "inchikey"],
			              "defaultValue" : "search",
			              "paramType": "path",
			              "allowMultiple"  : false
			            },      
			            {
			              "name": "representation",
			              "description": "",
			              "required": false,
			              "type": "string",
			              "enum" : ["all","smiles","reach","stdinchi","stdinchikey","names","iupac_name","synonym","cas","einecs"],
			              "defaultValue" : "all",
			              "paramType": "path",
			              "allowMultiple"  : false
			            },
			            {
			              "name": "search",
			              "description": "Compound identifier (SMILES, InChI, name, registry identifiers)",
			              "required": true,
			              "type": "string",
			              "paramType": "query",
			              "allowMultiple"  : false
			            },
			            {
			              "name": "b64search",
			              "description": "Base64 encoded mol file; if included, will be used instead of the 'search' parameter",
			              "required": false,
			              "type": "string",
			              "paramType": "query",
			              "allowMultiple"  : false
			            },			            
			            {
			              "name": "casesens",
			              "description": "Case sensitivy search if yes",
			              "required": false,
			              "type": "string",
			              "enum" : ["true","false"],
			              "defaultValue" : "false",
			              "paramType": "query",
			              "allowMultiple"  : false
			            },
			            {
				              "name": "condition",
				              "description": "Search condition",
				              "required": false,
				              "type": "string",
				              "enum" : ["","=","regexp","!=","like","not like"],
				              "defaultValue" : "false",
				              "paramType": "query",
				              "allowMultiple"  : false
				        },			       
			            {
				              "name": "bundle_uri",
				              "description": "Bundle URI",
				              "required": false,
				              "type": "string",
				              "defaultValue" : null,
				              "paramType": "query",
				              "allowMultiple"  : false
				        },						        
			            <#include "/apidocs/parameters_mol.ftl" >,				            
						<#include "/apidocs/parameters_page.ftl" >
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Compounds not found"
                        },
						<#include "/apidocs/error_aa.ftl" >,
						<#include "/apidocs/error_500.ftl" >                            
                    ]
                }
            ]
     	},
     	{
            "path": "/query/similarity",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Similarity search",
                    "notes": "Returns similar compounds",
                    "type": "Search similar compounds",
                    "nickname": "searchBySimilarity",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
			            {
			              "name": "search",
			              "description": "SMILES, InChI, IUPAC name",
			              "required": true,
			              "type": "string",
			              "paramType": "query",
			              "allowMultiple"  : false
			            },                    
						<#include "/apidocs/parameter_structuresearch.ftl" >,						            
			            {
			              "name": "threshold",
			              "description": "Similarity threshold",
			              "required": false,
			              "type": "double",
			              "paramType": "query",
			              "allowMultiple"  : false
			            },		
			            {
			              "name": "dataset_uri",
			              "description": "Restrict the search within the AMBIT dataset specified with the URI",
			              "required": false,
			              "type": "string",
			              "paramType": "query",
			              "allowMultiple"  : false
			            },			
			            {
				              "name": "filterBySubstance",
				              "description": "Restrict the search within the set of structures with assigned substances",
				              "required": false,
				              "type": "string",
				              "paramType": "query",
				              "allowMultiple"  : false,
				              "enum" : ["true","yes","on","false","no",""]
				        },						            
			            {
				              "name": "bundle_uri",
				              "description": "If the structure is used in the specified bundle URI, the selection tag will be returned",
				              "required": false,
				              "type": "string",
				              "defaultValue" : null,
				              "paramType": "query",
				              "allowMultiple"  : false
				        },					            
						<#include "/apidocs/parameters_mol.ftl" >,					            
						<#include "/apidocs/parameters_page.ftl" >				            			            
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Compounds not found"
                        },
						<#include "/apidocs/error_aa.ftl" >,
						<#include "/apidocs/error_500.ftl" >                            
                    ]
                }
            ]
     	},
	{
            "path": "/query/smarts",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Substructure search",
                    "notes": "Returns compounds with the specified substructure",
                    "type": "Search substructure",
                    "nickname": "searchBySmarts",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
			            {
			              "name": "search",
			              "description": "SMARTS, InChI",
			              "required": true,
			              "type": "string",
			              "paramType": "query",
			              "allowMultiple"  : false
			            },                    
			            <#include "/apidocs/parameter_structuresearch.ftl" >,
			            {
			              "name": "dataset_uri",
			              "description": "Restrict the search within the AMBIT dataset specified with the URI",
			              "required": false,
			              "type": "string",
			              "paramType": "query",
			              "allowMultiple"  : false
			            },		
			            {
				              "name": "filterBySubstance",
				              "description": "Restrict the search within the set of structures with assigned substances",
				              "required": false,
				              "type": "string",
				              "paramType": "query",
				              "allowMultiple"  : false,
				              "enum" : ["true","yes","on","false","no",""]
				        },						            
			            {
				              "name": "bundle_uri",
				              "description": "If the structure is used in the specified bundle URI, the selection tag will be returned",
				              "required": false,
				              "type": "string",
				              "defaultValue" : null,
				              "paramType": "query",
				              "allowMultiple"  : false
				        },					            
						<#include "/apidocs/parameters_mol.ftl" >,		
						<#include "/apidocs/parameters_page.ftl" >				            			            
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Compounds not found"
                        }
                    ]
                }
            ]
     	}     	
    ],
	<#include "/apidocs/profile/${menu_profile}/info.ftl" >  
}