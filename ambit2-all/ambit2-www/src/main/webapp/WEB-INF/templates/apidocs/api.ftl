{
    "apiVersion":  "${ambit_version_short}",
    "swaggerVersion": "1.2",
    "apis": [
        {
            "path": "/compound",
            "description": "Chemical compounds service"
        },
 		{
            "path": "/query/compound",
            "description": "Chemical structures search"
        },           
        {
            "path": "/feature",
            "description": "Feature service"
        },
        {
            "path": "/dataset",
            "description": "Dataset service"
        },   
        {
            "path": "/algorithm",
            "description": "OpenTox algorithms"
        },
        {
            "path": "/model",
            "description": "OpenTox prediction models"
        },
        {
            "path": "/task",
            "description": "Task service (asynchronous jobs)"
        },  
        {
            "path": "/substance",
            "description": "Chemical substances service"
        },
        {
            "path": "/property",
            "description": "Operations about properties"
        },      
        {
            "path": "/query",
            "description": "Queries"
        }
                                 
    ],
    "authorizations": {
       
    },
	<#include "/apidocs/info.ftl" >  
}