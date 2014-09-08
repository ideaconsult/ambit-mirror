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
            "path": "study",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Search substances by endpoint data",
                    "notes": "Returns substances list",
                    "type": "Search substance",
                    "nickname": "searchSubstancesByEndpoint",
                    "authorizations": {},
                    "parameters": [

						<#include "/apidocs/parameters_page.ftl" >				            			            
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Substances not found"
                        }
                    ]
                }
            ]
     	}	
    ],
	<#include "/apidocs/info.ftl" >  
}