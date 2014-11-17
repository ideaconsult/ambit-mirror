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
     	}	     	
    ],
	<#include "/apidocs/profile/${menu_profile}/info.ftl" >  
}