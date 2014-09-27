{
	"type":"object",
	"$schema": "http://json-schema.org/draft-03/schema",
	"id": "http://jsonschema.net",
	"required":false,
	"properties":{
		"conditions": {
			"type":"object",
			"id": "http://jsonschema.net/conditions",
			"required":false
		},
		"endpoint": {
			"type":"string",
			"id": "http://jsonschema.net/endpoint",
			"required":false
		},
		"result": {
			"$ref":"Result"
		}
	}
}
