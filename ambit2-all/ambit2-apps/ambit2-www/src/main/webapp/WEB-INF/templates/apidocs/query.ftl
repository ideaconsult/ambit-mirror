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
						}                    
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
	<#include "/apidocs/profile/${menu_profile}/info.ftl" >  
}