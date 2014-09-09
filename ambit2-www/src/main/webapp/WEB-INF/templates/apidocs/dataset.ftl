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
    "resourcePath": "/dataset",
	"apis": [
			{
			    "path": "/dataset",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "List datasets",
			            "notes": "Returns all datasets",
			            "type": "Datasets",
			            "nickname": "getAllDatasets",
			            "authorizations": {},
			            "parameters": [
							{
							    "name": "search",
							    "description": "Name",
							    "required": false,
							    "type": "string",
							    "paramType": "query",
							    "allowMultiple": false
							},			            
							<#include "/apidocs/parameters_page.ftl" >			            
			            ],
			            "responseMessages": [
			                {
			                    "code": 404,
			                    "message": "Datasets not found"
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