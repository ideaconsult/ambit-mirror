var _ambit = {
	'search' : { 
		'uri': null, 
		'result': null,
		'options' : {
			'imageInTable': true,
			'showProperties' : false,
			'showRegistry' : true,
			'showSMILES' : false,
			'showInChI' : false,
			'showEndpoints' : true,
			'showCalculated' : true,
			'showNames' : true,
			'showSimilarity' : false			
		}
	},		
	'datasets' : [
		{
			"URI":"/dataset/17",
			"type":"Dataset",
			"title":"ChEBI_Search_Results",
			"rightsHolder":"Unknown",
			"seeAlso":"",
			"rights":{
				"URI":"http://ambit.sf.net/resolver/rights/Unknown",
				"type":"license"
			}
		},
		{
			"URI":"/dataset/11",
			"type":"Dataset",
			"title":"RepDose Mouse Inhalation Studies",
			"rightsHolder":"Unknown",
			"seeAlso":"http://www.repdose.de",
			"rights":{
				"URI":"http://ambit.sf.net/resolver/rights/Unknown",
				"type":"license"
			}
		}
	],
	'models' : [
		{
			"URI":"/ambit2/model/2",
			"title":"XLogP",
			"algorithm":{
				"URI":"/algorithm/org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor",
				"algFormat":"JAVA_CLASS",
				"img":"/ambit2/images/cdk.png"
			},
			"trainingDataset":"",
			"independent":"/model/2/independent",
			"dependent":"/model/2/dependent",
			"predicted":"/model/2/predicted",
			"ambitprop":{
				"legend":"/model/2?media=image/png",
				"creator":"guest",
				"mimetype":"application/java",
				"content":"org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor" 

			}
		},
		{
			"URI":"http://localhost:8080/ambit2/model/3",
			"title":"ToxTree: Cramer rules",
			"algorithm":{
				"URI":"http://localhost:8080/ambit2/algorithm/toxtreecramer",
				"algFormat":"JAVA_CLASS",
				"img":"/ambit2/images/toxtree.png"
			},
			"trainingDataset":"",
			"independent":"http://localhost:8080/ambit2/model/3/independent",
			"dependent":"http://localhost:8080/ambit2/model/3/dependent",
			"predicted":"http://localhost:8080/ambit2/model/3/predicted",
			"ambitprop":{
				"legend":"http://localhost:8080/ambit2/model/3?media=image/png",
				"creator":"guest",
				"mimetype":"application/java",
				"content":"toxTree.tree.cramer.CramerRules" 

			}
		}		
	]
};