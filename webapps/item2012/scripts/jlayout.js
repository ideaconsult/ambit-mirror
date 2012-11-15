function initLayout() {
//layout
		var myLayout = $('body').layout({
			//applyDefaultStyles		: true,
			closable				: false,
			resizable				: false,
			slidable				: true,
			livePaneResizing		: true,
			west__closable			: true,
			west__slidable			: true,
			west__togglerLength_closed: '100%',
			west__spacing_closed:		20,
			north__spacing_open		:0,
			north__spacing_closed	:0,
			south__spacing_open		:0,
			south__spacing_closed	:0,
			east__spacing_open		:0,
			east__spacing_closed	:0,
			south__size				:48,
			south__minSize			:48,
			south__maxSize			:48,
			north__size				:48,
			west__size				:200,
			east__size				:200
		});
		myLayout.close('west');
}						