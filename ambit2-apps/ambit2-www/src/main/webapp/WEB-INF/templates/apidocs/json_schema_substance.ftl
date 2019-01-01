{
    "type": "object",
    "$schema": "http://json-schema.org/draft-03/schema",
    "id": "http://jsonschema.opentox.org/",
    "required": false,
    "properties": {
        "URI": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/URI",
            "required": false
        },
        "bundles": {
            "type": "object",
            "id": "hhttp://jsonschema.opentox.org/bundles",
            "required": false,
            "properties": {
                "https://apps.ideaconsult.net/data/bundle/1": {
                    "type": "object",
                    "id": "http://jsonschema.opentox.org/bundle/ID",
                    "required": false,
                    "properties": {
                        "count": {
                            "type": "number",
                            "id": "http://jsonschema.opentox.org/bundle/count",
                            "required": false
                        },
                        "remarks": {
                            "type": "null",
                            "id": "http://jsonschema.opentox.org/bundle/remarks",
                            "required": false
                        },
                        "tag": {
                            "type": "string",
                            "id": "http://jsonschema.opentox.org/bundle/tag",
                            "required": false
                        }
                    }
                }
            }
        },
        "composition": {
            "type": "array",
            "id": "http://jsonschema.opentox.org/composition",
            "required": false
        },
        "externalIdentifiers": {
            "type": "array",
            "id": "http://jsonschema.opentox.org/externalIdentifiers",
            "required": false,
            "items": {
                "type": "object",
                "id": "http://jsonschema.opentox.org/externalIdentifiers/0",
                "required": false,
                "properties": {
                    "id": {
                        "type": "string",
                        "id": "http://jsonschema.opentox.org/externalIdentifiers/0/id",
                        "required": false
                    },
                    "type": {
                        "type": "string",
                        "id": "http://jsonschema.opentox.org/externalIdentifiers/0/type",
                        "required": false
                    }
                }
            }
        },
        "format": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/format",
            "required": false
        },
        "i5uuid": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/i5uuid",
            "description": "Unique identifier for the substance",
            "required": false
        },
        "name": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/name",
            "required": false
        },
        "ownerName": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/ownerName",
            "required": false
        },
        "ownerUUID": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/ownerUUID",
            "description": "Unique identifier for the substance owner (company producing the substance)",
            "required": false
        },
        "publicname": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/publicname",
            "required": false
        },
        "referenceSubstance": {
            "type": "object",
            "id": "http://jsonschema.opentox.org/referenceSubstance",
            "description": "Reference substance",
            "required": false,
            "properties": {
                "i5uuid": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/referenceSubstance/i5uuid",
                    "required": false
                },
                "uri": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/referenceSubstance/uri",
                    "required": false
                }
            }
        },
        "substanceType": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/substanceType",
            "description": "Substance type",            
            "enum" : ["Existing Chemical","UVCB","mono constituent substance","multi constituent substance","nanomaterial","nanoparticle"],
            "required": false
        }
    }
}