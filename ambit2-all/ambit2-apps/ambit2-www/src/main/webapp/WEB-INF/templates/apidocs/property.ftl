{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json"
    ],		
    "resourcePath": "/property",
	"apis": [
     	{
            "path": "/property/{topcategory}/{endpointcategory}/{endpoint}/{property_uuid}",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Get property",
                    "notes": "Get property",
                    "type": "Property",
                    "nickname": "getPropertyByUUID",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
   								{
								    "name": "topcategory",
								    "description": "Top endpoint category",
								    "required": false,
								    "type": "string",
								    "paramType": "path",
								    "allowMultiple": false,
								    "defaultValue" : "ECOTOX",
								    "enum" : ["P-CHEM","ENV FATE","ECOTOX","TOX"]
								},
								{
								    "name": "endpointcategory",
								    "description": "Endpoint category",
								    "required": false,
								    "type": "string",
								    "paramType": "path",
								    "allowMultiple": false,
								    "defaultValue" :"EC_FISHTOX_SECTION",
								    <#include "/apidocs/parameter_endpointcategorysection_enum.ftl" >
								},       
								{
								    "name": "endpoint",
								    "description": "Endpoint name",
								    "required": false,
								    "type": "string",
								    "paramType": "path",
								    "allowMultiple": false,
								    "defaultValue" :"LC50"
								},    								
                        {
                            "name": "property_uuid",
                            "description": "Property UUID",
                            "required": true,
                            "type": "string",
                            "paramType": "path",
                            "allowMultiple": false,
                            "defaultValue" : "C41308FA2D27C9F34E5B3C2DA86074C524DA8F3D"
                        }
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Property not found"
                        },
						<#include "/apidocs/error_aa.ftl" >,
						<#include "/apidocs/error_500.ftl" >                        
                    ]
                }
            ]
     	},
    	{
            "path": "/property/{topcategory}/{endpointcategory}",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Effectrecord placeholder",
                    "notes": "Get JSON representation of an empty effect record, with placeholders for the required condition fields",
                    "type": "Property",
                    "nickname": "getEffectRecordByEndpointCategory",
                     <#include "/apidocs/authz.ftl" >
                    "parameters": [
   								{
								    "name": "topcategory",
								    "description": "Top endpoint category",
								    "required": false,
								    "type": "string",
								    "paramType": "path",
								    "allowMultiple": false,
								    "defaultValue" : "ECOTOX",
								    "enum" : ["P-CHEM","ENV FATE","ECOTOX","TOX"]
								},
								{
								    "name": "endpointcategory",
								    "description": "Endpoint category",
								    "required": false,
								    "type": "string",
								    "paramType": "path",
								    "allowMultiple": false,
								    "defaultValue" :"EC_FISHTOX_SECTION",
								    <#include "/apidocs/parameter_endpointcategorysection_enum.ftl" >
								}
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Property not found"
                        },
                        {
                            "code": 400,
                            "message": "Bad request for unsupported categories"
                        },                        
						<#include "/apidocs/error_aa.ftl" >,
						<#include "/apidocs/error_500.ftl" >                        
                    ]
                }
            ]
     	},
     	{
            "path": "/property",
            "operations": [
               
                {
		            "method": "POST",
		            "summary": "Create a new property.",
		            "notes": "Create a new property. Returns property URI. <a href='http://opentox.org/dev/apis/api-1.2/Feature' target='opentox'>OpenTox Feature API</a>. For more information see <a href='https://github.com/ideaconsult/examples-ambit/blob/master/ambit-json-docs/substance_property.md'>substance property docs</a>",
		            "type": "String",
		            "nickname": "createProperty",
                    "consumes": [
     	                 "application/rdf+xml",
     	                 "text/n3",
     	                 "application/x-www-form-urlencoded"
     	            ],
		            <#include "/apidocs/authz.ftl" >
		            "parameters": [
						{
						    "name": "body",
						    "description": "RDF/XML or N3 representation of the Feature e.g. <a href='https://svn.code.sf.net/p/ambit/code/trunk/ambit2-all/ambit2-apps/ambit2-www/src/test/resources/feature.rdf'>feature.rdf</a> and <a href='http://sourceforge.net/p/ambit/feature-requests/83/'>Issue #83</a>",
						    "required": false,
						    "type": "Effect",
						    "paramType": "body",
						    "allowMultiple": false
						},
						{
						    "name": "protocol",
						    "description": "Protocol name",
						    "required": false,
						    "type": "String",
						    "paramType": "form",
						    "allowMultiple": false
						},								
						{
						    "name": "endpointcategory",
						    "description": "Endpoint category",
						    "required": false,
						    "type": "String",
						    "paramType": "form",
						    "allowMultiple": false,
						    <#include "/apidocs/parameter_endpointcategorysection_enum.ftl" >						    
						},	
						{
						    "name": "name",
						    "description": "Endpoint name",
						    "required": false,
						    "type": "String",
						    "paramType": "form",
						    "allowMultiple": false
						},
						{
						    "name": "unit",
						    "description": "Endpoint units",
						    "required": false,
						    "type": "String",
						    "paramType": "form",
						    "allowMultiple": false
						},
						{
						    "name": "conditions",
						    "description": "Conditions (JSON)",
						    "required": false,
						    "type": "String",
						    "paramType": "form",
						    "allowMultiple": false
						}
													
		            ],
		            "responseMessages": [
		   	             			 {
		   	            			    "code": 200,
		   	            			    "message": "OK"
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
        "Effect"     : <#include "/apidocs/json_schema_effect.ftl" >,
        "Task" : <#include "/apidocs/json_schema_task.ftl" >,
        "Feature" : <#include "/apidocs/json_schema_feature.ftl" >
      },      
	<#include "/apidocs/profile/${menu_profile}/info.ftl" >  
}