{
    "apiVersion":  "${ambit_version_short}",
    "swaggerVersion": "1.2",
    "apis": [
        {
            "path": "/compound",
            "description": "Chemical compounds with a unique and defined chemical structure"
        },
 		{
            "path": "/query/compound",
            "description": "Chemical structures search"
        },           
        {
            "path": "/feature",
            "description": "A Feature is an object,representing any kind of property, assigned to a Compound"
        },
        {
            "path": "/dataset",
            "description": "Sets of chemical compounds and their features"
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
            "description": "Asynchronous jobs are handled via an intermediate Task resource"
        },  
        {
            "path": "/substance",
            "description": "Operations about substances"
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