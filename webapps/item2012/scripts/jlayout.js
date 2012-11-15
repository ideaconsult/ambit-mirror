function initLayout() {
//layout
		var myLayout = $('body').layout({
			closable:false,
			resizable				:false,
			slidable				:true,
			livePaneResizing		:true,
			west__closable			: true,
			west__slidable			: true,
			west__togglerLength_closed: '100%',
			west__spacing_closed:		20,
			north__spacing_open		:0,
			north__spacing_closed	:0,
			south__spacing_open		:0,
			south__spacing_closed	:0,
			south__size				:64,
			south__minSize			:64,
			south__maxSize			:64
		});
		myLayout.sizePane("north", 48);
		myLayout.sizePane("west", 180);
		myLayout.options.west.resizable = false;
}						