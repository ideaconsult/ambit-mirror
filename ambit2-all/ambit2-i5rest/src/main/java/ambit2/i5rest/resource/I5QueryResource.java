package ambit2.i5rest.resource;

import java.io.Serializable;

import org.ideaconsult.iuclidws.types.Types.DataLayer;
import org.ideaconsult.iuclidws.types.Types.DocumentType;
import org.ideaconsult.iuclidws.types.Types.DocumentTypeType;
import org.ideaconsult.iuclidws.types.Types.IndexTypes;
import org.ideaconsult.iuclidws.types.Types.LogicalExpressionQueryBlock;
import org.ideaconsult.iuclidws.types.Types.LogicalExpressionQueryBlockChoice_type0;
import org.ideaconsult.iuclidws.types.Types.LogicalExpressionQueryBlockChoice_type1;
import org.ideaconsult.iuclidws.types.Types.QueryBlockOperation;
import org.ideaconsult.iuclidws.types.Types.QueryExpression;
import org.ideaconsult.iuclidws.types.Types.QueryExpressionChoice_type0;
import org.ideaconsult.iuclidws.types.Types.QueryField_type0;
import org.ideaconsult.iuclidws.types.Types.QueryFields;
import org.ideaconsult.iuclidws.types.Types.QueryFieldsChoice_type0;
import org.ideaconsult.iuclidws.types.Types.QueryFieldsSequence;
import org.ideaconsult.iuclidws.types.Types.RangeQueryField;
import org.ideaconsult.iuclidws.types.Types.RangeQueryValue;
import org.ideaconsult.iuclidws.types.Types.RangeQueryValues;
import org.ideaconsult.iuclidws.types.Types.RangeQueryValuesSequence;
import org.ideaconsult.iuclidws.types.Types.RangeSearchOperator;
import org.ideaconsult.iuclidws.types.Types.SimpleEndpointQueryBlock;
import org.ideaconsult.iuclidws.types.Types.SimpleQueryBlock;
import org.ideaconsult.iuclidws.types.Types.StringQueryField;
import org.ideaconsult.iuclidws.types.Types.StringQueryValue;
import org.ideaconsult.iuclidws.types.Types.StringQueryValues;
import org.ideaconsult.iuclidws.types.Types.StringQueryValuesSequence;
import org.ideaconsult.iuclidws.types.Types.StringSearchOperator;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.i5rest.convertors.I5QueryResultsReporter;
import ambit2.rest.StringConvertor;

public class I5QueryResource<T extends Serializable> extends I5Resource<QueryExpression, T> {
	
	public static final String resourceKey = "key";
	public static final String resource = "/query";
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_HTML
		});		
	}
	protected SimpleEndpointQueryBlock getEndpointQueryBlock(
					String endpointKind,
					String fieldname,
					double minValue,
					double maxValue) {
        // build a endpoint point and a boiling point endpoint query
        final SimpleEndpointQueryBlock endpointQueryBlock = new SimpleEndpointQueryBlock();
        // build query blocks
        
        // index types and parent types
		final IndexTypes indexTypes = new IndexTypes();
		DocumentType dt = new DocumentType();
		dt.setType(DocumentTypeType.EndpointStudyRecord);
		indexTypes.addDocumentType(dt);

        // endpoint point
        
        endpointQueryBlock.setEndpointKind(endpointKind);           
        endpointQueryBlock.setIndexTypes(indexTypes);
        endpointQueryBlock.setParentType(DocumentTypeType.Substance);
        
        final QueryFields endpointQueryFields = new QueryFields();
        endpointQueryBlock.setQueryFields(endpointQueryFields);
        
        final RangeQueryField endpointQF = new RangeQueryField();
        QueryField_type0 qft0 = new QueryField_type0();
        qft0.setFieldName(fieldname);
        qft0.setID(fieldname);
        endpointQF.setQueryField(qft0);            
        
 
        final RangeQueryValues endpointValues = new RangeQueryValues();
        final RangeQueryValue endpointQV = new RangeQueryValue();
        endpointQV.setOp(RangeSearchOperator.OVERLAPPING);
        endpointQV.setSearchValueLower(minValue);
        endpointQV.setSearchValueUpper(maxValue);
         
        
        RangeQueryValuesSequence valuesSeq = new RangeQueryValuesSequence();
        valuesSeq.setRangeQueryValue(endpointQV);
        endpointValues.setRangeQueryValuesSequence(new RangeQueryValuesSequence[] {valuesSeq});
        endpointQF.setValues(endpointValues);
           
        QueryFieldsSequence qfSeq = new QueryFieldsSequence();
        QueryFieldsChoice_type0 qfct = new QueryFieldsChoice_type0();
        qfct.setRangeQueryField(endpointQF);
        qfSeq.setQueryFieldsChoice_type0(qfct);
        endpointQueryFields.setQueryFieldsSequence(new QueryFieldsSequence[] {qfSeq});
        return endpointQueryBlock;
	}
	
	protected SimpleQueryBlock getSubstanceQueryBlock(String fieldname,String value) {

        final SimpleQueryBlock casQueryBlock = new SimpleQueryBlock();
        
        StringQueryValue param = new StringQueryValue();
        param.setSearchExpression(value);
        param.setOp(StringSearchOperator.EQUALS);
        StringQueryValuesSequence ssssss = new StringQueryValuesSequence();
        ssssss.setStringQueryValue(param);
        StringQueryValues values = new StringQueryValues();
        values.addStringQueryValuesSequence(ssssss);
        
        QueryField_type0 q0 = new QueryField_type0();
        q0.setID(fieldname);
        q0.setFieldName(fieldname);
        
        StringQueryField sqf = new StringQueryField();
        sqf.setQueryField(q0);
        sqf.setValues(values);
        
        QueryFieldsChoice_type0 t = new QueryFieldsChoice_type0();
        t.setStringQueryField(sqf);
        QueryFieldsSequence s = new QueryFieldsSequence();
        s.setQueryFieldsChoice_type0(t);
        QueryFieldsSequence[] params = new QueryFieldsSequence[] {s};
        QueryFields qf = new QueryFields();
        qf.setQueryFieldsSequence(params);
        
        casQueryBlock.setQueryFields(qf);
      
        
        QueryExpressionChoice_type0 q = new QueryExpressionChoice_type0();
		q.setSimpleQueryBlock(casQueryBlock);
		
		casQueryBlock.setIndexType(DocumentTypeType.Substance);
		return casQueryBlock;
	}
	private QueryExpression buildLogicalExpression(
			final QueryBlockOperation operation,
			final SimpleQueryBlock left,
			final SimpleEndpointQueryBlock right) {
		final QueryExpression expression = new QueryExpression();
           expression.setComment("test expression");
           expression.setDataLayer(DataLayer.RAW_DATA);
           
           // combine query blocks
           final LogicalExpressionQueryBlock logicBlock  = new LogicalExpressionQueryBlock();
           logicBlock.setOperation(operation);
           LogicalExpressionQueryBlockChoice_type0 q0 = new LogicalExpressionQueryBlockChoice_type0();
           q0.setLogicalExpressionQueryBlock(logicBlock);
           
           LogicalExpressionQueryBlockChoice_type1 q1 = new LogicalExpressionQueryBlockChoice_type1();
           
           q1.setLogicalExpressionQueryBlock(logicBlock);
           q0.setSimpleEndpointQueryBlock(right);

           //q0.setSimpleQueryBlock(left);
           q1.setSimpleQueryBlock(left);
           logicBlock.setLogicalExpressionQueryBlockChoice_type0(q0);
           logicBlock.setLogicalExpressionQueryBlockChoice_type1(q1);
           
           QueryExpressionChoice_type0 qe = new QueryExpressionChoice_type0();
           qe.setLogicalExpressionQueryBlock(logicBlock);
           expression.setQueryExpressionChoice_type0(qe);

		return expression;
	}    	
	@Override
	protected QueryExpression createQuery(Context context, Request request,
			Response response) throws ResourceException {
		try {
			String key = SubstanceResource.getKey(request);
			if (key==null) key="50-00-0";
	        final SimpleEndpointQueryBlock endpointQueryBlock = getEndpointQueryBlock(
	        		"EC_FISHTOX",
	        		"EC_FISHTOX.EFFCONC.PRECISION_LOQUALIFIER",
	        		Integer.MIN_VALUE,
	        		Integer.MAX_VALUE
	        		);
	        
	        final SimpleQueryBlock casQueryBlock = getSubstanceQueryBlock(
	        		"/referenceSubstanceRef/ecSubstanceInventoryEntryRef/casNumber",
	        		key); //"58-08-2"
	        
	        return buildLogicalExpression(
	        		QueryBlockOperation.AND,casQueryBlock,
	        		endpointQueryBlock );        
		} catch (Exception x) {
			throw new ResourceException(x);

		}
	}
	@Override
	public IProcessor<QueryExpression, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		return new StringConvertor(new I5QueryResultsReporter(getRequest(),getSession()),MediaType.APPLICATION_XML);
	}

}
