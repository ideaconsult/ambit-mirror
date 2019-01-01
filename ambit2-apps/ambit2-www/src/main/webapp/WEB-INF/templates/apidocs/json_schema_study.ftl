{
    "type": "object",
    "$schema": "http://json-schema.org/draft-03/schema",
    "id": "http://jsonschema.opentox.org/",
    "required": false,
    "properties": {
        "citation": {
            "type": "object",
            "id": "http://jsonschema.opentox.org/citation",
            "required": false,
            "properties": {
                "owner": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/citation/owner",
                    "required": false
                },
                "title": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/citation/title",
                    "required": false
                },
                "year": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/citation/year",
                    "required": false
                }
            }
        },
        "effects": {
            "type": "array",
            "id": "http://jsonschema.opentox.org/effects",
            "required": true,
            "items": {
                "$ref": "Effect"
            }
        },
        "interpretation": {
            "type": "object",
            "id": "http://jsonschema.opentox.org/interpretation",
            "required": false,
            "properties": {
                "criteria": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/interpretation/criteria",
                    "required": false
                },
                "result": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/interpretation/result",
                    "required": false
                }
            }
        },
        "owner": {
            "type": "object",
            "id": "http://jsonschema.opentox.org/owner",
            "required": false,
            "properties": {
                "company": {
                    "type": "object",
                    "id": "http://jsonschema.opentox.org/owner/company",
                    "required": false,
                    "properties": {
                        "name": {
                            "type": "string",
                            "id": "http://jsonschema.opentox.org/owner/company/name",
                            "required": true
                        },
                        "uuid": {
                            "type": "string",
                            "id": "http://jsonschema.opentox.org/owner/company/uuid",
                            "required": true
                        }
                    }
                },
                "substance": {
                    "type": "object",
                    "id": "http://jsonschema.opentox.org/owner/substance",
                    "required": true,
                    "properties": {
                        "uuid": {
                            "type": "string",
                            "id": "http://jsonschema.opentox.org/owner/substance/uuid",
                            "description": "Unique identifier for the substance",
                            "required": true
                        }
                    }
                }
            }
        },
        "parameters": {
            "type": "object",
            "id": "http://jsonschema.opentox.org/parameters",
            "required": false,
            "properties": {
                "Sex": {
                    "type": "null",
                    "id": "http://jsonschema.opentox.org/parameters/Sex",
                    "required": false
                },
                "Species": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/parameters/Species",
                    "required": false
                },
                "Study year": {
                    "type": "null",
                    "id": "http://jsonschema.opentox.org/parameters/Study year",
                    "required": false
                }
            }
        },
        "protocol": {
            "type": "object",
            "id": "http://jsonschema.opentox.org/protocol",
            "required": true,
            "properties": {
                "category": {
                    "type": "object",
                    "id": "http://jsonschema.opentox.org/protocol/category",
                    "required": false,
                    "properties": {
                        "code": {
                            "type": "string",
                            "id": "http://jsonschema.opentox.org/protocol/category/code",
                            "required": false
                        },
                        "title": {
                            "type": "string",
                            "id": "http://jsonschema.opentox.org/protocol/category/title",
                            "required": false
                        }
                    }
                },
                "endpoint": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/protocol/endpoint",
                    "required": false
                },
                "guideline": {
                    "type": "array",
                    "id": "http://jsonschema.opentox.org/protocol/guideline",
                    "required": false,
                    "items": {
                        "type": "string",
                        "id": "http://jsonschema.opentox.org/protocol/guideline/0",
                        "required": false
                    }
                },
                "topcategory": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/protocol/topcategory",
                    "required": false
                }
            }
        },
        "reliability": {
            "type": "object",
            "id": "http://jsonschema.opentox.org/reliability",
            "required": true,
            "properties": {
                "r_isRobustStudy": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/reliability/r_isRobustStudy",
                    "required": false
                },
                "r_isUsedforClassification": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/reliability/r_isUsedforClassification",
                    "required": false
                },
                "r_isUsedforMSDS": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/reliability/r_isUsedforMSDS",
                    "required": false
                },
                "r_purposeFlag": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/reliability/r_purposeFlag",
                    "required": false
                },
                "r_studyResultType": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/reliability/r_studyResultType",
                    "required": false
                },
                "r_value": {
                    "type": "string",
                    "id": "http://jsonschema.opentox.org/reliability/r_value",
                    "required": false
                }
            }
        },
        "uuid": {
            "type": "string",
            "id": "http://jsonschema.opentox.org/uuid",
            "description": "Unique identifier for the study document",
            "required": true
        }
    }
}