{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json",
        "text/n3",
        "application/rdf+xml",
        "text/uri-list"
    ],	
    "resourcePath": "/algorithm",
    "apis": [
			{
			    "path": "/algorithm",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "List algorithms",
			            "notes": "Returns all algorithms <a href='http://opentox.org/dev/apis/api-1.2/Algorithm' target='opentox'>OpenTox Algorithm API</a>",
			            "type": "Algorithm",
			            "nickname": "getAllAlgorithms",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "search",
							    "description": "Algorithm name (starts with)",
							    "required": false,
							    "type": "string",
							    "paramType": "query",
							    "allowMultiple": false,
							    "defaultValue": "ToxTree"
							},    			            
							{
							    "name": "type",
							    "description": "Algorithm type",
							    "required": false,
							    "type": "string",
							    "paramType": "query",
							    "allowMultiple": false,
	                            "enum": [
	                                "Rules",
	                                "Regression",
	                                "Classification",
	                                "Clustering",
	                                "FeatureSelection",
	                                "Supervised",
	                                "UnSupervised",
	                                "SingleTarget",	  
	                                "MultipleTarget",
	                                "EagerLearning",
	                                "LazyLearning",
	                                "DescriptorCalculation",
	                                "AppDomain",
	                                "Structure",
	                                "Structure2D",
	                                "SMSD",
	                                "Fingerprints",
	                                "Finder",
	                                "PreferredStructure",
	                                "Mockup",
	                                "Expert"	                                	                                                              
	                            ]							    
							},			            
							<#include "/apidocs/parameters_page.ftl" >			            
			            ],
			            "responseMessages": [
			                {
			                    "code": 404,
			                    "message": "Algorithms not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >												                
			            ]
			        }
			    ]
			},
			{
			    "path": "/algorithm/{algorithm_id}",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Returns algorithm representation",
			            "notes": "Returns algorithm representation <a href='http://opentox.org/dev/apis/api-1.2/Algorithm' target='opentox'>OpenTox Algorithm API</a>",
			            "type": "Algorithm",
			            "nickname": "getAlgorithmByID",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "algorithm_id",
							    "description": "Algorithm ID",
							    "required": false,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false,
							    "defaultValue": "toxtreecramer"
							} 		            
			            ],
			            "responseMessages": [
			         		{
			        		   "code": 400,
			        		   "message": "Invalid algorithm identifier"
			        		},				                                 
			                {
			                    "code": 404,
			                    "message": "Algorithm not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >												                
			            ]
			        },
			        {
	                    "method": "POST",
	                    "summary": "Apply the algorithm. Returns a task.",
			            "notes": "Applies the algorithm to a dataset and returns a task with the result (dataset or model) <a href='http://opentox.org/dev/apis/api-1.2/Algorithm' target='opentox'>OpenTox Algorithm API</a>. See the Task service how to query the task.",
	                    "type": "void",
	                    "nickname": "applyAlgorithm",
	                    "consumes": [
	                        "application/x-www-form-urlencoded"
	                    ],
	                    <#include "/apidocs/authz.ftl" >
	                    "parameters": [
	                        {
	                            "name": "algorithm_id",
	                            "description": "Algorithm ID",
	                            "required": true,
	                            "type": "int",
	                            "paramType": "path",
	                            "allowMultiple": false,
	                            "defaultValue": "toxtreecramer"
	                        },
	                        {
	                            "name": "dataset_uri",
	                            "description": "is mandatory for all kind of prediction algorithms (machine learning or otherwise), as well for data processing algorithms. (See Dataset service)",
	                            "required": false,
	                            "type": "string",
	                            "paramType": "form",
	                            "allowMultiple": false,
	                            "defaultValue": "${ambit_root}/dataset/1?pagesize=3"
	                        },
	                        {
	                            "name": "prediction_feature",
	                            "description": " is mandatory for prediction (classification/regression) and other supervised learning algorithms. The URI of the feature with the endpoint to predict is expected as value. (see Feature service)",
	                            "required": false,
	                            "type": "string",
	                            "paramType": "form",
	                            "allowMultiple": false
	                        },
	                        {
	                            "name": "dataset_service",
	                            "description": "The dataset service to post the result dataset (see Dataset service)",
	                            "required": false,
	                            "type": "string",
	                            "paramType": "form",
	                            "allowMultiple": false
	                        },
	                        {
	                            "name": "result_dataset",
	                            "description": "optional parameter to specify the dataset URI where the results should be stored. If not present, the result URI is generated by the dataset service.",
	                            "required": false,
	                            "type": "string",
	                            "paramType": "form",
	                            "allowMultiple": false
	                        },
	                        {
	                            "name": "parameter",
	                            "description": "contains all the algorithm specific parameters.",
	                            "required": false,
	                            "type": "string",
	                            "paramType": "form",
	                            "allowMultiple": true
	                        }		                        
	                    ],
	                    "responseMessages": [

		                    <#include "/apidocs/error_task.ftl" >,	
	             			 {
	            			    "code": 404,
	            			    "message": "Algorithm not found"
	            			 },
	             			 {
		            			"code": 400,
		            			"message": "Bad request"
		            	    },	 		                    
	            			<#include "/apidocs/error_aa.ftl" >,
	            			<#include "/apidocs/error_500.ftl" >	
	                    ]
	                }			        
			    ]
			}
			
    ],
    "models" : {
      "Task" : <#include "/apidocs/json_schema_task.ftl" >,
      "Feature" : <#include "/apidocs/json_schema_feature.ftl" >,
	  "Model" : <#include "/apidocs/json_schema_model.ftl" >
    },     
	<#include "/apidocs/profile/${menu_profile}/info.ftl" >  
}