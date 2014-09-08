{
	<#include "/apidocs/version.ftl" >	
    "resourcePath": "/substance",
    "produces": [
        "application/json",
        "text/n3",
        "text/html"
    ],
    "apis": [
        {
            "path": "/substance/{substanceID}",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Find substance by ID",
                    "notes": "Returns a substance based on ID",
                    "type": "Substance",
                    "nickname": "getSubstanceById",
                    "authorizations": {},
                    "parameters": [
                        {
                            "name": "substanceId",
                            "description": "ID of substance that needs to be fetched",
                            "required": true,
                            "type": "string",
                            "paramType": "path",
                            "allowMultiple": false
                        }
                    ],
                    "responseMessages": [
                        {
                            "code": 400,
                            "message": "Invalid ID supplied"
                        },
                        {
                            "code": 404,
                            "message": "Substance not found"
                        }
                    ]
                },
                {
                    "method": "POST",
                    "summary": "Updates a substance in the store with form data",
                    "notes": "",
                    "type": "void",
                    "nickname": "updateSubstanceWithForm",
                    "consumes": [
                        "application/x-www-form-urlencoded"
                    ],
                    "authorizations": {
                        "oauth2": [
                            {
                                "scope": "write:substances",
                                "description": "modify substances in your account"
                            }
                        ]
                    },
                    "parameters": [
                        {
                            "name": "substanceID",
                            "description": "ID of substance that needs to be updated",
                            "required": true,
                            "type": "string",
                            "paramType": "path",
                            "allowMultiple": false
                        },
                        {
                            "name": "name",
                            "description": "Updated name of the substance",
                            "required": false,
                            "type": "string",
                            "paramType": "form",
                            "allowMultiple": false
                        },
                        {
                            "name": "status",
                            "description": "Updated status of the substance",
                            "required": false,
                            "type": "string",
                            "paramType": "form",
                            "allowMultiple": false
                        }
                    ],
                    "responseMessages": [
                        {
                            "code": 405,
                            "message": "Invalid input"
                        }
                    ]
                },
                {
                    "method": "DELETE",
                    "summary": "Deletes a substance",
                    "notes": "",
                    "type": "void",
                    "nickname": "deleteSubstance",
                    "authorizations": {
                        "oauth2": [
                            {
                                "scope": "write:substances",
                                "description": "modify substances in your account"
                            }
                        ]
                    },
                    "parameters": [
                        {
                            "name": "substanceID",
                            "description": "Substance id to delete",
                            "required": true,
                            "type": "string",
                            "paramType": "path",
                            "allowMultiple": false
                        }
                    ],
                    "responseMessages": [
                        {
                            "code": 400,
                            "message": "Invalid substance value"
                        }
                    ]
                }
                
            ]
        },
        {
            "path": "/substance",
            "operations": [
                {
                    "method": "POST",
                    "summary": "Add a new substance(s)",
                    "notes": "",
                    "type": "void",
                    "nickname": "addSubstance",
                    "consumes": [
                        "application/json",
                        "text/csv"
                    ],
                    "authorizations": {
                        "oauth2": [
                            {
                                "scope": "write:substances",
                                "description": "modify substances in your account"
                            }
                        ]
                    },
                    "parameters": [
                        {
                            "name": "body",
                            "description": "substance object that needs to be added to the store",
                            "required": true,
                            "type": "substance",
                            "paramType": "body",
                            "allowMultiple": false
                        }
                    ],
                    "responseMessages": [
                        {
                            "code": 405,
                            "message": "Invalid input"
                        }
                    ]
                },
                {
                    "method": "PUT",
                    "summary": "Update an existing substance",
                    "notes": "",
                    "type": "void",
                    "nickname": "updatesubstance",
                    "authorizations": {},
                    "parameters": [
                        {
                            "name": "body",
                            "description": "substance object that needs to be updated in the store",
                            "required": true,
                            "type": "substance",
                            "paramType": "body",
                            "allowMultiple": false
                        }
                    ],
                    "responseMessages": [
                        {
                            "code": 400,
                            "message": "Invalid ID supplied"
                        },
                        {
                            "code": 404,
                            "message": "substance not found"
                        },
                        {
                            "code": 405,
                            "message": "Validation exception"
                        }
                    ]
                }
            ]
        }
    ],
    "models": {
        "Tag": {
            "id": "Tag",
            "properties": {
                "id": {
                    "type": "integer",
                    "format": "int64"
                },
                "name": {
                    "type": "string"
                }
            }
        },
        "substance": {
            "id": "substance",
            "required": [
                "id",
                "name"
            ],
            "properties": {
                "id": {
                    "type": "integer",
                    "format": "int64",
                    "description": "unique identifier for the substance",
                    "minimum": "0.0",
                    "maximum": "100.0"
                },
                "category": {
                    "$ref": "Category"
                },
                "name": {
                    "type": "string"
                },
                "photoUrls": {
                    "type": "array",
                    "items": {
                        "type": "string"
                    }
                },
                "tags": {
                    "type": "array",
                    "items": {
                        "$ref": "Tag"
                    }
                },
                "status": {
                    "type": "string",
                    "description": "substance status",
                    "enum": [
                        "available",
                        "pending",
                        "sold"
                    ]
                }
            }
        },
        "Category": {
            "id": "Category",
            "properties": {
                "id": {
                    "type": "integer",
                    "format": "int64"
                },
                "name": {
                    "type": "string"
                }
            }
        }
    },
    	<#include "/apidocs/info.ftl" >  
}