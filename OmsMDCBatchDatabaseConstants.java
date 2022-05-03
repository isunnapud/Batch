/**
 * ==========================================================================
 * Filename: OmsMDCBatchDatabaseConstants.java
 * =============================================================================
 * <p>
 * =============================================================================
 * <p>
 * NOTICE
 * Confidential, unpublished property of United Parcel Service.
 * Use and distribution limited solely to authorized personnel.
 * <p>
 * The use, disclosure, reproduction, modification, transfer, or
 * transmittal of this work for any purpose in any form or by
 * any means without the written permission of United Parcel
 * Service is strictly prohibited.
 * <p>
 * Copyright (c) 2016-2017, United Parcel Service of America, Inc.
 * All Rights Reserved.
 * <p>
 * =============================================================================
 * Author/Architect - OMS@ups.com
 * Date Create – 11-03-2017
 * =============================================================================
 */
package com.ups.ops.oms.batch.mdc.constants;

/**
 * The Class OmsMDCBatchConstants.
 */
public class OmsMDCBatchDatabaseConstants {
	
	private OmsMDCBatchDatabaseConstants() {
		
	}

	/** The Constant RESPONSE_CODE. */
	public static final String RESPONSE_CODE = "RESPONSECODE";
	
	/** The Constant STATUS_MESSAGE. */
	public static final String STATUS_MESSAGE = "STATUSMESSAGE";
	
	/** The Constant NUMBER_OF_ROWS. */
	public static final String NUMBER_OF_ROWS = "NUMBEROFROWS";
	
	/** The Constant START_TIME. */
	public static final String START_TIME = "STARTTIME";
	
	/** The Constant END_TIME. */
	public static final String END_TIME = "ENDTIME";
	
	/** The Constant ELAPSED_TIME. */
	public static final String ELAPSED_TIME = "ELAPSEDTIME";
	
	/** The Constant BATCH_NAME. */
	public static final String BATCH_NAME = "BatchName";

	/** The Constant COMMENT_TEXT. */
	public static final String COMMENT_TEXT = "CommentText";

	/** The Constant BATCH_STATUS_CODE. */
	public static final String BATCH_STATUS_CODE = "BatchStatusCode";

	/** The Constant PROCESSING_JOB_LAST_RUN_TIMESTAMP. */
	public static final String PROCESSING_JOB_LAST_RUN_TIMESTAMP = "ProcessingJobLastRunTimestamp";

	/** The Constant LAST_RUN_TIMESTAMP. */
	public static final String LAST_RUN_TIMESTAMP = "Last_Run";

	/** The Constant CURRENT_RUN_TIMESTAMP. */
	public static final String CURRENT_RUN_TIMESTAMP = "Current_Run";
	
	/** The Constant INDEX_OF_PACKAGE_RECORD_TEXT. */
	public static final int INDEX_OF_PACKAGE_RECORD_TEXT = 5;
	
	/** The Constant INDEX_OF_SHIPMENT_CONTROL_RECORD_TEXT. */
	public static final int INDEX_OF_SHIPMENT_CONTROL_RECORD_TEXT = 3;
	
	/** The Constant INDEX_OF_SERVICE_TYPE_RECORD_TEXT. */
	public static final int INDEX_OF_SERVICE_TYPE_RECORD_TEXT = 4;
	
	/** The Constant INDEX_OF_ORDER_DOCUMENT_RECORD_TEXT. */
	public static final int INDEX_OF_ORDER_DOCUMENT_RECORD_TEXT = 2;
	
	public static final String ORGANISATION_NUMBER_COLUMN = "OrganizationNumber";
	public static final String ALL_JSONDATA_COLUMN = "AllJsonData";
	public static final String ORDER_SYSTEM_NUMBER_COLUMN = "OrderSystemNumber";
	public static final String ORDER_UPDATE_CODE_COLUMN = "OrderUpdateCode";
	public static final String ORDER_UPDATE_TIMESTAMP_COLUMN = "OrderUpdateTimestamp";
	public static final String RECORD_INSERT_TIMESTAMP_COLUMN = "RecordInsertTimestamp";
	public static final String ORDER_STATUS_RECORD_DOCUMENT_TEXT_COLUMN = "OrderStatusRecordDocumentText";
	public static final String ORDER_UPDATE_MIN_TIMESTAMP_COLUMN = "OrderUpdateMinTimestamp";
	public static final String DISPATCH_FACILITY_SYSTEM_NUMBER_COLUMN = "DispatchFacilitySystemNumber";
	public static final String RECORD_INSERT_MIN_TIMESTAMP_COLUMN = "RecordInsertMinTimestamp";
	public static final String ACKNOWLEDGEMENT_TIMESTAMP_COLUMN = "AcknowledgementTimestamp";
	public static final String ACKNOWLEDGEMENT_TYPECODE_COLUMN = "AcknowledgementTypeCode";
	
	//StoredProcedure data count
	public static final String RECORDCOUNT="RECORDCOUNT";
	
}
