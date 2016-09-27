<#if custom_structurequery??>
	<#assign custom_structurequery=custom_structurequery?url('UTF-8')>
</#if>

			<ul id='smartmenu' class="sm sm-mint">
				<li>
				<a href=""${ambit_root}" title="Home">Home</a>
			</li>
			<li>
	<li><a href="${ambit_root}/ui/_search?search=${custom_structurequery!'phenol'}" title="Chemical structure search">Structure search</a></li>
	
</li>

			
			<li>
				<a href="#">Help</a>
					<ul>


							<li> <a href="#">Guide</a>
								<ul>
								<li><a href='http://ambit.sourceforge.net/usage.html' target='guide'>Search by chemical structure</a></li>
							  <li><a href='http://ideaconsult.github.io/examples-ambit/apidocs/' target=_blank title="API documentation in Swagger JSON format">API Documentation</a></li>								
								<li><a href="http://ambit.sourceforge.net" target=_blank title="ExCAPE database is customized version of AMBIT web services">ambit.sf.net</a>	</li>									 
								</ul>
							</li>
							<li><a href='http://ambit.sourceforge.net/support.html' target='report' title='Found an error? Report here'>Submit an issue</a></li>	
							</li>
								<li><a href="http://excape-h2020.eu/" target=_blank title="This project has received funding from the European Union's Horizon 2020 Research and Innovation programme under Grant Agreement no. 671555">EU H2020 FET #671555</a></li>

								<li><a href='#' onClick='$( "#about-message" ).dialog("open");'>About</a></li>
								</ul>
							</li>
						
						</ul>				
			</li>			
							
			</ul>