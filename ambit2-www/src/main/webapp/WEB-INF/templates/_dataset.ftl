<#include "/html.ftl" >
<head>
<#include "/header.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jquery-migrate-1.2.1.min.js.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/colResizable-1.3.min.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jtoxkit.js'></script>

<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>

<script type='text/javascript'>
	var purl = $.url();
	var dataset_uri = purl.param('dataset_uri')===undefined?'${ambit_root}/query/similarity?search=c1ccccc1':purl.param('dataset_uri');
			
	$(document).ready(function() {

		$('#current').text(dataset_uri);
		$('#dataseturi').val(dataset_uri);	  
	    datasetAutocomplete(".dataseturi","${ambit_root}/dataset",10);

	  	 var ds = new jToxDataset($(".jtox-toolkit")[0]);
         ds.queryDataset(dataset_uri);
	});
	</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">

<form method='GET' name='searchform' id='searchform' action='${ambit_root}/ui/_dataset' style='padding:0;margin:0;'>
<!-- banner -->
<div class="row remove-bottom" id="header">
	<#include "/toplinks.ftl">
</div>
<div class="row remove-bottom">
		<#include "/logo.ftl">
		<div class="thirteen columns remove-bottom" id="query">
		<div class="six columns alpha">
			<div class="remove-bottom h3"   id='current'>
				Dataset browser
			</div>
		    <div class='help'>
		     Type first letters of a dataset name in the box to get a list of datasets.<br/>
		     Or view <a href='${ambit_root}/dataset?max=1000' target='datasets' title='All datasets list'>All datasets</a>
		    </div>			
		</div>
		<div class="four columns omega">
			<div class="remove-bottom h3" >
				&nbsp;
			</div>
		    <div class='h6'>
		    	Enter dataset name
		    </div>			
		</div>			
		<div class="four columns omega">
			<div class="remove-bottom h3">
				&nbsp;
			</div>
		    <div class='h6'>
		    	<input type='text' class='dataseturi' id='dataseturi' name='dataset_uri' value='' tabindex='1' >
		    </div>			
		</div>		
		<div class="two columns omega">
			<div class="remove-bottom h3" >
				&nbsp;
			</div>
		    <div class='h6'>
		    	<input class='ambit_search ' id='submit' type='submit' value='Browse' tabindex='2'>
		    </div>			
		</div>	
		</div>
</div>		
<div class="row remove-bottom" >
	  <div id="header_bottom" class="remove-bottom">&nbsp;</div>
</div>

</form>
<div class="row" style="margin-left:20px;margin-right:20px;padding:ppx;">
		<!-- Page Content
		================================================== -->

<div class="jtox-toolkit" data-kit="dataset" data-manual-init="yes" data-show-features="yes" data-show-export="yes" data-jsonp="true"></div>
<div class="jtox-template">
<!--[[ jToxKit.templates['all-dataset'] -->
          <div id="jtox-dataset">
            <div class="jtox-ds-features"></div>
            <div class="jtox-ds-tables">
              <div class="jtox-ds-fixed">
                <table></table>
              </div><div class="jtox-ds-variable">
                <table></table>
              </div>
            </div>
          </div>
<!-- // end of #jtox-dataset ]]-->
<!--[[ jToxKit.templates['dataset-one-feature'] -->
    <div id="jtox-ds-feature" class="jtox-ds-feature"><input type="checkbox" checked="yes" class="jtox-checkbox" /><span class="data-field jtox-title" data-field="title"> ? </span></div>
<!-- // end of #jtox-ds-feature ]]-->
<!--[[ jToxKit.templates['dataset-export'] -->
    <div id="jtox-ds-download" class="jtox-inline jtox-ds-download">
      <a target="_blank"><img class="borderless"/></a>
    </div>
<!-- // end of #jtox-ds-feature ]]-->
<!--[[ jToxKit.templates['dataset-export'] -->
    <div id="jtox-ds-export">
      <div class="jtox-inline">Download dataset as: </div>
      <div class="jtox-inline jtox-exportlist"></div>
    </div>
<!-- // end of #jtox-ds-feature ]]-->
<!--[[ jToxKit.templates['dataset-details'] -->
    <div id="jtox-one-detail">
      <span class="right data-field" data-field="title" style="width: 30%"></span>
      <span class="left data-field" data-field="value" style="width: 70%"></span>
    </div>
<!-- // end of #jtox-ds-feature ]]-->
  
  </div>		


<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>



<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
