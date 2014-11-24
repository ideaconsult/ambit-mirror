{
    "type": "object",
    "$schema": "http://json-schema.org/draft-03/schema",
    "id": "http://jsonschema.opentox.org/",
    "required": false,
    "properties": {
        "completed": {
            "type": "number",
            "description": "Time stamp",
            "id": "http://jsonschema.opentox.org/completed",
            "required": true
        },
        "errorCause": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/errorCause",
            "required": false
        },
        "error": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/error",
            "required": false
        },
        "id": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/id",
            "required": true
        },
        "name": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/name",
            "required": true
        },
        "policyError": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/policyError",
            "required": false
        },
        "result": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/result",
            "required": true
        },
        "started": {
            "type": "number",
            "description": "Time stamp",
            "id": "http://jsonschema.opentox.org/started",
            "required": false
        },
        "status": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/status",
            "enum" : ["Running","Completed","Queued","Error"],
            "required": true
        },
        "uri": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/uri",
            "required": true
        },
        "user": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/user",
            "required": false
        }
    }
}