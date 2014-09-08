{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json"
    ],		
    "resourcePath": "/property",
	"apis": [
     	{
            "path": "/property",
            "operations": [
                {
                    "method": "GET",
                    "summary": "List properties",
                    "notes": "Returns all properties",
                    "type": "Property",
                    "nickname": "getAllProperties",
                    "authorizations": {},
                    "parameters": [
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Properties not found"
                        }
                    ]
                }
            ]
     	}	
    ],
	<#include "/apidocs/info.ftl" >  
}