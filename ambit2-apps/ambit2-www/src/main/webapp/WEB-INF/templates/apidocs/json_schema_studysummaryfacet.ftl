{
    "type": "object",
    "$schema": "http://json-schema.org/draft-03/schema",
    "id": "http://jsonschema.ambit.sf.net/",
    "required": false,
    "properties": {
        "category": {
            "type": "object",
            "id": "http://jsonschema.ambit.sf.net/category",
            "required": false,
            "properties": {
                "description": {
                    "type": "number",
                    "id": "http://jsonschema.ambit.sf.net/category/description",
                    "required": false
                },
                "order": {
                    "type": "number",
                    "id": "http://jsonschema.ambit.sf.net/category/order",
                    "required": false
                },
                "title": {
                    "type": "string",
                    "id": "http://jsonschema.ambit.sf.net/category/title",
                    <#include "/apidocs/parameter_endpointcategorysection_enum.ftl">,
                    "required": false
                },
                "uri": {
                    "type": "string",
                    "id": "http://jsonschema.ambit.sf.net/category/uri",
                    "required": false
                }
            }
        },
        "count": {
            "type": "number",
            "id": "http://jsonschema.ambit.sf.net/count",
            "required": false
        },
        "topcategory": {
            "type": "object",
            "id": "http://jsonschema.ambit.sf.net/topcategory",
            "required": false,
            "properties": {
                "title": {
                    "type": "string",
                    "id": "http://jsonschema.ambit.sf.net/topcategory/title",
                    "required": false
                },
                "uri": {
                    "type": "string",
                    "id": "http://jsonschema.ambit.sf.net/topcategory/uri",
                    "enum" : ["P-CHEM","ENV FATE","ECOTOX","TOX"],
                    "required": false
                }
            }
        }
    }
}