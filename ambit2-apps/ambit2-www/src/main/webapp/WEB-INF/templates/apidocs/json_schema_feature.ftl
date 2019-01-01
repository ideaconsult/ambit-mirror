{
    "type": "object",
    "$schema": "http://json-schema.org/draft-03/schema",
    "id": "http://jsonschema.opentox.org/feature",
    "required": false,
    "properties": {
        "annotation": {
            "type": "array",
            "id": "http://jsonschema.opentox.org/annotation",
            "required": false
        },
        "creator": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/creator",
            "required": false
        },
        "isModelPredictionFeature": {
            "type": "boolean",
            "id": "http://jsonschema.opentox.org/feature/isModelPredictionFeature",
            "required": false
        },
        "isNominal": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/feature/isNominal",
            "required": false
        },
        "isNumeric": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/feature/isNumeric",
            "required": false
        },
        "order": {
            "type": "number",
            "id": "http://jsonschema.opentox.org/feature/order",
            "required": false
        },
        "sameAs": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/sameAs",
            "required": false
        },
        "source": {
            "type": "object",
            "id": "http://jsonschema.opentox.org/feature/source",
            "required": false,
            "properties": {
                "URI": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/feature/source/URI",
                    "required": false
                },
                "type": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/feature/source/type",
                    "required": false
                }
            }
        },
        "title": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/feature/title",
            "required": false
        },
        "type": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/feature/type",
            "required": false
        },
        "units": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/feature/units",
            "required": false
        }
    }
}