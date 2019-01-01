{
    "type": "object",
    "$schema": "http://json-schema.org/draft-03/schema",
    "id": "http://jsonschema.opentox.org/task",
    "required": false,
    "properties": {
        "completed": {
            "type": "number",
            "description": "Time stamp",
            "id": "http://jsonschema.opentox.org/task/completed",
            "required": true
        },
        "errorCause": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/task/errorCause",
            "required": false
        },
        "error": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/task/error",
            "required": false
        },
        "id": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/task/id",
            "required": true
        },
        "name": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/task/name",
            "required": true
        },
        "policyError": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/task/policyError",
            "required": false
        },
        "result": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/task/result",
            "required": true
        },
        "started": {
            "type": "number",
            "description": "Time stamp",
            "id": "http://jsonschema.opentox.org/task/started",
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
            "id": "http://jsonschema.opentox.org/task/uri",
            "required": true
        },
        "user": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/task/user",
            "required": false
        }
    }
}