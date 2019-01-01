{
    "type": "object",
    "$schema": "http://json-schema.org/draft-03/schema",
    "id": "http://jsonschema.opentox.org/",
    "required": false,
    "properties": {
        "category": {
            "type": "object",
            "id": "http://jsonschema.opentox.org/category",
            "required": false,
            "properties": {
                "description": {
                    "type": "number",
                    "id": "http://jsonschema.opentox.org/category/description",
                    "required": false
                },
                "order": {
                    "type": "number",
                    "id": "http://jsonschema.opentox.org/category/order",
                    "required": false
                },
                "title": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/category/title",
                    <#include "/apidocs/parameter_endpointcategorysection_enum.ftl">,
                    "required": false
                },
                "uri": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/category/uri",
                    "required": false
                }
            }
        },
        "count": {
            "type": "number",
            "id": "http://jsonschema.opentox.org/count",
            "required": false
        },
        "topcategory": {
            "type": "object",
            "id": "http://jsonschema.opentox.org/topcategory",
            "required": false,
            "properties": {
                "title": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/topcategory/title",
                    "required": false
                },
                "uri": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/topcategory/uri",
                    "enum" : ["P-CHEM","ENV FATE","ECOTOX","TOX"],
                    "required": false
                }
            }
        }
    }
}