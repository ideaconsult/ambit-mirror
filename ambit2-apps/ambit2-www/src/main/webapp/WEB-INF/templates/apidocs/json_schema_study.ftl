{
    "type": "object",
    "$schema": "http://json-schema.org/draft-03/schema",
    "id": "http://jsonschema.ambit.sf.net/",
    "required": false,
    "properties": {
          "uuid": {
            "type": "string"
          },
          "investigation_uuid": {
            "type": "string"
          },
          "assay_uuid": {
            "type": "string"
          },    
        "citation": {
            "type": "object",
            "id": "http://jsonschema.ambit.sf.net/citation",
            "required": false,
            "properties": {
                "owner": {
                    "type": "string",
                    "id": "http://jsonschema.ambit.sf.net/citation/owner",
                    "required": false
                },
                "title": {
                    "type": "string",
                    "id": "http://jsonschema.ambit.sf.net/citation/title",
                    "required": false
                },
                "year": {
                    "type": "string",
                    "id": "http://jsonschema.ambit.sf.net/citation/year",
                    "required": false
                }
            }
        },
        "effects": {
            "type": "array",
            "id": "http://jsonschema.ambit.sf.net/effects",
            "required": true,
            "items": {
                "$ref": "Effect"
            }
        },
        "interpretation": {
            "type": "object",
            "id": "http://jsonschema.ambit.sf.net/interpretation",
            "required": false,
            "properties": {
                "criteria": {
                    "type": "string",
                    "id": "http://jsonschema.ambit.sf.net/interpretation/criteria",
                    "required": false
                },
                "result": {
                    "type": "string",
                    "id": "http://jsonschema.ambit.sf.net/interpretation/result",
                    "required": false
                }
            }
        },
        "owner": {
            "type": "object",
            "id": "http://jsonschema.ambit.sf.net/owner",
            "required": false,
            "properties": {
                "company": {
                    "type": "object",
                    "id": "http://jsonschema.ambit.sf.net/owner/company",
                    "required": false,
                    "properties": {
                        "name": {
                            "type": "string",
                            "id": "http://jsonschema.ambit.sf.net/owner/company/name",
                            "required": true
                        },
                        "uuid": {
                            "type": "string",
                            "id": "http://jsonschema.ambit.sf.net/owner/company/uuid",
                            "required": true
                        }
                    }
                },
                "substance": {
                    "type": "object",
                    "id": "http://jsonschema.ambit.sf.net/owner/substance",
                    "required": true,
                    "properties": {
                        "uuid": {
                            "type": "string",
                            "id": "http://jsonschema.ambit.sf.net/owner/substance/uuid",
                            "description": "Unique identifier for the substance",
                            "required": true
                        }
                    }
                }
            }
        },
        "parameters": {
            "type": "object",
            "id": "http://jsonschema.ambit.sf.net/parameters",
            "required": false,
            "properties": {
                "Sex": {
                    "type": "null",
                    "id": "http://jsonschema.ambit.sf.net/parameters/E.method",
                    "required": false
                },
                "Species": {
                    "type": "string",
                    "id": "http://jsonschema.ambit.sf.net/parameters/Species",
                    "required": false
                },
                "Study year": {
                    "type": "null",
                    "id": "http://jsonschema.ambit.sf.net/parameters/E.cell_type",
                    "required": false
                }
            }
        },
        "protocol": {
            "type": "object",
            "id": "http://jsonschema.ambit.sf.net/protocol",
            "required": true,
            "properties": {
                "category": {
                    "type": "object",
                    "id": "http://jsonschema.ambit.sf.net/protocol/category",
                    "required": false,
                    "properties": {
                        "code": {
                            "type": "string",
                            "id": "http://jsonschema.ambit.sf.net/protocol/category/code",
                            "required": false
                        },
                        "title": {
                            "type": "string",
                            "id": "http://jsonschema.ambit.sf.net/protocol/category/title",
                            "required": false
                        }
                    }
                },
                "endpoint": {
                    "type": "string",
                    "id": "http://jsonschema.ambit.sf.net/protocol/endpoint",
                    "required": false
                },
                "guideline": {
                    "type": "array",
                    "id": "http://jsonschema.ambit.sf.net/protocol/guideline",
                    "required": false,
                    "items": {
                        "type": "string",
                        "id": "http://jsonschema.ambit.sf.net/protocol/guideline/0",
                        "required": false
                    }
                },
                "topcategory": {
                    "type": "string",
                    "id": "http://jsonschema.ambit.sf.net/protocol/topcategory",
                    "required": false
                }
            }
        },
        "reliability": {
            "type": "object",
            "id": "http://jsonschema.ambit.sf.net/reliability",
            "required": true,
            "properties": {
                "r_isRobustStudy": {
                    "type": "string",
                    "id": "http://jsonschema.ambit.sf.net/reliability/r_isRobustStudy",
                    "required": false
                },
                "r_isUsedforClassification": {
                    "type": "string",
                    "id": "http://jsonschema.ambit.sf.net/reliability/r_isUsedforClassification",
                    "required": false
                },
                "r_isUsedforMSDS": {
                    "type": "string",
                    "id": "http://jsonschema.ambit.sf.net/reliability/r_isUsedforMSDS",
                    "required": false
                },
                "r_purposeFlag": {
                    "type": "string",
                    "id": "http://jsonschema.ambit.sf.net/reliability/r_purposeFlag",
                    "required": false
                },
                "r_studyResultType": {
                    "type": "string",
                    "id": "http://jsonschema.ambit.sf.net/reliability/r_studyResultType",
                    "required": false
                },
                "r_value": {
                    "type": "string",
                    "id": "http://jsonschema.ambit.sf.net/reliability/r_value",
                    "required": false
                }
            }
        }
    }
}