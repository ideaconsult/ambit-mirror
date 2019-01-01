<div class='helptitle' style='font-weight:bold;'>QMap: A Quantitative Structure-Activity-Similarity Map </div>
<div class='helpcontent'>
The QMap is a type of activity landscape<a href='#' class='chelp activity_landscape'>?</a>.
Uses G2<a href='#' class='chelp g2'>?</a> statistics to rank the activity cliffs<a href='#' class='chelp activity_cliff'>?</a>
in a dataset (with predefined activity difference<a href='#' class='chelp athreshold'>?</a> and similarity thresholds<a href='#' class='chelp sthreshold'>?</a>).
<br/>
<a href="http://toxmatch.sourceforge.net/index.htm#background" target=_blank title='More information'>More information</a>
</div>

<div id="keys" style="display:none;">
  <ul>
  	<li><a href="#activity_landscape">Activity landscape</a></li>
    <li><a href="#activity_cliff">Activity cliff</a></li>
    <li><a href="#sar">SAR Analysis</a></li>
    <li><a href="#sasmap">SAS Map</a></li>
    <li><a href="#dataset">Dataset</a></li>
    <li><a href="#feature">Activity</a></li>
    <li><a href="#g2">G2 likelihood</a></li>
    <li><a href="#athreshold">Activity threshold</a></li>
    <li><a href="#sthreshold">Similarity threshold</a></li>
    <li><a href="#bubble">Bubble chart</a></li>
  </ul>
  
  <div id="bubble">
  	Each circle represents one structure. The circles area is proportional to G2<a href='#' class='chelp g2'>?</a> statistics,
  	therefore the larger the circle, the higher is the activity cliff at this structure.
  	<br/>
  	If more than one QMap <a href='#' class='chelp qmap'>?</a> is drawn on the same chart, there could be several circles, representing the
  	same compound.
  </div>
  <div id="activity_cliff">
    A pair of structurally <i>similar</i> compounds with <i>large<i> differences in potency.
  </div>
  <div id="activity_landscape">
    A  representation that integrates the analyses of the structural similarity of and potency differences between compounds sharing the same biological activity.
  </div>    
  <div id="sar">
    The characterization of activity landscapes is performed by visual exploration with the help of SAS maps, network graphs, or by quantifying the relationship between the chemical similarity and activity similarity. The activity similarity is usually defined by absolute differences between activities, or absolute differences, normalized by the activity range: SAR Index ,SALI index.
  </div>
    <div id="sasmap">
    Structure-Activity-Similarity Map
  </div>
    <div id="dataset">
    The dataset analysed
  </div>  
  <div id="feature">
    The property analysed
  </div>  
  <div id="g2">
    The G2 statistics represents the likelihood of a compound forming an activity cliff, which is defined by a large difference in activity (event t) with other compounds in the dataset, given high similarity (event s). 
    See also <a href="http://toxmatch.sourceforge.net/index.htm#ranking">activity cliffs ranking</a>
  </div>
  <div id="qmap">
  A Quantitative Structure-Activity-Similarity Map (type of activity landscape <a href='#' class='chelp activity_landscape'>?</a>) 
Uses G2<a href='#' class='chelp g2'>?</a> statistics to rank the activity cliffs <a href='#' class='chelp activity_cliff'>?</a>
in a dataset (with predefined activity difference<a href='#' class='chelp athreshold'>?</a>  
and similarity thresholds<a href='#' class='chelp sthreshold'>?</a> ).
  </div>
      <div id="athreshold">
        A value defining whether a difference of activity of pair compounds will be considered of large (>= threshold) or small ( < threshold).
        The default value is one standard deviation.
  </div>
   <div id="sthreshold">
    A value defining whether a pair of compounds will be considered of high similarity (>= threshold) or of low similarity ( <threshold).
    The default value is 0.8 (Tanimoto similarity on <a href="http://cdk.sf.net/">CDK</a> hashed 1024 bit fingerprints). 
    See also <a href="http://toxmatch.sourceforge.net/index.htm#ranking">activity cliffs ranking</a>
  </div>
</div>      