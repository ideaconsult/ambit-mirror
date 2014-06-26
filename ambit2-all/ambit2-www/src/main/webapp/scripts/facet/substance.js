var facet = {
		root: null,
		substanceComponent : null,
		searchStudy : function () {
			var selected = $("input[name^='category']:checked:enabled",'#fsearchForm');
			var params = [];
			$.each(selected,function(index,value) {
				var item = {};
				item['name']  = $(value).attr('name');
				item['value'] = $(value).attr('value');
				params.push(item);
			});
			if (this.substanceComponent != undefined && this.substanceComponent!= null)  {
				var substanceQuery =  this.root + '/substance?' + jQuery.param(params);
				this.substanceComponent.querySubstance(substanceQuery);
			}	
		}		
}
