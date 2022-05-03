/**
 * ==========================================================================
 * Filename: OmsMDCBatchConstants.java
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
public class OmsMDCBatchConstants {
	
	/**
	 * Instantiates a new oms Eucashery batch constants.
	 */
	private OmsMDCBatchConstants() {
		
	}
	
	/** The Constant DATE_FORMAT_YYYY_MM_DD. */
	public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
	
	/** The Constant DATE_FORMAT_YYYYMMDD. */
	public static final String DATE_FORMAT_YYYYMMDD = "yyyyMMdd";
	
	/** The Constant OUTPUT_TEXT_FILE_NAME. */
	public static final String OUTPUT_TEXT_FILE_NAME = "OMS_MDC_Batch_OutputFile";
	
	/** The Constant FTP_SERVER_ADDRESS. */
	public static final String OUTPUT_TEXT_FILE_EXTENSION = ".dat";
	
	/** The Constant FTP_SERVER_ADDRESS. */
	public static final String FTP_SERVER_ADDRESS = "njrarltcda004cf.linux.us.ams1907.com";
	
	/** The Constant FTP_SERVER_USER_NAME. */
	public static final String FTP_SERVER_USER_NAME = "omsftp";
	
	/** The Constant FTP_SERVER_PASSWORD. */
	public static final String FTP_SERVER_PASSWORD = "ADm5DXm7cuWSRs";
	
	/** The Constant FTP_SERVER_REMOTE_DIR. */
	//public static final String FTP_SERVER_REMOTE_DIR = "/opt/UPS/OPSYSMOD/data/MDC/";
			
	public static final String FTP_SERVER_REMOTE_DIR = "/home/omsftp/oms/";

	/** The Constant INFOLIB_BATCH_NAME. */
	public static final String MDC_BATCH_NAME = "MDCJob";
	
	/** The Constant SUCCESS_STATUS_CODE. */
	public static final String SUCCESS_STATUS_CODE = "00";
	
	/** The Constant FAILURE_STATUS_CODE. */
	public static final String FAILURE_STATUS_CODE = "01";
	
	/** The Constant SUCCESS_COMMENT_TEXT. */
	public static final String SUCCESS_COMMENT_TEXT = "Successful";
	
	/** The Constant FAILURE_COMMENT_TEXT. */
	public static final String FAILURE_COMMENT_TEXT = "Failure";
	
	/** The Constant STRING_YES. */
	public static final String STRING_YES = "Yes";
	
	/** The Constant STRING_NO. */
	public static final String STRING_NO = "No";
	
	/** The Constant CHAR_YES. */
	public static final String CHAR_YES = "Y";
	
	/** The Constant CHAR_NO. */
	public static final String CHAR_NO = "N";
	
	public static final String RECORD_TYPE_DETAIL = "D";
	
	public static final String NEW_CALL_INSTANCE = "        ";
	
	public static final String PRINT_CONTROL_NUMBER = "       ";
	
	public static final String FILLER_230 = " ";
	
	public static final String FILLER_208 = " ";
	
	public static final String FILLER_201 = " ";
	
	public static final String FILLER_203 = " ";
	
	public static final String FILLER_1 = "1";
	
	public static final String RECORD_TYPE = "1";
	public static final String CONTROL = "1";
	
	public static final String CREATION_DATE = "1";
	public static final String FILLER_3378 = "1";
	
	public static final String SPACE = " ";

	public static final String ORDER_HAVE_TO_BE_ROUTED = "01";
	public static final String ORDER_ALREADY_ROUTED = "02";
	public static final String ORDER_CANCELLED_CODE = "07";

	public static final String CARRIAGE_RETURN = "\r";
	public static final String LINE_FEED = "\n";

	public static final String UNDERSCORE = "_";
	public static final String DOT = ".";
	
	public static Boolean MDC_RETRY_COMPLETE = Boolean.FALSE;
	public static Integer RETRY_COUNT = 1;
	public static final Integer MAX_RETRY = 2; 
	public static Integer FILE_RETRY_COUNT=1;
	public static Integer SCRIPT_RETRY_COUNT=1;
	public static final String MDC_DATA_PROCESSING_JOB = "mdcDataProcessingJob";
	public static final String MDC_DATA_PROCESSING_STEP = "mdcDataProcessingStep";
	public static final String PATH_SEPARATOR = "/";

	public static final String THIRD_PARTY = "01";
	public static final String CONSIGNEE = "02";
	public static final String CASH = "03";
	public static final String CHARGE_CARD = "04";
	public static final String SHIPPER = "05";
	public static final String ONEZ = "06";
	public static final String PAY_PAL = "07";
	public static final String ALT_PYMT = "08";
	
	public static final String DEFAULT_SHIPPER_NUM = "0000999X99";

	public static final String REJECTED_CODE = "22";

}
