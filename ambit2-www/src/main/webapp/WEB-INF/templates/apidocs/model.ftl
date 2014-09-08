{
	<#include "/apidocs/version.ftl" >
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
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Models not found"
                        }
                    ]
                }
            ]
     	}
    ],
	<#include "/apidocs/info.ftl" >  
}