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
            "path": "/query/compound/search/all",
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
			              "name": "search",
			              "description": "Compound identifier",
			              "required": true,
			              "type": "string",
			              "paramType": "query",
			              "allowMultiple"  : false
			            },
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
			              "description": "SMILES, InChI, chemical name",
			              "required": true,
			              "type": "string",
			              "paramType": "query",
			              "allowMultiple"  : false
			            },
			            {
			              "name": "threshold",
			              "description": "Similarity threshold",
			              "required": false,
			              "type": "double",
			              "paramType": "query",
			              "allowMultiple"  : false
			            },			            
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
	<#include "/apidocs/info.ftl" >  
}