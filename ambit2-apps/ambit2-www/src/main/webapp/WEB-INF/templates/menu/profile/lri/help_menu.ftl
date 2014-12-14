<ul>
	<li><a href="#">OpenTox API</a>
	<ul>
		<li><a href='${ambit_root}/api-docs' target=_blank title="API documentation in Swagger JSON format">API Documentation</a></li>
		<li><a href='http://ideaconsult.github.io/examples-ambit/apidocs/' class="qxternal" target=_blank title="API documentation via swagger-ui">API Documentation (public services)</a></li>
		<li><a href="${ambit_root}/compound" title="Chemical compound">Compound</a></li>
		<li><a href="${ambit_root}/dataset">Datasets</a></li>
		<li><a href="${ambit_root}/feature" title="Features (identifiers, measured and calculated properties)">Features</a></li>
		<li><a href="${ambit_root}/algorithm">Algorithm</a></li>
		<li><a href="${ambit_root}/model">Model</a></li>
		<li>
			<a href="${ambit_root}">Search</a>
			<ul>
				<li><a href="${ambit_root}/query/similarity?search=c1ccccc1Oc2ccccc2&threshold=0.9">Similarity</a></li>
				<li><a href="${ambit_root}/query/smarts?text=%22%22">Substructure</a></li>
			
			</ul>
		</li>
	</ul>
	</li>
	<li>
		<a href="${ambit_root}/depict">Demo</a>
		<#include "/menu/profile/lri/demo_menu.ftl">
	</li>		
	<li><a href="#">About</a>
		<ul>
		<li><a href="#">Version</a>
			<ul class="mega-menu">
			<li>
				<div style="width:400px;max-width:100%;">
	          		<div style="padding:5px 24px;">
						<div class='h6' style='color:#729203;' title='${ambit_version_long}'>AMBIT v${ambit_version_short}</div>
					</div>
				</div>		
			</li>
			</ul>
		</li>
		<li><a href="http://www.ideaconsult.net/" title="Developed by IdeaConsult Ltd.">IdeaConsult</a>	</li>
		<li><a href="http://cefic-lri.org/" title='This project has received funding from CEFIC Long Range  Research Initiative'>CEFIC LRI</a>	</li>
		</ul>
	</li>

</ul>