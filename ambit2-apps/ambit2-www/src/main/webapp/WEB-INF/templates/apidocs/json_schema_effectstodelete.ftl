{
  "$schema": "http://json-schema.org/draft-04/schema#",
  "id": "http://ambit.jsonschema.net",
  "type": "object",
  "properties": {
    "study": {
      "id": "http://ambit.jsonschema.net/study",
      "type": "array",
      "items": {
        "id": "http://ambit.jsonschema.net/study/0",
        "type": "object",
        "properties": {
          "owner": {
            "id": "http://ambit.jsonschema.net/study/0/owner",
            "type": "object",
            "properties": {
              "substance": {
                "id": "http://ambit.jsonschema.net/study/0/owner/substance",
                "type": "object",
                "properties": {
                  "uuid": {
                    "id": "http://ambit.jsonschema.net/study/0/owner/substance/uuid",
                    "type": "string"
                  }
                }
              }
            }
          },
          "effects_to_delete": {
            "id": "http://ambit.jsonschema.net/study/0/effects_to_delete",
            "type": "array",
            "items": {
              "id": "http://ambit.jsonschema.net/study/0/effects_to_delete/0",
              "type": "object",
              "properties": {
                "result": {
                  "id": "http://ambit.jsonschema.net/study/0/effects_to_delete/0/result",
                  "type": "object",
                  "properties": {
                    "idresult": {
                      "id": "http://ambit.jsonschema.net/study/0/effects_to_delete/0/result/idresult",
                      "type": "integer"
                    },
                    "deleted": {
                      "id": "http://ambit.jsonschema.net/study/0/effects_to_delete/0/result/deleted",
                      "type": "boolean"
                    },
                    "remarks": {
                      "id": "http://ambit.jsonschema.net/study/0/effects_to_delete/0/result/remarks",
                      "type": "string"
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  },
  "required": [
    "study"
  ]
}