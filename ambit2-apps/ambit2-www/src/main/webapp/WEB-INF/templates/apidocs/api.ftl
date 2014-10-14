{
    "apiVersion":  "${ambit_version_short}",
    "swaggerVersion": "1.2",
    "apis": [
        {
            "path": "/compound",
            "description": "OpenTox Chemical Compounds service"
        },
 		{
            "path": "/query/compound",
            "description": "Chemical structures search"
        },           
        {
            "path": "/feature",
            "description": "OpenTox Feature service"
        },
        {
            "path": "/dataset",
            "description": "OpenTox Dataset service"
        },   
        {
            "path": "/algorithm",
            "description": "OpenTox Algorithms service"
        },
        {
            "path": "/model",
            "description": "OpenTox Prediction Models service"
        },
        {
            "path": "/task",
            "description": "OpenTox Task service (asynchronous jobs)"
        },  
        {
            "path": "/substance",
            "description": "Chemical Substances service"
        },
        {
            "path": "/substanceowner",
            "description": "Substance owners"
        },        
        {
            "path": "/property",
            "description": "Chemical substances Properties service"
        },      
        {
            "path": "/query",
            "description": "Queries"
        }
                                 
    ],
	<#include "/apidocs/authz.ftl" >
	<#include "/apidocs/info.ftl" >  
}