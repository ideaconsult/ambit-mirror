{
	"type":"object",
	"$schema": "http://json-schema.org/draft-03/schema",
	"id": "http://jsonschema.ambit.sf.net/",
	"required":false,
	"properties":{
		"conditions": {
			"type":"object",
			"id": "http://jsonschema.ambit.sf.net/conditions",
			"required":false
		},
		"endpoint": {
			"type":"string",
			"id": "http://jsonschema.ambit.sf.net/endpoint",
			"required":true
		},
		"endpointtype" : {
			"type":"string",
			"id": "http://jsonschema.ambit.sf.net/endpointtype",
			"required":false		
		},
		"result": {
			"$ref":"Result"
		}
	}
}
