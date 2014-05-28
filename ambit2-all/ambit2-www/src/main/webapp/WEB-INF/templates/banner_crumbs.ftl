
<!-- banner -->
<div class="row half-bottom" id="header" style="padding-top:5px">
	<div class="two columns">
	<a href="${ambit_root}/ui"><img class='scale-with-grid' border='0' src='${ambit_root}/images/ambit-logo-small.png' title='Home' alt='AMBIT logo'></a>
	</div>
	<div class="fourteen columns remove-bottom">
		<#include "/menu/smartmenu.ftl">
	</div>
</div>
<div class="row remove-bottom" id="header">
	<#include "/toplinks.ftl">
</div>
<div class="row half-bottom">
<!--
		<div id='welcome' class="one column remove-bottom help">&nbsp;</div>
		-->
		<div class="thirteen columns remove-bottom" style="padding-left:10px;">
			<div id="breadCrumb" class="breadCrumb module remove-bottom h5">
                    <ul>
                        <li>
                            <a href="${ambit_root}" title="AMBIT Home">Home</a>
                        </li>
                    </ul>
			</div>
		</div>
</div>	


<script type='text/javascript'>
	$(document).ready(function() {    
		$( "#smartmenu" ).smartmenus();    
	});
 </script>