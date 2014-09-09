{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json"
    ],		
    "resourcePath": "/property",
	"apis": [
     	{
            "path": "/property/{property_uuid}",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Get property",
                    "notes": "Get property",
                    "type": "Property",
                    "nickname": "getPropertyByUUID",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
                        {
                            "name": "property_uuid",
                            "description": "Property UUID",
                            "required": true,
                            "type": "string",
                            "paramType": "path",
                            "allowMultiple": false
                        }
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Property not found"
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