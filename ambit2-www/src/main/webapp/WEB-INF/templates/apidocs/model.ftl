{
	<#include "/apidocs/version.ftl" >
    "produces": [
                 "application/json",
                 "text/n3",
                 "application/rdf+xml",
                 "text/uri-list"
             ],		
    "resourcePath": "/model",
	"apis": [
     	{
            "path": "/model",
            "operations": [
                {
                    "method": "GET",
                    "summary": "List models",
                    "notes": "Returns all models",
                    "type": "Model",
                    "nickname": "getAllModels",
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
                            "message": "Models not found"
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