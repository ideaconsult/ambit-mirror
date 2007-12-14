 <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                 xmlns="http://www.w3.org/1999/xhtml">
   <xsl:template match="QMRF">
     <html xmlns="http://www.w3.org/1999/xhtml">
       <title>QMRF</title><body>
       	 <h2>QSAR Model Reporting Format</h2>
       	 <table>
       	 <tr>
       	 <td>Version:</td><td> <xsl:value-of select="@version" /> </td>
         </tr>
         <tr>
       	 <td>Name:</td><td> <xsl:value-of select="@name" /> </td>
         </tr>
         <tr>
         <td>Author:</td><td> <xsl:value-of select="@author" /></td>
         </tr>
         <tr>
		 <td>Date:</td><td><xsl:value-of select="@date" /> </td>
         </tr>
         <tr>
         <td>Contact:</td><td><xsl:value-of select="@contact" /> </td>
         </tr>
         <tr>
		 <td>e-mail:</td><td><xsl:value-of select="@email" /> </td>                  
         </tr>
         <tr>
         <td>www:</td><td>
       		<xsl:call-template name="print_href"/>
         </td>
         </tr>
         </table>
	
	  	 <xsl:apply-templates select="/QMRF/QMRF_chapters/QSAR_identifier"/>
	  	 <xsl:apply-templates select="/QMRF/QMRF_chapters/QSAR_General_information"/>
	  	 <xsl:apply-templates select="/QMRF/QMRF_chapters/QSAR_Endpoint"/>	
	  	 <xsl:apply-templates select="/QMRF/QMRF_chapters/QSAR_Algorithm"/>		
	  	 
  		<div style="color:#FF0000;">
	  	 More (under development)
	  	 </div>
	  	 <xsl:apply-templates select="/QMRF/QMRF_chapters/QSAR_Miscelaneous"/>		 	  	 
	  	  	 
	  	   	 
      	<!-- 

          <xsl:for-each select="/QMRF/QMRF_chapters//*"> 
               <xsl:call-template name="QMRF_chapters"/> 
          </xsl:for-each> 
 -->
 		 <hr></hr>
		  <h2>Catalogs</h2>	
		  <h3>Software catalog</h3>	
 			<table border="0">
	          <xsl:for-each select="/QMRF/Catalogs/software_catalog/*"> 
               <xsl:call-template name="print_software"/>  
    	      </xsl:for-each>  
			</table>    
			
					  <hr></hr>
		  <h3>References</h3>	
 			<table border="0">
	          <xsl:for-each select="/QMRF/Catalogs/publications_catalog/*"> 
               <xsl:call-template name="print_publication"/> 
    	      </xsl:for-each>  
			</table>    
		  <hr></hr>			
		  <h3>Endpoints catalog</h3>	
 			<table border="0">
	          <xsl:for-each select="/QMRF/Catalogs/endpoints_catalog/*"> 
               <xsl:call-template name="print_endpoint"/> 
    	      </xsl:for-each>  
			</table>  					
	
		  <hr></hr>
		  <h3>Species catalog</h3>	
 			<table border="0">
	          <xsl:for-each select="/QMRF/Catalogs/epecies_catalog/*"> 
               <xsl:call-template name="print_species"/> 
    	      </xsl:for-each>  
			</table>  			

     </body></html>
   </xsl:template>

   <xsl:template name="QMRF_chapters">
   		<h3>
  		<xsl:value-of select="@chapter"/> <xsl:text>. </xsl:text> <xsl:value-of select="@name"/>
  		</h3>
        <xsl:call-template name="print_help"/> 
   </xsl:template>
   
   <xsl:template name="QMRF_chapters_text">
   		<h3>
  		<xsl:value-of select="@chapter"/> <xsl:text>. </xsl:text> <xsl:value-of select="@name"/>
  		</h3>
        <xsl:call-template name="print_help"/> 
        <div><xsl:value-of select="text()"/></div>
   </xsl:template>

   
   <xsl:template name="print_help">
  		<div style="color:#AAAAAA;">
  		<xsl:value-of select="@help"/>
  		</div>   
   </xsl:template>
   <xsl:template name="Catalogs">
   		Catalog
   		<div>
  		<xsl:value-of select="@id"/> <xsl:text>. </xsl:text> <xsl:value-of select="@name"/>
  		</div>
   </xsl:template>
   <!-- Chapter 1 -->   
   <xsl:template match="QSAR_identifier">
   		<xsl:call-template name="QMRF_chapters"/>
        <xsl:for-each select="QSAR_id1"> 
            <xsl:call-template name="QMRF_chapters_text"/> 
        </xsl:for-each>     

        <xsl:for-each select="QSAR_id2"> 
            <xsl:call-template name="QMRF_chapters_text"/> 
        </xsl:for-each>   

        <xsl:for-each select="QSAR_software"> 
            <xsl:call-template name="QMRF_chapters"/> 
        </xsl:for-each>   

        <div> 
         <xsl:for-each select="QSAR_software/software_ref"> 
	         <a href="#{@idref}">
           	 [<xsl:value-of select="id(@idref)/@name"/>] 
           	 </a>
 	     </xsl:for-each>          
	
        </div>          
   </xsl:template>
   
    <!-- Chapter 2 -->   
   <xsl:template match="QSAR_General_information">
  		<xsl:call-template name="QMRF_chapters"/>
        <xsl:for-each select="qmrf_date"> 
            <xsl:call-template name="QMRF_chapters_text"/> 
        </xsl:for-each>   
        
   		<h4>
        <xsl:value-of select="qmrf_authors/@chapter"/> . <xsl:value-of select="qmrf_authors/@name"/>         
        </h4>  	
         <xsl:for-each select="qmrf_authors/author_ref"> 
             <xsl:call-template name="print_author_reference"/>, 
 	     </xsl:for-each>          
        <xsl:for-each select="qmrf_date_revision"> 
            <xsl:call-template name="QMRF_chapters_text"/> 
        </xsl:for-each>   

        <xsl:for-each select="qmrf_revision"> 
            <xsl:call-template name="QMRF_chapters_text"/> 
        </xsl:for-each> 
         	
        <xsl:for-each select="model_authors"> 
            <xsl:call-template name="QMRF_chapters_text"/> 
         <xsl:for-each select="model_authors/author_ref"> 
             <xsl:call-template name="print_author_reference"/>, 
 	     </xsl:for-each>          
            
        </xsl:for-each>          	
    
        <xsl:for-each select="model_date"> 
            <xsl:call-template name="QMRF_chapters_text"/> 
        </xsl:for-each>     

       <xsl:for-each select="references"> 
            <xsl:call-template name="QMRF_chapters_text"/> 
        </xsl:for-each>   

	          <xsl:for-each select="references/book_ref"> 
	               [<xsl:call-template name="print_reference"/>] 
    	      </xsl:for-each>  			
	          <xsl:for-each select="references/article_ref"> 
	               [<xsl:call-template name="print_reference"/>] 
    	      </xsl:for-each>          

        <xsl:for-each select="info_availability"> 
            <xsl:call-template name="QMRF_chapters_text"/> 
        </xsl:for-each>                      

        <xsl:for-each select="related_models"> 
            <xsl:call-template name="QMRF_chapters_text"/> 
        </xsl:for-each>                      
   </xsl:template>

   <!-- Chapter 3 -->   
   <xsl:template match="QSAR_Endpoint">
  		<xsl:call-template name="QMRF_chapters"/>
   		<h4>
        <xsl:value-of select="model_species/@chapter"/> . <xsl:value-of select="model_species/@name"/>         
        </h4>  		
        <div> 
	          <xsl:for-each select="model_species/species_ref"> 
	               <xsl:call-template name="print_species_reference"/> 
    	      </xsl:for-each>          
        </div>
   		<h4>
        <xsl:value-of select="model_endpoint/@chapter"/> . <xsl:value-of select="model_endpoint/@name"/>         
        </h4>  		        
        <div>
	          <xsl:for-each select="model_endpoint/endpoint_ref"> 
	               <xsl:call-template name="print_name_reference"/> 
    	      </xsl:for-each>             
        </div>   
   		<h4>
        <xsl:value-of select="endpoint_comments/@chapter"/> . <xsl:value-of select="endpoint_comments/@name"/>         
        </h4>  		        
        <div> <xsl:value-of select="endpoint_comments"/> </div>        
   		<h4>
        <xsl:value-of select="endpoint_units/@chapter"/> . <xsl:value-of select="endpoint_units/@name"/>         
        </h4>  		        
        <div> <xsl:value-of select="endpoint_units"/> </div>      
   		<h4>
        <xsl:value-of select="endpoint_variable/@chapter"/> . <xsl:value-of select="endpoint_variable/@name"/>         
        </h4>  		        
        <div> <xsl:value-of select="endpoint_variable"/> </div>      
   		<h4>
        <xsl:value-of select="endpoint_protocol/@chapter"/> . <xsl:value-of select="endpoint_protocol/@name"/>         
        </h4>  		        
        <div> <xsl:value-of select="endpoint_protocol"/> </div>      
   		<h4>
        <xsl:value-of select="endpoint_data_quality/@chapter"/> . <xsl:value-of select="endpoint_data_quality/@name"/>         
        </h4>  		        
        <div> <xsl:value-of select="endpoint_data_quality"/> </div>                      
                       
   </xsl:template>   
     <!-- Chapter 4 -->   
   <xsl:template match="QSAR_Algorithm">
  		<xsl:call-template name="QMRF_chapters"/>
   		<h4>
        <xsl:value-of select="algorithm_type/@chapter"/> . <xsl:value-of select="algorithm_type/@name"/>         
        </h4>  		
        <div> <xsl:value-of select="algorithm_type"/> </div>  
   		<h4>
        <xsl:value-of select="algorithm_explicit/@chapter"/> . <xsl:value-of select="algorithm_explicit/@name"/>         
        </h4>  		
        <div> <xsl:value-of select="algorithm_explicit"/> </div>  
   		<h4>
        <xsl:value-of select="algorithm_references/@chapter"/> . <xsl:value-of select="algorithm_references/@name"/>         
        </h4>  		
        <div> <xsl:value-of select="algorithm_references"/> </div>       
   		<h4>
        <xsl:value-of select="algorithm_comments/@chapter"/> . <xsl:value-of select="algorithm_comments/@name"/>         
        </h4>  		
        <div> <xsl:value-of select="algorithm_comments"/> </div>               
   		<h4>
        <xsl:value-of select="algorithms_descriptors/@chapter"/> . <xsl:value-of select="algorithms_descriptors/@name"/>         
        </h4>  		
        <div> <xsl:value-of select="algorithms_descriptors"/> </div>   
   		<h4>
        <xsl:value-of select="descriptors_selection/@chapter"/> . <xsl:value-of select="descriptors_selection/@name"/>         
        </h4>  		
        <div> <xsl:value-of select="descriptors_selection"/> </div>                              
   		<h4>
        <xsl:value-of select="descriptors_generation/@chapter"/> . <xsl:value-of select="descriptors_generation/@name"/>         
        </h4>  		
        <div> <xsl:value-of select="descriptors_generation"/> </div>  
   		<h4>
        <xsl:value-of select="descriptors_generation_software/@chapter"/> . <xsl:value-of select="descriptors_generation_software/@name"/>         
        </h4>  		
        <div> 
         <xsl:for-each select="descriptors_generation_software/software_ref"> 
	         <a href="#{@idref}">
           	 [<xsl:value-of select="id(@idref)/@name"/>] 
           	 </a>
 	     </xsl:for-each>          

        </div>
   		<h4>
        <xsl:value-of select="descriptors_chemicals_ratio/@chapter"/> . <xsl:value-of select="ddescriptors_chemicals_ratio/@name"/>         
        </h4>  		
        <div> <xsl:value-of select="descriptors_chemicals_ratio"/> </div>                                                    
   </xsl:template>    
   
   <!-- Chapter 9 -->   
   <xsl:template match="QSAR_Miscelaneous">
   		<xsl:call-template name="QMRF_chapters"/>
        <xsl:for-each select="comments"> 
            <xsl:call-template name="QMRF_chapters"/> 
	        <div> <xsl:value-of select="text()"/> </div>            
        </xsl:for-each>     


        <xsl:for-each select="attachments"> 
            <xsl:call-template name="QMRF_chapters"/> 
            
            <table>
            <tr>
            <td>Training data set</td>
            <td>
 	        <xsl:for-each select="attachment_training_data"> 
 	      	        <xsl:for-each select="molecules"> 
 	      	        <div>	
						  <xsl:call-template name="print_href"/>
					</div>							  
	 		     </xsl:for-each>          
 		     </xsl:for-each>          
 		    </td> 
            </tr><tr>
            <td>Validation data set</td>
            <td>
 	        <xsl:for-each select="attachment_validation_data"> 
 	      	        <xsl:for-each select="molecules"> 
 	      	        	  <div>	
						  <xsl:call-template name="print_href"/>
						  </div>
	 		     </xsl:for-each>          
 		     </xsl:for-each>   
 		     </td>
            </tr><tr>	
            <td> Other documents</td>
            <td>            	     
 	        <xsl:for-each select="attachment_documents"> 
 	      	        <xsl:for-each select="document"> 
 	      	        <div>	
						  <xsl:call-template name="print_href"/>
					</div>							  
	 		     </xsl:for-each>          
 		     </xsl:for-each>   
 		     </td>
 		     </tr>     
 		     </table> 
        </xsl:for-each>   
        <div> 
	
        </div>          
   </xsl:template>
   
   <!-- Misc templates -->     
     <xsl:template name="print_software"> 
			<tr>
			<a name="{@id}"/> <!-- bookmark -->
			<td><b><xsl:value-of select="@name"/></b></td>
			<td><xsl:value-of select="contact"/></td>			
			</tr>
			<tr>
			<td>Description:</td>
			<td><i><xsl:value-of select="software_description"/></i></td>			
			</tr>
			<tr>
			<td>Reference:</td>
			<td>
	          <xsl:for-each select="references/publication_ref"> 
	               [<xsl:call-template name="print_reference"/>] 
    	      </xsl:for-each>  			
			</td>			
			</tr>			
			<tr>
			<td>Web site:</td>
			<td>
			<xsl:call-template name="print_href"/>
			</td>			
			</tr>					
			<tr><td></td><td></td>	</tr>
	</xsl:template>
	
	 <xsl:template name="print_href">
	 		<a>
			<xsl:attribute name="href">
			<xsl:value-of select="@url" />
			</xsl:attribute>
			<xsl:value-of select="@url"/>
			</a> 
	 </xsl:template>
	 <xsl:template name="print_reference"> 
<!--  	 <xsl:value-of select="@idref"/> -->	
		  <a href="#{@idref}">
  			<xsl:value-of select="id(@idref)/@id"/>
    	  </a>
	 </xsl:template>
     
	 <xsl:template name="print_author_reference"> 
  			<xsl:value-of select="id(@idref)/@name"/>
	 </xsl:template>
	 <xsl:template name="print_name_reference"> 
  			<xsl:value-of select="id(@idref)/@name"/>
	 </xsl:template>	 
	 <xsl:template name="print_species_reference"> 
  			<xsl:value-of select="id(@idref)/@common_name"/>
	 </xsl:template>		      
	

	<!-- print_publication  -->
	 <xsl:template name="print_publication"> 
	 	 <tr>
	 	 <td> 
	 	 <a name="{@id}"/>
		 [<xsl:value-of select="@id"/>] 
		 </td>
		 <td>
		 <i><xsl:value-of select="@title"/></i>
		 </td>
		 </tr>
	 </xsl:template>  
	<!-- print_endpoint  -->
	 <xsl:template name="print_endpoint"> 
	 	 <tr>
	 	 <td> 
	 	 <a name="{@id}"/>
		 <xsl:value-of select="@name"/>
		 </td>
		 </tr>
	 </xsl:template>  
	 	<!-- print_species  -->
	 <xsl:template name="print_species"> 
	 	 <tr>
	 	 <td> 
	 	 <a name="{@id}"/>
		 <xsl:value-of select="@common_name"/>
		 </td>
		 <td>
 		 <xsl:value-of select="@latin_name"/>
		 </td>
		 </tr>
	 </xsl:template>  	 
      
 </xsl:stylesheet>
