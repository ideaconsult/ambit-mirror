{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json",
        "text/n3",
        "application/rdf+xml",
        "text/uri-list"
    ],
    "resourcePath": "/task",
    "apis": [
        {
            "path": "/task",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Returns all tasks",
                    "notes": "Returns all tasks <a href='http://opentox.org/dev/apis/api-1.2/AsyncTask' target='opentox'>OpenTox Task API</a>",
                    "type": "Task",
                    "nickname": "getAllTasks",
                    "authorizations": {},
                    "parameters": [
                        {
                            "name": "search",
                            "description": "Task status",
                            "required": false,
                            "type": "string",
                            "paramType": "query",
                            "allowMultiple": false,
                            "enum": [
                                "Running",
                                "Completed",
                                "Queued",
                                "Error"
                            ]
                        }
						,			            
						<#include "/apidocs/parameters_page.ftl" >	                        
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Tasks not found"
                        },
						<#include "/apidocs/error_500.ftl" >	                        
                    ]
                }
            ]
        },
 		{
            "path": "/task/{id}",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Find task by identifier",
                    "notes": "Returns task representation <a href='http://opentox.org/dev/apis/api-1.2/AsyncTask' target='opentox'>OpenTox Task API</a>",
                    "type": "Task",
                    "nickname": "getTaskByID",
                    "authorizations": {},
                    "parameters": [
                        {
                            "name": "id",
                            "description": "Task UUID",
                            "required": false,
                            "type": "string",
                            "paramType": "path",
                            "allowMultiple": false
                        }                       
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Task not found"
                        },
                        <#include "/apidocs/error_task.ftl" >,                                   
						<#include "/apidocs/error_500.ftl" >	     
                    ]
                }
            ]
        }        
    ],
    "models" : {
      "Task" : <#include "/apidocs/json_schema_task.ftl" >   	  
    },    
    <#include "/apidocs/profile/${menu_profile}/info.ftl" >  
}