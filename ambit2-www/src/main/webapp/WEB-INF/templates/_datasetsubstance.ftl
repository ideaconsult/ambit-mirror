<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl">
<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jquery-migrate-1.2.1.min.js.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/colResizable-1.3.min.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jtoxkit.js'></script>

<script language="JavaScript">
    
    function onSelectedUpdate(el) {
      var par = $(el).parents('.jtox-foldable')[0];
    	var tEl = $('.title', par)[0];
    	var v = $('input[type="checkbox"]:checked', par).length;
    	tEl.innerHTML = tEl.innerHTML.replace(/(.+)\((\d+)\/(\d+)(.*)?/, '$1(' + v + '/$3$4');;
    }
</script>    

<script type='text/javascript' src='${ambit_root}/scripts/config-substance.js'></script>

<script type='text/javascript'>
		
	$(document).ready(function() {
	    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/substance" title="Substance">Substances</a></li>');
	    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/substanceowner" title="Substance">Substances per owner</a></li>');
		jQuery("#breadCrumb ul").append('<li><a href="${ambit_request}" title="${ambit_request}">Dataset</a></li>');
		jQuery("#breadCrumb").jBreadCrumb();

	  	var ds = new jToxCompound($(".jtox-toolkit")[0],config_substancedataset);
        ds.queryDataset("${ambit_request}");
	});
	</script>

<script language="JavaScript">    
    function onDetailedRow(row, data, index) {
     console.log("onDetailedRow");
      var el = $('.jtox-details-composition', row);
      var uri = $(el).data('uri');
      console.log(uri);

      el = $(el).parents('table')[0];
      el = el.parentNode;
      $(el).empty();
      var div = document.createElement('div');
      el.appendChild(div);
      var ds = new jToxSubstance(div, {
      			crossDomain: true, 
      			substanceUri: uri, 
      			showControls: false,
      			showDiagrams: true , 
      			onDetails: function (root, data, event) {
        			var comp = new jToxStudy(root);
        			comp.querySubstance(data);
      			} 
      		} );
    }
        
</script>
   
</head>
<body>


<div class="container" style="margin:0;padding:0;">

<#include "/banner_crumbs.ftl">

		<!-- Page Content
		================================================== -->


<div class="sixteen columns " style="padding:0 2px 2px 2px 0;margin-top:5px;margin-left:25;margin-right:25;">		
	

	<div class="jtox-toolkit" data-kit="compound" 
	data-manual-init="yes" 
	data-show-features="yes" 
	data-cross-domain="false"	
	data-show-export="yes" 
	data-jsonp="false"></div></div>


<div class='row add-bottom' style="height:140px;">&nbsp;</div>



<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
