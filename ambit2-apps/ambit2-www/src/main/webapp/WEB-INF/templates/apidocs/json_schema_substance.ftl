{
    "type": "object",
    "$schema": "http://json-schema.org/draft-03/schema",
    "id": "http://jsonschema.ambit.sf.net/",
    "required": false,
    "properties": {
        "URI": {
            "type": "string",
            "id": "http://jsonschema.ambit.sf.net/URI",
            "required": false
        },
        "bundles": {
            "type": "object",
            "id": "hhttp://jsonschema.ambit.sf.net/bundles",
            "required": false,
            "properties": {
                "https://apps.ideaconsult.net/data/bundle/1": {
                    "type": "object",
                    "id": "http://jsonschema.ambit.sf.net/bundle/ID",
                    "required": false,
                    "properties": {
                        "count": {
                            "type": "number",
                            "id": "http://jsonschema.ambit.sf.net/bundle/count",
                            "required": false
                        },
                        "remarks": {
                            "type": "null",
                            "id": "http://jsonschema.ambit.sf.net/bundle/remarks",
                            "required": false
                        },
                        "tag": {
                            "type": "string",
                            "id": "http://jsonschema.ambit.sf.net/bundle/tag",
                            "required": false
                        }
                    }
                }
            }
        },
        "composition": {
            "type": "array",
            "id": "http://jsonschema.ambit.sf.net/composition",
            "required": false
        },
        "externalIdentifiers": {
            "type": "array",
            "id": "http://jsonschema.ambit.sf.net/externalIdentifiers",
            "required": false,
            "items": {
                "type": "object",
                "id": "http://jsonschema.ambit.sf.net/externalIdentifiers/0",
                "required": false,
                "properties": {
                    "id": {
                        "type": "string",
                        "id": "http://jsonschema.ambit.sf.net/externalIdentifiers/0/id",
                        "required": false
                    },
                    "type": {
                        "type": "string",
                        "id": "http://jsonschema.ambit.sf.net/externalIdentifiers/0/type",
                        "required": false
                    }
                }
            }
        },
        "format": {
            "type": "string",
            "id": "http://jsonschema.ambit.sf.net/format",
            "required": false
        },
        "i5uuid": {
            "type": "string",
            "id": "http://jsonschema.ambit.sf.net/i5uuid",
            "description": "Unique identifier for the substance",
            "required": false
        },
        "name": {
            "type": "string",
            "id": "http://jsonschema.ambit.sf.net/name",
            "required": false
        },
        "ownerName": {
            "type": "string",
            "id": "http://jsonschema.ambit.sf.net/ownerName",
            "required": false
        },
        "ownerUUID": {
            "type": "string",
            "id": "http://jsonschema.ambit.sf.net/ownerUUID",
            "description": "Unique identifier for the substance owner (company producing the substance)",
            "required": false
        },
        "publicname": {
            "type": "string",
            "id": "http://jsonschema.ambit.sf.net/publicname",
            "required": false
        },
        "referenceSubstance": {
            "type": "object",
            "id": "http://jsonschema.ambit.sf.net/referenceSubstance",
            "description": "Reference substance",
            "required": false,
            "properties": {
                "i5uuid": {
                    "type": "string",
                    "id": "http://jsonschema.ambit.sf.net/referenceSubstance/i5uuid",
                    "required": false
                },
                "uri": {
                    "type": "string",
                    "id": "http://jsonschema.ambit.sf.net/referenceSubstance/uri",
                    "required": false
                }
            }
        },
        "substanceType": {
            "type": "string",
            "id": "http://jsonschema.ambit.sf.net/substanceType",
            "description": "Substance type",            
            "enum" : ["Existing Chemical","UVCB","mono constituent substance","multi constituent substance","nanomaterial","nanoparticle","ontology term"],
            "required": false
        }
    }
}