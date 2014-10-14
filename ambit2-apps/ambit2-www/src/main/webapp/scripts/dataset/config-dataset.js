var config_dataset = {
  "baseFeatures": {
		"http://www.opentox.org/api/1.1#CompositionInfo" : {
		  "visibility": "details",
			"title": "Composition",
			"data": "compound.URI",
			"column": { bVisible: false },
			"basic": true,
			"render" : function(data, type, full) {
        return (type != "details") ? "-" : '<span class="jtox-details-composition" data-URI="' + data + '"></span>';
      }
		},
		"http://www.opentox.org/api/1.1#Similarity" : {
			"title": "Similarity",
			"data" : "compound.metric", // you need to point where is the information located
			"accumulate": true,
			"render" : function(data, type, full) {
				return data;
			}
		}
  },
  "groups": createGroups,
	"columns": {
  	"dataset": {
    	'Info': { bVisible: false },
    	'Id': { sWidth: "25%" },
    	'Title': { sWidth: "45%" },
    	'Stars': { sWidth: "30%" }
  	},
  	"model": { 
    	'Info': { bVisible: false },
    	'Algorithm': { bVisible: false },
    	'Id': { sWidth: "25%" },
    	'Title': { sWidth: "45%" },
    	'Stars': { sWidth: "30%" }
  	},
  	"substance": {
    	'Contained in as': { iOrder: 20, mData: "composition", sTitle: "Contained in as", mRender: jT.ui.renderRelation }
  	}
	}
};
