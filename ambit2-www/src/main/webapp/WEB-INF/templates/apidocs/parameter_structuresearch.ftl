			            {
			              "name": "b64search",
			              "description": "Base64 encoded SMILES or mol file; if included, will be used instead of the 'search' parameter",
			              "required": false,
			              "type": "string",
			              "paramType": "query",
			              "allowMultiple"  : false
			            },					            
			            {
			              "name": "type",
			              "description": "Defines the expected content of the search parameter",
			              "required": false,
			              "type": "string",
			              "enum" : ["smiles","mol", "url"],
			              "defaultValue" : "smiles",
			              "paramType": "query",
			              "allowMultiple"  : false
			            }					