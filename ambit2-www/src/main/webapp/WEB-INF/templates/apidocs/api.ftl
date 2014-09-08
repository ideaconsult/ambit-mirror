{
    "apiVersion":  "${ambit_version_short}",
    "swaggerVersion": "1.2",
    "apis": [
        {
            "path": "/compound",
            "description": "Provides different representations for chemical compounds with a unique and defined chemical structure."
        },
        {
            "path": "/feature",
            "description": "A Feature is an object,representing any kind of property, assigned to a Compound."
        },
        {
            "path": "/dataset",
            "description": "Provides access to chemical compounds and their features (e.g. structural, physical-chemical, biological, toxicological properties)."
        },   
        {
            "path": "/algorithm",
            "description": "Provides access to OpenTox algorithms."
        },
        {
            "path": "/model",
            "description": "Provides access to OpenTox prediction models"
        },
        {
            "path": "/task",
            "description": "Asynchronous jobs are handled via an intermediate Task resource. A resource, submitting an asynchronous job should return the URI of the task."
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