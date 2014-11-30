{
    "type": "object",
    "$schema": "http://json-schema.org/draft-03/schema",
    "id": "http://jsonschema.net/model",
    "required": false,
    "properties": {
        "URI": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/URI",
            "required": false
        },
        "algorithm": {
            "type": "object",
            "id": "http://jsonschema.opentox.org/algorithm",
            "required": false,
            "properties": {
                "URI": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/algorithm/URI",
                    "required": false
                },
                "algFormat": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/algorithm/algFormat",
                    "required": false
                },
                "img": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/algorithm/img",
                    "required": false
                }
            }
        },
        "ambitprop": {
            "type": "object",
            "id": "http://jsonschema.opentox.org/model/ambitprop",
            "required": false,
            "properties": {
                "content": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/model/ambitprop/content",
                    "required": false
                },
                "creator": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/model/ambitprop/creator",
                    "required": false
                },
                "evaluations": {
                    "type": "object",
                    "id": "http://jsonschema.opentox.org/model/ambitprop/evaluations",
                    "required": false
                },
                "legend": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/model/ambitprop/legend",
                    "required": false
                },
                "mimetype": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/model/ambitprop/mimetype",
                    "required": false
                },
                "stars": {
                    "type": "number",
                    "id": "http://jsonschema.opentox.org/model/ambitprop/stars",
                    "required": false
                }
            }
        },
        "dependent": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/model/dependent",
            "required": false
        },
        "id": {
            "type": "number",
            "id": "http://jsonschema.opentox.org/model/id",
            "required": false
        },
        "independent": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/model/independent",
            "required": false
        },
        "predicted": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/model/predicted",
            "required": false
        },
        "stars": {
            "type": "number",
            "id": "http://jsonschema.opentox.org/model/stars",
            "required": false
        },
        "title": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/model/title",
            "required": false
        },
        "trainingDataset": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/model/trainingDataset",
            "required": false
        }
    }
}