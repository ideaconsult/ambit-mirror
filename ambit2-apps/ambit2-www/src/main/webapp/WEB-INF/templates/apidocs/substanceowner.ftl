{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json",
        "application/x-javascript",
        "text/x-arff",
        "text/x-arff-3col",
        "text/csv"
    ],		
    "resourcePath": "/substanceowner",
	"apis": [
			{
			    "path": "/substanceowner",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "List substance owners",
			            "notes": "Returns a list of substance owners",
			            "type": "Facet",
			            "nickname": "getSubstanceOwners",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							<#include "/apidocs/parameters_page.ftl" >			            
			            ],
			            "responseMessages": [
			     			{
			     				"code": 200,
			     				 "message": "OK. Substance owners found"
			     			},				                                 
			                {
			                    "code": 404,
			                    "message": "Substance owners not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }
			    ]
			},
			{
			    "path": "/substanceowner/{uuid}",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get a substance owner",
			            "notes": "Returns substance owner representation",
			            "type": "Facet",
			            "nickname": "getSubstanceOwnerByUUID",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
			                <#include "/apidocs/parameter_substanceowner_uuid.ftl" >
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid substance owner identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Substance owner not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }
			        ,
			         {
			            "method": "DELETE",
			              "summary": "Delete all substance by a substance owner",
			            "notes": "Not (yet) implemented",
			            "nickname": "deleteSubstanceOwnerByUUID",
			            "parameters": [
						                <#include "/apidocs/parameter_substanceowner_uuid.ftl" >
						            ],			            
			            "responseMessages": [
			     			     			{
			     			    			 "code": 200,
			     			    			 "message": "OK"
			     			    			},				                                 
			     			     			{
			     			     				"code": 400,
			     			     			    "message": "Invalid substance owner identifier"
			     			     			},						                                 
			     			                {
			     			                    "code": 404,
			     			                    "message": "Substance owner not found"
			     			                },
			     							<#include "/apidocs/error_aa.ftl" >,
			     							<#include "/apidocs/error_500.ftl" >			                
			     			            ]
			                                 
			         }   
			    ]
			},
{
			    "path": "/substanceowner/{uuid}/substance",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get all substances of the substance owner",
			            "notes": "Returns substances representation. See Substance service.",
			            "type": "Substance",
			            "nickname": "getSubstancesByOwner",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
			                <#include "/apidocs/parameter_substanceowner_uuid.ftl" >,
			                <#include "/apidocs/parameters_page.ftl" >
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid substance owner identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Substance owner not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }
			    ]
			},
			{
			    "path": "/substanceowner/{uuid}/structure",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get structures of a substance owner as a Dataset",
			            "notes": "Returns a dataset, containing all structures (without study data). See OpenTox Dataset service ",
			            "type": "Dataset",
			            "nickname": "getStructuresByOwner",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
			                <#include "/apidocs/parameter_substanceowner_uuid.ftl" >,
			                <#include "/apidocs/parameters_page.ftl" >	
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid substance owner identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Substance owner not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }
			    ]
			},
			{
			    "path": "/substanceowner/{uuid}/dataset",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get structures and study data of a substance owner as a Dataset",
			            "notes": "Returns a dataset, containing all structures with study data. See OpenTox Dataset service. Uses Property resources instead of Feature resources.",
			            "type": "Dataset",
			            "nickname": "getDatasetByOwner",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
			                <#include "/apidocs/parameter_substanceowner_uuid.ftl" >,
			                <#include "/apidocs/parameters_page.ftl" >	
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid substance owner identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Substance owner not found"
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