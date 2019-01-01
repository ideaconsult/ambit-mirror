{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json",
        "application/x-javascript",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "text/csv"
    ],		
    "resourcePath": "/investigation",
	"apis": [
			{
			    "path": "/investigation",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Query protocol applications",
			            "notes": "Returns a list of result records, according to the search criteria",
	                    "type": "array",
	                    "items": {
	                        "$ref": "Study"
	                    },			            
			            "nickname": "getInvestigationResults",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "search",
							    "description": "Search parameter, UUID of the investigation or a substance",
							    "required": false,
							    "type": "string",
							    "paramType": "query",
							    "allowMultiple": false,
							    "defaultValue": ""
							},
							{
							    "name": "type",
							    "description": "Query type",
							    "required": true,
							    "type": "string",
							    "paramType": "query",
							    "allowMultiple": false,
							    "defaultValue": "byinvestigation",
							    "enum" : [
							       "byinvestigation",
							       "bysubstance",
							       "byprovider",
							       "bycitation",
							       "bystudytype",
							       "bystructure_inchikey",
							       "bystructure_smiles",
							       "bystructure_name",
							       "bysubstance_name",
							       "bysubstance_type"
							    ]
							},	
							{
							    "name": "inchikey",
							    "description": "Search parameter, InChI key(s) of the substance component(s), comma delimited",
							    "required": false,
							    "type": "string",
							    "paramType": "query",
							    "allowMultiple": true,
							    "defaultValue": "YUYCVXFAYWRXLS-UHFFFAOYSA-N"
							},				
							{
							    "name": "id",
							    "description": "Search parameter, chemical structure or substance identifier(s), comma delimited",
							    "required": false,
							    "type": "string",
							    "paramType": "query",
							    "allowMultiple": true,
							    "defaultValue": ""
							},			
							<#include "/apidocs/parameters_page.ftl" >			            
			            ],
			            "responseMessages": [
			     			{
			     				"code": 200,
			     				 "message": "OK. Substance(s) found"
			     			},				                                 
			                {
			                    "code": 404,
			                    "message": "Substances not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }
			        ]
			 }
				
    ],
    "models" : {
      "Study"     : <#include "/apidocs/json_schema_study.ftl" >   	  
    },
	<#include "/apidocs/profile/${menu_profile}/info.ftl" >  
}