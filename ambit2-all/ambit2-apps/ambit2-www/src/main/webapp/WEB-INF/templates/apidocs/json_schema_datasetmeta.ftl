{
    "type": "object",
    "$schema": "http://json-schema.org/draft-03/schema",
    "id": "http://jsonschema.opentox.org",
    "required": false,
    "properties": {
        "URI": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/URI",
            "required": false
        },
        "rightsHolder": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/rightsHolder",
            "required": false
        },
        "rights": {
            "type": "object",
            "id": "http://jsonschema.opentox.org/rights",
            "required": false,
            "properties": {
                "URI": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/rights/URI",
                    "required": false
                },
                "type": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/rights/type",
                    "required": false
                }
            }
        },
        "seeAlso": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/seeAlso",
            "required": false
        },
        "source": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/source",
            "required": false
        },
        "stars": {
            "type": "number",
            "id": "http://jsonschema.opentox.org/stars",
            "required": false
        },
        "title": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/title",
            "required": false
        },
        "type": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/type",
            "required": false
        }
    }
}