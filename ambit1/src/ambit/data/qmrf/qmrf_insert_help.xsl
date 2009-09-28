 <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                 xmlns="http://www.w3.org/1999/xhtml">


<xsl:output method="xml"  encoding="utf-8" doctype-system="qmrf.dtd"
		doctype-public="http://ambit.acad.bg/qmrf/qmrf.dtd" indent="yes"/>



<xsl:variable name="amp">&amp;</xsl:variable>
<xsl:template match="*">
  <xsl:copy>
    <xsl:copy-of select="@*"/>

		<xsl:choose>
			<!--  If help attribute exists, add reference to &help entity-->
			<xsl:when test="@help">

			    <xsl:attribute name="help">
			 		<xsl:choose>
					<xsl:when test="@chapter='1.1'">
					<xsl:text>Provide a short and indicative title for the model including relevant keyword. Some possible keywords are: endpoint modelled (as specified in field 3.2, recommended), name of the model, name of the modeller, and name of the software coding the model. Examples: "BIOWIN for Biodegradation"; "TOPKAT Developmental Toxicity Potential Aliphatic Model".</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=1.2">
					<xsl:text>If appropriate, identify any model that is related to the model described in the present QMRF. Example: "TOPKAT Developmental Toxicity Potential Heteroaromatic Model and TOPKAT Developmental Toxicity Potential Carboaromatic Model" (these two models are related to the primary model "TOPKAT Developmental Toxicity Potential Aliphatic Model").</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=1.3">
					<xsl:text>If appropriate, specify the name and the version of the software that implements the model. Examples: "BIOWIN v. 4.2 (EPI Suite)","TOPKAT v. 6.2"</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=2.1">
					<xsl:text>Report the date of QMRF drafting (day/month/year). Example: "5 November 2006"</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=2.2">
					<xsl:text>Indicate the name and the contact details of the author(s) of the QMRF (first version of the QMRF).</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=2.3">
					<xsl:text>Indicate the date (day/month/year) of any update of the QMRF. The QMRF can be updated for a number of reasons such as additions of new information (e.g. addition of new validation studies in section 7) and corrections of information. </xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=2.4">
					<xsl:text>Indicate the name and the contact details of the author(s) of the updates QMRF (see field 2.3) and list which sections and fields have been modified.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=2.5">
					<xsl:text>Indicate the name of model developer(s)/author(s), and the corresponding contact details; possibly report the contact details of the corresponding author.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=2.6">
					<xsl:text>Report the year of release/publication of the model described in the current QMRF.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=2.7">
					<xsl:text>List the main bibliographic references (if any) to original paper(s) explaining the model development and/or software implementation. Any other reference such as references to original experimental data and related models can be reported in field 9.2 "Bibliography"</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=2.8">
					<xsl:text>Indicate whether the model is proprietary or non-proprietary and specify (if possible) what kind of information about the model cannot be disclosed or are not available (e.g., training and external validation sets, source code, and algorithm). Example: "The model is non-proprietary but the training and test sets are not available"; "The model is proprietary and the algorithm and the datasets are confidential".</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=2.9">
					<xsl:text>Indicate if you are aware or suspect that another QMRF is available for the current model you are describing. If possible, identify this other QMRF.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=3">
					<xsl:text>PRINCIPLE 1: "A DEFINED ENDPOINT". ENDPOINT refers to any physicochemical, biological, or environmental effect that can be measured and therefore modelled. The intent of PRINCIPLE 1 (a (Q)SAR should be associated with a defined endpoint) is to ensure clarity in the endpoint being predicted by a given model, since a given endpoint could be determined by different experimental protocols and under different experimental conditions. It is therefore important to identify the experimental system that is being modelled by the (Q)SAR.</xsl:text>
					</xsl:when>


					<xsl:when test="@chapter=3.1">
					<xsl:text>Indicate the species for the endpoint being modelled.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=3.2">
					<xsl:text>Choose the endpoint (physicochemical, biological, or environmental effect) from the pre-defined classification. If the pre-defined classification does not include the endpoint of interest, select "Other" and report the endpoint in the subsequent field 3.3.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=3.3">
					<xsl:text>Include in this field any other information to define the endpoint being modelled. Specify the endpoint further if relevant, e.g. according to test organism such as species, strain, sex, age or life stage; according to test duration and protocol; according to the detailed nature of endpoint etc. You can also define here the endpoint of interest in case this is not listed in the pre-defined classification (see field 3.2) or you can add information about a second endpoint modelled by the same model. Example: "Nitrate radical degradation rate constant: kNO3".</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=3.4">
					<xsl:text>Specify the units of the endpoint measured.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=3.5">
					<xsl:text>Specify the relationship between the dependent variable being modelled and the endpoint measured since the two quantities may be different. Example: For modelling purposes all rate constants (i.e. Nitrate radical degradation rate constant kNO3) were transformed to logarithmic units and multiplied by -1 to obtain positive values. The dependent variable is: -log(kNO3).</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=3.6">
					<xsl:text>Make any useful reference to a specific experimental protocol (or protocols) followed in the collection of the experimental data sets.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=3.7">
					<xsl:text>Provide available information about the test data selection and evaluation and include a description of the data quality used to develop the model. This includes provision of information about the variability of the test data, i.e. repeatability (variability over time) and reproducibility (variability between laboratories) and sources of error (confounding factors which may influence testing results).</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=4">
					<xsl:text>PRINCIPLE 2: "AN UNAMBIGUOUS ALGORITHM". The (Q)SAR estimate of an endpoint is the result of applying an ALGORITHM to a set of structural parameters which describe the chemical structure. The intent of PRINCIPLE 2 (a (Q)SAR should be associated with a unambiguous algorithm) is to ensure transparency in the model algorithm that generates predictions of an endpoint from information on chemical structure and/or physicochemical properties. In this context, algorithm refers to any mathematical equation, decision rule or output from a formalised modelling approach.</xsl:text>
					</xsl:when>


					<xsl:when test="@chapter=4.1">
					<xsl:text>Describe the type of model (e.g., SAR, QSAR, Expert System, Neural Network, etc.).</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=4.2">
					<xsl:text>Report the algorithm (only the algorithm) for generating predictions from the descriptors; more text information about the algorithm can be reported in the following fields of this section or as supporting information (see field 9.3). If the algorithm is too long and complicated and thus cannot be reported here, include in this field a reference to a paper or a document where the algorithm is described in detail. This material can be attached as supporting information.</xsl:text>
					</xsl:when>
					<!-- Deleted in 1.2 All numbering moved one up
					<xsl:when test="@chapter=4.3">
					<xsl:text>If necessary, include the description of the algorithm and/or any comment about it. If in the previous field, only a reference to a paper is reported, it is advised to provide a brief description of the algorithm in this field.</xsl:text>
					</xsl:when>
					-->

					<xsl:when test="@chapter=4.3">
					<xsl:text>Identify the number and the name or identifier of the descriptors included in the model. In this context, descriptors refers to e.g. physicochemical parameters, structural fragments etc.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=4.4">
					<xsl:text>Indicate the number and the type (name) of descriptors initially screened, and explain the method used to select the descriptors and develop the model from them.</xsl:text>
					</xsl:when>


					<xsl:when test="@chapter=4.5">
					<xsl:text>Explain the approach used to derive the algorithm and the method (approach) used to generate each descriptor.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=4.6">
					<xsl:text>Specify the name and the version of the software used to generate the descriptors. If relevant, report the specific settings chosen in the software to generate a descriptor.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=4.7">
					<xsl:text>Report the following ratio: number of descriptors to number of chemicals (chemicals from the training set), if applicable (if not, explain why).</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=5">
					<xsl:text>PRINCIPLE 3: "A DEFINED DOMAIN OF APPLICABILITY".APPLICABILITY DOMAIN refers to the response and chemical structure space in which the model makes predictions with a given reliability. Ideally the applicability domain should express the structural, physicochemical and response space of the model. The CHEMICAL STRUCTURE (x variable) space can be expressed by information on physicochemical properties and/or structural fragments. The RESPONSE (y variable) can be any physicochemical, biological or environmental effect that is being predicted. According to PRINCIPLE 3 a (Q)SAR should be associated with a defined domain of applicability. Section 5 can be repeated (e.g., 5.a, 5.b, 5.c, etc) as many time as necessary if more than one method has been used to assess the applicability domain.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=5.1">
					<xsl:text>Describe the response and chemical structure space in which the model makes predictions with a given reliability. Discuss if relevant whether:
a.	fixed or probabilistic boundaries define the applicability domain;
b.	structural features, a descriptor or a response space defines the applicability domain;
c.	in the case of SAR, there exists a description of the limits on its applicability (inclusion and/or exclusion rules regarding the chemical classes to which the substructure is applicable);
d.	in the case of SAR, there exist rules describing the modularity effects of the substructure's molecular environment;
e.	on the case of QSAR, there exist inclusion and/or exclusion rules that define the descriptor variable ranges for which the QSAR is applicable;
f.	in the case of QSAR, there exist inclusion and/or exclusion rules that define the response variable ranges for which the QSAR is applicable;
g.	there exists a (graphical) expression of how the descriptor values of the chemicals in the training set are distributed in relation to the endpoint values predicted by the model.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=5.2">
					<xsl:text>Describe the method used to assess the applicability domain of the model.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=5.3">
					<xsl:text>Specify the name and the version of the software used to apply the applicability domain method, where applicable. If relevant, report the specific settings chosen in the software to apply the method.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=5.4">
					<xsl:text>Describe for example the inclusion and/or exclusion rules (fixed or probabilistic boundaries, structural features, descriptor space, response space) that define the applicability domain.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=6">
					<xsl:text>PRINCIPLE 4: "APPROPRIATE MEASURES OF GOODNESS-OF-FIT, ROBUSTENESS AND PREDICTIVITY". PRINCIPLE 4 expresses the need to perform validation to establish the performance of the model. GOODNESS-OF-FIT and ROBUSTNESS refer to the internal model performance.</xsl:text>
					</xsl:when>


					<xsl:when test="@chapter=6.1">
					<xsl:text>Indicate whether the training set is somehow available (e.g., published in a paper, embedded in the software implementing the model, stored in a database) and appended to the current QMRF as supporting information (field 9.3). If it is not available, explain why. Example: "It is available and attached"; "It is available but not attached" ; "It is not available because the dataset is proprietary"; "The dataset could not be retrieved".</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=6.2">
					<xsl:text>Indicate whether the following information for the training set is reported as supporting information (see field 9.3): a) Chemical names (common names and/or IUPAC names); b) CAS numbers; c) SMILES; d) InChI codes; e) MOL files; f) Structural formula; g) Any other structural information.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=6.3">
					<xsl:text>Indicate whether the descriptor values of the training set are available and are attached as supporting information (see field 9.3).</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=6.4">
					<xsl:text>Indicate whether dependent variable values of the training set are available and attached as supporting information (see field 9.3).</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=6.5">
					<xsl:text>Indicate any other relevant information about the training set (e.g. number and type of compounds in the training set (e.g. for models predicting positive and negative results the number of positives and the number of negatives in the training set)).</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=6.6">
					<xsl:text>Indicate whether raw data have been processed before modelling (e.g. averaging of replicate values); if yes, report whether both raw data and processed data are given.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=6.7">
					<xsl:text>Report here goodness-of-fit statistics (r<sup>2</sup>, r<sup>2</sup> adjusted, standard error, sensitivity, specificity, false negatives, etc).</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=6.8">
					<xsl:text>Report here the corresponding statistics.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=6.9">
					<xsl:text>Report here the corresponding statistics, the strategy for splitting the dataset (e.g. random), the percentage of left out compounds and the number of cross-validations.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=6.10">
					<xsl:text>Report here the corresponding statistics and the number of iterations.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=6.11">
					<xsl:text>Report here the corresponding statistics and the number of iterations.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=6.12">
					<xsl:text>Report here the corresponding statistics.</xsl:text>
					</xsl:when>


					<xsl:when test="@chapter=7">
					<xsl:text>PRINCIPLE 4: "APPROPRIATE MEASURES OF GOODNESS-OF-FIT, ROBUSTENESS AND PREDICTIVITY". PRINCIPLE 4 expresses the need to perform validation to establish the performance of the model. GOODNESS-OF-FIT and ROBUSTNESS refer to the internal model performance.</xsl:text>
					</xsl:when>


					<xsl:when test="@chapter=7.1">
					<xsl:text>Indicate whether the following information for the external validation set is reported as supporting information (see field 9.3): a)Chemical names (common names and/or IUPAC names) b) CAS numbers c) SMILES d) InChI codes e) MOL files f) Structural formula g) Any other structural information. Also indicate the name of the file for the supporting information.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=7.2">
					<xsl:text>Indicate whether the following information for the external validation set is reported as supporting information (see field 9.3): a) Chemical names (common names and/or IUPAC names); b) CAS numbers; c) SMILES; d) InChI codes; e) MOL files; f) Structural formula; g) Any other structural information.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=7.3">
					<xsl:text>Indicate whether descriptor values of the external validation set are somehow available and attached as supporting information (see field 9.3).</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=7.4">
					<xsl:text>Indicate whether dependent variable values of the external validation set are somehow available and attached as supporting information (see field 9.3).</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=7.5">
					<xsl:text>Indicate any other relevant information about the validation set. Example: "External validation set with 56 compounds appended."</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=7.6">
					<xsl:text>Indicate any experimental design for getting the test set (e.g. by randomly setting aside chemicals before modelling, by literature search after modelling, by prospective experimental testing after modelling, etc.).</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=7.7">
					<xsl:text>Report here the corresponding statistics. In the case of classification models, include false positive and negative rates.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=7.8">
					<xsl:text>Discuss whether the external validation set is sufficiently large and representative of the applicability domain.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=7.9">
					<xsl:text>Add any other useful comments about the external validation procedure.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=8">
					<xsl:text>PRINCIPLE 5: "A MECHANISTIC INTERPRETATION, IF POSSIBLE". According to PRINCIPLE 5, a (Q)SAR should be associated with a mechanistic interpretation, if possible.</xsl:text>
					</xsl:when>


					<xsl:when test="@chapter=8.1">
					<xsl:text>Provide information on the mechanistic basis of the model (if possible). In the case of SAR, you may want to describe (if possible) the molecular features that underlie the properties of the molecules containing the substructure (e.g. a description of how sub-structural features could act as nucleophiles or electrophiles, or form part or all of a receptor-binding region). In the case of QSAR, you may give (if possible) a physicochemical interpretation of the descriptors used (consistent with a known mechanism of biological action). If it is not possible to provide a mechanistic interpretation, try to explain why.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=8.2">
					<xsl:text>Indicate whether the mechanistic basis of the model was determined a priori (i.e. before modelling, by ensuring that the initial set of training structures and/or descriptors were selected to fit pre-defined mechanism of action) or a posteriori (i.e. after modelling, by interpretation of the final set of training structures and or descriptors).</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=8.3">
					<xsl:text>Report any other useful information about the (purported) mechanistic interpretation described in the previous fields (8.1 and 8.2) such as any reference supporting the mechanistic basis.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=9">
					<xsl:text>PRINCIPLE 5: "A MECHANISTIC INTERPRETATION, IF POSSIBLE". According to PRINCIPLE 5, a (Q)SAR should be associated with a mechanistic interpretation, if possible.</xsl:text>
					</xsl:when>


					<xsl:when test="@chapter=9.1">
					<xsl:text>Add here other relevant and useful comments (e.g. other related models, known applications of the model) that may facilitate regulatory considerations on the model described. You may also want to add any useful information that will aid the interpretation of the reliability of a specific model prediction.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=9.2">
					<xsl:text>Report useful references other than those directly associated with the model development (references describing the model development are reported in field 2.5).</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=9.3">
					<xsl:text>Indicate whether supporting information is attached (e.g. external documents) to this QMRF and specify its content and possibly its utility.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=10">
					<xsl:text>The summary section is specific for the JRC Inventory. If the model is submitted to JRC for inclusion in the JRC Inventory of QSAR models, then this summary is compiled by JRC after QMRF submission. The QMRF author does not have to fill in any of the fields of the summary section.</xsl:text>
					</xsl:when>


					<xsl:when test="@chapter=10.1">
					<xsl:text>A unique number (numeric identifier) is assigned to any QMRF that is published in the JRC inventory. The number encodes the following information: model described in the QMRF (as derived from field 4.2), software implementing the model (as derived from field 1.3), version of the QMRF for the same model and the same software (as derived from the information included in field 2.4) and author of the QMRF (as derived from field 2.2). The number is unique for any QMRF uploaded and stored in the JRC inventory.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=10.2">
					<xsl:text>The date (month/year) of publication in the JRC inventory is reported here.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=10.3">
					<xsl:text>Any relevant keywords associated with the present QMRF are reported here.</xsl:text>
					</xsl:when>

					<xsl:when test="@chapter=10.4">
					<xsl:text>Any comments that are relevant for the publication of the QMRF in the JRC Inventory (e.g., comments about updates and about supporting information) are reported here.</xsl:text>
					</xsl:when>

					<xsl:otherwise>
						<xsl:text></xsl:text>
					</xsl:otherwise>
					</xsl:choose>

				    </xsl:attribute>

			</xsl:when>
			<xsl:otherwise>

			</xsl:otherwise>
		</xsl:choose>


    <xsl:apply-templates/>
  </xsl:copy>
</xsl:template>


 </xsl:stylesheet>


